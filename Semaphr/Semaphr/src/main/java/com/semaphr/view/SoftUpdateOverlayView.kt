package com.semaphr.view

import android.R.color
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.semaphr.databinding.LayoutSoftUpdateOverlayViewBinding


class SoftUpdateOverlayView(context: Context, title: String, message: String, dismissable: Boolean) : FrameLayout(context) {
    private var binding: LayoutSoftUpdateOverlayViewBinding

    init {
        binding = LayoutSoftUpdateOverlayViewBinding.inflate(LayoutInflater.from(context), this, true)
        val description = HtmlCompat.fromHtml(message.replaceLinksWithHtml(), Html.FROM_HTML_MODE_LEGACY)
        binding.titleTextView.text = title
        binding.descriptionTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.descriptionTextView.text = description
        binding.closeButton.setOnClickListener { hideOverlay() }
        binding.closeButton.visibility = if (dismissable) VISIBLE else INVISIBLE
        binding.actionButton.setOnClickListener {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
            } catch (e: ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
            }
        }
        binding.popupConstraintLayout.setOnClickListener {  }
    }

    fun showOverlay() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(this, params)
    }

    fun hideOverlay() {
        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(this)
        } catch (e: IllegalArgumentException) { }
    }
}