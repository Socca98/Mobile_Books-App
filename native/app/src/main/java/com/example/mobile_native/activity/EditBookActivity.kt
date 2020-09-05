package com.example.mobile_native.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_native.R
import com.example.mobile_native.model.Book
import com.example.mobile_native.network.NetworkAPIAdapter
import com.example.mobile_native.network.ValidateSyncedObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_book.*

class EditBookActivity : AppCompatActivity() {
    private val realm: Realm by lazy { Realm.getDefaultInstance() }
    private val id: String by lazy { intent.getStringExtra("id")!! }
    lateinit var book: Book


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.requestFeature(Window.FEATURE_ACTION_BAR)
        setContentView(R.layout.activity_edit_book)

        if (supportActionBar != null) {
            supportActionBar!!.title = "Edit Book"
        }

        book = this.realm.where(Book::class.java)
            .equalTo("id", this.id)
            .findFirst()!!

        titleInput.setText(book.title.toString())
        authorInput.setText(book.author.toString())

        // Notify when entering update Activity that object is not synced
        if (!ValidateSyncedObject.checkSyncedObject(book))
            Toast.makeText(
                baseContext,
                "Updating not synced book",
                Toast.LENGTH_LONG
            ).show()

        submitButton.setOnClickListener {
            val newBook = Book()
            newBook.id = this.book.id
            newBook.title = titleInput.text.toString()
            newBook.author = authorInput.text.toString()
            // If its not synced object (aka doesn't exist on server)
            // then PUT will not find the target book so we avoid that with this 'if'
            if (ValidateSyncedObject.checkSyncedObject(book)) {
                // If we have internet connection and book is synced, then we also update the object on the server
                sendPutRequest(id, newBook)
            }
            else {
                // We can update books locally, even if that book is not on the server
                this.updateBook(newBook)
            }
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun updateBook(newBook: Book) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            this.book.title = newBook.title
            this.book.author = newBook.author
        }
    }

    private fun sendPutRequest(id: String, newBook: Book) {
        // RxJava sending request
        val networkApiAdapter = NetworkAPIAdapter.instance
        val callUpdate = networkApiAdapter.update(id, newBook)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { s ->
                    // Only success permits to local update
                    this.updateBook(newBook)
                    Toast.makeText(
                        baseContext,
                        "Put success! $s",
                        Toast.LENGTH_LONG
                    ).show()
                },
                { err ->
                    Toast.makeText(
                        baseContext,
                        "PUT failed! $err",
                        Toast.LENGTH_LONG
                    ).show()
                },
                { println("onComplete") }
            )
    }


}

