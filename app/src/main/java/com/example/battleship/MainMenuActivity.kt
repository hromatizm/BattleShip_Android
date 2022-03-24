package com.example.battleship

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var buttonStart: Button
    private lateinit var titleAnimation: ObjectAnimator
    private lateinit var buttonAnimation: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Делаем фулл-скрин
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main_menu)

        // Анимация
        title = findViewById(R.id.title)
        buttonStart = findViewById(R.id.button_start)

        titleAnimation = ObjectAnimator
            .ofFloat(
                title, "translationY", -100f, 150f
            ).setDuration(900)
        titleAnimation.start()

        buttonAnimation = ObjectAnimator
            .ofFloat(
                buttonStart, "translationY", 1000f, 50f
            ).setDuration(900)
        buttonAnimation.start()

    }

    fun installBoats(view: View) {
        val intent = Intent(this, InstallBoatsActivity::class.java)
        startActivity(intent)
    }
}