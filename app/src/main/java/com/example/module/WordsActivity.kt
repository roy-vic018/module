package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityWordsBinding

class WordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the back button
        binding.btnBack.setOnClickListener {
            finish() // Navigate back to the previous screen
        }

        // Word to video mapping
        val wordMap = mapOf(
            binding.btnBye to Pair("Bye", "word_bye"),
            binding.btnHello to Pair("Hello", "word_hello"),
            binding.btnThankYou to Pair("Thank You", "word_thank_you"),
            binding.btnYes to Pair("Yes", "word_yes"),
            binding.btnNo to Pair("No", "word_no")
        )

        // Set click listeners for all word buttons
        wordMap.forEach { (button, pair) ->
            button.setOnClickListener {
                showSign(pair.first, pair.second)
            }
        }
    }

    /**
     * Opens SignActivity with the corresponding word's display text and video code.
     */
    private fun showSign(displayText: String, videoCode: String) {
        Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText)
            putExtra("VIDEO_CODE", videoCode)
            putExtra("CATEGORY", "words")
            startActivity(this)
        }
    }
}
