package com.example.battleship

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.getY

class MainMenuActivity : AppCompatActivity() {

    private lateinit var app: MyApplication
    private lateinit var title: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonNewGame: Button
    private lateinit var buttonExit: Button
    private lateinit var titleAnimation: ObjectAnimator
    private lateinit var buttonStartAnimation: ObjectAnimator
    private lateinit var buttonNewGameAnimation: ObjectAnimator
    private lateinit var buttonExitAnimation: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = MyApplication.getAppInstance()

        // Делаем фулл-скрин
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        // Прозрачный Navigation bar:
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)


        // Скрываем Tool bar:
        supportActionBar?.hide()


        setContentView(R.layout.activity_main_menu)

///////////////////////////////////////////////////////////////

        // Анимация
        title = findViewById(R.id.title)
        buttonStart = findViewById(R.id.button_start)
        buttonNewGame = findViewById(R.id.button_new_game)
        buttonExit = findViewById(R.id.button_exit)
        val buttonStartY = getY(buttonStart)
        val buttonNewGameY = getY(buttonNewGame)
        val buttonExitY = getY(buttonExit)

        titleAnimation = ObjectAnimator
            .ofFloat(
                title, "translationY",  (-1) * app.displayHeight.toFloat() , buttonStartY
            ).setDuration(1000)

        buttonStartAnimation = ObjectAnimator
            .ofFloat(
                buttonStart, "translationY", app.displayHeight.toFloat(), buttonStartY
            ).setDuration(800)

        buttonNewGameAnimation = ObjectAnimator
            .ofFloat(
                buttonNewGame, "translationY", app.displayHeight.toFloat(), buttonNewGameY
            ).setDuration(900)

        buttonExitAnimation = ObjectAnimator
            .ofFloat(
                buttonExit, "translationY", app.displayHeight.toFloat(), buttonExitY
            ).setDuration(1000)

    }

    fun onMainButtonClick(view: View) {
        if (app.isHumanBoatInstalled) {
            startActivity(
                Intent(this, TurnsActivity::class.java)
            )
        } else {
            startActivity(
                Intent(this, InstallBoatsActivity::class.java)
            )
        }
    }

    override fun onStart() {
        if (!app.isFirstStartOfMainMenuActivity) {
            buttonStart.text = getString(R.string.button_resume)
            buttonNewGame.visibility = View.VISIBLE
        }
        titleAnimation.start()
        buttonStartAnimation.start()
        buttonNewGameAnimation.start()
        buttonExitAnimation.start()
        super.onStart()
    }

    fun onExitButtonClick(view: View) {
        startActivity(
            Intent(this, ConfirmExitActivity::class.java)
        )
    }

    fun onNewGameButtonClick(view: View) {
        startActivity(
            Intent(this, ConfirmNewGameActivity::class.java)
        )
    }
}