package com.semaphr.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.semaphr.Semaphr
import com.semaphr.log
import com.semaphr.model.SemaphrStatus
import com.semaphr.service.SemaphrService
import com.semaphr.utils.AppDetailsHelper
import com.semaphr.utils.KSResult
import com.semaphr.view.ForceUpdateOverlayView
import com.semaphr.view.SoftUpdateOverlayView
import com.semaphr.view.SystemMessageOverlayView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class SemaphrManager constructor(private val apiKey: String, application: Application) {
    private var currentStatus: MutableLiveData<SemaphrStatus> = MutableLiveData(SemaphrStatus.None())
    private var currentActivity: FragmentActivity? = null
    private var overlayView: WeakReference<FrameLayout> = WeakReference(null)

    val semaphrService: SemaphrService = SemaphrService()
    var appDetailsHelper: AppDetailsHelper = AppDetailsHelper(application.applicationContext)

    private val lifecycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)

            updateStatus()
        }

    }

    private val applicationLifecycleObserver: Application.ActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            currentActivity = p0 as? FragmentActivity
        }
        override fun onActivityStarted(p0: Activity) {}
        override fun onActivityResumed(p0: Activity) {}
        override fun onActivityPaused(p0: Activity) {}
        override fun onActivityStopped(p0: Activity) {}
        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
        override fun onActivityDestroyed(p0: Activity) {
            if (currentActivity === p0) {
                currentActivity = null
            }
        }
    }

    init {
        application.registerActivityLifecycleCallbacks(applicationLifecycleObserver)
        currentStatus.observeForever {
            when (it) {
                is SemaphrStatus.Block -> { showBlockMessage(it.title, it.message) }
                is SemaphrStatus.None -> {}
                is SemaphrStatus.Update -> { showUpdateMessage(it.title, it.message, it.dismissable) }
                is SemaphrStatus.Notify -> { showNotifyMessage(it.title, it.message, it.dismissable) }
            }
        }
    }

    fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    fun end() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
    }

    private fun updateStatus() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                getCurrentStatus()
            }
        }
    }

    suspend fun checkKeys(): KSResult<Boolean> {
        return semaphrService.checkKeys(apiKey, appDetailsHelper.applicationId)
    }

    private suspend fun getCurrentStatus() {
        val appDetails = appDetailsHelper.toAppDetails()
        val result = semaphrService.getCurrentStatus(apiKey, appDetails)
        when (result) {
            is KSResult.Success -> {
                currentStatus.value = result.data
            }
            is KSResult.Error -> {
                Semaphr.log(result.exception.message ?: "\n\n WARNING Semaphr cannot connect to the internet. The SDK won't work.\n\n")
            }
        }
    }

    private fun showBlockMessage(title: String, message: String) {
        currentActivity?.lifecycleScope?.launch {
            currentActivity?.let {
                val view = ForceUpdateOverlayView(it, title, message)
                view.showOverlay()
                hideOverlay()
                overlayView = WeakReference(view)
            }
        }
    }

    private fun showUpdateMessage(title: String, message: String, dismissable: Boolean) {
        currentActivity?.lifecycleScope?.launch {
            currentActivity?.let {
                val view = SoftUpdateOverlayView(it, title, message, dismissable)
                view.showOverlay()
                hideOverlay()
                overlayView = WeakReference(view)
            }
        }
    }

    private fun showNotifyMessage(title: String, message: String, dismissable: Boolean) {
        currentActivity?.lifecycleScope?.launch {
            currentActivity?.let {
                val view = SystemMessageOverlayView(it, title, message, dismissable)
                view.showOverlay()
                hideOverlay()
                overlayView = WeakReference(view)
            }
        }
    }

    private fun hideOverlay() {
        (overlayView.get() as? ForceUpdateOverlayView)?.hideOverlay()
        (overlayView.get() as? SoftUpdateOverlayView)?.hideOverlay()
        (overlayView.get() as? SystemMessageOverlayView)?.hideOverlay()
    }
}