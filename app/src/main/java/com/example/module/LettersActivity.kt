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

        // Full letter to image mapping (A-Z)
        val letterMap = mapOf(
            binding.btnA to Pair("A", "letter_a"),
            binding.btnB to Pair("B", "letter_b"),
            binding.btnC to Pair("C", "letter_c"),
            binding.btnD to Pair("D", "letter_d"),
            binding.btnE to Pair("E", "letter_e"),
            binding.btnF to Pair("F", "letter_f"),
            binding.btnG to Pair("G", "letter_g"),
            binding.btnH to Pair("H", "letter_h"),
            binding.btnI to Pair("I", "letter_i"),
            binding.btnJ to Pair("J", "letter_j"),
            binding.btnK to Pair("K", "letter_k"),
            binding.btnL to Pair("L", "letter_l"),
            binding.btnM to Pair("M", "letter_m"),
            binding.btnN to Pair("N", "letter_n"),
            binding.btnO to Pair("O", "letter_o"),
            binding.btnP to Pair("P", "letter_p"),
            binding.btnQ to Pair("Q", "letter_q"),
            binding.btnR to Pair("R", "letter_r"),
            binding.btnS to Pair("S", "letter_s"),
            binding.btnT to Pair("T", "letter_t"),
            binding.btnU to Pair("U", "letter_u"),
            binding.btnV to Pair("V", "letter_v"),
            binding.btnW to Pair("W", "letter_w"),
            binding.btnX to Pair("X", "letter_x"),
            binding.btnY to Pair("Y", "letter_y"),
            binding.btnZ to Pair("Z", "letter_z")
        )

        // Set click listeners for all letter buttons
        letterMap.forEach { (button, pair) ->
            button.setOnClickListener {
                showSign(pair.first, pair.second)
            }
        }
    }

    private fun showSign(displayText: String, imageCode: String) {
        Intent(this, SignActivity::class.java).apply {
            putExtra("DISPLAY_TEXT", displayText)
            putExtra("IMAGE_CODE", imageCode)
            startActivity(this)
        }
    }
}