package com.example.mobile_native.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Book(
    @PrimaryKey
    var id: String? = null,
    var title: String? = null,
    var author: String? = null
): RealmObject()


/*
Int vs Int?
https://stackoverflow.com/questions/6389437/explanation-of-int-vs-int
 */