import pprint

from pymongo import MongoClient
from bson.objectid import ObjectId  # For ObjectId to work


class BookRepository:

    def __init__(self):
        self.client = MongoClient('localhost', 27017)
        self.client.drop_database("goodreads")  # clear everything that was before
        self.db = self.client.goodreads  # create database
        self.books = self.db.books  # create table in the database

    def get_all(self):
        return [{
            'id': str(book['_id']),
            'title': book['title'],
            'author': book['author'],
        } for book in self.books.find()]

    def add(self, book):
        book = {key: book[key] for key in book}
        self.books.insert_one(book)  # automatically generates an ObjectId for the book
        return 200

    def update(self, book_id, book):
        my_query = {"_id": ObjectId(book_id)}
        new_values = {"$set": {"title": book["title"], "author": book["author"]}}
        self.books.update_one(my_query, new_values)
        return 200

    def delete(self, book_id):
        self.books.remove(ObjectId(book_id))
        return 200

    def check_database_content(self):
        for book in self.books.find():
            pprint.pprint(book)
