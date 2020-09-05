import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:non_native/model/book.dart';

class NetworkApi {
  static const BASE_URL = "http://10.0.2.2:5000/"; //this ip works with Emulator! Choose your correct url.
  static const URL_ORDERS_ALL = "http://10.0.2.2:5000/books";
  // static const URL_ORDER_INDIVIDUAL = "http://10.0.2.2:5000/books/{id}";

  Future<Book> createBook(String id, String title, String author) async {
    final http.Response response = await http.post(
      URL_ORDERS_ALL + "/$id",
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: jsonEncode(<String, String>{
        'title': title,
      }),
    );
    if (response.statusCode == 201) {
      // If the server did return a 201 CREATED response,
      // then parse the JSON.
      return Book.fromJson(json.decode(response.body));
    } else {
      // If the server did not return a 201 CREATED response,
      // then throw an exception.
      throw Exception('Failed to load album');
    }
  }


}