package com.example.battleship.boats

import com.example.battleship.coordinates.Coordinate
import com.example.battleship.fields.TechField

// Фабрика создания корабля
class BoatFactory(val techField: TechField) {

    fun makeBoat(id: Int, coordBegin: Coordinate, isVertical: Boolean): Boat {
        val boatDraft = makeBoatDraft(id, coordBegin, isVertical)
        makeFrame(boatDraft)
        return boatDraft
    }

    // Сперва создается черновик корабля,
    // который потом проверяется, может ли корабль с такими координатами корректно стоять на поле,
    // не вылезая за пределы поля и не касаясь уже стоящих кораблей
    private fun makeBoatDraft(id: Int, coordBegin: Coordinate, isVertical: Boolean): Boat {
        return Boat(id, coordBegin, isVertical)
    }

    // Расчет рамки вокруг корабля:
    private fun makeFrame(boat: Boat) {
        val frameSize = boat.size + 2 // Длинна рамки на 2 поля длиннее, чем корабль

        if (boat.isVertical) { // Если корабль вертикальный, то рамка будет вертикальная
            // Самая верхняя числовая координата рамки находится на 1 выше корабля:
            val frameTopNumber = boat.coordBegin.number - 1
            // Добавляем клетку над кораблем:
            boat.frame.add(Coordinate(boat.coordBegin.letter, frameTopNumber))
            // Добавляем клетку под кораблем:
            boat.frame.add(Coordinate(boat.coordEnd.letter, frameTopNumber + frameSize - 1))
            // Добавляем 2 полоски клеток (справа и слева от корабля):
            for (number in 0 until frameSize) {
                boat.frame.add(Coordinate(boat.coordBegin.letter - 1, frameTopNumber + number))
                boat.frame.add(Coordinate(boat.coordBegin.letter + 1, frameTopNumber + number))
            }
        } else { // Если корабль горизонтальный, то рамка будет горизонтальная
            // Самая левая буквенная координата рамки находится на 1 левеее корабля:
            val frameLeftLetter = boat.coordBegin.letter - 1
            boat.frame.add(Coordinate(frameLeftLetter, boat.coordBegin.number))
            boat.frame.add(Coordinate(frameLeftLetter + frameSize - 1, boat.coordEnd.number))
            for (number in 0 until frameSize) {
                boat.frame.add(Coordinate(frameLeftLetter + number, boat.coordBegin.number - 1))
                boat.frame.add(Coordinate(frameLeftLetter + number, boat.coordBegin.number + 1))
            }
        }
    }
}