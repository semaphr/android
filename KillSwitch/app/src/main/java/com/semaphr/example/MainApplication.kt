package com.semaphr.example

import android.app.Application
import com.semaphr.Semaphr

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //val API_KEY = "c20794c261a6aa1f07c297ad5c1075ff9373359831913f021df0fe6da3f26491981780fe1c2314ef94c21aa2665638b14c634a978cfe9fcdcab19516a8524983"
        val API_KEY = "7c1b9cbf51df8cbeb739c0cfd1421dbdb943adf251e240b87137384e0df08f56208cd610c02075348772acc2a3a28a8867a711c1395b0b2af6e1fd30902f3ed1"
        Semaphr.configure(this, API_KEY)
    }

}