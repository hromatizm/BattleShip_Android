package com.example.battleship

import android.app.Application
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.battleship.boats.BoatFactory
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.SavedButton
import com.example.battleship.seabutton.SeaButton
import com.example.battleship.fields.TechField
import com.example.battleship.seabutton.RobotButton
import com.example.battleship.turns.TurnSequence

class MyApplication : Application() {

    companion object {
        private lateinit var appInstance: MyApplication
        fun getAppInstance(): MyApplication {
            return appInstance
        }
    }

    var isVertical = true // Признак вертикальности корабля при расстановке
    var isPopupOnScreen = false // Меню поддвержения установки корабля активно
    var isConfirm = true // Нужно ли поддвержадть установку корабля
    var isIndex = true // Нужно ли отображать буквы и цифры ввокруг поля

    var isFirstStartOfMainMenuActivity = true // Первый запуск MainMenuActivity
    var isFirstStartOfTurnsActivity = true // Первый запуск TurnsActivity
    var isHumanBoatInstalled = false // Завершена расстановка кораблей игроком

    private val displayWidth = Resources.getSystem().displayMetrics.widthPixels.toString()
    val displayHeight = Resources.getSystem().displayMetrics.heightPixels.toString()

    private val seaButtonSizeWithIndex = (displayWidth.toInt() / 11.5).toInt()
    private val seaButtonSizeWithoutIndex = (displayWidth.toInt() / 10.5).toInt()

    val humanTechField = TechField(this)
    val humanBoatFactory = BoatFactory(humanTechField)

    val robotTechField = TechField(this)
    val robotBoatFactory = BoatFactory(robotTechField)

    val listOfHumanBoatsId = mutableListOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14)
    val listOfRobotBoatsId = mutableListOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14)

    private var isHumanButtonMapSaved = false
    private var isRobotButtonMapSaved = false
    private val savedHumanButtonMap = mutableMapOf<Int, SavedButton>()
    private val savedRobotButtonMap = mutableMapOf<Int, SavedButton>()

    var turnsCounter = 0

    lateinit var turnSequence : TurnSequence

    init {
        humanTechField.buttonMap = HumanButton.buttonMap
        robotTechField.buttonMap = RobotButton.buttonMap
    }

    private fun saveButton(b: SeaButton): SavedButton {
        return SavedButton(b.isBlank, b.isBoat, b.isFail, b.isDead)
    }

    fun saveHumanButtonMap() {
        HumanButton.buttonMap.forEach { savedHumanButtonMap[it.key] = saveButton(it.value) }
        isHumanButtonMapSaved = true
    }

    fun saveRobotButtonMap() {
        RobotButton.buttonMap.forEach { savedRobotButtonMap[it.key] = saveButton(it.value) }
        isRobotButtonMapSaved = true
    }

    fun restoreHumanButtonMap() {
        if (isHumanButtonMapSaved)
            HumanButton.buttonMap.forEach {
                it.value.isBlank = savedHumanButtonMap[it.key]?.isBlank ?: true
                it.value.isBoat = savedHumanButtonMap[it.key]?.isBoat ?: false
                it.value.isFail = savedHumanButtonMap[it.key]?.isFail ?: false
                it.value.isDead = savedHumanButtonMap[it.key]?.isDead ?: false
            }
    }

    fun restoreRobotButtonMap() {
        if (isRobotButtonMapSaved)
           RobotButton.buttonMap.forEach {
                it.value.isBlank = savedRobotButtonMap[it.key]?.isBlank ?: true
                it.value.isBoat = savedRobotButtonMap[it.key]?.isBoat ?: false
                it.value.isFail = savedRobotButtonMap[it.key]?.isFail ?: false
                it.value.isDead = savedRobotButtonMap[it.key]?.isDead ?: false
            }
    }

    fun getAllChildren(parent: LinearLayout): List<View> {
        val childCount = parent.childCount
        val buttonList: MutableList<View> = ArrayList()
        for (i in 0 until childCount)
            buttonList.add(parent.getChildAt(i))
        return buttonList
    }

    fun fitScreenSize(groupView: LinearLayout) {
        val seaLines = getAllChildren(groupView)
        for (line in seaLines) {
            val lineViews = getAllChildren(line as LinearLayout)
            for (view in lineViews) {
                view.layoutParams.height = if (isIndex) {
                    seaButtonSizeWithIndex
                } else {
                    seaButtonSizeWithoutIndex
                }
                view.layoutParams.width = view.layoutParams.height
            }
        }
        groupView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = if (!isIndex) {
                Gravity.CENTER_HORIZONTAL
            } else {
                Gravity.START
            }
        }
    }

    fun toggleIndex(groupView: LinearLayout) {
        val seaLines = getAllChildren(groupView)
        println("SeaLines ${seaLines.size}")
        for (line in seaLines) {
            val lineViews = getAllChildren(line as LinearLayout)
            lineViews.filter { it !is SeaButton }.forEach {
                it.visibility = if (!isIndex) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }
    }

    override fun onCreate() {
        appInstance = this
        super.onCreate()
    }
}