package com.example.battleship

import android.app.Application
import android.content.res.Resources
import android.view.View
import android.widget.LinearLayout
import com.example.battleship.boats.BoatFactory
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.SavedButton
import com.example.battleship.seabutton.SeaButton
import com.example.battleship.fields.TechField

class MyApplication : Application() {

    companion object {
        private lateinit var appInstance: MyApplication
        fun getAppInstance(): MyApplication {
            return appInstance
        }
    }

    var isHumanBoatInstalled = false
    var isVertical = true
    var isPopupOnScreen = false
    var isConfirm = true

    val displayWidth = Resources.getSystem().displayMetrics.widthPixels.toString()
    val displayHeight = Resources.getSystem().displayMetrics.heightPixels.toString()

    val seaButtonSize = (displayWidth.toInt() / 11.5).toInt()

    val humanTechField = TechField(this)
    val humanBoatFactory = BoatFactory(humanTechField)
    val robotTechField = TechField(this)
    val robotBoatFactory = BoatFactory(robotTechField)

    val listOfHumanBoatsId = mutableListOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14)
    val listOfRobotBoatsId = mutableListOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14)

    var isHumanButtonMapSaved = false
    val savedHumanButtonMap = mutableMapOf<Int, SavedButton>()

    init {
        humanTechField.buttonMap = HumanButton.buttonMap
    }

    fun saveButton(b: SeaButton): SavedButton {
        return SavedButton(b.isBlank, b.isBoat, b.isFail, b.isDead)
    }

    fun saveHumanButtonMap() {
        HumanButton.buttonMap.forEach { savedHumanButtonMap[it.key] = saveButton(it.value) }
        isHumanButtonMapSaved = true
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
                view.layoutParams.height = seaButtonSize
                view.layoutParams.width = seaButtonSize
            }
        }
    }

//    fun runTurns(){
//        val intent = Intent(context?.applicationContext, TurnsActivity::class.java)
////                            ContextCompat.startActivity(context!!, intent, null)
//    }

    override fun onCreate() {
        appInstance = this
        super.onCreate()
    }
}