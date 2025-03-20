package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityGreetingsBinding

class GreetingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGreetingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGreetingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val wordMap = mapOf(
            binding.btnHello to Pair("Hello", "w_hello"),
            binding.btnBye to Pair("Bye", "w_bye"),
            binding.btnThankYou to Pair("Thank You", "w_thank_you")
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
            putExtra("CATEGORY", "greetings")
        }
        startActivity(intent)
    }
}
