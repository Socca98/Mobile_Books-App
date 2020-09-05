class Book {
  String _id;
  String _title;
  String _author;

  Book(this._title, this._author);

  Book.withId(this._id, this._title, this._author);

  String get id => _id;

  String get title => _title;

  String get author => _author;

  set id(String value) {
    _id = value;
  }

  set title(String newTitle) {
    if (newTitle.length <= 255) {
      this._title = newTitle;
    }
  }

  set author(String newAuthor) {
    if (newAuthor.length <= 255) {
      this._author = newAuthor;
    }
  }

  // Convert a Book object into a Map object
  Map<String, dynamic> toMap() {
    var map = Map<String, dynamic>();
    if (id != null) {
      // map['id'] = int.parse(_id);
      map['id'] = _id;
    }
    map['title'] = _title;
    map['author'] = _author;

    return map;
  }

  // Extract a Book object from a Map object
  Book.fromMapObject(Map<String, dynamic> map) {
    // this._id = map['id'].toString();
    this._id = map['id'];
    this._title = map['title'];
    this._author = map['author'];
  }

  factory Book.fromJson(Map<String, dynamic> json) {
    return Book.withId(
        json['id'], json['title'], json['author']);
  }
}
