package com.example.battleship.seabutton

import android.content.Context
import android.util.AttributeSet
import com.example.battleship.R
import com.example.battleship.coordinates.Coordinate
import com.google.android.material.button.MaterialButton
import kotlin.properties.Delegates

open class SeaButton(context: Context, attrs: AttributeSet?) :
    MaterialButton(context, attrs, R.style.Square_Button) {

    var seaButtonId by Delegates.notNull<Int>()
    lateinit var coord : Coordinate
    var isBoat : Boolean = false
}