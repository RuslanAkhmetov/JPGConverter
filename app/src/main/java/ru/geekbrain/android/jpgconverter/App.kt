package ru.geekbrain.android.jpgconverter

import android.app.Application
import android.content.Context

class App: Application() {

    companion object{
        lateinit var  instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getAppContext()=
        applicationContext
}

val Context.app: App
    get() {
        return applicationContext as App
    }