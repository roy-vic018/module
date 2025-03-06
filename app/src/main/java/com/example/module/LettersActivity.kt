package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityLettersBinding

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

        // Full letter-to-video mapping (A-Z)
        val letterMap = mapOf(
            binding.btnA to Pair("A", "a"),
            binding.btnB to Pair("B", "b"),
            binding.btnC to Pair("C", "c"),
            binding.btnD to Pair("D", "d"),
            binding.btnE to Pair("E", "e"),
            binding.btnF to Pair("F", "f"),
            binding.btnG to Pair("G", "g"),
            binding.btnH to Pair("H", "h"),
            binding.btnI to Pair("I", "i"),
            binding.btnJ to Pair("J", "j"),
            binding.btnK to Pair("K", "k"),
            binding.btnL to Pair("L", "l"),
            binding.btnM to Pair("M", "m"),
            binding.btnN to Pair("N", "n"),
            binding.btnO to Pair("O", "o"),
            binding.btnP to Pair("P", "p"),
            binding.btnQ to Pair("Q", "q"),
            binding.btnR to Pair("R", "r"),
            binding.btnS to Pair("S", "s"),
            binding.btnT to Pair("T", "t"),
            binding.btnU to Pair("U", "u"),
            binding.btnV to Pair("V", "v"),
            binding.btnW to Pair("W", "w"),
            binding.btnX to Pair("X", "x"),
            binding.btnY to Pair("Y", "y"),
            binding.btnZ to Pair("Z", "z")
        )

        // Set click listeners for all letter buttons
        letterMap.forEach { (button, pair) ->
            button.setOnClickListener {
                showSign(pair.first, pair.second)
            }
        }
    }

    /**
     * Opens SignActivity with the corresponding letter's display text and video code.
     */
    private fun showSign(displayText: String, videoCode: String) {
        Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText)
            putExtra("VIDEO_CODE", videoCode)
            startActivity(this)
        }
    }
}
