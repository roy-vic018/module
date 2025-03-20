package com.example.module

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.module.databinding.ActivityMainBinding
import com.google.android.material.progressindicator.CircularProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listener for the Letters CardView
        binding.btnLetters.setOnClickListener {
            startActivity(Intent(this, LettersActivity::class.java))
        }

        // Set up click listener for the Numbers CardView
        binding.btnNumbers.setOnClickListener {
            startActivity(Intent(this, NumbersActivity::class.java))
        }

        // Set up click listener for the Words CardView
        binding.btnWords.setOnClickListener {
            startActivity(Intent(this, WordsActivity::class.java))
        }

        // Set up click listener for the Quiz CardView
        binding.btnStartQuiz.setOnClickListener {
            startActivity(Intent(this, NumbersQuizActivity::class.java))
        }

        // Trigger the bulk video download using WorkManager
        val bulkDownloadWorkRequest = OneTimeWorkRequestBuilder<BulkDownloadWorker>().build()
        WorkManager.getInstance(this).enqueue(bulkDownloadWorkRequest)

        updateProgress()
    }

    override fun onResume() {
        super.onResume()
        updateProgress()
    }

    private fun updateProgress() {
        val prefs = getSharedPreferences("progress", MODE_PRIVATE)

        // Letters (A-Z)
        val lettersCompleted = prefs.getStringSet("letters_completed", emptySet())?.size ?: 0
        updateModuleProgress(binding.lettersProgress, binding.tvLettersPercent, lettersCompleted, 26)

        // Numbers (0-9)
        val numbersCompleted = prefs.getStringSet("numbers_completed", emptySet())?.size ?: 0
        updateModuleProgress(binding.numbersProgress, binding.tvNumbersPercent, numbersCompleted, 10)

        // Words (5 items)
        val wordsCompleted = prefs.getStringSet("words_completed", emptySet())?.size ?: 0
        updateModuleProgress(binding.wordsProgress, binding.tvWordsPercent, wordsCompleted, 5)
    }

    private fun updateModuleProgress(
        progressBar: CircularProgressIndicator,
        textView: TextView,
        completed: Int,
        total: Int
    ) {
        val progress = (completed.toFloat() / total * 100).toInt()
        progressBar.progress = progress
        textView.text = "$progress%"

        // Change color when completed
        if (progress == 100) {
            progressBar.setIndicatorColor(ContextCompat.getColor(this, R.color.green_500))
            textView.setTextColor(ContextCompat.getColor(this, R.color.green_500))
        }
    }
}