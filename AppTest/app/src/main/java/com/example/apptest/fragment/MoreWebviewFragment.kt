package com.example.apptest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.apptest.R

class MoreWebviewFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.more_webview, container, false)
        val url: String = MoreWebviewFragmentArgs.fromBundle(requireArguments()).url
        val mwebview = v.findViewById<WebView>(R.id.more_webview)
        mwebview.loadUrl(url)
        val webSettings: WebSettings = mwebview.getSettings()
        webSettings.javaScriptEnabled = true
        mwebview.setWebViewClient(WebViewClient())
        return v
    }
}