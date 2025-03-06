package com.example.module

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private var frontViewVideoId: String = ""
    private var sideViewVideoId: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val displayText = intent.getStringExtra("DISPLAY_TEXT") ?: ""
        val videoCode = intent.getStringExtra("VIDEO_CODE") ?: ""
        binding.txtSign.text = displayText

        // Load video IDs for front and side views
        frontViewVideoId = getVideoId(videoCode, "front")
        sideViewVideoId = getVideoId(videoCode, "side")

        // Configure WebView
        setupWebView()

        // Set up back button
        binding.btnBack.setOnClickListener { finish() }

        // Set up front view button
        binding.btnFrontView.setOnClickListener { loadVideo(frontViewVideoId) }

        // Set up side view button
        binding.btnSideView.setOnClickListener { loadVideo(sideViewVideoId) }

        // Load default video (front view)
        loadVideo(frontViewVideoId)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false // Enables autoplay
                cacheMode = WebSettings.LOAD_NO_CACHE
            }
        }
    }

    private fun loadVideo(videoId: String) {
        if (videoId.isNotEmpty()) {
            val htmlContent = """
                <html>
                <body style="margin:0;padding:0;overflow:hidden;">
                    <iframe src="https://drive.google.com/file/d/$videoId/preview" 
                            width="100%" height="100%" style="border:none;" 
                            allow="autoplay"></iframe>
                    <script>
                        setTimeout(function() {
                            var iframe = document.querySelector("iframe");
                            iframe.contentWindow.postMessage('{"event":"command","func":"playVideo","args":""}', '*');
                        }, 1000);
                    </script>
                </body>
                </html>
            """.trimIndent()

            binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } else {
            Toast.makeText(this, "Video not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getVideoId(videoCode: String, viewType: String): String {
        val videoMap = mapOf(
            "a_front" to "1l4ut1DxO-23ZFXeWdOS-vkl9241x3SOh",
            "a_side" to "1Q6NzBPEJHro1xf31nq3xVQEJCGWpY1b_",
            "b_front" to "1l4ut1DxO-23ZFXeWdOS-vkl9241x3SOh",
            "b_side" to "1Q6NzBPEJHro1xf31nq3xVQEJCGWpY1b_"
        )
        val key = "${videoCode.lowercase()}_$viewType"
        return videoMap[key] ?: ""
    }
}