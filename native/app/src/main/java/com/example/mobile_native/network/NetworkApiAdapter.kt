package com.example.mobile_native.network

import com.example.mobile_native.model.Book
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class NetworkAPIAdapter private constructor() {

    private object Holder {
        val INSTANCE = NetworkAPIAdapter()
    }

    companion object {
        val instance: NetworkAPIAdapter by lazy { Holder.INSTANCE }
        const val BASE_URL: String =
            "http://10.0.2.2:5000/" //this ip works with Emulator! Choose your correct url.
        private const val URL_ORDERS_ALL: String = "books"
        private const val URL_ORDER_INDIVIDUAL: String = "books/{id}"
    }

    private val bookService: BooksService

    init {
        val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //convert Call to Observable in order to use RxJava
            .build()

        bookService = retrofit.create(BooksService::class.java)
    }

    fun fetchAll(): Observable<List<Book>> {
        return bookService.fetchAll()
    }

    fun insert(dto: Book): Observable<Int> {
        return bookService.insert(dto.title!!, dto.author!!)
    }

    fun update(id: String, dto: Book): Observable<String> {
        return bookService.update(id, dto.title!!, dto.author!!)
    }

    fun delete(id: String): Observable<String> {
        return bookService.delete(id)
    }

    // Interface
    interface BooksService {
        @GET(URL_ORDERS_ALL)
        fun fetchAll(): Observable<List<Book>>

        @FormUrlEncoded
        @POST(URL_ORDERS_ALL)
        fun insert(
            @Field("title") title: String,
            @Field("author") author: String
        ): Observable<Int>

        @FormUrlEncoded
        @PUT(URL_ORDER_INDIVIDUAL)
        fun update(
            @Path("id") id: String,
            @Field("title") title: String,
            @Field("author") author: String
        ): Observable<String>

        @DELETE(URL_ORDER_INDIVIDUAL)
        fun delete(@Path("id") id: String): Observable<String>
    }
}



