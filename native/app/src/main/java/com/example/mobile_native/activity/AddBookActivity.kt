package com.example.mobile_native.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_native.R
import com.example.mobile_native.model.Book
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_add_book.*

class AddBookActivity : AppCompatActivity() {
    val realm: Realm by lazy { Realm.getDefaultInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        setContentView(R.layout.activity_add_book)

        //Set a big title for this activity, ex:"Add a Book"
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.add_book_title)
        }

        submitButton.setOnClickListener {
            if (this.formValidated()) {
                this.addBook()
                Toast.makeText(this, "Added locally.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "The input is wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addBook() {
        //The UUID from java.util is different from ObjectId of MongoDb, so we cannot generate ids here
        //A bonus: use these incremented ids in order to know what objects we added/have and are not on the server
        //because objects synced with server have ids of form "5e00b4ea32b645a529b672a0"
        this.realm.executeTransaction {
            val book = this.realm.createObject(Book::class.java, getNewId())
            book.title = titleInput.text.toString()
            book.author = authorInput.text.toString()
        }
    }

    /**
     * Validate the input given in AddBookActivity.
     */
    private fun formValidated(): Boolean {
        if (titleInput.text.toString() == "")
            return false

        if (authorInput.text.toString() == "")
            return false

        return true
    }

    /**
     * Generate an id for each new object. I could had used a random uuid generator, but I must
     * differentiate between local objects and server objects, in order to know which to synchronize.
     * The server objects have uuid (mongodb style) and local ones have consecutive numbers from 0.
     */
    private fun getNewId(): String {
        val books = realm.where<Book>().findAll()
        var id = 0

        for (book in books) {
            try {
                val k = book.id?.toInt()
                if (k != null && k > id) {
                    id = k
                }
            } catch (e: Exception) {}
        }
        return (id + 1).toString()
    }
}
