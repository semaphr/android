package com.semaphr

import android.app.Application
import android.util.Log
import com.semaphr.manager.SemaphrManager
import com.semaphr.utils.KSResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun Semaphr.Companion.log(string: String) {
    val tag = "Semaphr-Log"
    Log.d(tag, string)
}

public class Semaphr {

    companion object {
        val instance = Semaphr()

        fun configure(application: Application, apiKey: String) {
            instance.configure(application, apiKey)
        }
    }

    // This is used for linking the SDK to your account
    private lateinit var apiKey: String

    private lateinit var application: Application

    // This is used for opening the correct page in the google play store, if this is not
    private lateinit var googlePlayID: String

    private lateinit var manager: SemaphrManager

    // Configures the Kil Switch with the API key from the web console
    public fun configure(application: Application, apiKey: String) {
        this.apiKey = apiKey
        this.application = application

        this.manager = SemaphrManager(apiKey, application)

        checkKeys()

        manager.start()
    }

    // Enables the SDK if it was previously disabled
    public fun enable() {
        manager.end()
        manager.start()
    }

    // Disables the SDK, no events will be sent or mesages taken into consideration
    public fun disable() {
        manager.end()
    }

    private fun checkKeys() {
        if (apiKey.isNullOrEmpty()) {
            Semaphr.log("API Key is invalid. Make sure you've used the right value from the Web interface.")
            return
        }

        GlobalScope.launch {
            val result = manager.checkKeys()
            when (result) {
                is KSResult.Success -> {
                    if (!result.data) {
                        Semaphr.log("\n\n WARNING the bundle key combo for the Kill Switch SDK is invalid. The SDK won't work.\n\n")
                    }
                }
                is KSResult.Error -> {
                    Semaphr.log("\n\n WARNING Semaphr cannot connect to the internet. The SDK won't work.\n\n")
                }
            }
        }
    }

}