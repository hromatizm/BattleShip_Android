package com.example.battleship.turns

import android.util.Log
import android.widget.TextView
import com.example.battleship.coordinates.GetCoord
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.SeaButton
import com.example.battleship.fields.TechField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Осуществляет ходы
class Turn(val techField: TechField, private val statusText: TextView) {

    var counter = 0 // Счетчик ходов

    suspend fun makeRobotTurn(): Pair<Boolean, Int> { // true - игра продложается
        counter++
        Log.d("zzz", "Ход № $counter")
        Log.d("zzz", statusText.id.toString())
        withContext(Dispatchers.Main) {
            statusText.text = "Ход № $counter"
        }
//            if (isHuman) View.topLabel.text = "Ваш ход"
        var isScored = false // Признак, что "попал" в корабль
        val turnCoord =
//            if (isHuman) GetCoord().turnHuman() else
            GetCoord().turnRobot(techField) // Получаем координату хода
        val targetOnTechField =
            techField.fieldArray[turnCoord.number][turnCoord.letter] // Код на ТехПоле с такой координатой
        val targetButton = HumanButton.buttonMap[turnCoord.id] as SeaButton

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
                        statusText.text = "Ход № $counter - Убит"
                    }

                    for (coord in boat.frame) { // добавляем рамку корабля в коллекцию с полями "мимо"
                        techField.failList.add(coord)
                        if(coord.letter in 1..10 && coord.number in 1..10) {
                            val frameButton = HumanButton.buttonMap[coord.id] as SeaButton
                            frameButton.setIsFail()
                        }
                    }
                    techField.aliveBoatCounter-- // Декремент счетчика живых кораблей
                    if (techField.aliveBoatCounter == 0) { // Если живых кораблей не осталось
                        with(techField) {
                            update()
                            lastTurnCoord = turnCoord
                            withContext(Dispatchers.Main) {
                                humanFieldUiUpdate()
                            }
                            Thread.sleep(1000)
                        }
                        withContext(Dispatchers.Main) {
                            statusText.text = "GAME OVER  Сделано ходов: $counter"
                        }
                        return false to counter
                    }
                } else { // Если попал, но остались жизни
                    withContext(Dispatchers.Main) {
                        statusText.text = "Ход № $counter - Ранен"
                    }
                }
            }
            targetOnTechField <= 0 -> { // Если поле помечено как "мимо" или как сбитый корабль
                println("Нет смысла стрелять в эту клетку")
                counter--
                return makeRobotTurn() // Рекурсия
            }
            else -> {
                withContext(Dispatchers.Main) {
                    statusText.text = "Ход № $counter - Мимо"
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
                humanFieldUiUpdate()
            }
            Thread.sleep(1000)
        }
        // Если "попал" и остались живые корабли
        if (isScored && techField.aliveBoatCounter > 0) {
            counter--
            return makeRobotTurn() // Рекурсия
        }
        return true to counter
    }
}
