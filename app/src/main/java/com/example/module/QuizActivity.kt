package com.example.module

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import com.example.module.databinding.ActivityQuizBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

// Added an optional property "codeSuffix" for word questions.
data class QuizQuestion(
    val type: String, // "number", "letter", or "word"
    val correctAnswer: String,
    val videoUrl: String,
    val options: List<String>,
    val codeSuffix: String? = null
)

data class QuizProgress(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val currentStreak: Int,
    val bestStreak: Int
)

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private var player: ExoPlayer? = null
    private lateinit var currentQuestion: QuizQuestion
    private val questions = mutableListOf<QuizQuestion>()
    private var currentProgress = QuizProgress(0, 0, 0, 0)
    private val sharedPref by lazy { getSharedPreferences("QuizProgress", MODE_PRIVATE) }
    private var questionTimer: CountDownTimer? = null
    private val questionTimeInMillis: Long = 15000

    // Flag to ensure only one answer is processed per question.
    private var questionAnswered = false
    // Flag to prevent quiz start until instructions are accepted.
    private var instructionsAccepted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.videoQuestion.player = exoPlayer
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        }

        setupQuiz()
        loadProgress()
        setupDraggableOptions()
        setupDropZone()
        setupClickListeners()
        showNextQuestion()

        // Instead of starting the quiz immediately, show instructions first.
        showQuizInstruction()
    }

    override fun onDestroy() {
        super.onDestroy()
        questionTimer?.cancel()
        player?.release()
    }

    /**
     * Displays the quiz instructions as a non-cancelable dialog.
     * The quiz will not begin until the user taps "OK".
     */
    private fun showQuizInstruction() {
        AlertDialog.Builder(this)
            .setTitle("Quiz Instructions")
            .setMessage(
                "Welcome to the Drag & Drop Quiz!\n\n" +
                        "Instructions:\n" +
                        "1. Drag the option you think is correct into the drop zone.\n" +
                        "2. Once dropped, your answer will be evaluated.\n" +
                        "3. You cannot proceed until you have read these instructions.\n\n" +
                        "Press OK to start the quiz."
            )
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                instructionsAccepted = true
                dialog.dismiss()
                showNextQuestion()  // Start quiz after instructions are accepted.
            }
            .show()
    }

    /**
     * Generates quiz questions from all modules.
     */
    private fun setupQuiz() {
        questions.addAll(generateNumberQuestions())
        questions.addAll(generateLetterQuestions())
        questions.addAll(generateWordQuestions())
        questions.shuffle()
        currentProgress = currentProgress.copy(totalQuestions = questions.size)
    }

    private fun generateNumberQuestions(): List<QuizQuestion> {
        return (0..9).map { number ->
            QuizQuestion(
                type = "number",
                correctAnswer = number.toString(),
                videoUrl = VideoRepository.getCloudinaryUrl("n$number", "front"),
                options = generateOptions(number.toString(), 0..9)
            )
        }
    }

    private fun generateLetterQuestions(): List<QuizQuestion> {
        return ('A'..'Z').map { letter ->
            QuizQuestion(
                type = "letter",
                correctAnswer = letter.toString(),
                videoUrl = VideoRepository.getCloudinaryUrl("l" + letter.lowercase(Locale.getDefault()), "front"),
                options = generateOptions(letter.toString(), ('A'..'Z').toList())
            )
        }
    }

    /**
     * Generate word questions using Tagalog display text and corresponding code suffix.
     */
    private fun generateWordQuestions(): List<QuizQuestion> {
        // Define a list of pairs: (Tagalog text, English code suffix)
        val wordPairs = listOf(
            Pair("Magandang Umaga", "goodmorning"),
            Pair("Magandang Hapon", "goodafternoon"),
            Pair("Magandang Gabi", "goodevening"),
            Pair("Ingat ka", "takecare"),
            Pair("Paalam", "bye"),
            Pair("Help", "help"),
            Pair("Doctor", "doctor"),
            Pair("Hospital", "hospital"),
            Pair("Police", "police"),
            Pair("Painful/Hurt", "painful"),
            Pair("Emergency", "emergency")
        )
        return wordPairs.map { pair ->
            QuizQuestion(
                type = "word",
                correctAnswer = pair.first, // Tagalog version
                videoUrl = VideoRepository.getCloudinaryUrl("w_" + pair.second, "front"),
                options = generateOptions(pair.first, wordPairs.map { it.first }),
                codeSuffix = pair.second
            )
        }
    }

    private fun <T> generateOptions(correct: T, pool: Iterable<T>): List<String> {
        val options = mutableListOf(correct.toString())
        val availableOptions = pool.toList() - correct
        while (options.size < 4 && availableOptions.isNotEmpty()) {
            val randomItem = availableOptions.shuffled().first().toString()
            if (!options.contains(randomItem)) options.add(randomItem)
        }
        while (options.size < 4) options.add(correct.toString())
        return options.shuffled().map {
            it.replace("_", " ").replaceFirstChar { ch -> ch.titlecase(Locale.getDefault()) }
        }
    }

    /**
     * Returns a local URI for the video if it exists; otherwise, returns the online URI.
     */
    private fun getLocalVideoUri(videoCode: String, viewType: String, onlineUrl: String): Uri {
        val videosDir = File(getExternalFilesDir(null), "videos")
        val fileName = "${videoCode}_${viewType}.mp4"
        val localFile = File(videosDir, fileName)
        return if (localFile.exists()) Uri.fromFile(localFile) else onlineUrl.toUri()
    }

    @SuppressLint("NewApi")
    private fun showNextQuestion() {
        // If instructions have not been accepted, do not start a question.
        if (!instructionsAccepted) return

        questionTimer?.cancel()
        questionAnswered = false
        if (questions.isEmpty()) {
            showResults()
            return
        }
        currentQuestion = questions.removeFirst()

        // Build video code based on question type.
        val videoCode = when (currentQuestion.type) {
            "number" -> "n" + currentQuestion.correctAnswer
            "letter" -> "l" + currentQuestion.correctAnswer.lowercase(Locale.getDefault())
            "word" -> "w_" + (currentQuestion.codeSuffix ?: currentQuestion.correctAnswer.replace(" ", "_"))
            else -> currentQuestion.correctAnswer
        }
        val localUri = getLocalVideoUri(videoCode, "front", currentQuestion.videoUrl)
        val mediaItem = MediaItem.fromUri(localUri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playbackParameters = PlaybackParameters(1.5f)
        player?.playWhenReady = true

        updateProgressUI()

        // Update draggable answer options.
        listOf(binding.option1, binding.option2, binding.option3, binding.option4)
            .forEachIndexed { index, textView ->
                textView.text = currentQuestion.options[index]
            }
        startQuestionTimer()
    }

    private fun startQuestionTimer() {
        questionTimer?.cancel()
        questionTimer = object : CountDownTimer(questionTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.txtTimer.text = secondsLeft.toString()
                binding.txtTimer.setTextColor(if (secondsLeft <= 5) Color.RED else Color.WHITE)
            }

            override fun onFinish() {
                checkAnswer("") // No answer given; mark as wrong.
                binding.txtTimer.text = "0"
            }
        }.start()
    }

    /**
     * Checks the answer once per question and proceeds to the next question.
     */
    private fun checkAnswer(selectedAnswer: String) {
        if (questionAnswered) return
        questionAnswered = true
        questionTimer?.cancel()

        val isCorrect = selectedAnswer.equals(currentQuestion.correctAnswer, ignoreCase = true)
        val newCurrentStreak = if (isCorrect) currentProgress.currentStreak + 1 else 0

        currentProgress = currentProgress.copy(
            correctAnswers = currentProgress.correctAnswers + if (isCorrect) 1 else 0,
            currentStreak = newCurrentStreak,
            bestStreak = maxOf(currentProgress.bestStreak, newCurrentStreak)
        )

        saveProgress()
        updateProgressUI()
        showFeedback(isCorrect)

        Handler(Looper.getMainLooper()).postDelayed({
            if (questions.isEmpty()) showResults() else showNextQuestion()
        }, 1000)
    }

    /**
     * Sets up draggable behavior for each answer option.
     */
    private fun setupDraggableOptions() {
        listOf(binding.option1, binding.option2, binding.option3, binding.option4).forEach { textView ->
            textView.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("option", textView.text.toString())
                view.startDragAndDrop(
                    clipData,
                    View.DragShadowBuilder(view),
                    null,
                    0
                )
                true
            }
            // Disable click as fallback.
            textView.isClickable = false
        }
    }

    /**
     * Sets up the drop zone for the draggable answer options.
     */
    private fun setupDropZone() {
        binding.dropZone.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                DragEvent.ACTION_DROP -> {
                    val droppedText = event.clipData.getItemAt(0).text.toString()
                    checkAnswer(droppedText)
                    true
                }
                else -> true
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        // Do not set onClick listeners on options; only draggable behavior is used.
    }

    private fun showFeedback(isCorrect: Boolean) {
        val color = if (isCorrect) Color.GREEN else Color.RED
        binding.videoQuestion.foreground =
            android.graphics.drawable.ColorDrawable(color).apply { alpha = 80 }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.videoQuestion.foreground = null
        }, 300)
    }

    private fun showResults() {
        // Trigger confetti if the user got a perfect score.
        if (currentProgress.correctAnswers == currentProgress.totalQuestions) {
            showConfetti()
        }

        // Define a passing threshold (70% of total questions)
        val passingThreshold = (currentProgress.totalQuestions * 0.7).toInt()
        val passed = currentProgress.correctAnswers >= passingThreshold

        // Construct message based on pass or fail
        val message = if (passed) {
            "🎉 Congratulations! 🎉\n" +
                    "You scored ${currentProgress.correctAnswers}/${currentProgress.totalQuestions}.\n\n" +
                    "✅ You have successfully completed the general quiz!\n" +
                    "More interactive games will be unlocked soon. Stay tuned! 🚀"
        } else {
            "❌ Oops! It seems you had a tough time.\n" +
                    "Keep practicing and try again!\n\n" +
                    "📊 Score: ${currentProgress.correctAnswers}/${currentProgress.totalQuestions}"
        }

        AlertDialog.Builder(this)
            .setTitle("Quiz Complete!")
            .setMessage(message)
            .setPositiveButton(if (passed) "Finish" else "Retry") { _, _ ->
                if (passed) {
                    finish()
                } else {
                    recreate()
                }
            }
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

    @SuppressLint("SetTextI18n")
    private fun updateProgressUI() {
        val totalQuestions = currentProgress.totalQuestions
        val questionsAttempted = totalQuestions - questions.size
        val progressPercent = ((questionsAttempted.toFloat() / totalQuestions) * 100).toInt()

        binding.txtScore.text = "Score: ${currentProgress.correctAnswers}"

        val prevProgress = binding.progressBar.progress
        val progressAnimator = ValueAnimator.ofInt(prevProgress, progressPercent).apply {
            duration = 500
            interpolator = OvershootInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                binding.progressBar.progress = value
                binding.txtProgressPercent.text = "$value%"
                val translationX =
                    (binding.progressBar.width * (value / 100f)) - (binding.progressIndicator.width / 2f)
                binding.progressIndicator.translationX = translationX
            }
        }
        progressAnimator.start()

        binding.txtStreakMain.text =
            "Current Streak: ${currentProgress.currentStreak}\nBest Streak: ${currentProgress.bestStreak}"
    }
}
