package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityNumbersBinding

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

        // Number to video mapping (0-9)
        val numberMap = mapOf(
            binding.btn1 to Pair("1", "n1"),
            binding.btn2 to Pair("2", "n2"),
            binding.btn3 to Pair("3", "n3"),
            binding.btn4 to Pair("4", "n4"),
            binding.btn5 to Pair("5", "n5"),
            binding.btn6 to Pair("6", "n6"),
            binding.btn7 to Pair("7", "n7"),
            binding.btn8 to Pair("8", "n8"),
            binding.btn9 to Pair("9", "n9"),
            binding.btn0 to Pair("0", "n0")
        )

        // Set click listeners for all number buttons
        numberMap.forEach { (button, pair) ->
            button.setOnClickListener {
                showSign(pair.first, pair.second)
            }
        }
    }

    /**
     * Opens SignActivity with the corresponding number's display text and video code.
     */
    private fun showSign(displayText: String, videoCode: String) {
        Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText)
            putExtra("VIDEO_CODE", videoCode) // Corrected key from "IMAGE_CODE" to "VIDEO_CODE"
            startActivity(this)
        }
    }
}
