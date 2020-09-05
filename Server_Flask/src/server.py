# FLASK_APP=server.py flask run --host=0.0.0.0
from src.book_repository import BookRepository

from flask import Flask
from flask import request
from flask import jsonify

import sys

app = Flask(__name__)

bookRepo = BookRepository()

bookRepo.add({'title': 'Poezii', 'author': 'Mihai Eminescu'})
bookRepo.add({'title': 'Amintiri din copilarie', 'author': 'Ion Creanga'})


# bookRepo.add({'title': 'padurea hoia baciu', 'author': 'L.Rebreanu'})
# bookRepo.add({'title': 'morometii', 'author': '??'})

# You can check content with Postman app
# bookRepo.check_database_content()


@app.route("/")
def hello():
    return "Hello World!"


@app.route("/books", methods=['GET', 'POST'])
def books():
    if request.method == 'GET':
        return jsonify(bookRepo.get_all())
    elif request.method == 'POST':
        # print(request.form, file=sys.stderr)
        return jsonify(bookRepo.add(request.form))


@app.route('/books/<book_id>', methods=['PUT', 'DELETE'])
def books_id(book_id):
    if request.method == 'PUT':
        print(request.form, file=sys.stderr)
        return jsonify(bookRepo.update(book_id, request.form))
    elif request.method == 'DELETE':
        return jsonify(bookRepo.delete(book_id))


if __name__ == '__main__':
    app.run()
