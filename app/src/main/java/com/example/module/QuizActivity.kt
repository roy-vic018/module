package com.example.module

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.module.databinding.ActivityQuizBinding
import java.util.Locale

data class QuizQuestion(
    val type: String,
    val correctAnswer: String,
    val imageResource: Int,
    val options: List<String>
)

data class QuizProgress(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val currentStreak: Int,
    val bestStreak: Int
)

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var currentQuestion: QuizQuestion
    private val questions = mutableListOf<QuizQuestion>()
    private var currentProgress = QuizProgress(0, 0, 0, 0)
    private val sharedPref by lazy { getSharedPreferences("QuizProgress", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupQuiz()
        loadProgress()
        setupClickListeners()
        showNextQuestion()
    }

    private fun setupQuiz() {
        // Generate questions from all modules
        questions.addAll(generateLetterQuestions())
        questions.addAll(generateNumberQuestions())
        questions.addAll(generateWordQuestions())
        questions.shuffle()
        currentProgress = currentProgress.copy(totalQuestions = questions.size)
    }

    private fun generateLetterQuestions(): List<QuizQuestion> {
        return ('A'..'Z').map { letter ->
            QuizQuestion(
                type = "letter",
                correctAnswer = letter.toString(),
                imageResource = resources.getIdentifier("letter_${letter.lowercase()}", "drawable", packageName),
                options = generateOptions(letter.toString(), 'A'..'Z')
            )
        }
    }

    private fun generateNumberQuestions(): List<QuizQuestion> {
        return (1..10).map { number ->
            QuizQuestion(
                type = "number",
                correctAnswer = number.toString(),
                imageResource = resources.getIdentifier("n$number", "drawable", packageName),
                options = generateOptions(number.toString(), 1..10)
            )
        }
    }

    private fun generateWordQuestions(): List<QuizQuestion> {
        val words = listOf("bye", "hello", "thank_you", "yes", "no")
        return words.map { word ->
            QuizQuestion(
                type = "word",
                correctAnswer = word.replace("_", " ").capitalize(Locale.ROOT),
                imageResource = resources.getIdentifier("word_$word", "drawable", packageName),
                options = generateOptions(word, words)
            )
        }
    }

    private fun <T> generateOptions(correct: T, pool: Iterable<T>): List<String> {
        val options = mutableListOf(correct.toString())
        val availableOptions = pool.toList() - correct

        while (options.size < 4 && availableOptions.isNotEmpty()) {
            val randomItem = availableOptions.shuffled().first().toString()
            if (!options.contains(randomItem)) {
                options.add(randomItem)
            }
        }

        // Fill remaining slots if needed
        while (options.size < 4) {
            options.add(correct.toString())
        }

        return options.shuffled().map { it.replace("_", " ").replaceFirstChar { it.titlecase() } }
    }

    @SuppressLint("NewApi")
    private fun showNextQuestion() {
        if (questions.isEmpty()) {
            showResults()
            return
        }

        currentQuestion = questions.removeFirst()
        binding.imgQuestion.setImageResource(currentQuestion.imageResource)
        updateProgressUI()

        listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4).forEachIndexed { index, button ->
            button.text = currentQuestion.options[index]
        }
    }

    private fun checkAnswer(selectedAnswer: String) {
        val isCorrect = selectedAnswer.equals(currentQuestion.correctAnswer, ignoreCase = true)

        currentProgress = currentProgress.copy(
            correctAnswers = currentProgress.correctAnswers + (if (isCorrect) 1 else 0),
            currentStreak = if (isCorrect) currentProgress.currentStreak + 1 else 0,
            bestStreak = maxOf(currentProgress.bestStreak, currentProgress.currentStreak)
        )

        saveProgress()
        showFeedback(isCorrect)
        Handler(Looper.getMainLooper()).postDelayed({ showNextQuestion() }, 1000)
    }

    private fun showFeedback(isCorrect: Boolean) {
        val color = if (isCorrect) Color.GREEN else Color.RED
        binding.imgQuestion.setColorFilter(color)
    }

    private fun showResults() {
        AlertDialog.Builder(this)
            .setTitle("Quiz Complete!")
            .setMessage("Score: ${currentProgress.correctAnswers}/${currentProgress.totalQuestions}")
            .setPositiveButton("Retry") { _, _ -> recreate() }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .show()
    }

    private fun saveProgress() {
        with(sharedPref.edit()) {
            putInt("bestScore", maxOf(currentProgress.correctAnswers, sharedPref.getInt("bestScore", 0)))
            putInt("bestStreak", maxOf(currentProgress.bestStreak, sharedPref.getInt("bestStreak", 0)))
            apply()
        }
    }

    private fun loadProgress() {
        currentProgress = currentProgress.copy(
            bestStreak = sharedPref.getInt("bestStreak", 0)
        )
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4).forEach { button ->
            button.setOnClickListener { checkAnswer(button.text.toString()) }
        }
    }

    // Add to QuizActivity.kt
    private var progressAnimator: ValueAnimator? = null

    @SuppressLint("SetTextI18n")
    private fun updateProgressUI() {
        val progress = (currentProgress.correctAnswers.toFloat() / currentProgress.totalQuestions) * 100
        val prevProgress = binding.progressBar.progress

        // Animate progress bar
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofInt(prevProgress, progress.toInt()).apply {
            duration = 500
            interpolator = OvershootInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                binding.progressBar.progress = value
                binding.txtProgressPercent.text = "$value%"

                // Update rocket position
                val translationX = (binding.progressBar.width * (value / 100f)) -
                        (binding.progressIndicator.width / 2f)
                binding.progressIndicator.translationX = translationX
            }
            start()
        }

        // Animate streak
        binding.txtStreakMain.text = "Current Streak: ${currentProgress.currentStreak}\nBest Streak: ${currentProgress.bestStreak}"
        binding.txtStreakProgress.text = "Streak: ${currentProgress.currentStreak}"

        // Particle effect on streak increase
        if (currentProgress.currentStreak > 0 && currentProgress.currentStreak % 5 == 0) {
            showStreakEffect(currentProgress.currentStreak)
        }
    }

    private fun showStreakEffect(streak: Int) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.streak_pulse).apply {
            repeatCount = 2
        }
    }
}