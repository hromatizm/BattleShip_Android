package com.example.battleship.seabutton

import android.content.Context
import android.util.AttributeSet
import com.example.battleship.R
import com.example.battleship.coordinates.Coordinate
import com.google.android.material.button.MaterialButton
import java.io.Serializable
import kotlin.properties.Delegates

open class SeaButton(context: Context, attrs: AttributeSet?) :
    MaterialButton(context, attrs, R.style.Square_Button), Serializable {

    var seaButtonId by Delegates.notNull<Int>()
    lateinit var coord: Coordinate

    // Состояние клетки:
    var isBlank: Boolean = true // Пусто
    var isBoat: Boolean = false // Корабль
    var isFail: Boolean = false // Мимо
    var isDead: Boolean = false // Сбитый корабль

    fun getIsBlank() = isBlank
    fun getIsBoat() = isBoat
    fun getIsFail() = isFail
    fun getIsDead() = isDead

    fun setIsBoat() {
        this.isBoat = true
        this.isBlank = false
    }

    fun setIsFail() {
        this.isFail = true
        this.isBlank = false
    }

    fun setIsDead() {
        this.isDead = true
        this.isBlank = false
        this.isBoat = false
    }

}