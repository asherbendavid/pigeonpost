package cvc.dashingdog.pigeonpost.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BloggerApi {
    @GET("feeds/posts/default?alt=json&max-results=25")
    suspend fun getFeed(): BloggerFeedResponse

    companion object {
        fun create(): BloggerApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://androidstudio.googleblog.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(BloggerApi::class.java)
        }
    }
}