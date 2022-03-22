package boats

import com.example.battleship.coordinates.Coordinate

class Boat(var id: Int, var coordBegin: Coordinate, val isVertical: Boolean) {
    /*
        id кораблей:
        41 - четырехпалубный корабль
        31, 32 - трехпалубные корабли
        21, 22, 23 - двухпалубные корабли
        11, 12, 13, 14 - однопалубные корабли
     */
    // Размер корабля - первая цифра id:
    var size: Int = id.toString()[0].toString().toInt()

    // Кол-во "жизней" корабля, для фиксации состояния "убит":
    var lives: Int = size

    // Координата конца корабля нужна для проверки, что корабль не выходит за пределы поля:
    var coordEnd: Coordinate

    // Коллекция всех координат корабля:
    val coordinates: MutableList<Coordinate> = arrayListOf()

    // Рамка вокруг корабля (туда не могут быть поставлены другие корабли, и там "мимо" после убийства корабля):
    val frame: MutableList<Coordinate> = arrayListOf()

    init {
        coordinates.add(coordBegin)
        // Если размер корабля больше чем 1, то добавляем в коллекцию остальные координаты:
        if (size > 1) {
            for (i in 1 until size) {
                val last = coordinates[i - 1] // Берем последнюю координату из коллекции
                val new = when { // На ее основе создаем новую
                    isVertical -> Coordinate(last.letter, last.number + 1)
                    else -> Coordinate(last.letter + 1, last.number)
                }
                coordinates.add(new)
            }
        }
        coordEnd = coordinates.last()
    }

    fun liveMinus() {
        lives--
    }

    // Вывод координат в консоль. Для отладки.
    fun print() {
        for (coord in this.coordinates) {
            val l: Int = coord.letter
            val n: Int = coord.number
        }
    }
}