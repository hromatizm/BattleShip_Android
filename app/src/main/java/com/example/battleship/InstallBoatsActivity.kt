package com.example.battleship

import android.os.Build
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.view.*
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import boats.Boat
import boats.BoatInstaller
import com.example.battleship.coordinates.Coordinate
import com.example.battleship.seabutton.HumanButton
import coordinates.HumanCoordGetterController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class InstallBoatsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var humanField: LinearLayout
    private lateinit var radioButtonHorizontal: RadioButton
    private lateinit var radioButtonVertical: RadioButton
    lateinit var welcomeText: TextView
    lateinit var root: View
    val model = Model.getInstance()

    lateinit var coordGetterController: HumanCoordGetterController

    lateinit var humanInstaller: BoatInstaller
    lateinit var robotInstaller: BoatInstaller

    //    val coordGetterController = HumanCoordGetterController(this, this as View.OnClickListener )
    lateinit var app: MyApplication

    private val seaButtonSize = (MyApplication.getAppInstance().displayWidth.toInt() / 11.5).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        app = MyApplication.getAppInstance()
        coordGetterController = HumanCoordGetterController(this, this)
        humanInstaller = BoatInstaller(app.humanBoatFactory, true, coordGetterController)
        robotInstaller = BoatInstaller(app.robotBoatFactory, false, null)

// Делаем фул-скрин

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar?.hide()

        setContentView(R.layout.activity_install_boats)

// Подгоняем поле под размер экрана

        humanField = findViewById(R.id.field)

        val seaLines = getAllChildren(humanField)
        for (line in seaLines) {
            val lineViews = getAllChildren(line as LinearLayout)
            for (view in lineViews) {
                view.layoutParams.height = seaButtonSize
                view.layoutParams.width = seaButtonSize
            }
        }

// RadioButton - какой корабль будем ставить: вертиальный или горизонтальный

        radioButtonHorizontal = findViewById(R.id.horizontal_button)
        radioButtonVertical = findViewById(R.id.vertical_button)
        radioButtonVertical.isChecked = true

        radioButtonVertical.setOnCheckedChangeListener { _, isChecked ->
            HumanCoordGetterController.isVertical = when {
                isChecked -> true
                else      -> false
            }
        }
// Поле для вывода на экран приглашения поставить корабль
        welcomeText = findViewById(R.id.welcome_text)
        coordGetterController.parent = this
//       startListenButtons()
        startGame()
    }

    private fun startGame() {
        app.humanTechField.buttonMap = HumanButton.buttonMap
//            robotTechField.buttonMap = SeaController.robotButtonMap
        MainScope().launch {
            humanInstaller.installAllHuman(
                listOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14),
                welcomeText
            )
//                View.boatInstallFinished()

            robotInstaller.installAllRobot(
                listOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14)
            )
//                turnSequence.start()
        }
    }

    private fun getAllChildren(parent: LinearLayout): List<View> {
        val childCount = parent.childCount
        val buttonList: MutableList<View> = ArrayList()
        for (i in 0 until childCount)
            buttonList.add(parent.getChildAt(i))

        return buttonList
    }

    override fun onClick(view: View) {
        coordGetterController.onClickForInstall(view)
    }

    override fun onResume() {
        super.onResume()
        coordGetterController.startListenButtons()
    }

    override fun onRestart() {
        super.onRestart()
        coordGetterController.startListenButtons()
    }
}


