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


class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val mWebView = v.findViewById<View>(R.id.webview) as WebView
        mWebView.loadUrl("http://services.accgo.net/dancestudio/UnderConstruction.aspx")

        // Enable Javascript

        // Enable Javascript
        val webSettings: WebSettings = mWebView.getSettings()
        webSettings.javaScriptEnabled = true

        // Force links and redirects to open in the WebView instead of in a browser

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(WebViewClient())

        return v
    }
}
