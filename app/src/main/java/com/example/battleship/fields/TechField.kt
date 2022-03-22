package fields

import boats.Boat
import com.example.battleship.coordinates.Coordinate
import com.example.battleship.seabutton.SeaButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Техническое поле боя, в котором храниться вся информация состоянии клеток на текущий момент игры.
// На его основе печается UI поле.
open class TechField {

    /*
   Коды технического поля сражения:
   Палубы кораблей:
   41 - четырехпалубный корабль
   31, 32 - трехпалубные корабли
   21, 22, 23 - двухпалубные корабли
   11, 12, 13, 14 - однопалубные корабли
   Те же коды со знаком минус - палубы, в которые попали.
   Поля рядом с кораблями (теже коды, но с умножением на 10):
   410
   310, 320
   210, 220, 230
   110, 120, 130, 140
   Поля, куда стреляли и не попали:
   0
   Остальные поля:
   1
    */

    // Коллекция кораблей с доступом по ключу - ID корабля:
    open var boatList = mutableMapOf<Int, Boat>()

    // Коллекция всех координат кораблей на поле
    open var boatsAndFramesCoordsList = mutableListOf<Coordinate?>()

    // Счетчик не сбитых кораблей (для фиксации конца игры):
    var aliveBoatCounter = 0

    // Коллекция коодиннат в которых стоят сбитые клетки кораблей:
    open var scoredList = arrayListOf<Coordinate>()

    // Коллекция коодиннат куда стреляли, но "мимо":
    open var failList = arrayListOf<Coordinate>()

    open var lastTurnCoord: Coordinate? = null

    // Тех поле 12 на 12 изначально заполенено кодом 1:
    var fieldArray = Array(12) { Array(12) { 1 } }

    open var buttonMap = mapOf<Int, SeaButton>()
    private val dC = 9760.toChar().toString()

    // 2 Верхняие строки техполя с номерами колонок и буквами (для печати техполя на время отладки)
    private val strIndex = arrayOf("_", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "_")
    private val strLetters = arrayOf("_", "_", "А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К", "_", "_")

    // Очищение поля, чтобы можно было вибивать заново с чистого листа ту же расстоновку кораблей
    fun clearField() {
        fieldArray = Array(12) { Array(12) { 1 } } // Инициализируем начальным состоянием
        aliveBoatCounter = boatList.size // Восстанавливаем счетчик живых кораблей
        scoredList.clear() // Очищаем список сбитых
        failList.clear() // Очищаем список "мимо"
        for (boat in boatList.values) // Восстанавливаем жизини всех кораблей
            boat.lives = boat.size
        update() // Обнавляем ТехПоле (переносим коды из коллекций в основной массив fieldArray
    }

    // Вывод техполя в консоль (для отладки):
    fun print() {
        for (item in strIndex)
            print("%-4s".format(item))
        println()
        for (item in strLetters)
            print("%-4s".format(item))
        println()
        for (str in fieldArray) {
            print("%-4s".format(fieldArray.indexOf(str)))
            for (item in str)
                print("%-4d".format(item))
            print("%-4s".format(fieldArray.indexOf(str)))
            println()
        }
        println()
    }

    suspend fun uiUpdate() {
        withContext(Dispatchers.Main) {
            for (coord in scoredList) {
                with(buttonMap.getValue(coord.id)) {
//                    setMaxSize(29.0, 29.0)
//                    setPrefSize(29.0, 29.0)
                    text = dC
//                    style = when {
//                        buttonMap.getValue(coord.coordId) is HumanButton && this.coord == lastTurnCoord ->
//                            "-fx-padding: 0.0 3.0 0.0 0.0;" +
//                                    "-fx-text-alignment: center;" +
//                                    "-fx-border-color: red;" +
//                                    "-fx-background-color: radial-gradient(focus-distance 0% , center 50% 50% , radius 50% , red, yellow);" +
//                                    "-fx-background-radius: 12;" +
//                                    "-fx-border-radius: 12;" +
//                                    "-fx-border-width: 1px;" +
//                                    "-fx-font-size: 1.7em;" +
//                                    "-fx-pref-width: 29px;" +
//                                    "-fx-pref-height: 29px;"
//                        buttonMap.getValue(coord.coordId) is HumanButton && this.coord != lastTurnCoord ->
//                            "-fx-padding: 0.0 3.0 0.0 0.0;" +
//                                    "-fx-text-alignment: center;" +
//                                    "-fx-border-color: black;" +
//                                    "-fx-background-color: radial-gradient(focus-distance 0% , center 50% 50% , radius 50% , red, yellow);" +
//                                    "-fx-background-radius: 12;" +
//                                    "-fx-border-radius: 12;" +
//                                    "-fx-border-width: 1px;" +
//                                    "-fx-font-size: 1.7em;" +
//                                    "-fx-pref-width: 29px;" +
//                                    "-fx-pref-height: 29px;"
//                        buttonMap.getValue(coord.coordId) !is HumanButton && this.coord == lastTurnCoord ->
//                            "-fx-padding: 0.0 0.0 0.0 0.5;" +
//                                "-fx-text-alignment: center;" +
//                                "-fx-border-color: red;" +
//                                "-fx-background-color: radial-gradient(focus-distance 0% , center 50% 50% , radius 50% , red, yellow);" +
//                                "-fx-background-radius: 12;" +
//                                "-fx-border-radius: 12;" +
//                                "-fx-border-width: 1px;" +
//                                "-fx-font-size: 1.7em;" +
//                                "-fx-pref-width: 29px;" +
//                                "-fx-pref-height: 29px;"
//                        else -> "-fx-padding: 0.0 0.0 0.0 0.5;" +
//                                "-fx-text-alignment: center;" +
//                                "-fx-border-color: black;" +
//                                "-fx-background-color: radial-gradient(focus-distance 0% , center 50% 50% , radius 50% , red, yellow);" +
//                                "-fx-background-radius: 12;" +
//                                "-fx-border-radius: 12;" +
//                                "-fx-border-width: 1px;" +
//                                "-fx-font-size: 1.7em;" +
//                                "-fx-pref-width: 29px;" +
//                                "-fx-pref-height: 29px;"
//                    }
                }
            }
            for (coord in failList) {
                if (coord.number in 1..10 && coord.letter in 1..10) {
                    with(buttonMap.getValue(coord.id)) {
//                    setMaxSize(29.0, 29.0)
//                    setPrefSize(29.0, 29.0)
//                        style = when (this.coord) {
//                            lastTurnCoord -> "-fx-border-color: red;" +
//                                    "-fx-background-color: radial-gradient(focus-distance 0% , center 50% 50% , radius 10% , darkblue, white);" +
//                                    "-fx-background-radius: 1;" +
//                                    "-fx-border-radius: 3;" +
//                                    "-fx-border-width: 1px;" +
//                                    "-fx-pref-width: 9px;" +
//                                    "-fx-pref-height: 9px;" +
//                                    "-fx-pref-width: 29px;" +
//                                    "-fx-pref-height: 29px;"
//                            else -> "-fx-border-color: lightgray;" +
//                                    "-fx-background-color: radial-gradient(focus-distance 0% , center 50% 50% , radius 10% , darkblue, white);" +
//                                    "-fx-background-radius: 1;" +
//                                    "-fx-border-radius: 3;" +
//                                    "-fx-border-width: 1px;" +
//                                    "-fx-pref-width: 9px;" +
//                                    "-fx-pref-height: 9px;" +
//                                    "-fx-pref-width: 29px;" +
//                                    "-fx-pref-height: 29px;"
//                        }
                    }
                }
            }
        }
    }

    // Обновление кодов техполя (после действия игрока):
    fun update() {
        for (boat in boatList) {
            for (coord in boat.value.coordinates) // Клетки, где корабли
                fieldArray[coord.number][coord.letter] = boat.value.id
        }
        for (boat in boatList) {
            for (coord in boat.value.frame) // Клетки, где рамки вокруг кораблей
                fieldArray[coord.number][coord.letter] = boat.value.id * 10
        }
        for (coord in scoredList) { // Клетки, со сбитыми кораблями
            if (fieldArray[coord.number][coord.letter] < 0)
                continue
            else fieldArray[coord.number][coord.letter] *= -1
        }
        for (coord in failList) { // Клетки, куда стреляли, но "мимо"
            fieldArray[coord.number][coord.letter] = 0
        }
    }
}