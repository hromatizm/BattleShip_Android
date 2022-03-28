package com.example.battleship

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.app.Application
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.example.battleship.boats.BoatFactory
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.SavedButton
import com.example.battleship.seabutton.SeaButton
import com.example.battleship.fields.TechField
import com.example.battleship.seabutton.RobotButton
import com.example.battleship.turns.TurnSequence

class MyApplication : Application() {

    companion object {

        const val PREFS_CONFIRM_KEY = "PREFS_CONFIRM_KEY"
        const val PREFS_INDEX_KEY = "PREFS_INDEX_KEY"

        private lateinit var appInstance: MyApplication
        fun getAppInstance(): MyApplication {
            return appInstance
        }
    }

    var isVertical = true // Признак вертикальности корабля при расстановке
    var isPopupOnScreen = false // Меню поддвержения установки корабля активно
    var isConfirm = true // Нужно ли поддвержадть установку корабля
    var isIndex = true // Нужно ли отображать буквы и цифры ввокруг поля
    var isHumanFieldActive = true // Является ли поле активным
    var isRobotFieldActive = false // Является ли поле активным

    var isFirstStartOfMainMenuActivity = true // Первый запуск MainMenuActivity
    var isFirstStartOfTurnsActivity = true // Первый запуск TurnsActivity
    var isHumanBoatInstalled = false // Завершена расстановка кораблей игроком

    lateinit var humanFieldView: LinearLayout
    lateinit var robotFieldView: LinearLayout

    private val displayWidth = Resources.getSystem().displayMetrics.widthPixels.toString()
    val displayHeight = Resources.getSystem().displayMetrics.heightPixels.toString()

    val seaButtonSizeWithIndex = (displayWidth.toInt() / 11.5).toInt()
    val seaButtonSizeWithoutIndex = (displayWidth.toInt() / 10.5).toInt()
    val shrunkSeaButtonSizeWithIndex = (seaButtonSizeWithIndex * 0.66).toInt()
    val shrunkSeaButtonSizeWithoutIndex = (seaButtonSizeWithoutIndex * 0.66).toInt()

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

    lateinit var turnSequence: TurnSequence

    var turnsActivityAnimator = ValueAnimator().setDuration(1_000)

    var textForStatusText = "" // Для сохранения текста из TurnsActivity

    init {
        humanTechField.buttonMap = HumanButton.buttonMap
        robotTechField.buttonMap = RobotButton.buttonMap
    }

    fun setHumanFieldActive() {
        isHumanFieldActive = true
        isRobotFieldActive = false
    }

    fun setRobotFieldActive() {
        isHumanFieldActive = false
        isRobotFieldActive = true
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

    // Подогнать размер клетки поля под размер экрана:
    // isActive - является ли поле активным. Не активное поле уменьшется.
    fun fitScreenSize(groupView: LinearLayout, isActive: Boolean) {
        val seaLines = getAllChildren(groupView)
        for (line in seaLines) {
            val lineViews = getAllChildren(line as LinearLayout)
            for (view in lineViews) {
                view.layoutParams.height = when {
                    isIndex && isActive -> seaButtonSizeWithIndex
                    isIndex && !isActive -> shrunkSeaButtonSizeWithIndex
                    !isIndex && isActive -> seaButtonSizeWithoutIndex
                    else -> shrunkSeaButtonSizeWithoutIndex
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

    // Перерисовка обоих игровых полей - ативное становится большим, неактивное - маленьким
    fun resizeFields() {
        fitScreenSize(humanFieldView, isHumanFieldActive)
        fitScreenSize(robotFieldView, isRobotFieldActive)
        getAllChildren(humanFieldView).forEach { it.requestLayout() }
        getAllChildren(robotFieldView).forEach { it.requestLayout() }
    }

    fun animateFieldResize(groupView: LinearLayout, newSize: Int) {
        val seaLines = getAllChildren(groupView)
        for (line in seaLines) {
            val lineViews = getAllChildren(line as LinearLayout)
            for (view in lineViews) {
                view.layoutParams.height = newSize
                view.layoutParams.width = newSize
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

    // Вкл - Выкл индекс вокруг поля (буквы и цифры)
    fun toggleIndex(groupView: LinearLayout) {
        val seaLines = getAllChildren(groupView)
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

    fun turnActivityAnimationInit(
        fieldToEnlage: LinearLayout,
        fieldToReduce: LinearLayout,
    ) {
        val smallSize: Int
        val largeSize: Int


        if (isIndex) {
            smallSize = shrunkSeaButtonSizeWithIndex
            largeSize = seaButtonSizeWithIndex
        } else {
            smallSize = shrunkSeaButtonSizeWithoutIndex
            largeSize = seaButtonSizeWithoutIndex
        }

        val sizeGap = largeSize - smallSize

        turnsActivityAnimator.setObjectValues(0, sizeGap)
        turnsActivityAnimator.addUpdateListener { animation ->
            val seaLines = getAllChildren(fieldToEnlage)
            for (line in seaLines) {
                getAllChildren(line as LinearLayout).forEach {
                    it.layoutParams.height = smallSize + animation.animatedValue as Int
                    it.layoutParams.width = it.layoutParams.height
                }
                fieldToEnlage.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                val fieldToEnlarge = getAllChildren(fieldToReduce)
                for (line in seaLines) {
                    getAllChildren(line as LinearLayout).forEach {
                        it.layoutParams.height =
                            largeSize - animation.animatedValue as Int
                        it.layoutParams.width = it.layoutParams.height
                    }
                    fieldToReduce.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                }
            }
            getAllChildren(fieldToEnlage).forEach { it.requestLayout() }
            getAllChildren(fieldToReduce).forEach { it.requestLayout() }
        }

        turnsActivityAnimator.setEvaluator(object : TypeEvaluator<Int> {
            // fraction: 0 - начало анимации, 1 - конец
            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                return (fraction * endValue).toInt()
            }
        })
    }

    override fun onCreate() {
        appInstance = this
        super.onCreate()
    }
}