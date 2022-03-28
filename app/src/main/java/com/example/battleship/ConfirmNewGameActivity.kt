package com.example.battleship

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.RobotButton

class ConfirmNewGameActivity : AppCompatActivity() {

    private lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = MyApplication.getAppInstance()

        // Делаем фул-скрин
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar?.hide()

        setContentView(R.layout.activity_confirm_new_game)
    }

    fun newGame(view: View) {
        app.resetGame()
        startActivity(
            Intent(this, InstallBoatsActivity::class.java)
        )
    }

    fun returnToGame(view: View) {
        onBackPressed()
    }
}