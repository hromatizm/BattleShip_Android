package com.example.battleship.boats

import android.content.Context
import android.widget.TextView
import com.example.battleship.MyApplication
import com.example.battleship.coordinates.Coordinate
import com.example.battleship.coordinates.GetCoord
import com.example.battleship.fields.TechField4Algorithm
import kotlin.properties.Delegates

// Расстановщик кораблей на поле
class BoatInstaller(
    private val factory: BoatFactory,
    private val welcomeText: TextView?,
    private val context: Context?
) {
    private val techField = factory.techField
    val app = MyApplication.getAppInstance()

    var newCoordForTurn: Coordinate? by Delegates.observable(null) { _, _, new ->
        if (new?.letter != null) {

        }
    }

    fun installHuman(coord: Coordinate) {
        val boat = factory.makeBoat(app.listOfHumanBoatsId.removeFirst(), coord, app.isVertical)
        if (app.listOfHumanBoatsId.isEmpty()) {
            app.isHumanBoatInstalled = true
            techField.update()

        }
        with(techField) {  // На ТехПоле
            boatList[boat.id] = boat // Добавлем в коллекцию кораблей
            update() // Обновляем ТехПоле
            aliveBoatCounter++ // Инкремент счетчика кораблей
            boatsAndFramesCoordsList.addAll(boat.coordinates)
            boatsAndFramesCoordsList.addAll(boat.frame)
        }

    }

    fun printWelcome(id: Int) {
        val size = id / 10 // Размер корабля - первая цифра id
        val num = id - size * 10 // Номер корабля данного размера - вторая цифра id
        val boatNumber = when {
            size < 4 -> "$num-й "
            else -> ""
        }
        welcomeText?.text = ("Поставьте $boatNumber $size-палубный...")
    }

    fun printReady() {
        welcomeText?.text = ("Корабли расставлены")
    }

//    fun installAllHuman(
//        welcomeText: TextView
//    ) {
//        while (app.listOfHumanBoatsId.isNotEmpty()) {
//            Log.d("zzz", app.listOfHumanBoatsId.toString())
////            installHuman(app.listOfHumanBoatsId[0])
//            app.listOfHumanBoatsId.removeFirst()
//        }
//        MainScope().launch {
//            welcomeText.text = "Готово!"
//        }
//        techField.print()
//    }

    fun printError() {
        println("Не корректные координаты")
    }

    // Установка одного корабля. Принимает в качесвте параметра только id корабля
    fun installRobot(id: Int): Pair<Boolean, Boat> {
        var boat: Boat
        var testCount = 0
        do {
            testCount++
            val readPair = GetCoord().boatRobot() // Считываем пару
            val coordBegin = readPair.first // Первый элемент пары - начальная координата корабля
            val isVertical = readPair.second // Второй элемент пары - признак вертикальности
            boat = factory.makeBoat(id, coordBegin, isVertical) // Через фабрику делаем корабль

            if (techField.boatList.isNotEmpty() && testCount > 100) {
                println("Ups!!")
                return false to boat
            }
        } while (!testBoat(boat)) // Проверка - может ли корабль с такими координатами корректно стоять на поле.
        // Если проверка пройдена, то ставим его на поле:
        with(techField) {  // На ТехПоле
            boatList[boat.id] = boat // Добавлем в коллекцию кораблей
            update() // Обновляем ТехПоле
            aliveBoatCounter++ // Инкремент счетчика кораблей
        }

        techField.boatsAndFramesCoordsList.addAll(boat.coordinates)
        techField.boatsAndFramesCoordsList.addAll(boat.frame)
        return true to boat
    }

    // Принимает список id кораблей для установки:

    fun installAllRobot(boatsIdToInstall: Collection<Int>) { // Установка всех нужных кораблей:
        for (id in boatsIdToInstall) {
            installRobot(id)
        }
    }

    // Проверка - может ли корабль с такими координатами корректно стоять на поле:
    fun testBoat(boat: Boat): Boolean {
        if (boat.coordEnd.letter > 10 || boat.coordEnd.number > 10) { // Последняя координата не больше 10
            return false
        }

        if (techField !is TechField4Algorithm && techField.boatList.isEmpty()) // Если ставиться первый корабль, то ОК.
            return true

        for (coord in boat.coordinates) {  // Координаты корбля
            for (boatOnField in techField.boatList) { // Уже стоящие корабли
                // Сравниваем координаты корабля с координатами поставленных кораблей:
                for (coordBF in boatOnField.value.coordinates) {
                    if (coord == coordBF)
                        return false
                }
                // Сравниваем координаты корабля с координатами рамок поставленных кораблей:
                for (coordFrame in boatOnField.value.frame) {
                    if (coord == coordFrame)
                        return false
                }
            }
            // Сравниваем координаты корабля с координатами сбитых кораблей:
            for (coordScored in techField.scoredList) {
                if (coord == coordScored) {
                    return false
                }
            }
            // Сравниваем координаты корабля с координатами "мимо"
            for (coordFail in techField.failList) {
                if (coord == coordFail) {
                    return false
                }
            }
        }
        return true
    }
}