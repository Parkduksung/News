package com.work.news

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fabric.with(this, Crashlytics())
    }

    fun context(): Context = applicationContext

    companion object {
        lateinit var instance: App
            private set
    }
}