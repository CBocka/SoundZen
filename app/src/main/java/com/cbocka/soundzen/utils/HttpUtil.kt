package com.cbocka.soundzen.utils

import android.os.AsyncTask
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.Arrays
import java.util.concurrent.ExecutionException


class HttpUtil {
    companion object {
        private var instance: HttpUtil? = null

        fun getInstance(): HttpUtil? {
            if (instance == null) instance = HttpUtil()
            return instance
        }
    }

    fun <T> getResponseData(httpRequest: String, type: Class<T>?): T? {
        val httpTask = HttpTask(httpRequest)
        var responseData = "[]"
        try {
            responseData = httpTask.execute().get()!!
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val gson = Gson()
        val data = gson.fromJson(responseData, type)
        return data
    }

    fun <T> getResponseData(httpRequest: String, type: Class<Array<T>>?): List<T>? {
        val httpTask = HttpTask(httpRequest)
        var responseData = "[]"
        try {
            responseData = httpTask.execute().get().toString()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val gson = Gson()
        val data = gson.fromJson(responseData, type)
        return Arrays.asList(*data)
    }

    inner class HttpTask(private val httpRequest: String) :
        AsyncTask<Void?, Void?, String?>() {
        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .url(httpRequest)
                .build()
            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}