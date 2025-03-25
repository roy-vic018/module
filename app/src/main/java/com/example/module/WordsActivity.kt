package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityWordsBinding
import com.bumptech.glide.Glide

class WordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Improved back button implementation
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Update quiz card based on progress (it may be locked or unlocked)
        updateQuizCard()

        // Words to video mapping
        val numberMap = mapOf(
            binding.btnGoodmorning to Pair("Magandang Umaga", "w_goodmorning"),
            binding.btnGoodafternoon to Pair("Magandang Hapon", "w_goodafternoon"),
            binding.btnGoodevening to Pair("Magandang Gabi", "w_goodevening"),
            binding.btnTakecare to Pair("Ingat ka", "w_takecare"),
            binding.btnBye to Pair("Paalam", "w_bye"),
        )

        // Set click listeners for all words buttons
        numberMap.forEach { (button, pair) ->
            button.setOnClickListener {
                showSign(pair.first, pair.second)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the quiz card state every time the activity resumes.
        updateQuizCard()
    }

    /**
     * Opens SignActivity with the corresponding number's display text and video code.
     */
    private fun showSign(displayText: String, videoCode: String) {
        // Track progress in SharedPreferences
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)
        val completed = prefs.getStringSet("words_completed", mutableSetOf())?.toMutableSet()
            ?: mutableSetOf()
        if (completed.add(displayText)) {
            prefs.edit().putStringSet("words_completed", completed).apply()
        }

        Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText)
            putExtra("VIDEO_CODE", videoCode)
            putExtra("CATEGORY", "words")
            startActivity(this)
        }
    }

    /**
     * Updates the quiz CardView based on progress.
     * If the user has completed all 10 numbers, the quiz becomes unlocked.
     */
    private fun updateQuizCard() {
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)
        val wordsCompletedCount = prefs.getStringSet("words_completed", emptySet())?.size ?: 0

        if (wordsCompletedCount < 5) {
            // Module not complete; lock the quiz.
            binding.cardQuiz.isClickable = false
            binding.txtQuizLabel.text = "Quiz Locked"
            Glide.with(this)
                .asGif()
                .load(R.raw.animated_locked) // Your animated GIF resource for locked state
                .into(binding.imgQuizIcon)
            binding.cardQuiz.setOnClickListener(null)
        } else {
            // Module complete; unlock quiz.
            binding.cardQuiz.isClickable = true
            binding.txtQuizLabel.text = "Take Words Quiz"
            Glide.with(this)
                .asGif()
                .load(R.raw.animated_unlocked) // Your animated GIF resource for unlocked state
                .into(binding.imgQuizIcon)
            binding.cardQuiz.setOnClickListener {
                val intent = Intent(this, WordsQuizActivity::class.java)
                intent.putExtra("quizType", "word")
                startActivity(intent)
            }
        }
    }
}
