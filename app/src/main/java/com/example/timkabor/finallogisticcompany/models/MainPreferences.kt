package com.example.timkabor.finallogisticcompany.models

import android.content.Context
import android.content.SharedPreferences

class MainPreferences(context: Context) {
    val PREFS_NAME = "com.farkopfdevelopers.cache"
    val TOKEN_NAME = "token"
    val ORDER_LIST = "order_list"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

//    var token: String
//        get() = prefs.getString(TOKEN_NAME, "NO_TOKEN")
//        set(value) = prefs.edit().putString(TOKEN_NAME, value).apply()

}
