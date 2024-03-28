package com.cbocka.soundzen.utils

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class Utils {
    companion object {

        fun encodeURIComponent(s: String?): String? {
            if (s == null) {
                return ""
            }
            val result: String? = try {
                URLEncoder.encode(s, "UTF-8")
                    .replace("\\+".toRegex(), "%20")
                    .replace("\\%21".toRegex(), "!")
                    .replace("\\%27".toRegex(), "'")
                    .replace("\\%28".toRegex(), "(")
                    .replace("\\%29".toRegex(), ")")
                    .replace("\\%7E".toRegex(), "~")
            } catch (e: UnsupportedEncodingException) {
                s
            }
            return result
        }

        fun base64ToMp3(base64String: String): ByteArray {
            return Base64.decode(base64String, Base64.DEFAULT)
        }
    }
}