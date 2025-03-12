package com.example.module

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.module.databinding.ActivitySignBinding
import kotlin.math.abs

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private var frontViewVideoUrl: String = ""
    private var sideViewVideoUrl: String = ""
    private lateinit var currentCategory: String
    private lateinit var currentList: List<String>
    private var currentIndex: Int = 0
    private lateinit var gestureDetector: GestureDetector
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    @SuppressLint("ClickableViewAccessibility")
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
            "letters" -> ('a'..'z').map { "l$it" }
            "numbers" -> (0..9).map { "n$it" }
            "words" -> listOf("w_bye", "w_hello", "w_thank_you", "w_yes", "w_no")
            else -> emptyList()
        }

        // Find current item index
        currentIndex = currentList.indexOf(videoCode).takeIf { it != -1 } ?: 0
        binding.txtSign.text = displayText

        // Update video URLs and initialize ExoPlayer
        updateVideoUrls(videoCode)
        setupExoPlayer()

        // Play the front view video by default
        playVideo(frontViewVideoUrl)

        // Set up button listeners
        binding.btnBack.setOnClickListener { finish() }
        binding.btnFrontView.setOnClickListener {
            Log.d("ButtonDebug", "Front View Button Clicked. URL: $frontViewVideoUrl")
            playVideo(frontViewVideoUrl)
        }
        binding.btnSideView.setOnClickListener {
            Log.d("ButtonDebug", "Side View Button Clicked. URL: $sideViewVideoUrl")
            playVideo(sideViewVideoUrl)
        }

        // Set up gesture detector for swipe navigation
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
            "letters" -> newVideoCode.substring(1).uppercase()
            "numbers" -> newVideoCode.substring(1)
            "words" -> newVideoCode.substring(2)
                .replace("_", " ")
                .split(" ")
                .joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                }
            else -> newVideoCode
        }
        binding.txtSign.text = displayText

        // Update video URLs and play the front view video
        updateVideoUrls(newVideoCode)
        playVideo(frontViewVideoUrl) // Play the new video

        // Track progress for the current item
        trackProgress(displayText)
    }

    private fun trackProgress(displayText: String) {
        val key = "${currentCategory}_completed"
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)
        val completed = prefs.getStringSet(key, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if (completed.add(displayText)) {
            prefs.edit().putStringSet(key, completed).apply()
        }
    }

    private fun getCloudinaryUrl(videoCode: String, viewType: String): String {
        val cloudName = "decewbra0"
        val videoMap = mapOf(
            //Number's mapping
            "n0_front" to "n0_front_t8hyuj",
            "n0_side" to "n0_side_qrkm8r",
            "n1_front" to "n1_front_ugvwss",
            "n1_side" to "n1_side_zay8ip",
            "n2_front" to "n2_front_l5nezn",
            "n2_side" to "n2_front_l5nezn",
            "n3_front" to "n3_front_dc6wuq",
            "n3_side" to "n3_side_psknm1",
            "n4_front" to "n4_front_hmypt4",
            "n4_side" to "n4_side_zse7nv",
            "n5_front" to "n5_front_xprh2d",
            "n5_side" to "n5_side_kd32jo",
            "n6_front" to "n6_front_s6lvbn",
            "n6_side" to "n6_side_wiyn3t",
            "n7_front" to "n7_front_tbs3z4",
            "n7_side" to "n7_side_m8xneo",
            "n8_front" to "n8_front_oitlxs",
            "n8_side" to "n8_side_i7yjnd",
            "n9_front" to "n9_front_bmvirh",
            "n9_side" to "n9_side_edexse",
            //Letter's mapping
            "la_front" to "tjac32fkpjvhis4lmjpd",
            "la_side" to "ivixpjsxtojwdp1np1v6",
        )
        val publicId = videoMap["${videoCode}_$viewType"] ?: return ""
        return "https://res.cloudinary.com/$cloudName/video/upload/$publicId.mp4"
    }

    @OptIn(UnstableApi::class)
    private fun updateVideoUrls(videoCode: String) {
        frontViewVideoUrl = getCloudinaryUrl(videoCode, "front")
        sideViewVideoUrl = getCloudinaryUrl(videoCode, "side")

        Log.d("VideoDebug", "Front View URL: $frontViewVideoUrl")
        Log.d("VideoDebug", "Side View URL: $sideViewVideoUrl")
    }

    @OptIn(UnstableApi::class)
    private fun setupExoPlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.videoPlayer.player = player
        binding.videoPlayer.useController = false // Hide controls for autoplay experience
        binding.videoPlayer.visibility = View.VISIBLE
        binding.videoPlayer.setKeepContentOnPlayerReset(true)

        // Set repeat mode to loop the video indefinitely
        player?.repeatMode = Player.REPEAT_MODE_ALL

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> Log.d("VideoDebug", "Video is ready to play")
                    Player.STATE_BUFFERING -> Log.d("VideoDebug", "Buffering...")
                    Player.STATE_ENDED -> Log.d("VideoDebug", "Video playback ended")
                    Player.STATE_IDLE -> Log.d("VideoDebug", "Player is idle")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("VideoDebug", "Playback error: ${error.message}")
                Toast.makeText(this@SignActivity, "Video playback error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun playVideo(videoUrl: String) {
        if (videoUrl.isEmpty()) {
            Toast.makeText(this, "Video not available", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("VideoDebug", "Playing Video: $videoUrl")

        // Stop and clear existing MediaItem
        player?.stop()
        player?.clearMediaItems()

        // Set a new MediaItem
        val mediaItem = MediaItem.fromUri(videoUrl.toUri())
        player?.setMediaItem(mediaItem)
        player?.prepare() // Prepare the media for playback
        player?.playWhenReady = true // Auto-play immediately
        player?.setPlaybackSpeed(1.5f) // Set speed to 1.5x
    }

    override fun onStart() {
        super.onStart()
        player?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}