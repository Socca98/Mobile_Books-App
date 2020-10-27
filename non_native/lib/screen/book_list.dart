import 'dart:async';
import 'package:flutter/material.dart';
import 'package:non_native/model/book.dart';
import 'package:non_native/utils/database_helper.dart';
import 'package:sqflite/sqflite.dart';

import 'book_detail.dart';

class BookList extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return BookListState();
  }
}

class BookListState extends State<BookList> {
  DatabaseHelper databaseHelper = DatabaseHelper();
  List<Book> bookList;
  int count = 0;

  @override
  Widget build(BuildContext context) {
    if (bookList == null) {
      bookList = List<Book>();
      updateListView();
    }

    return Scaffold(
      appBar: AppBar(
        title: Text('Books'),
        actions: <Widget>[
          IconButton(
            icon: Icon(
              Icons.sync,
              color: Colors.white,
            ),
            onPressed: () {
              // do synchronization
            },
          )
        ],
      ),
      body: getBookListView(),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          navigateToDetail(Book('', ''), 'Add Book');
        },
        tooltip: 'Add Book',
        child: Icon(Icons.add),
      ),
    );
  }

  ListView getBookListView() {
    return ListView.builder(
      itemCount: count,
      itemBuilder: (BuildContext context, int position) {
        return Card(
          color: Colors.white,
          elevation: 2.0,
          child: ListTile(
            leading: CircleAvatar(
              backgroundColor: Colors.amber,
              child: Text(getFirstLetter(this.bookList[position].title),
                  style: TextStyle(fontWeight: FontWeight.bold)),
            ),
            title: Text(this.bookList[position].title,
                style: TextStyle(fontWeight: FontWeight.bold)),
            subtitle: Text(this.bookList[position].author),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                GestureDetector(
                  child: Icon(Icons.delete,color: Colors.red,),
                  onTap: () {
                    _delete(context, bookList[position]);
                  },
                ),
              ],
            ),
            onTap: () {
              debugPrint("ListTile Tapped");
              navigateToDetail(this.bookList[position], 'Edit Book');
            },
          ),
        );
      },
    );
  }

  /// Get first 2 letters for the avatar circle.
  getFirstLetter(String title) {
    return title.substring(0, 2);
  }

  void _delete(BuildContext context, Book book) async {
    int result = await databaseHelper.deleteBook(book.id);
    if (result != 0) {
      _showSnackBar(context, 'Book Deleted Successfully');
      updateListView();
    }
  }

  void _showSnackBar(BuildContext context, String message) {
    final snackBar = SnackBar(content: Text(message));
    Scaffold.of(context).showSnackBar(snackBar);
  }

  void navigateToDetail(Book book, String title) async {
    bool result =
    await Navigator.push(context, MaterialPageRoute(builder: (context) {
      return BookDetail(book, title);
    }));

    if (result == true) {
      updateListView();
    }
  }

  /// Updates the list view after each operation
  void updateListView() {
    final Future<Database> dbFuture = databaseHelper.initializeDatabase();
    dbFuture.then((database) {
      Future<List<Book>> bookListFuture = databaseHelper.getBookList();
      bookListFuture.then((bookList) {
        setState(() {
          this.bookList = bookList;
          this.count = bookList.length;
        });
      });
    });
  }


}