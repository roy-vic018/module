package com.example.module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLetters.setOnClickListener {
            startActivity(Intent(this, LettersActivity::class.java))
        }

        binding.btnNumbers.setOnClickListener {
            startActivity(Intent(this, NumbersActivity::class.java))
        }

        binding.btnWords.setOnClickListener {
            startActivity(Intent(this, WordsActivity::class.java))
        }

        // Set up click listener for the Quiz CardView
        binding.btnStartQuiz.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }
    }
}