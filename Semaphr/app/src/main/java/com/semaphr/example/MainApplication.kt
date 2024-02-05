package com.semaphr.example

import android.app.Application
import com.semaphr.Semaphr

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // TODO: Replace with your own API Key
        val API_KEY = "7d5ba78a2e8314bd199734e88beb96f543516531a403cd049c1b9a8f3d9e69f572315eb12552ddc20334a52a6197f08864214fdf18fd1de99cdfdc946ce662a8"
        Semaphr.configure(this, API_KEY)
    }

}