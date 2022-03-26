package com.example.battleship.coordinates

import android.view.View
import com.example.battleship.boats.Boat
import com.example.battleship.boats.BoatFactory
import com.example.battleship.boats.BoatInstaller
import com.example.battleship.fields.TechField
import com.example.battleship.fields.TechField4Algorithm
import com.example.battleship.seabutton.RobotButton
import kotlin.properties.Delegates
import kotlin.random.Random

// Получение координаты для утановки корабля или хода:
class GetCoord {

    // Генератор случайной координаты корабля:
    private fun randomBoat(): Pair<Coordinate, Boolean> {
        val letter = (1..10).random()
        val number = (1..10).random()
        val isVertical = Random.nextBoolean()
        return Coordinate(letter, number) to isVertical
    }

    fun boatRobot(): Pair<Coordinate, Boolean> {
        return randomBoat()
    }

    fun turnRobot(tF: TechField): Coordinate {
        val techField =
            TechField4Algorithm( // Создаем копию ТехПоля, чтобы не менять оригинал
                tF.scoredList,
                tF.failList,
                tF.boatList
            )

        // Ищем недобитый корабль (и добиваем):
        val damagedBoat: Boat
        if (techField.boatList.values.any { it.lives != 0 && it.lives < it.size }) {
            damagedBoat =
                techField.boatList.values.find { it.lives != 0 && it.lives < it.size }!!
            val damagedCoords = arrayListOf<Coordinate>()
            for (coord in damagedBoat.coordinates) // Находим сбитые клетки корабля
                if (tF.fieldArray[coord.number][coord.letter] == damagedBoat.id * (-1))
                    damagedCoords.add(coord)

            // Ищем координаты в которых возможно нахождение остальной части корабля:
            val potentialCords = arrayListOf<Coordinate>()

            if (damagedCoords.size == 1) {
                with(potentialCords) {
                    add(
                        Coordinate(
                            damagedCoords[0].letter,
                            damagedCoords[0].number + 1
                        )
                    )
                    add(
                        Coordinate(
                            damagedCoords[0].letter,
                            damagedCoords[0].number - 1
                        )
                    )
                    add(
                        Coordinate(
                            damagedCoords[0].letter - 1,
                            damagedCoords[0].number
                        )
                    )
                    add(
                        Coordinate(
                            damagedCoords[0].letter + 1,
                            damagedCoords[0].number
                        )
                    )
                }
            } else if (damagedBoat.isVertical) {
                with(potentialCords) {
                    damagedCoords.sortedBy { it.number }
                    add(
                        Coordinate(
                            damagedCoords[0].letter,
                            damagedCoords[0].number - 1
                        )
                    )
                    add(
                        Coordinate(
                            damagedCoords.last().letter,
                            damagedCoords.last().number + 1
                        )
                    )
                }
            } else {
                with(potentialCords) {
                    damagedCoords.sortedBy { it.number }
                    add(
                        Coordinate(
                            damagedCoords[0].letter - 1,
                            damagedCoords[0].number
                        )
                    )
                    add(
                        Coordinate(
                            damagedCoords.last().letter + 1,
                            damagedCoords.last().number
                        )
                    )
                }
            }
            // Исключаем возможные коодинаты где может быть недобитый корабль, куда бить не имеет смысла
            // И возвращаем случайную координату из оставшихся.
            val returnCoord = potentialCords.filter {
                (it.number > 0) &&
                        (it.number < 11) &&
                        (it.letter > 0) &&
                        (it.letter < 11) &&
                        with(it) { techField.failList.none { this == it } }
            }.random()
            println("${returnCoord.letter}${returnCoord.number}")

            return returnCoord
        }

        // Если нет недобитых кораблей:
        val boatSizeList = mutableListOf<Int>() // Список с размерами живых кораблей
        for (boat in techField.boatList.values) if (boat.size == boat.lives) boatSizeList.add(
            boat.size
        )
        val longestBoatSize =
            boatSizeList.maxByOrNull { it } // Размер самого длиннго живого корабля
        val longestBoatNumber =
            boatSizeList.count { it == longestBoatSize } // Количество таких кораблей

        val longestBoatsIdList =
            mutableListOf<Int>() // Список ID самых длинных кораблей
        for (i in 1..longestBoatNumber) {
            longestBoatsIdList.add(longestBoatSize!! * 10 + i)
        }
//        longestBoatsIdList.run(::println)
        val installedCoordsMap =
            mutableMapOf<Coordinate?, Int>() // Мапа расставленных координат: кордината - количество
        val installer = BoatInstaller(BoatFactory(techField), null, null)
        for (i in 1..5) { // Делается 5 расстановок

            // Могут быть случаи ближе к концу игры, когда при расстановке кораблей (самых длинных, найденных выше)
            // (для целей поиска клетки для выстрела)
            // после растановоки первых кораблей, остальным не будет места.
            // Далее - проверка на такой случай: если все корабли не смогли расставиться, то они перераставляются еще раз
            do {
                techField.boatList.clear()
                for (id in longestBoatsIdList) {
                    val testBoatPair =
                        installer.installRobot(
                            id
                        ) // Установщик возвращает пару: корабль и булеан (результат проверки)
                    val isTestBoatOk = testBoatPair.first
                    if (!isTestBoatOk) // Если проверка пройдена не успешно
                        continue // то эта неудачная итерация пропускается
                    techField.boatList[id] = testBoatPair.second
                }
            } while (techField.boatList.size != longestBoatsIdList.size) // Пока не будут расставлены все корабли

            var installedCoords =
                arrayOf<Coordinate?>() // Список координат расставленных кораблей
            for (boat in techField.boatList)
                installedCoords += boat.value.coordinates
            for (coord in installedCoords) {
                val existKey: Coordinate?
                if (installedCoordsMap.map { it.key }.any { it == coord }) {
                    existKey = installedCoordsMap.map { it.key }.first { it == coord }
                    installedCoordsMap[existKey] = installedCoordsMap[existKey]!! + 1
                } else installedCoordsMap[coord] = 1
            }
        }
        val mostFrequentCord =
            // Координата, которая встречается в мапе чаще всех, ее и возвращаем
            installedCoordsMap.filterValues { coord -> coord == installedCoordsMap.maxByOrNull { it.value }?.value }
                .map { it.key }[0]
        return mostFrequentCord!!
    }
}

