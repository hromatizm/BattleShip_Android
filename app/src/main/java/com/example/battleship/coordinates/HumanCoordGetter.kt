package com.example.battleship.coordinates

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.example.battleship.boats.Boat
import com.example.battleship.boats.BoatInstaller
import com.example.battleship.MyApplication
import com.example.battleship.R
import com.example.battleship.TurnsActivity
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.SeaButton

class HumanCoordGetter(
    private val context: Context?,
    var parent: View.OnClickListener?,
    private val installer: BoatInstaller
) {
    private val humanButtonMap = HumanButton.buttonMap
    private val app = MyApplication.getAppInstance()
    private lateinit var boatTemp: Boat

    init {
        if (!app.isHumanBoatInstalled)
            boatTemp = Boat(app.listOfHumanBoatsId[0], Coordinate(1, 1), app.isVertical)
    }

    fun onClickForInstall(view: View) {
        val button = view as HumanButton
        boatTemp.coordBegin = button.coord
        boatTemp.coordinates[0] = boatTemp.coordBegin
        // Если размер корабля больше чем 1, то добавляем в коллекцию остальные координаты:
        if (boatTemp.size > 1) {
            for (i in 1 until boatTemp.size) {
                val last =
                    boatTemp.coordinates[i - 1] // Берем последнюю координату из коллекции
                val new = when { // На ее основе создаем новую
                    app.isVertical -> Coordinate(last.letter, last.number + 1)
                    else -> Coordinate(last.letter + 1, last.number)
                }
                boatTemp.coordinates[i] = (new)
            }
        }
        boatTemp.coordEnd = boatTemp.coordinates[boatTemp.size - 1]
        var isTryToInstall = false
        for (button in HumanButton.buttonMap.values) {
            if (!boatTemp.coordinates.any { it in app.humanTechField.boatsAndFramesCoordsList })
                for (coord in boatTemp.coordinates)
                    if (coord.id == button.seaButtonId &&
                        boatTemp.coordEnd.letter <= 10 &&
                        boatTemp.coordEnd.number <= 10
                    ) {
                        button.setStrokeColorResource(R.color.dark_blue)
                        button.strokeWidth = 7
                        isTryToInstall = true
                        app.isPopupOnScreen = false
                    }
        }
        Log.d("zzz", isTryToInstall.toString())
        if (isTryToInstall) tryToInstallBoat(button, boatTemp)
    }

    private fun tryToInstallBoat(button: HumanButton, boat: Boat) {
        if (app.isConfirm) {
            showPopupForInstall(button, boat)
        } else {
            installBoat(button, boat)
        }
    }

    private fun installBoat(button: HumanButton, boat: Boat) {
        if (boat.coordEnd.letter <= 10 && boatTemp.coordEnd.number <= 10)
            installer.installHuman(button.coord)
        HumanButton.buttonMap.values.filter { it.coord in boatTemp.coordinates }
            .forEach {
                it.setIsBoat()
                button.strokeWidth = 0
            }
        app.humanTechField.fieldUiUpdate()
        app.isPopupOnScreen = false
        if (!app.isHumanBoatInstalled) {
            installer.printWelcome(app.listOfHumanBoatsId[0])
            boatTemp = Boat(
                app.listOfHumanBoatsId[0],
                Coordinate(1, 1),
                app.isVertical
            )
        } else { // Расстановка завершена
            installer.printReady()
            HumanButton.buttonCounter = 0
            app.saveHumanButtonMap()
            BoatInstaller(app.robotBoatFactory, null, null).installAllRobot()
            runTurnsActivity()
        }
    }

    private fun runTurnsActivity() {
        val intent = Intent(context, TurnsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    private fun showPopupForInstall(button: HumanButton, boat: Boat) {
        stopListenButtons()
        Log.d("zzz", "showPopupForInstall")
        Log.d("zzz", app.isPopupOnScreen.toString())
        if (!app.isPopupOnScreen) {
            Log.d("zzz", app.isPopupOnScreen.toString())
            val gravity = when {
                button.coord.letter > 5 -> Gravity.END
                else -> Gravity.START
            }
            val popupMenu = PopupMenu(context, button, R.style.popupMenu)
            popupMenu.inflate(R.menu.confirm_installation)
            popupMenu.gravity = gravity
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popup_confirm -> {
                        installBoat(button, boat)
                        true
                    }
                    R.id.popup_cancel -> {
                        HumanButton.buttonMap.values.filter { it.coord in boatTemp.coordinates }
                            .forEach { it.strokeWidth = 0 }
                        app.humanTechField.fieldUiUpdate()
                        app.isPopupOnScreen = false
                        startListenButtons()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.setOnDismissListener {
                HumanButton.buttonMap.values.filter { it.coord in boatTemp.coordinates }
                    .forEach { it.strokeWidth = 0 }
                app.humanTechField.fieldUiUpdate()
                startListenButtons()
                app.isPopupOnScreen = false
            }
        }
    }

    fun startListenButtons() {
        for (button in humanButtonMap.values)
            button.setOnClickListener(parent)
    }

    private fun stopListenButtons() {
        for (button in humanButtonMap.values)
            button.setOnClickListener(null)
    }
}



