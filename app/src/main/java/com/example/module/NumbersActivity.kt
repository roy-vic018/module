package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityNumbersBinding
import com.bumptech.glide.Glide

class NumbersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNumbersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumbersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Improved back button implementation
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Update quiz card based on progress (it may be locked or unlocked)
        updateQuizCard()

        // Number to video mapping (0-9)
        val numberMap = mapOf(
            binding.btn0 to Pair("0", "n0"),
            binding.btn1 to Pair("1", "n1"),
            binding.btn2 to Pair("2", "n2"),
            binding.btn3 to Pair("3", "n3"),
            binding.btn4 to Pair("4", "n4"),
            binding.btn5 to Pair("5", "n5"),
            binding.btn6 to Pair("6", "n6"),
            binding.btn7 to Pair("7", "n7"),
            binding.btn8 to Pair("8", "n8"),
            binding.btn9 to Pair("9", "n9"),
        )

        // Set click listeners for all number buttons
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
        val completed = prefs.getStringSet("numbers_completed", mutableSetOf())?.toMutableSet()
            ?: mutableSetOf()
        if (completed.add(displayText)) {
            prefs.edit().putStringSet("numbers_completed", completed).apply()
        }

        Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText)
            putExtra("VIDEO_CODE", videoCode)
            putExtra("CATEGORY", "numbers")
            startActivity(this)
        }
    }

    /**
     * Updates the quiz CardView based on progress.
     * If the user has completed all 10 numbers, the quiz becomes unlocked.
     */
    private fun updateQuizCard() {
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)
        val numbersCompletedCount = prefs.getStringSet("numbers_completed", emptySet())?.size ?: 0

        if (numbersCompletedCount < 10) {
            // Module not complete; lock the quiz.
            binding.cardQuiz.isClickable = false
            binding.txtQuizLabel.text = "QUIZ LOCKED"
            Glide.with(this)
                .asGif()
                .load(R.raw.animated_locked) // Your animated GIF resource for locked state
                .into(binding.imgQuizIcon)
            binding.cardQuiz.setOnClickListener(null)
        } else {
            // Module complete; unlock quiz.
            binding.cardQuiz.isClickable = true
            binding.txtQuizLabel.text = "TAKE NUMBERS QUIZ"
            Glide.with(this)
                .asGif()
                .load(R.raw.animated_unlocked) // Your animated GIF resource for unlocked state
                .into(binding.imgQuizIcon)
            binding.cardQuiz.setOnClickListener {
                val intent = Intent(this, NumbersQuizActivity::class.java)
                intent.putExtra("quizType", "number")
                startActivity(intent)
            }
        }
    }
}
