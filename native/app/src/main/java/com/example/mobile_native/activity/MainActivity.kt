package com.example.mobile_native.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobile_native.R
import com.example.mobile_native.adapter.BookAdapter
import com.example.mobile_native.model.Book
import com.example.mobile_native.network.InternetConnection
import com.example.mobile_native.network.NetworkAPIAdapter
import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.kotlin.createObject
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnDeleteListener {
    private val realm: Realm by lazy { Realm.getDefaultInstance() } //reference to database Realm
    private lateinit var adapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //supportActionBar!!.hide()
        //Toast.makeText(this, "OnCreate main", Toast.LENGTH_SHORT).show()

        addBookFab.setOnClickListener { addBook() }

        val books: ArrayList<Book> = this.getBooks()    //get all books from local database
        this.adapter = BookAdapter(books)

        books_recycleview.layoutManager = LinearLayoutManager(this)
        books_recycleview.adapter = this.adapter

        // Animate the spin wheel
        spinWheel.setOnClickListener {
            ObjectAnimator.ofFloat(spinWheel, "rotation", 360f).apply {
                duration = 1000
                start()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        spinWheel.rotation = 0f
                    }
                })
            }
        }
    }

    /**
     * Clicking '+' button opens a new activity.
     */
    private fun addBook() {
        startActivity(Intent(this, AddBookActivity::class.java))
    }

    private fun getBooks(): ArrayList<Book> {
        return ArrayList(this.realm.where(Book::class.java).findAll())
    }

    /**
     * What to do after delete button is pressed.
     */
    override fun setOnDeleteListener() {
        this.adapter.items = getBooks()
        this.adapter.notifyDataSetChanged()
    }

    /**
     * Integrates all buttons in the top right.
     * Add the synchronize button to the upper menu.
     * And the settings one.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.buttons, menu)
        return true
    }

    /**
     * Navigate to Settings activity.
     */
    private fun goToPreferenceActivity() {
        startActivity(Intent(this, PreferenceActivity::class.java))
    }

    /**
     * This function is a switch statement ('when' keyword).
     * If item.itemId == btn_Refresh -> { do this}
     * Decides what happens when we click the synchronize button.
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_refresh -> {
            // This button should do nothing if no internet connection
            if (InternetConnection.getInstance(this).isOnline) {
                synchronize()
                Toast.makeText(this.baseContext, "Syncing..!", Toast.LENGTH_LONG)
                    .show()
            } else
                Toast.makeText(this.baseContext, "No internet connection!", Toast.LENGTH_LONG)
                    .show()
            true
        }
        R.id.item_preference -> {
            goToPreferenceActivity()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }


    /**
     * ============== Synchronization Logic ==============
     * In real world Web apps (not mobiles), if there is no internet connection the operation fails.
     * They do the HTTP Request immediately (Aliexpres, Emag).
     *
     * But since our app must work offline, as I have seen at other applications,
     * they usually make a queue with history of operations.
     * When you have internet connection or press a button you
     * do the queue actions on the server. I think this is a variant of an audit log.
     *
     * The synchronization logic I chose:
     * -edit/delete do not work without internet, !!they send http request upon button clicking!!
     * -if you add an object and do not press sync(refresh button), that object won't be on the server,
     * so if you edit/delete it, it will happen locally
     * -if (internetConnection && Server NOT running) ->
     * -If sync fails, my program deletes your non-synced added books
     * -after you press sync:
     *  we insert everything with an anomalous id into the server (ex: "1","33", "2", "42")
     *  we delete everything from the local database
     *  we copy everything from the server (not optimised at all, I know)
     */
    private fun synchronize() {
        // Without RxJava, I cannot do sequential requests. Sending requests in each
        // OnResponse of a Call object does not work.
        val networkApiAdapter = NetworkAPIAdapter.instance

        val listInserts = ArrayList<Observable<Int>>()
        val newBooks = arrayListOf<Book>()

        // Check books which are not synced
        val localBooks = ArrayList(realm.where(Book::class.java).findAll())
        for (localBook in localBooks) {
            if (localBook.id != null)
                if (localBook.id!!.length < 4) {   //'4' = random length to differentiate between ObjectId and our ids
                    //Enter this 'if', if book exists only locally
                    val postObservable = networkApiAdapter.insert(localBook)
                    listInserts.add(postObservable)
                    newBooks.add(localBook)
                }
        }

        val postRequests = Observable.concat(listInserts)
            // It does not necessarily mean that all responses were 200,
            // you should implement yourself what happens to items that were not successfully `POST`ed
            .take(listInserts.size.toLong())
            .doOnComplete {
                // called when this observable completes, i.e. when all POST requests have been sent
                executeFinalActions()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { s ->
                    println("posts: $s")
                },
                { err ->
                    println(err)
                }
            )
    }

    private fun executeFinalActions() {
        // Fetch all books from server
        val networkApiAdapter = NetworkAPIAdapter.instance
        val getRequest = networkApiAdapter.fetchAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { serverBooks ->
                    val rlm = Realm.getDefaultInstance()
                    // Delete locally only after all syncing is successful
                    rlm.executeTransaction(Realm::deleteAll)

                    for (serverBook in serverBooks) {
                        rlm.executeTransaction {
                            val book = it.createObject<Book>(serverBook.id) //it==realm
                            book.title = serverBook.title
                            book.author = serverBook.author
                        }
                    }
                    adapter.items = getBooks()
                    adapter.notifyDataSetChanged()
                },
                { err ->
                    println(err)
                    Toast.makeText(
                        baseContext,
                        "Sync: GET failed!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
    }
}