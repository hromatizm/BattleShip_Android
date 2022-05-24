package com.example.battleship.turns

import android.content.Context
import android.media.MediaActionSound
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.battleship.MyApplication
import com.example.battleship.R
import com.example.battleship.coordinates.Coordinate
import com.example.battleship.coordinates.GetCoord
import com.example.battleship.seabutton.SeaButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// Осуществляет ходы
class TurnRobot(
    private val statusText: TextView,
    private val turnNumber: TextView,
    val context: Context
) : Turn {
    val app = MyApplication.getAppInstance()
    val techField = app.humanTechField
    val buttonMap = techField.buttonMap
    var failSound = MediaPlayer.create(context, R.raw.water_bubbling)
    var explosionShortSound = MediaPlayer.create(context, R.raw.explosion_short)
    var explosionLongSound = MediaPlayer.create(context, R.raw.explosion_long)

    override suspend fun makeTurn(turnCoord: Coordinate?): Pair<Boolean, Int> { // true - игра продложается
        withContext(Dispatchers.Main) {
            turnNumber.text = context.getString(R.string.turn_number, app.turnsCounter)
            app.statusTextHuman.text = ""
        }

//            if (isHuman) View.topLabel.text = "Ваш ход"
        var isScored = false // Признак, что "попал" в корабль
        val turnCoord =
//            if (isHuman) GetCoord().turnHuman() else
            GetCoord().turnRobot(techField) // Получаем координату хода
        val targetOnTechField =
            techField.fieldArray[turnCoord.number][turnCoord.letter] // Код на ТехПоле с такой координатой
        val targetButton = buttonMap[turnCoord.id] as SeaButton

        when {
            targetOnTechField in 11..49 -> { // Если код на ТехПоле - это id корабля
                isScored = true // то "попал"
                techField.scoredList.add(turnCoord) // Добавлем координату в коллекцию, где клетки, в которые "попал"
                targetButton.setIsDead()
                val boat =
                    techField.boatList[targetOnTechField] // Из коллекции кораблей берем корабль по id
                boat?.liveMinus() // Сокращаем жизнь
                if (boat?.lives!! == 0) { // Если убит
                    withContext(Dispatchers.Main) {
                        if (app.isSoundActive) {
                            if (explosionLongSound.isPlaying) {
                                explosionLongSound.stop()
                                explosionLongSound.release()
                                explosionLongSound =
                                    MediaPlayer.create(context, R.raw.explosion_long)
                            }
                            explosionLongSound.start()
                            if (app.isVibrationActive) {
                                app.vibrateLong()
                            }
                        }
                        statusText.text =
                            "Убил"
                    }

                    for (coordF in boat.frame) { // добавляем рамку корабля в коллекцию с полями "мимо"
                        techField.failList.add(coordF)
                        if (coordF.letter in 1..10 && coordF.number in 1..10) {
                            val frameButton = buttonMap[coordF.id] as SeaButton
                            frameButton.setIsFail()
                        }
                    }
                    techField.aliveBoatCounter-- // Декремент счетчика живых кораблей
                    if (techField.aliveBoatCounter == 0) { // Если живых кораблей не осталось
                        with(techField) {
                            update()
                            lastTurnCoord = turnCoord
                            withContext(Dispatchers.Main) {
                                fieldUiUpdate()
                            }
                            delay(1_000)
                        }
                        app.isGameOver = true
                        statusText.visibility = View.GONE
                        app.statusTextHuman.visibility = View.GONE
                        turnNumber.setTextColor(
                            ContextCompat.getColor(app.applicationContext, R.color.red)
                        )
                        withContext(Dispatchers.Main) {
                            val params = turnNumber.layoutParams
                            params.height = 200
                            turnNumber.layoutParams = params
                            turnNumber.text =
                                context.getString(R.string.game_over, app.turnsCounter)
                        }
                        app.isHumanWin = false
                        return false to app.turnsCounter
                    }
                } else { // Если попал, но остались жизни
                    withContext(Dispatchers.Main) {

                        if (app.isSoundActive) {
                            if (explosionShortSound.isPlaying) {
                                explosionShortSound.stop()
                                explosionShortSound.release()
                                explosionShortSound =
                                    MediaPlayer.create(context, R.raw.explosion_short)
                            }
                            explosionShortSound.start()
                            if (app.isVibrationActive) {
                                app.vibrateShort()
                            }
                        }
                        statusText.text =
                            "Ранил"
                    }
                }
            }
            targetOnTechField <= 0 -> { // Если поле помечено как "мимо" или как сбитый корабль
                println("Нет смысла стрелять в эту клетку")
                return makeTurn(null) // Рекурсия
            }
            else -> {
                withContext(Dispatchers.Main) {
                    if (app.isSoundActive) {
                        if (failSound.isPlaying) {
                            failSound.stop()
                            failSound.release()
                            failSound = MediaPlayer.create(context, R.raw.water_bubbling)
                        }
                        failSound.start()
                    }
                    statusText.text =
                        "Мимо"
                }
                targetButton.setIsFail()
                techField.failList.add(turnCoord)
            }
        }
        // В конце попытки хода перерисовываем интерфейс
        with(techField) {
            update()
            lastTurnCoord = turnCoord
            withContext(Dispatchers.Main) {
                fieldUiUpdate()
            }

            delay(1_000)
        }
        // Если "попал" и остались живые корабли
        if (isScored && techField.aliveBoatCounter > 0) {
            return makeTurn(null) // Рекурсия
        }
        return true to app.turnsCounter
    }
}
