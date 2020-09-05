Requisites
Mongodb install


Native Android app:
* CRUD 
    * Edit, Delete only available with Internet Connection
* Sync with Flask server
* small animation (spinwheel icon rotates on click)
* settings/preference with Dark Theme
* requests using RxJava

Non native Flutter app:
* TBI (To be implemented) 

<img src="https://i.imgur.com/0qYUPPa.png" width="300"> 
<img src="https://i.imgur.com/02sEwJ1.png" width="300"> 
<img src="https://i.imgur.com/1Le4U6p.png" width="300"> 
<img src="https://i.imgur.com/pjN38Vw.png" width="300"> 

### Details
- Implemented a REST CRUD app having a RecycleView with objects 'Book'. 
The objects are kept in a Realm local database and on a Flask server. 
Http calls to the server are made with Retrofit 2 and RxJava. 
Only Create/Add operation is allowed while there is no internet connection. 
Objects are synchronized with server when pressing a menu button.



















