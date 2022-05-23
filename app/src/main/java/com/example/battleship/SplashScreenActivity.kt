package com.example.battleship

import android.animation.*
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import com.example.battleship.viewmodel.SplashScreenVM

class SplashScreenActivity : AppCompatActivity() {

    lateinit var root: ConstraintLayout
    lateinit var letter1: TextView
    lateinit var letter2: TextView
    lateinit var letter3: TextView
    lateinit var letter4: TextView
    lateinit var letter5: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setContentView(R.layout.activity_splash_screen)

////////////////////////////////////////////////////////////////////////////////////////////////////

        root = findViewById(R.id.splash_root)

        letter1 = findViewById(R.id.letter1)
        letter2 = findViewById(R.id.letter2)
        letter3 = findViewById(R.id.letter3)
        letter4 = findViewById(R.id.letter4)
        letter5 = findViewById(R.id.letter5)

        val viewModel = ViewModelProvider(this)[SplashScreenVM::class.java]
        viewModel.animator0Data.observe(this) { float ->
            root.alpha = float
        }

        viewModel.animator1Data.observe(this) { string ->
            letter1.text = string
        }

        viewModel.animator2Data.observe(this) { string ->
            letter2.text = string
        }

        viewModel.animator3Data.observe(this) { string ->
            letter3.text = string
        }

        viewModel.animator4Data.observe(this) { string ->
            letter4.text = string
        }

        viewModel.animator5Data.observe(this) { float ->
            letter5.alpha = float
        }

        viewModel.animator6Data.observe(this) { float ->
            root.alpha = float
        }

        viewModel.isAnimationFinished.observe(this) { boolean ->
            if (boolean)
                startActivity(Intent(this, MainMenuActivity::class.java))
        }
    }
}