package com.example.battleship.turns

import com.example.battleship.coordinates.Coordinate

interface Turn {
    suspend fun makeTurn(turnCoord: Coordinate?): Pair<Boolean, Int>
}