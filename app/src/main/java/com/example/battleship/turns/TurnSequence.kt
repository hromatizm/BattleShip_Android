package com.example.battleship.turns

import android.util.Log
import kotlin.system.exitProcess

// Поочередные ходы. Получает в качестве параметра коллекцию типа Turn
class TurnSequence(val players: List<Turn>) {

    // Ходы делаются по очереди до тех пор, пока не неступит GAME OVER в каком-то из Turn в коллекции.
    suspend fun start(): Int {
        Log.d("zzz", "TurnSequence - start")
        var isGoingOn = true
        var turnCounter = 0
        var result = false to 0
        while (isGoingOn) {
            Log.d("zzz", "TurnSequence - while")
            for (player in players) {
                Log.d("zzz", "TurnSequence - for")
                    result = player.makeRobotTurn()
                Log.d("zzz", result.first.toString())
                isGoingOn = result.first
                if(!isGoingOn) {
                    Thread.sleep(2_000)
                    return turnCounter
                }
                turnCounter = result.second
                Log.d("zzz", result.second.toString())
            }
        }
        return turnCounter // Нужно для анализа при массовых случайных расстановках
    }
}