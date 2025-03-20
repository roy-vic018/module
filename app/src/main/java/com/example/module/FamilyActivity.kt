package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityFamilyBinding

class FamilyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val wordMap = mapOf(
            binding.btnMother to Pair("Mother", "w_mother"),
            binding.btnFather to Pair("Father", "w_father"),
            binding.btnBrother to Pair("Brother", "w_brother")
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
