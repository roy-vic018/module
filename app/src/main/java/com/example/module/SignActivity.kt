package com.example.module

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayText = intent.getStringExtra("DISPLAY_TEXT") ?: ""
        val imageCode = intent.getStringExtra("IMAGE_CODE") ?: ""

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Set text and image
        binding.txtSign.text = displayText
        val resId = resources.getIdentifier(
            imageCode.lowercase(),
            "drawable",
            packageName
        )

        if (resId != 0) {
            binding.imgSign.setImageResource(resId)
        } else {
            // Fallback image or error handling
            binding.imgSign.setImageResource(R.drawable.default_image)
        }
    }
}