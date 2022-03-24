package com.example.battleship

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.battleship.boats.BoatInstaller
import com.example.battleship.seabutton.HumanButton
import com.example.battleship.coordinates.HumanCoordGetter

class InstallBoatsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var app: MyApplication

    lateinit var welcomeText: TextView
    private lateinit var humanField: LinearLayout
    private lateinit var radioButtonHorizontal: RadioButton
    private lateinit var radioButtonVertical: RadioButton
    private lateinit var confirmToggleButton: Button

    private lateinit var coordGetterController: HumanCoordGetter

    private lateinit var humanInstaller: BoatInstaller
    private lateinit var robotInstaller: BoatInstaller

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        app = MyApplication.getAppInstance()

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

//////////////////////////////////////////////////////////////////

        welcomeText = findViewById(R.id.welcome_text)
        confirmToggleButton = findViewById(R.id.confirm_toggle)

        humanInstaller = BoatInstaller(app.humanBoatFactory, welcomeText, this)
        coordGetterController = HumanCoordGetter(this, this, humanInstaller)

        robotInstaller = BoatInstaller(app.robotBoatFactory, null, this)

// Подгоняем поле под размер экрана
        humanField = findViewById(R.id.human_field)

        app.fitScreenSize(humanField)

// RadioButton - какой корабль будем ставить: вертиальный или горизонтальный

        radioButtonHorizontal = findViewById(R.id.horizontal_button)
        radioButtonVertical = findViewById(R.id.vertical_button)
        radioButtonVertical.isChecked = app.isVertical
        radioButtonHorizontal.isChecked = !app.isVertical

        radioButtonVertical.setOnCheckedChangeListener { _, isChecked ->
            app.isVertical = when {
                isChecked -> true
                else -> false
            }
        }
// Поле для вывода на экран приглашения поставить корабль

        coordGetterController.parent = this

        startInstallBoats()
    }

    private fun startInstallBoats() {
        if (!app.isHumanBoatInstalled) {
            humanInstaller.printWelcome(app.listOfHumanBoatsId[0])
        } else {
            humanInstaller.printReady()
        }
        app.restoreHumanButtonMap()
        app.humanTechField.humanFieldUiUpdate()

    }

    override fun onClick(view: View) {
        if (!app.isHumanBoatInstalled) {
            coordGetterController.onClickForInstall(view)
        } else {
            humanInstaller.printReady()
        }
    }

    fun confirmToggle(view: View) {
        if (app.isConfirm) {
            app.isConfirm = false
            confirmToggleButton.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        } else {
            app.isConfirm = true
            confirmToggleButton.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        coordGetterController.startListenButtons()
    }

    override fun onResume() {
        super.onResume()
        coordGetterController.startListenButtons()
        if (app.isConfirm) {
            confirmToggleButton.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        } else {
            confirmToggleButton.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HumanButton.buttonCounter = 0
        app.saveHumanButtonMap()
    }
}




