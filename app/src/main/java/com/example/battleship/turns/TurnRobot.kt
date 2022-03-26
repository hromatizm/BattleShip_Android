package com.example.battleship.turns

import android.util.Log
import android.widget.TextView
import com.example.battleship.MyApplication
import com.example.battleship.R
import com.example.battleship.coordinates.Coordinate
import com.example.battleship.coordinates.GetCoord
import com.example.battleship.seabutton.SeaButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// Осуществляет ходы
class TurnRobot(private val statusText: TextView) :
    Turn {
    val app = MyApplication.getAppInstance()
    val techField = app.humanTechField
    val buttonMap = techField.buttonMap

    override suspend fun makeTurn(coord: Coordinate?): Pair<Boolean, Int> { // true - игра продложается
        Log.d("zzz", app.turnsCounter.toString())
        withContext(Dispatchers.Main) {
            statusText.text = statusText.context.getString(R.string.turn_number, app.turnsCounter)
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
                        statusText.text =
                            statusText.context.getString(R.string.turn_dead, app.turnsCounter)
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
                        withContext(Dispatchers.Main) {
                            statusText.text =
                                statusText.context.getString(R.string.game_over, app.turnsCounter)
                        }
                        return false to app.turnsCounter
                    }
                } else { // Если попал, но остались жизни
                    withContext(Dispatchers.Main) {
                        statusText.context.getString(R.string.turn_scored, app.turnsCounter)
                    }
                }
            }
            targetOnTechField <= 0 -> { // Если поле помечено как "мимо" или как сбитый корабль
                println("Нет смысла стрелять в эту клетку")
                return makeTurn(null) // Рекурсия
            }
            else -> {
                withContext(Dispatchers.Main) {
                    statusText.text =
                        statusText.context.getString(R.string.turn_fail, app.turnsCounter)
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
