package com.example.battleship.turns

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import com.example.battleship.MainMenuActivity
import com.example.battleship.MyApplication
import com.example.battleship.TurnsActivity
import com.example.battleship.coordinates.Coordinate
import com.example.battleship.seabutton.RobotButton
import kotlinx.coroutines.*

// Поочередные ходы. Получает в качестве параметра коллекцию типа Turn
class TurnSequence(
    private val context: Context?,
    var parent: View.OnClickListener?,
//    val turnsActivity: TurnsActivity,
    val turnRobot: TurnRobot,
    val turnHuman: TurnHuman,
) {
    var isGoingOn = true
    var result = false to 0 // Если меньше 0, то ход продолжается (паподание в корабль)
    // Если больше 0, то это кол-во сделанных ходов

    val app = MyApplication.getAppInstance()

    // Ходы делаются по очереди до тех пор, пока не неступит GAME OVER в каком-то из Turn

    suspend fun robotTurn() {
        app.robotTechField.makeUiGray()
        when {
            app.isLandscape -> app.setRobotFieldActive()
            else -> app.setHumanFieldActive()
        }
        if (app.isFirstTurn || app.isLandscape) {
            app.fitScreenSize(app.humanFieldView, app.isHumanFieldActive)
            app.robotFieldView?.let { app.fitScreenSize(it, app.isRobotFieldActive) }
            app.isFirstTurn = false
        } else {
            withContext(Dispatchers.Main) {
                app.robotFieldView?.let { app.turnActivityAnimationInit(app.humanFieldView, it) }
                app.turnsActivityAnimator.start()
                app.turnsActivityAnimator.doOnEnd { app.robotTechField.makeUiGray() }
            }
        }
        delay(1_000)
        app.turnsCounter++
        result = turnRobot.makeTurn(null)
        isGoingOn = result.first
        if (isGoingOn) {
            startListenButtons()
            app.setRobotFieldActive()
            if (app.isLandscape) {
                app.fitScreenSize(app.humanFieldView, app.isHumanFieldActive)
                app.robotFieldView?.let { app.fitScreenSize(it, app.isRobotFieldActive) }
                app.robotTechField.fieldUiUpdate()
            } else {
                withContext(Dispatchers.Main) {
                    app.robotFieldView?.let {
                        app.turnActivityAnimationInit(it, app.humanFieldView)
                    }
                    app.turnsActivityAnimator.start()
                    app.turnsActivityAnimator.doOnEnd {
                        app.robotTechField.fieldUiUpdate()
                    }
                }
            }
        } else {
            stopListenButtons()
            return
//            delay(10_000)
//            runMainMenuActivity()
        }
    }

    suspend fun humanTurn(view: View?) {
        val button = view as RobotButton
        val turnCoord: Coordinate
        if (button.getIsFail() || button.getIsDead()) {
            return
        } else {
            turnCoord = button.coord
            result = turnHuman.makeTurn(turnCoord)
            isGoingOn = result.first
        }
        if (!isGoingOn) {
            stopListenButtons()
            return
//            delay(5000)
//            runMainMenuActivity()
        } else if (result.second >= 0) {
            stopListenButtons()
            robotTurn()
        }
    }

    private fun runMainMenuActivity() {
        val intent = Intent(context, MainMenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    fun startListenButtons() {
        for (button in RobotButton.buttonMap.values)
            button.setOnClickListener(parent)
    }

    fun stopListenButtons() {
        for (button in RobotButton.buttonMap.values)
            button.setOnClickListener(null)
    }
}
