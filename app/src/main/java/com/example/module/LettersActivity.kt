package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityLettersBinding
import com.bumptech.glide.Glide

class LettersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLettersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLettersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Update the quiz card based on progress (letters completed)
        updateQuizCard()

        // Full letter-to-video mapping (A-Z)
        val letterMap = mapOf(
            binding.btnA to Pair("A", "la"),
            binding.btnB to Pair("B", "lb"),
            binding.btnC to Pair("C", "lc"),
            binding.btnD to Pair("D", "ld"),
            binding.btnE to Pair("E", "le"),
            binding.btnF to Pair("F", "lf"),
            binding.btnG to Pair("G", "lg"),
            binding.btnH to Pair("H", "lh"),
            binding.btnI to Pair("I", "li"),
            binding.btnJ to Pair("J", "lj"),
            binding.btnK to Pair("K", "lk"),
            binding.btnL to Pair("L", "ll"),
            binding.btnM to Pair("M", "lm"),
            binding.btnN to Pair("N", "ln"),
            binding.btnO to Pair("O", "lo"),
            binding.btnP to Pair("P", "lp"),
            binding.btnQ to Pair("Q", "lq"),
            binding.btnR to Pair("R", "lr"),
            binding.btnS to Pair("S", "ls"),
            binding.btnT to Pair("T", "lt"),
            binding.btnU to Pair("U", "lu"),
            binding.btnV to Pair("V", "lv"),
            binding.btnW to Pair("W", "lw"),
            binding.btnX to Pair("X", "lx"),
            binding.btnY to Pair("Y", "ly"),
            binding.btnZ to Pair("Z", "lz")
        )

        // Set click listeners for all letter buttons
        letterMap.forEach { (button, pair) ->
            button.setOnClickListener {
                showSign(pair.first, pair.second)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh quiz card state whenever activity resumes
        updateQuizCard()
    }

    /**
     * Opens SignActivity with the corresponding letter's display text and video code.
     */
    private fun showSign(displayText: String, videoCode: String) {
        // Track progress in SharedPreferences
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)
        val completed = prefs.getStringSet("letters_completed", mutableSetOf())?.toMutableSet()
            ?: mutableSetOf()
        if (completed.add(displayText)) {
            prefs.edit().putStringSet("letters_completed", completed).apply()
        }

        Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText) // Use the provided displayText
            putExtra("VIDEO_CODE", videoCode)    // Use the provided videoCode
            putExtra("CATEGORY", "letters")      // Add category for swipe navigation
            startActivity(this)
        }
    }

    /**
     * Updates the quiz CardView based on progress.
     * If the user has completed all 26 letters, the quiz becomes unlocked.
     */
    private fun updateQuizCard() {
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)
        val lettersCompletedCount = prefs.getStringSet("letters_completed", emptySet())?.size ?: 0

        if (lettersCompletedCount < 26) {
            // Module not complete; lock the quiz.
            binding.cardQuiz.isClickable = false
            binding.txtQuizLabel.text = "QUIZ LOCKED"
            Glide.with(this)
                .asGif()
                .load(R.raw.animated_locked) // animated GIF resource for locked state
                .into(binding.imgQuizIcon)
            binding.cardQuiz.setOnClickListener(null)
        } else {
            // Module complete; unlock quiz.
            binding.cardQuiz.isClickable = true
            binding.txtQuizLabel.text = "TAKE LETTERS QUIZ"
            Glide.with(this)
                .asGif()
                .load(R.raw.animated_unlocked) // animated GIF resource for unlocked state
                .into(binding.imgQuizIcon)
            binding.cardQuiz.setOnClickListener {
                val intent = Intent(this, LettersQuizActivity::class.java)
                intent.putExtra("quizType", "letter")
                startActivity(intent)
            }
        }
    }
}