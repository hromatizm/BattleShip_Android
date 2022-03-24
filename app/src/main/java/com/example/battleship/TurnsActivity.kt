package com.example.battleship

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.battleship.turns.Turn
import com.example.battleship.turns.TurnSequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TurnsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var app: MyApplication
    private lateinit var humanField: LinearLayout
    private lateinit var robotField: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var startButton: Button
    private lateinit var turnSequence: TurnSequence

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

        setContentView(R.layout.activity_turns)

/////////////////////////////////////////////////////////////////////////////////////

        humanField = findViewById(R.id.human_field)
        robotField = findViewById(R.id.robot_field)
        statusText = findViewById(R.id.status_text)
        startButton = findViewById(R.id.start)

        startButton.setOnClickListener(this)

        app.fitScreenSize(humanField)
        app.fitScreenSize(robotField)

        app.restoreHumanButtonMap()
        app.humanTechField.humanFieldUiUpdate()

        val robotTurns = Turn(app.humanTechField, statusText)
        turnSequence = TurnSequence(listOf(robotTurns))
        Log.d("zzz", "Turns")
        statusText.text = "Turns"
        Log.d("zzz", statusText.id.toString())

    }

    override fun onClick(v: View?) {
        GlobalScope.launch {
            startOver()
        }
    }

    suspend fun startOver() {
        turnSequence.start()
        startActivity(
            Intent(this, MainMenuActivity::class.java)
        )
    }
}