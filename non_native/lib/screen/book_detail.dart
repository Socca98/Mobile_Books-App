import 'package:flutter/material.dart';
import 'package:non_native/model/book.dart';
import 'package:non_native/utils/database_helper.dart';
import 'package:non_native/utils/network_api.dart';

class BookDetail extends StatefulWidget {
  final String appBarTitle;
  final Book book;

  BookDetail(this.book, this.appBarTitle);

  @override
  State<StatefulWidget> createState() {
    return BookDetailState(this.book, this.appBarTitle);
  }
}

class BookDetailState extends State<BookDetail> {
  DatabaseHelper databaseHelper = DatabaseHelper();
  // NetworkApi networkApi = NetworkApi();

  String appBarTitle;
  Book book;

  TextEditingController titleController = TextEditingController();
  TextEditingController authorController = TextEditingController();

  BookDetailState(this.book, this.appBarTitle);

  @override
  Widget build(BuildContext context) {
    TextStyle textStyle = Theme.of(context).textTheme.headline6;

    titleController.text = book.title;
    authorController.text = book.author;

    return WillPopScope(
        onWillPop: () {
          moveToLastScreen();
        },
        child: Scaffold(
          appBar: AppBar(
            title: Text(appBarTitle),
            leading: IconButton(
                icon: Icon(Icons.arrow_back),
                onPressed: () {
                  moveToLastScreen();
                }),
          ),
          body: Padding(
            padding: EdgeInsets.only(top: 15.0, left: 10.0, right: 10.0),
            child: ListView(
              children: <Widget>[
                Padding(
                  padding: EdgeInsets.only(top: 15.0, bottom: 15.0),
                  child: TextField(
                    controller: titleController,
                    style: textStyle,
                    onChanged: (value) {
                      updateTitle();
                    },
                    decoration: InputDecoration(
                        labelText: 'Title',
                        labelStyle: textStyle,
                        border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(5.0))),
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 15.0, bottom: 15.0),
                  child: TextField(
                    controller: authorController,
                    style: textStyle,
                    onChanged: (value) {
                      updateAuthor();
                    },
                    decoration: InputDecoration(
                        labelText: 'Author',
                        labelStyle: textStyle,
                        border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(5.0))),
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 15.0, bottom: 15.0),
                  child: Row(
                    children: <Widget>[
                      Expanded(
                        child: RaisedButton(
                          color: Theme.of(context).primaryColorDark,
                          textColor: Theme.of(context).primaryColorLight,
                          child: Text(
                            'Save',
                            textScaleFactor: 1.5,
                          ),
                          onPressed: () {
                            setState(() {
                              _save();
                            });
                          },
                        ),
                      ),
                      Container(
                        width: 5.0,
                      ),
                      Expanded(
                        child: RaisedButton(
                          color: Theme.of(context).primaryColorDark,
                          textColor: Theme.of(context).primaryColorLight,
                          child: Text(
                            'Delete',
                            textScaleFactor: 1.5,
                          ),
                          onPressed: () {
                            setState(() {
                              _delete();
                            });
                          },
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ));
  }

  void moveToLastScreen() {
    Navigator.pop(context, true);
  }

  // Update the title of book object
  void updateTitle() {
    book.title = titleController.text;
  }

  // Update the description of book object
  void updateAuthor() {
    book.author = authorController.text;
  }

  // Save data to database
  void _save() async {
    moveToLastScreen();

    // book.date = DateFormat.yMMMd().format(DateTime.now());
    int result;
    if (book.id != null) {
      // Case 1: Update operation
      result = await databaseHelper.updateBook(book);
    } else {
      // Case 2: Insert Operation
      result = await databaseHelper.insertBook(book);
    }

    if (result != 0) {
      // Success
      _showAlertDialog('Status', 'Book Saved Successfully');
    } else {
      // Failure
      _showAlertDialog('Status', 'Problem Saving Book');
    }
  }

  void _delete() async {
    moveToLastScreen();

    if (book.id == null) {
      _showAlertDialog('Status', 'No Book was deleted');
      return;
    }

    int result = await databaseHelper.deleteBook(book.id);
    if (result != 0) {
      _showAlertDialog('Status', 'Book Deleted Successfully');
    } else {
      _showAlertDialog('Status', 'Error Occured while Deleting Book');
    }
  }

  void _showAlertDialog(String title, String message) {
    AlertDialog alertDialog = AlertDialog(
      title: Text(title),
      content: Text(message),
    );
    showDialog(context: context, builder: (_) => alertDialog);
  }

  /// Check what ids exist and choose a free number.
  /// Waits for getBookList to return result then proceeds to the for loop
  // Future<String> generateId() async {
  //   int id = 0;
  //   List<Book> bookList = await databaseHelper.getBookList();
  //   for (book in bookList) {
  //     int k = int.parse(book.id);
  //     // int k = book.id;
  //     if(k != null && k > id) {
  //       id = k;
  //     }
  //   }
  //   return (id+1).toString();
  // }
}
