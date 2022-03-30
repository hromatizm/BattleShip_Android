package com.example.battleship

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceManager
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.RobotButton
import com.example.battleship.seabutton.SeaButton
import com.example.battleship.turns.TurnHuman
import com.example.battleship.turns.TurnRobot
import com.example.battleship.turns.TurnSequence
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TurnsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var app: MyApplication
    lateinit var rootView: View
    private lateinit var humanFieldView: LinearLayout
    private lateinit var robotFieldView: LinearLayout
    private lateinit var statusTextRobot: TextView
    private lateinit var statusTextHuman: TextView
    private lateinit var turnNumber: TextView
//    var textOfStatusTextRobot = ""
//    var textOfStatusTextHuman = ""
//    var textOfTurnNumber = ""
    private lateinit var turnSequence: TurnSequence
    lateinit var animator: ValueAnimator

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

        rootView = findViewById(R.id.root)
        humanFieldView = findViewById(R.id.human_field)
        robotFieldView = findViewById(R.id.robot_field)
        statusTextRobot = findViewById(R.id.status_text_robot)
        statusTextHuman = findViewById(R.id.status_text_human)
        turnNumber = findViewById(R.id.turn_number)

        app.humanFieldView = humanFieldView
        app.robotFieldView = robotFieldView

        app.statusTextHuman = statusTextHuman
        app.statusTextRobot = statusTextRobot

        app.fitScreenSize(humanFieldView, app.isHumanFieldActive)
        app.fitScreenSize(robotFieldView, app.isRobotFieldActive)

        app.restoreHumanButtonMap()
        app.humanTechField.fieldUiUpdate()
        app.restoreRobotButtonMap()
        app.robotTechField.fieldUiUpdate()

        val robotTurns = TurnRobot(statusTextRobot, turnNumber, this)
        val humanTurns = TurnHuman(statusTextHuman, turnNumber, this)

        turnSequence = TurnSequence(this, this, robotTurns, humanTurns)
        turnSequence.startListenButtons()
        app.turnSequence = turnSequence
        animator = app.turnsActivityAnimator
//        app.turnActivityAnimationInit(humanFieldView,robotFieldView)

//        savedInstanceState?.run {
//            textOfStatusText = getString(STATUS_TEXT).toString()
//        }

//        statusTextRobot.text = textOfStatusTextRobot
//        statusTextHuman.text = textOfStatusTextHuman
//        turnNumber.text = textOfTurnNumber

        if (app.isFirstStartOfTurnsActivity) {
            MainScope().launch { startOver() }
            app.isFirstStartOfTurnsActivity = false
        }
    }

//    private fun animatorInit() {
//        animator.setObjectValues(app.shrunkSeaButtonSizeWithoutIndex, app.seaButtonSizeWithoutIndex)
//        animator.addUpdateListener { animation ->
//            val seaLines = app.getAllChildren(humanFieldView)
//            for (line in seaLines) {
//                app.getAllChildren(line as LinearLayout).forEach {
//                    it.layoutParams.height = animation.animatedValue as Int
//                    it.layoutParams.width = it.layoutParams.height
//                }
//                humanFieldView.layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                ).apply {
//                    gravity = if (!app.isIndex) {
//                        Gravity.CENTER_HORIZONTAL
//                    } else {
//                        Gravity.START
//                    }
//                }
//                app.getAllChildren(humanFieldView).forEach { it.requestLayout() }
//            }
//        }
//
//        animator.setEvaluator(object : TypeEvaluator<Int> {
//            // fraction: 0 - начало анимации, 1 - конец
//            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
//                return (fraction * endValue).toInt()
//            }
//        })
//    }

    // Вкл - Выкл отображения индекса вокруг поля
    fun indexToggle(view: View) {

//        app.isIndex = !app.isIndex
////        setOptionsButtonsFont()
//        app.toggleIndex(humanFieldView)
//        app.fitScreenSize(humanFieldView, app.isHumanFieldActive)
//        app.toggleIndex(robotFieldView)
//        app.fitScreenSize(robotFieldView, app.isRobotFieldActive)
    }

    override fun onClick(view: View?) {
        when (view) {
            is RobotButton -> {
                println("${(view as SeaButton).coord.letter} ${(view as SeaButton).coord.number}")
                MainScope().launch { turnSequence.humanTurn(view) }
            }
        }
    }

    private suspend fun startOver() {
        turnSequence.robotTurn()
    }

    private fun hideSystemUI(mainContainer: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, mainContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

//    private fun setOptionsButtonsFont() {
//        if (app.isIndex) {
//            indexToggleButton.apply {
//                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
//            }
//        } else {
//            indexToggleButton.apply {
//                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            }
//        }
//    }

    fun showSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI(rootView)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        app.isIndex = preferences.getBoolean("show_index", true)
        app.isSoundActive = preferences.getBoolean("sound_settings", true)

        app.fitScreenSize(humanFieldView, app.isHumanFieldActive)
        app.toggleIndex(humanFieldView)
        app.fitScreenSize(robotFieldView, app.isRobotFieldActive)
        app.toggleIndex(robotFieldView)

        statusTextRobot.text = app.textForStatusTextHuman
        statusTextHuman.text = app.textForStatusTextRobot
        turnNumber.text = app.textForTurnNumber

//        setOptionsButtonsFont()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun onPause() {
        Thread.sleep(1000)
        app.textForStatusTextHuman = statusTextHuman.text.toString()
        app.textForStatusTextRobot = statusTextRobot.text.toString()
        app.textForTurnNumber = turnNumber.text.toString()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        HumanButton.buttonCounter = 0
        app.saveHumanButtonMap()
        RobotButton.buttonCounter = 0
        app.saveRobotButtonMap()
//        Log.d("zzz", statusText.text.toString())
//        Log.d("zzz", textOfStatusText)
    }

    fun goBackFromTurns(view: View) {
        onBackPressed()
    }

}