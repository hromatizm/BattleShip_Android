package coordinates

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import boats.Boat
import com.example.battleship.Model
import com.example.battleship.MyApplication
import com.example.battleship.R
import com.example.battleship.coordinates.Coordinate
import com.example.battleship.coordinates.GetCoord
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.seabutton.SeaButton

class HumanCoordGetterController(var context: Context?, var parent: View.OnClickListener?) {

    val buttonMap = HumanButton.buttonMap
    val app = MyApplication.getAppInstance()
    val model = Model.getInstance()

    companion object {
        var isVertical = true
        var boatTemp = Boat(41, Coordinate(1, 1), isVertical)
    }

    fun onClickForInstall(view: View?) {

        val button = view as HumanButton
        boatTemp.coordBegin = button.coord
        boatTemp.coordinates[0] = boatTemp.coordBegin
        // Если размер корабля больше чем 1, то добавляем в коллекцию остальные координаты:
        if (boatTemp.size > 1) {
            for (i in 1 until boatTemp.size) {
                val last =
                    boatTemp.coordinates[i - 1] // Берем последнюю координату из коллекции
                val new = when { // На ее основе создаем новую
                    isVertical -> Coordinate(last.letter, last.number + 1)
                    else       -> Coordinate(last.letter + 1, last.number)
                }
                boatTemp.coordinates[i] = (new)
            }
        }
        boatTemp.coordEnd = boatTemp.coordinates[boatTemp.size - 1]

        for (button in HumanButton.buttonMap.values) {
            if (!boatTemp.coordinates.any { it in app.humanTechField.boatsAndFramesCoordsList })
                for (coord in boatTemp.coordinates)
                    if (coord.id == button.seaButtonId &&
                        boatTemp.coordEnd.letter <= 10 &&
                        boatTemp.coordEnd.number <= 10
                    ) {
                        button.setBackgroundColor(Color.GRAY)
                        showPopupForInstall(button, boatTemp)
                        model.isPopupOnScreen = true

                    }
        }
    }

    fun showPopupForInstall(button: HumanButton, boat: Boat) {
        stopListenButtons()
        if (!model.isPopupOnScreen) {
            val gravity = when {
                button.coord.letter > 5 -> Gravity.RIGHT
                else                    -> Gravity.LEFT
            }
            val popupMenu = PopupMenu(context, button, R.style.popupMenu)
            popupMenu.inflate(R.menu.confirm_installation)
            popupMenu.gravity = gravity
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.root          -> {
                        Log.d("zzz", "root")
                        true
                    }
                    R.id.popup_confirm -> {
                        if (boat.coordEnd.letter <= 10 && boatTemp.coordEnd.number <= 10)
                            GetCoord.newCoordForInstall = button.coord
                        HumanButton.buttonMap.values.filter { it.coord in boatTemp.coordinates }
                            .forEach {
                                with(it) {
                                    setBackgroundColor(
                                        ContextCompat.getColor(context!!, R.color.black)
                                    )
                                    isBoat = true
                                }
                            }
                        model.isPopupOnScreen = false
                        true
                    }
                    R.id.popup_cancel  -> {
                        HumanButton.buttonMap.values.filter { it.coord in boatTemp.coordinates }
                            .forEach {
                                it.setBackgroundColor(
                                    ContextCompat.getColor(context!!, R.color.sky_blue)
                                )
                            }
                        model.isPopupOnScreen = false
                        startListenButtons()
                        true
                    }

                    else               -> {
                        buttonMap.values.filter { !it.isBoat }.forEach {
                            it.setBackgroundColor(
                                ContextCompat.getColor(context!!, R.color.sky_blue)
                            )

                        }
                        true
                    }
                }
            }
            popupMenu.setOnDismissListener {
                buttonMap.values.filter { !it.isBoat }.forEach {
                    it.setBackgroundColor(
                        ContextCompat.getColor(context!!, R.color.sky_blue)
                    )
                }
                startListenButtons()
                model.isPopupOnScreen = false
            }
        }

    }

    fun startListenButtons() {
        for (button in buttonMap.values)
            (button as SeaButton).setOnClickListener(parent)
    }

    fun stopListenButtons() {
        for (button in buttonMap.values)
            (button as SeaButton).setOnClickListener(null)
    }

}



