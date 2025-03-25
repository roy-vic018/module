package com.example.module

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import com.example.module.databinding.ActivityNumbersQuizBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit


data class NumbersQuizQuestion(
    val type: String, // Always "number" for this activity
    val correctAnswer: String,
    val videoUrl: String,
    val options: List<String>
)

data class NumbersQuizProgress(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val currentStreak: Int,
    val bestStreak: Int
)

class NumbersQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNumbersQuizBinding
    private lateinit var player: ExoPlayer
    private lateinit var currentQuestion: NumbersQuizQuestion
    private val questions = mutableListOf<NumbersQuizQuestion>()
    private var currentProgress = NumbersQuizProgress(0, 0, 0, 0)
    private val sharedPref by lazy { getSharedPreferences("NumbersQuizProgress", MODE_PRIVATE) }

    // Timer variables: 15 sec per question.
    private var questionTimer: CountDownTimer? = null
    private val questionTimeInMillis: Long = 15000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumbersQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ExoPlayer for video playback.
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.videoQuestion.player = exoPlayer
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        }

        setupQuiz()
        loadProgress()
        setupClickListeners()
        showNextQuestion()
    }

    override fun onDestroy() {
        super.onDestroy()
        questionTimer?.cancel()
        player.release()
    }

    /**
     * Setup quiz by generating only number questions.
     */
    private fun setupQuiz() {
        questions.addAll(generateNumberQuestions())
        questions.shuffle()
        currentProgress = currentProgress.copy(totalQuestions = questions.size)
    }

    /**
     * Generate number questions from 0 to 9 using VideoRepository.
     */
    private fun generateNumberQuestions(): List<NumbersQuizQuestion> {
        return (0..9).map { number ->
            NumbersQuizQuestion(
                type = "number",
                correctAnswer = number.toString(),
                videoUrl = VideoRepository.getCloudinaryUrl("n$number", "front"),
                options = generateOptions(number.toString(), 0..9)
            )
        }
    }

    /**
     * Generate options for a given correct answer from a pool.
     */
    private fun <T> generateOptions(correct: T, pool: Iterable<T>): List<String> {
        val options = mutableListOf(correct.toString())
        val availableOptions = pool.toList() - correct
        while (options.size < 4 && availableOptions.isNotEmpty()) {
            val randomItem = availableOptions.shuffled().first().toString()
            if (!options.contains(randomItem)) {
                options.add(randomItem)
            }
        }
        while (options.size < 4) {
            options.add(correct.toString())
        }
        return options.shuffled().map {
            it.replace("_", " ").replaceFirstChar { ch -> ch.titlecase(Locale.ROOT) }
        }
    }

    /**
     * Checks for a local video file; if available, returns its URI, otherwise returns the online URL.
     */
    private fun getLocalVideoUri(videoCode: String, viewType: String, onlineUrl: String): Uri {
        val videosDir = File(getExternalFilesDir(null), "videos")
        val fileName = "${videoCode}_${viewType}.mp4"
        val localFile = File(videosDir, fileName)
        return if (localFile.exists()) {
            Uri.fromFile(localFile)
        } else {
            onlineUrl.toUri()
        }
    }

    @SuppressLint("NewApi")
    private fun showNextQuestion() {
        // Cancel any existing timer.
        questionTimer?.cancel()

        if (questions.isEmpty()) {
            showResults()
            return
        }
        currentQuestion = questions.removeFirst()
        // Build video code from correctAnswer (e.g., "n5" for answer "5")
        val videoCode = "n" + currentQuestion.correctAnswer
        val localUri = getLocalVideoUri(videoCode, "front", currentQuestion.videoUrl)
        val mediaItem = MediaItem.fromUri(localUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playbackParameters = PlaybackParameters(1.5f)
        player.playWhenReady = true

        updateProgressUI()

        listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
            .forEachIndexed { index, button ->
                button.text = currentQuestion.options[index]
            }
        startQuestionTimer()
    }

    /**
     * Starts a 15-second timer for the current question.
     * If the timer finishes, checkAnswer("") is called to mark the question as incorrect.
     */
    private fun startQuestionTimer() {
        questionTimer?.cancel()
        questionTimer = object : CountDownTimer(questionTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.txtTimer.text = secondsLeft.toString()

                // Change text color to red when 5 seconds or less remain, otherwise white.
                if (secondsLeft <= 5) {
                    binding.txtTimer.setTextColor(Color.RED)
                } else {
                    binding.txtTimer.setTextColor(Color.WHITE)
                }
            }
            override fun onFinish() {
                // Timer finished: mark the question as wrong.
                checkAnswer("")
                // Optionally reset the timer text.
                binding.txtTimer.text = "0"
            }
        }.start()
    }


    private fun checkAnswer(selectedAnswer: String) {
        // Cancel timer if applicable.
        questionTimer?.cancel()

        val isCorrect = selectedAnswer.equals(currentQuestion.correctAnswer, ignoreCase = true)
        currentProgress = currentProgress.copy(
            correctAnswers = currentProgress.correctAnswers + if (isCorrect) 1 else 0,
            currentStreak = if (isCorrect) currentProgress.currentStreak + 1 else 0,
            bestStreak = maxOf(currentProgress.bestStreak, currentProgress.currentStreak)
        )

        saveProgress()
        // Update progress UI immediately to reflect the new score.
        updateProgressUI()
        showFeedback(isCorrect)

        Handler(Looper.getMainLooper()).postDelayed({
            if (questions.isEmpty()) {
                showResults()
            } else {
                showNextQuestion()
            }
        }, 1000)
    }


    private fun showFeedback(isCorrect: Boolean) {
        val color = if (isCorrect) Color.GREEN else Color.RED
        binding.videoQuestion.foreground = android.graphics.drawable.ColorDrawable(color).apply {
            alpha = 80 // Semi-transparent overlay for feedback.
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.videoQuestion.foreground = null
        }, 300)
    }

    private fun showResults() {
        // Trigger confetti if the user got a perfect score.
        if (currentProgress.correctAnswers == currentProgress.totalQuestions) {
            showConfetti()
        }

        // Define a passing threshold (e.g., 70% correct answers).
        val passingThreshold = (currentProgress.totalQuestions * 0.7).toInt()
        val passed = currentProgress.correctAnswers >= passingThreshold

        // Save the flag to indicate whether this module is passed.
        with(getSharedPreferences("progress", MODE_PRIVATE).edit()) {
            putBoolean("numbers_passed", passed)
            apply()
        }

        // Build the message based on pass/fail.
        val message = if (passed) {
            // Show positive feedback.
            "Congratulations! You scored ${currentProgress.correctAnswers}/${currentProgress.totalQuestions}.\n" +
                    "You've unlocked the next module!"
        } else {
            // Show an encouraging message.
            "You scored ${currentProgress.correctAnswers}/${currentProgress.totalQuestions}.\n" +
                    "Keep practicing and try again to unlock the next module."
        }

        AlertDialog.Builder(this)
            .setTitle("Quiz Complete!")
            .setMessage(message)
            .setPositiveButton("Retry") { _, _ -> recreate() }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .show()
    }

    private fun showConfetti() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        binding.konfettiView.start(party)
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
        listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
            .forEach { button ->
                button.setOnClickListener { checkAnswer(button.text.toString()) }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgressUI() {
        // Calculate how many questions have been attempted.
        val attemptedQuestions = currentProgress.totalQuestions - questions.size
        // Compute progress percentage based on attempted questions.
        val progressPercent = ((attemptedQuestions.toFloat() / currentProgress.totalQuestions) * 100).toInt()

        // Optionally, still show the score (if desired).
        binding.txtScore.text = "Score: ${currentProgress.correctAnswers}"

        val prevProgress = binding.progressBar.progress
        val progressAnimator = ValueAnimator.ofInt(prevProgress, progressPercent).apply {
            duration = 500
            interpolator = OvershootInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                binding.progressBar.progress = value
                binding.txtProgressPercent.text = "$value%"
                val translationX = (binding.progressBar.width * (value / 100f)) - (binding.progressIndicator.width / 2f)
                binding.progressIndicator.translationX = translationX
            }
        }
        progressAnimator.start() // Animate progress bar and update streak UI.

        binding.txtStreakMain.text =
            "Current Streak: ${currentProgress.currentStreak}\nBest Streak: ${currentProgress.bestStreak}"
    }


    private fun showStreakEffect(streak: Int) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.streak_pulse).apply {
            repeatCount = 2
        }
        binding.txtStreakMain.startAnimation(anim)
    }
}
