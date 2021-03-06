package com.example.battleship.seabutton

import android.content.Context
import android.util.AttributeSet
import com.example.battleship.coordinates.Coordinate

class RobotButton (context: Context, attrs: AttributeSet?) :
    SeaButton(context, attrs) {
    companion object {
        var buttonCounter = 0
        val buttonMap = mutableMapOf<Int, RobotButton>()
    }

    init {

        ++buttonCounter
        seaButtonId = when {
            buttonCounter % 10 == 0 -> buttonCounter * 10 + 10
            else -> buttonCounter + 10
        }
        val number = when {
            buttonCounter % 10 == 0 -> buttonCounter / 10
            else -> (buttonCounter + 10) / 10
        }

        val letter = when {
            buttonCounter % 10 == 0 -> 10
            else -> buttonCounter - 10 * (buttonCounter / 10)
        }
        coord = Coordinate(letter, number)
        buttonMap[this.seaButtonId] = this
    }
}