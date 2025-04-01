package com.example.module

import android.annotation.SuppressLint
import android.net.Uri
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
import java.io.File
import kotlin.math.abs

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private var frontViewVideoUrl: String = ""
    private var sideViewVideoUrl: String = ""
    private lateinit var currentCategory: String
    // For letters and numbers we'll use a List<String>
    private var currentList: List<String> = emptyList()
    // For words, we use a list of pairs: (Tagalog display text, video code)
    private var currentWordList: List<Pair<String, String>> = emptyList()
    private var currentIndex: Int = 0
    // When category is words, currentVideoCode will be taken from the pair's second element.
    private var currentVideoCode: String = ""
    private lateinit var gestureDetector: GestureDetector
    private var player: ExoPlayer? = null
    private var showRightIndicator = true


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

        // Set the title dynamically based on category
        binding.txtSignTitle.text = when (currentCategory) {
            "letters" -> getString(R.string.sign_title_alphabet) // "Learn Alphabet"
            "numbers" -> getString(R.string.sign_title_numbers)  // "Learn Numbers"
            "words" -> getString(R.string.sign_title_words)      // "Learn Words"
            else -> getString(R.string.sign_title_alphabet)
        }

        // Initialize list based on category
        if (currentCategory == "words") {
            // Use the Tagalog mapping defined in WordsActivity
            currentWordList = listOf(
                Pair("Magandang Umaga", "w_goodmorning"),
                Pair("Magandang Hapon", "w_goodafternoon"),
                Pair("Magandang Gabi", "w_goodevening"),
                Pair("Ingat ka", "w_takecare"),
                Pair("Paalam", "w_bye"),
                Pair("Help", "w_help"),
                Pair("Doctor", "w_doctor"),
                Pair("Hospital", "w_hospital"),
                Pair("Police", "w_police"),
                Pair("Painful/Hurt", "w_painful"),
                Pair("Emergency", "w_emergency")
            )
            // Find the index based on the passed video code
            currentIndex = currentWordList.indexOfFirst { it.second == videoCode }.takeIf { it != -1 } ?: 0
            // Set display text and currentVideoCode based on the pair
            val wordPair = currentWordList[currentIndex]
            binding.txtSign.text = wordPair.first
            currentVideoCode = wordPair.second
        } else {
            // For letters and numbers, use your existing lists
            currentList = when (currentCategory) {
                "letters" -> ('a'..'z').map { "l$it" }
                "numbers" -> (0..9).map { "n$it" }
                else -> emptyList()
            }
            // Find index and display text using the existing logic
            currentIndex = currentList.indexOf(videoCode).takeIf { it != -1 } ?: 0
            val display = when (currentCategory) {
                "letters" -> currentList[currentIndex].substring(1).uppercase()
                "numbers" -> currentList[currentIndex].substring(1)
                else -> currentList[currentIndex]
            }
            binding.txtSign.text = display
            currentVideoCode = currentList[currentIndex]
        }

        // Update video URLs and initialize ExoPlayer
        updateVideoUrls(currentVideoCode)
        setupExoPlayer()

        // Play the front view video by default
        playVideo(frontViewVideoUrl, "front")

        // Set up button listeners
        binding.btnBack.setOnClickListener { finish() }
        binding.btnFrontView.setOnClickListener {
            Log.d("ButtonDebug", "Front View Button Clicked. URL: $frontViewVideoUrl")
            playVideo(frontViewVideoUrl, "front")
        }
        binding.btnSideView.setOnClickListener {
            Log.d("ButtonDebug", "Side View Button Clicked. URL: $sideViewVideoUrl")
            playVideo(sideViewVideoUrl, "side")
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
        if (currentCategory == "words") {
            if (currentIndex < currentWordList.size - 1) {
                currentIndex++
                updateDisplay()
            } else {
                Toast.makeText(this, "Last item", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (currentIndex < currentList.size - 1) {
                currentIndex++
                updateDisplay()
            } else {
                Toast.makeText(this, "Last item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToPreviousItem() {
        if (currentCategory == "words") {
            if (currentIndex > 0) {
                currentIndex--
                updateDisplay()
            } else {
                Toast.makeText(this, "First item", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (currentIndex > 0) {
                currentIndex--
                updateDisplay()
            } else {
                Toast.makeText(this, "First item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Update the swipe indicators based on the current item, then fade out after 2 seconds.
    private fun updateSwipeIndicators() {
        // Retain your original logic:
        if (currentCategory == "words") {
            binding.leftSwipeIndicator.visibility =
                if (currentIndex == 0) View.GONE else View.VISIBLE
            binding.rightSwipeIndicator.visibility =
                if (currentIndex == currentWordList.size - 1) View.GONE else View.VISIBLE
        } else {
            binding.leftSwipeIndicator.visibility =
                if (currentIndex == 0) View.GONE else View.VISIBLE
            binding.rightSwipeIndicator.visibility =
                if (currentIndex == currentList.size - 1) View.GONE else View.VISIBLE
        }

        // For any visible indicator, schedule a fade-out after 2 seconds.
        if (binding.leftSwipeIndicator.visibility == View.VISIBLE) {
            // Reset alpha (in case it was faded previously)
            binding.leftSwipeIndicator.alpha = 1f
            binding.leftSwipeIndicator.postDelayed({
                binding.leftSwipeIndicator.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction { binding.leftSwipeIndicator.visibility = View.INVISIBLE }
            }, 2000)
        }
        if (binding.rightSwipeIndicator.visibility == View.VISIBLE) {
            binding.rightSwipeIndicator.alpha = 1f
            binding.rightSwipeIndicator.postDelayed({
                binding.rightSwipeIndicator.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction { binding.rightSwipeIndicator.visibility = View.INVISIBLE }
            }, 2000)
        }
    }

    private fun updateDisplay() {
        if (currentCategory == "words") {
            val wordPair = currentWordList[currentIndex]
            binding.txtSign.text = wordPair.first
            currentVideoCode = wordPair.second
            // Update video URLs and play the front view video
            updateVideoUrls(currentVideoCode)
            playVideo(frontViewVideoUrl, "front")
            trackProgress(wordPair.first)
        } else {
            val newVideoCode = currentList[currentIndex]
            val displayText = when (currentCategory) {
                "letters" -> newVideoCode.substring(1).uppercase()
                "numbers" -> newVideoCode.substring(1)
                else -> newVideoCode
            }
            binding.txtSign.text = displayText
            currentVideoCode = newVideoCode
            updateVideoUrls(newVideoCode)
            playVideo(frontViewVideoUrl, "front")
            trackProgress(displayText)
        }
        updateSwipeIndicators()
    }

    private fun trackProgress(displayText: String) {
        val key = "${currentCategory}_completed"
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)
        val completed = prefs.getStringSet(key, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if (completed.add(displayText)) {
            prefs.edit().putStringSet(key, completed).apply()
        }
    }

    @OptIn(UnstableApi::class)
    private fun updateVideoUrls(videoCode: String) {
        frontViewVideoUrl = VideoRepository.getCloudinaryUrl(videoCode, "front")
        sideViewVideoUrl = VideoRepository.getCloudinaryUrl(videoCode, "side")
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
    private fun playVideo(onlineUrl: String, viewType: String) {
        val uri = getLocalVideoUri(currentVideoCode, viewType, onlineUrl)
        Log.d("VideoDebug", "Playing Video: $uri")
        player?.stop()
        player?.clearMediaItems()
        val mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        player?.setPlaybackSpeed(1.2f)
        player?.volume = 0f
    }

    /**
     * Helper to determine the local URI for a video.
     * Files are expected in a "videos" folder named as "<videoCode>_<viewType>.mp4".
     */
    private fun getLocalVideoUri(videoCode: String, viewType: String, onlineUrl: String): Uri {
        val videosDir = File(getExternalFilesDir(null), "videos")
        val fileName = "${videoCode}_${viewType}.mp4"
        val localFile = File(videosDir, fileName)
        return if (localFile.exists()) {
            Uri.fromFile(localFile)
        } else {
            onlineUrl.toUri()
        }
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
