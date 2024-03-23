package com.cbocka.soundzen.data.database.resource

import java.lang.Exception

sealed class Resource {
    data class Error(val exception: Exception) : Resource()
    data class Success<T>(val data : T) : Resource()
}