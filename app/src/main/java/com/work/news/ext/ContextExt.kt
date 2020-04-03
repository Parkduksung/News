package com.work.news.ext

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

@TargetApi(Build.VERSION_CODES.M)
fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetwork?.let { true } ?: false
}