package com.example.battleship

import android.content.Intent
import android.graphics.Paint
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.RobotButton
import com.example.battleship.seabutton.SeaButton
import com.example.battleship.turns.TurnHuman
import com.example.battleship.turns.TurnRobot
import com.example.battleship.turns.TurnSequence
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TurnsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var app: MyApplication
    private lateinit var root: View
    private lateinit var humanField: LinearLayout
    private lateinit var robotField: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var turnSequence: TurnSequence
    private lateinit var indexToggleButton: Button


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

        root = findViewById(R.id.root)
        humanField = findViewById(R.id.human_field)
        robotField = findViewById(R.id.robot_field)
        statusText = findViewById(R.id.status_text)
        indexToggleButton = findViewById(R.id.index_toggle)

        app.fitScreenSize(humanField)
        app.fitScreenSize(robotField)

        app.restoreHumanButtonMap()
        app.humanTechField.fieldUiUpdate()
        app.restoreRobotButtonMap()
        app.robotTechField.fieldUiUpdate()

        val robotTurns = TurnRobot(statusText)
        val humanTurns = TurnHuman(statusText)

        turnSequence = TurnSequence(this, this, robotTurns, humanTurns)
        turnSequence.startListenButtons()
        app.turnSequence = turnSequence

        if (app.isFirstStartOfTurnsActivity){
            GlobalScope.launch { startOver() }
            app.isFirstStartOfTurnsActivity = false
        }
    }

    // Вкл - Выкл отображения индекса вокруг поля
    fun indexToggle(view: View) {
        app.isIndex = !app.isIndex
        setOptionsButtonsFont()
        app.toggleIndex(humanField)
        app.fitScreenSize(humanField)
        app.toggleIndex(robotField)
        app.fitScreenSize(robotField)
    }

    override fun onClick(view: View?) {
        when (view) {
            is RobotButton -> {
                println("${(view as SeaButton).coord.letter} ${(view as SeaButton).coord.number}")
                GlobalScope.launch { turnSequence.humanTurn(view) }
            }
        }
    }

    private suspend fun startOver() {
        turnSequence.robotTurn()
//        startActivity(
//            Intent(this, MainMenuActivity::class.java)
//        )
    }
    private fun hideSystemUI(mainContainer: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, mainContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setOptionsButtonsFont() {
        if (app.isIndex) {
            indexToggleButton.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        } else {
            indexToggleButton.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI(root)
        app.fitScreenSize(humanField)
        app.toggleIndex(humanField)
        app.fitScreenSize(robotField)
        app.toggleIndex(robotField)
        setOptionsButtonsFont()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        Thread.sleep(1000)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        HumanButton.buttonCounter = 0
        app.saveHumanButtonMap()
        RobotButton.buttonCounter = 0
        app.saveRobotButtonMap()
    }
}