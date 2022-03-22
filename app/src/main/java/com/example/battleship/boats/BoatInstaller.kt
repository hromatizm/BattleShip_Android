package boats

import android.widget.TextView
import com.example.battleship.coordinates.GetCoord
import com.example.battleship.seabutton.HumanButton
import coordinates.HumanCoordGetterController

import fields.TechField4Algorithm
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// Расстановщик кораблей на поле
class BoatInstaller(
    private val factory: BoatFactory,
    private val isHuman: Boolean,
    private val coordGetterController : HumanCoordGetterController?
) {
    lateinit var welcomeText: TextView
    val techField = factory.techField

    // Приглашение поставить корабль:
    fun printWelcome(size: Int, num: Int, view: TextView) {

        welcomeText = view
        val boatNumber = when {
            size < 4 -> "$num-й "
            else     -> ""
        }
        welcomeText.text = ("Поставьте $boatNumber $size-палубный...")
    }

    fun printError() {
        println("Не корректные координаты")
    }

    // Установка одного корабля. Принимает в качесвте параметра только id корабля
    suspend fun install(id: Int, welcomeText: TextView?): Pair<Boolean, Boat> {
        val size = id / 10 // Размер корабля - первая цифра id
        val num = id - size * 10 // Номер корабля данного размера - вторая цифра id
        var boat: Boat
        var testCount = 0
        do {

            testCount++
            if (isHuman) {
                coordGetterController?.startListenButtons()
                MainScope().launch {
                    welcomeText?.run {
                        printWelcome(size, num, welcomeText)
                    }
                }

            } // Сообщение с приглашением поставить корабль
            val readPair =
                if (isHuman) GetCoord().boatHuman(id) else GetCoord().boatRobot() // Считываем пару
            val coordBegin = readPair.first // Первый элемент пары - начальная координата корабля
            val isVertical = readPair.second // Второй элемент пары - признак вертикальности
            boat = factory.makeBoat(id, coordBegin, isVertical) // Через фабрику делаем корабль

            if (isHuman && !testBoat(boat))
                printError()
            if (techField.boatList.size > 0 && testCount > 100) {
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
        if (isHuman) {
            for (boat in techField.boatList) // Перебираем коллекцию кораблей из ТехПоля
                for (coord in boat.value.coordinates) {
//                    SeaController.humanButtonMap.getValue(coord.id).style =
//                        "-fx-padding: 0.0 3.0 0.0 0.0;" +
//                                "-fx-text-alignment: center;" +
//                                "-fx-border-color: darkblue;" +
//                                "-fx-background-color: radial-gradient(focus-distance 0% , center 50% 50% , radius 100% , skyblue, blue);" +
//                                "-fx-background-radius: 6;" +
//                                "-fx-border-radius: 6;" +
//                                "-fx-border-width: 1px;" +
//                                "-fx-font-size: 1.7em;" +
//                                "-fx-pref-width: 29px;" +
//                                "-fx-pref-height: 29px;"
//
                }
        }
        techField.boatsAndFramesCoordsList.addAll(boat.coordinates)
        techField.boatsAndFramesCoordsList.addAll(boat.frame)
        return true to boat
    }

    // Принимает список id кораблей для установки:
    suspend fun installAllHuman(
        boatsIdToInstall: Collection<Int>,
        welcomeText: TextView
    ) { // Установка всех нужных кораблей:
//        for (button in HumanButton.buttonMap.values) {
//            button.addEventFilter(
//                MouseEvent.MOUSE_ENTERED_TARGET,
//                SeaController.humanCoordGetterController!!.handlerMouseEnter4Install
//            )
//            button.addEventFilter(
//                MouseEvent.MOUSE_EXITED_TARGET,
//                SeaController.humanCoordGetterController!!.handlerMouseExit4Install
//            )
//            button.addEventFilter(
//                MouseEvent.MOUSE_CLICKED,
//                SeaController.humanCoordGetterController!!.handlerMouseClick4Install
//            )
//        }
        for (id in boatsIdToInstall) {
            install(id, welcomeText)
        }
//        for (button in HumanButton.buttonMap.values) {
//            button.removeEventFilter(
//                MouseEvent.MOUSE_ENTERED_TARGET,
//                SeaController.humanCoordGetterController!!.handlerMouseEnter4Install
//            )
//            button.removeEventFilter(
//                MouseEvent.MOUSE_EXITED_TARGET,
//                SeaController.humanCoordGetterController!!.handlerMouseExit4Install
//            )
//            button.removeEventFilter(
//                MouseEvent.MOUSE_CLICKED,
//                SeaController.humanCoordGetterController!!.handlerMouseClick4Install
//            )
//        }

        MainScope().launch {
            welcomeText.text = "Готово!"
        }
        techField.print()
    }

    suspend fun installAllRobot(boatsIdToInstall: Collection<Int>) { // Установка всех нужных кораблей:
        for (id in boatsIdToInstall) {
            install(id, null)

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