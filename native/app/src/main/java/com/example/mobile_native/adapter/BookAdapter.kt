package com.example.mobile_native.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_native.R
import com.example.mobile_native.activity.EditBookActivity
import com.example.mobile_native.activity.OnDeleteListener
import com.example.mobile_native.model.Book
import com.example.mobile_native.network.InternetConnection
import com.example.mobile_native.network.NetworkAPIAdapter
import com.example.mobile_native.network.ValidateSyncedObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.book_list_item.view.*

class BookAdapter(books: ArrayList<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    var items: ArrayList<Book> = books

    /**
    This is called once when the RecyclerView is created for each element.
    Assigns to each element in the 'myDataset' a BookViewHolder.
    To this holder we assign a layout/xml using the function inflate().

    aka each item in the recycle view has a Holder and a Layout(list_item)

    https://medium.com/@hinchman_amanda/working-with-recyclerview-in-android-kotlin-84a62aef94ec
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BookViewHolder(layoutInflater.inflate(R.layout.book_list_item, parent, false))
    }

    /**
    This is called after onCreateViewHolder(..) and every time a list element is assigned new values
    (scrolling causes recycling/reassigning).

    Instead of creating new views when they go out of scrolling view, it replaces data in existing ones.
    What is onBindViewHolder, response bellow.
    https://stackoverflow.com/questions/37523308/when-onbindviewholder-is-called-and-how-it-works
     */
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(this.items[position])
    }

    override fun getItemCount() = this.items.size

    class BookViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val context: Context = itemView!!.context

        /**
         * This function could be written in onBindViewHolder, but we separate responsibilities
         */
        fun bind(model: Book) {
            val parentWidth: Int = itemView.width
            val maxWidth: Int = parentWidth - 2 * itemView.deleteButton.width

            // Set max width for the recycler view items
            itemView.bookTitle.maxWidth = maxWidth
            itemView.bookAuthor.maxWidth = maxWidth

            // Get string representations of Book's fields
            var titleText: String = model.title.toString()
            var authorText: String = model.author.toString()

            // If text is very big, we cannot show everything on the list item view, so we add dots
            if (model.title!!.length > 30) {
                titleText = model.title!!.toString().substring(1, 30) + "..."
            } else if (model.author!!.length > 30) {
                authorText = model.author!!.substring(1, 30) + "..."
            }

            // Set text for the recycler view items
            itemView.bookTitle.text = titleText
            itemView.bookAuthor.text = authorText

            // Set what happens when EDIT button is pressed
            this.itemView.editButton.setOnClickListener {
                // Check internet connection
                if (InternetConnection.getInstance(context).isOnline) {
                    val intent = Intent(context, EditBookActivity::class.java)
                    intent.putExtra("id", model.id)
                    this.context.startActivity(intent)
                } else
                    Toast.makeText(
                        context,
                        "No internet connection or object not synced!",
                        Toast.LENGTH_SHORT
                    ).show()
            }

            // Set what happens when DELETE button is pressed
            this.itemView.deleteButton.setOnClickListener {
                // Check internet connection
                if (InternetConnection.getInstance(context).isOnline) {
                    // Check if user really wants to delete
                    AlertDialog.Builder(this.context)
                        .setMessage("Do you want to delete this book?")
                        .setPositiveButton("Yes") { _, _ ->
                            // We have internet connection and its synced, so we send DELETE request
                            if (ValidateSyncedObject.checkSyncedObject(model)) {
                                sendDeleteRequest(model.id!!)
                            } else {
                                // Delete local book
                                deleteLocalBook(model.id)

                                Toast.makeText(
                                    this.context,
                                    "Deleted un-synced book.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }.setNegativeButton("No", null)
                        .create()
                        .show()
                } else
                    Toast.makeText(
                        context,
                        "No internet connection!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

        private fun sendDeleteRequest(id: String) {
            // RxJava sending request
            val networkApiAdapter = NetworkAPIAdapter.instance
            val callDelete = networkApiAdapter.delete(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { s ->
                        // Delete local book only if request was successful
                        Toast.makeText(
                            this.context,
                            "Delete success! $s",
                            Toast.LENGTH_LONG
                        ).show()
                        deleteLocalBook(id)
                    },
                    { err ->
                        Toast.makeText(
                            this.context,
                            "Delete failed! $err",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
        }

        private fun deleteLocalBook(id: String?) {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.where(Book::class.java)
                    .equalTo("id", id)
                    .findFirst()
                    ?.deleteFromRealm()
                if (this.context is OnDeleteListener)
                    this.context.setOnDeleteListener()
            }
        }
    }
}
