package com.semaphr.example

import android.app.Application
import com.semaphr.Semaphr

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // TODO: Replace with your own API Key
        val API_KEY = "7c1b9cbf51df8cbeb739c0cfd1421dbdb943adf251e240b87137384e0df08f56208cd610c02075348772acc2a3a28a8867a711c1395b0b2af6e1fd30902f3ed1"
        Semaphr.configure(this, API_KEY)
    }

}