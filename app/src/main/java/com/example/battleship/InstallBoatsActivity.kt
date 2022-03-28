package com.example.battleship

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.*
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.battleship.boats.BoatInstaller
import com.example.battleship.coordinates.HumanCoordGetter
import com.example.battleship.seabutton.HumanButton
import kotlin.properties.Delegates

class InstallBoatsActivity : AppCompatActivity(), View.OnClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var app: MyApplication

    lateinit var header: TextView
    lateinit var welcomeText: TextView
    var textOfWelcomeText = ""

    lateinit var makeHeaderVisible: ObjectAnimator
    lateinit var makeWelcomeTextVisible: ObjectAnimator
    var toSet = AnimatorSet()

    private lateinit var humanFieldView: LinearLayout
    private lateinit var radioButtonHorizontal: RadioButton
    private lateinit var radioButtonVertical: RadioButton

    //    private lateinit var confirmToggleButton: Button
//    private lateinit var indexToggleButton: Button
    private lateinit var root: View

    private lateinit var coordGetterController: HumanCoordGetter

    private lateinit var humanInstaller: BoatInstaller
    private lateinit var robotInstaller: BoatInstaller

    var animator = ValueAnimator().setDuration(1_000)
    private var endValue by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        app = MyApplication.getAppInstance()

        app.isFirstStartOfMainMenuActivity = false

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

        root = findViewById(R.id.root)
        header = findViewById(R.id.header)

        welcomeText = findViewById(R.id.welcome_text)
//        confirmToggleButton = findViewById(R.id.confirm_toggle)
//        indexToggleButton = findViewById(R.id.index_toggle)

        humanInstaller = BoatInstaller(app.humanBoatFactory, welcomeText, this)
        coordGetterController = HumanCoordGetter(this, this, humanInstaller)

        robotInstaller = BoatInstaller(app.robotBoatFactory, null, this)

// Подгоняем поле под размер экрана
        humanFieldView = findViewById(R.id.human_field)
        app.humanFieldView = humanFieldView

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

      //  welcomeText.text = textOfWelcomeText

        coordGetterController.parent = this

        makeHeaderVisible = ObjectAnimator
            .ofFloat(
                header, "alpha", 0f, 1f
            ).setDuration(1_000)

        makeWelcomeTextVisible = ObjectAnimator
            .ofFloat(
                welcomeText, "alpha", 0f, 1f
            ).setDuration(1_000)

        toSet.playTogether(makeHeaderVisible, makeWelcomeTextVisible)
        startInstallBoats()
    }

    private fun startInstallBoats() {
        if (!app.isHumanBoatInstalled) {
            humanInstaller.printWelcome(app.listOfHumanBoatsId[0])
        } else {
            humanInstaller.printReady()
        }
        if(app.isFirstStartOfInstallBoatsActivity) {
            app.restoreHumanButtonMap()
            app.humanTechField.fieldUiUpdate()
        }
        app.isFirstStartOfInstallBoatsActivity = false
    }

    private fun animationInit() {
        animator.setObjectValues(0, endValue)
        animator.addUpdateListener { animation ->
            val seaLines = app.getAllChildren(humanFieldView)
            for (line in seaLines) {
                app.getAllChildren(line as LinearLayout).forEach {
                    it.layoutParams.height = animation.animatedValue as Int
                    it.layoutParams.width = it.layoutParams.height
                }
                humanFieldView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = if (!app.isIndex) {
                        Gravity.CENTER_HORIZONTAL
                    } else {
                        Gravity.START
                    }
                }
                app.getAllChildren(humanFieldView).forEach { it.requestLayout() }
            }
        }

        animator.setEvaluator(object : TypeEvaluator<Int> {
            // fraction: 0 - начало анимации, 1 - конец
            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                return (fraction * endValue).toInt()
            }
        })
    }

    override fun onClick(view: View) {
        if (!app.isHumanBoatInstalled) {
            coordGetterController.onClickForInstall(view)
        } else {
            humanInstaller.printReady()
        }
    }

//    fun confirmToggle(view: View) {
//        app.isConfirm = !app.isConfirm
//        setOptionsButtonsFont()
//    }
//
//    fun indexToggle(view: View) {
//        app.isIndex = !app.isIndex
//        setOptionsButtonsFont()
//        app.toggleIndex(humanField)
//        app.fitScreenSize(humanField)
//    }

    override fun onStart() {
        super.onStart()
        coordGetterController.startListenButtons()
    }

    override fun onResume() {
        super.onResume()

        hideSystemUI(root)
//        setOptionsButtonsFont()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        app.isIndex = preferences.getBoolean("show_index", true)
        app.isConfirm = preferences.getBoolean("confirm_install", true)
        preferences.registerOnSharedPreferenceChangeListener(this)

        app.fitScreenSize(humanFieldView, app.isHumanFieldActive)
        app.toggleIndex(humanFieldView)
        endValue = if (app.isIndex) {
            app.seaButtonSizeWithIndex
        } else {
            app.seaButtonSizeWithoutIndex
        }

        animationInit()
        animator.doOnStart {
            header.alpha = 0f
            welcomeText.alpha = 0f
        }
        animator.start()
        animator.doOnEnd {
            toSet.start()
        }
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        HumanButton.buttonCounter = 0
        app.saveHumanButtonMap()
    }

    private fun hideSystemUI(mainContainer: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, mainContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI(mainContainer: View) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            mainContainer
        ).show(WindowInsetsCompat.Type.systemBars())
    }

//    private fun setOptionsButtonsFont() {
//        if (app.isConfirm) {
//            confirmToggleButton.apply {
//                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
//            }
//        } else {
//            confirmToggleButton.apply {
//                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            }
//        }
//        if (app.isIndex) {
//            indexToggleButton.apply {
//                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
//            }
//        } else {
//            indexToggleButton.apply {
//                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            }
//        }
//    }

    fun showSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        app.isIndex = sharedPreferences.getBoolean("show_index", true)
        app.isConfirm = sharedPreferences.getBoolean("confirm_install", true)
    }

    fun goBackFromInstall(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}




