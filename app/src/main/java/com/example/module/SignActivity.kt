package com.example.module

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivitySignBinding
import kotlin.math.abs

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private var frontViewVideoId: String = ""
    private var sideViewVideoId: String = ""
    private lateinit var currentCategory: String
    private lateinit var currentList: List<String>
    private var currentIndex: Int = 0
    private lateinit var gestureDetector: GestureDetector

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val displayText = intent.getStringExtra("DISPLAY_TEXT") ?: ""
        val videoCode = intent.getStringExtra("VIDEO_CODE") ?: ""
        currentCategory = intent.getStringExtra("CATEGORY") ?: "letters"

        // Initialize item list based on category
        currentList = when (currentCategory) {
            "letters" -> listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
            "numbers" -> listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            "words" -> listOf("Bye", "Hello", "Thank You", "Yes", "No")
            else -> emptyList()
        }

        // Find current item index
        currentIndex = currentList.indexOf(videoCode)
        if (currentIndex == -1) currentIndex = 0

        binding.txtSign.text = displayText

        // Load initial videos
        updateVideoIds(videoCode)
        setupWebView()
        loadVideo(frontViewVideoId)

        // Button listeners
        binding.btnBack.setOnClickListener { finish() }
        binding.btnFrontView.setOnClickListener { loadVideo(frontViewVideoId) }
        binding.btnSideView.setOnClickListener { loadVideo(sideViewVideoId) }

        // Gesture detector setup
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2.x - (e1?.x ?: 0f)
                if (abs(diffX) > 100 && abs(velocityX) > 100) {
                    when {
                        diffX < 0 -> goToNextItem() // Swipe right to left
                        diffX > 0 -> goToPreviousItem() // Swipe left to right
                    }
                    return true
                }
                return false
            }
        })

        // Set touch listener on the root view
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun goToNextItem() {
        if (currentIndex < currentList.size - 1) {
            currentIndex++
            updateDisplay()
        } else {
            Toast.makeText(this, "Last item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToPreviousItem() {
        if (currentIndex > 0) {
            currentIndex--
            updateDisplay()
        } else {
            Toast.makeText(this, "First item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDisplay() {
        val newVideoCode = currentList[currentIndex]
        val displayText = when (currentCategory) {
            "letters" -> newVideoCode.uppercase()
            "numbers" -> newVideoCode
            "words" -> newVideoCode.replaceFirstChar { it.uppercase() }
            else -> newVideoCode
        }
        binding.txtSign.text = displayText
        updateVideoIds(newVideoCode)
        loadVideo(frontViewVideoId)
    }

    private fun updateVideoIds(videoCode: String) {
        frontViewVideoId = getVideoId(videoCode, "front")
        sideViewVideoId = getVideoId(videoCode, "side")
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun setupWebView() {
        binding.webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_NO_CACHE
            }

            // Allow swipes on the WebView
            setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                performClick()
                false
            }
        }
    }

    private fun loadVideo(videoId: String) {
        if (videoId.isEmpty()) {
            Toast.makeText(this, "Video not available", Toast.LENGTH_SHORT).show()
            return
        }
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
    }

    private fun getVideoId(videoCode: String, viewType: String): String {
        val videoMap = mapOf(
            // Letters
            "a_front" to "1l4ut1DxO-23ZFXeWdOS-vkl9241x3SOh",
            "a_side" to "1Q6NzBPEJHro1xf31nq3xVQEJCGWpY1b_",
            "b_front" to "1l4ut1DxO-23ZFXeWdOS-vkl9241x3SOh", // Replace with actual ID
            "b_side" to "1Q6NzBPEJHro1xf31nq3xVQEJCGWpY1b_",   // Replace with actual ID
            // Add more entries for all letters, numbers, and words
        )
        return videoMap["${videoCode}_$viewType"] ?: ""
    }
}