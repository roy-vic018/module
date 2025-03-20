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

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnGreetings.setOnClickListener {
            startActivity(Intent(this, GreetingsActivity::class.java))
        }

        binding.btnEmotions.setOnClickListener {
            startActivity(Intent(this, EmotionsActivity::class.java))
        }

        binding.btnFamily.setOnClickListener {
            startActivity(Intent(this, FamilyActivity::class.java))
        }
    }
}
