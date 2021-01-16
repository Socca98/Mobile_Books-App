# Mobile_Books-App
Managing a list of Books. Same CRUD+Local database done in Android (Kotlin) and Flutter. The Android app has additional functionalities.

## Functionalities
Native Android app:
* CRUD - store in local database using **Realm**
    * Edit, Delete only available with Internet Connection
* Synchronize books with Flask server
* settings/preference with Dark Theme
* requests using **RxJava**
* small animation (360 rotation on click)

Non native Flutter app:
* CRUD - store in Sqlite local database


## Native app photos
<p float="left">
    <img src="https://i.imgur.com/0qYUPPa.png" width="250"> 
    <img src="https://i.imgur.com/02sEwJ1.png" width="250"> 
    <img src="https://i.imgur.com/1Le4U6p.png" width="250"> 
    <img src="https://i.imgur.com/pjN38Vw.png" width="250"> 
</p>

## Non native app photos
<p float="left">
    <img src="https://i.imgur.com/eCSLtD1.png" width="250"> 
    <img src="https://i.imgur.com/99QTKuX.png" width="250"> 
</p>

## Details
Implemented a REST CRUD app 
   * having a RecycleView with 'Book' objects. 
   * the objects are kept in a *Realm* local database and on a Flask server. 
   * Http calls to the server are made with *Retrofit 2* and *RxJava*. 
   * only Create/Add operation is allowed while there is no internet connection. 
   * objects are synchronized with server when pressing a menu button.

## Setup requirements
Mongodb server install

















