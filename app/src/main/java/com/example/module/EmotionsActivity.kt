package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityEmotionsBinding

class EmotionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val wordMap = mapOf(
            binding.btnSad to Pair("Sad", "w_sad"),
            binding.btnHappy to Pair("Happy", "w_happy"),
            binding.btnAngry to Pair("Angry", "w_angry")
        )

        wordMap.forEach { (button, pair) ->
            button.setOnClickListener {
                showSign(pair.first, pair.second)
            }
        }
    }

    private fun showSign(displayText: String, videoCode: String) {
        val intent = Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText)
            putExtra("VIDEO_CODE", videoCode)
            putExtra("CATEGORY", "emotions")
        }
        startActivity(intent)
    }
}
