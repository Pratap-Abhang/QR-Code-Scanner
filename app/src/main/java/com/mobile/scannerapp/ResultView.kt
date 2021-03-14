package com.mobile.scannerapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_result_view.*

class ResultView : AppCompatActivity() {
    var webView: WebView? = null
    var url: String? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_view)

        if(intent.hasExtra("website")){
            url = intent.extras?.getString("website")
            Log.e("url", url!!)
            if (!CheckNetwork.isInternetAvailable(this@ResultView)) //if not connection available
            {
                textData.setText("No Internet Connection")
                webView?.visibility = View.GONE
            }
            webView = findViewById<WebView>(R.id.webView)
            progressBar = findViewById(R.id.progress_bar)
            webView?.getSettings()?.javaScriptEnabled = true
            webView?.getSettings()?.domStorageEnabled = true
            webView?.loadUrl(url!!)
            if (Build.VERSION.SDK_INT >= 19) {
                webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else {
                webView?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            //        webView.loadUrl(url);
            webView?.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    progressBar?.setVisibility(View.GONE)
                }
            })
        }else{
            url = intent.extras?.getString("text")
            textData.setText(url)
            webView?.visibility = View.GONE
        }


    }
}