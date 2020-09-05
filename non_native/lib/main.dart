import 'package:flutter/material.dart';
import 'package:non_native/screen/book_list.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: new BookList(),
    );
  }
}

/*
Tutorial taken from here:
https://simpleactivity435203168.wordpress.com/2019/09/19/to-do-list-in-flutter-with-sqlite-as-local-database/

 */









