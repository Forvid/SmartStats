package ru.netology.nmedia.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stats = findViewById<StatsView>(R.id.stats).apply {
            data = listOf(500f, 300f, 200f, 100f)
        }

        // запуск ValueAnimator
        stats.startCircleAnimation()
    }
}
