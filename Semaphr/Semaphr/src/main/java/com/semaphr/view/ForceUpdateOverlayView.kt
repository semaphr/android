package com.semaphr.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.text.HtmlCompat
import com.semaphr.databinding.LayoutForceUpdateOverlayViewBinding

fun String.replaceLinksWithHtml(): String {
    // Regular expression pattern to find URLs in the text
    val urlPattern = "\\b(?:https?://|www\\.)\\S+\\b".toRegex()

    // Find all URLs using the regular expression
    val urls = urlPattern.findAll(this).map { it.value }.toList()

    // Replace each URL with an HTML link tag
    var newText = this
    for (url in urls) {
        if (!url.startsWith("http")) {
            val fixedUrl = "http://$url"
            newText = newText.replace(url, "<a href=\"$fixedUrl\" target=\"$url\">$url</a>")
        } else {
            newText = newText.replace(url, "<a href=\"$url\" target=\"$url\">$url</a>")
        }
    }

    return newText
}

class ForceUpdateOverlayView(context: Context, title: String, message: String) : FrameLayout(context) {
    private var binding: LayoutForceUpdateOverlayViewBinding

    init {
        binding = LayoutForceUpdateOverlayViewBinding.inflate(LayoutInflater.from(context), this, true)
        val description = HtmlCompat.fromHtml(message.replaceLinksWithHtml(), Html.FROM_HTML_MODE_LEGACY)
        binding.titleTextView.text = title
        binding.descriptionTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.descriptionTextView.text = description

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