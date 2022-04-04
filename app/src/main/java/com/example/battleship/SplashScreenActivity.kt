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

class SplashScreenActivity : AppCompatActivity() {

    lateinit var animator0: ObjectAnimator

    private val animator1 = ValueAnimator().setDuration(1_000)
    private val letterList1 = listOf(" ", "Q", "U", "O", "P", "X", "S")

    private val animator2 = ValueAnimator().setDuration(1_000)
    private val letterList2 = listOf(" ", "Y", "M", "G", "R", "A", "H")

    private val animator3 = ValueAnimator().setDuration(1_000)
    private val letterList3 = listOf(" ", "D", "Y", "M", "G", "R", "C")

    private val animator4 = ValueAnimator().setDuration(1_000)
    private val letterList4 = listOf(" ", "G", "R", "A", "Q", "U", "H")

    lateinit var animator5: ObjectAnimator
    lateinit var animator6: ObjectAnimator

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

        animator1.setObjectValues("", "S")
        animator2.setObjectValues("", "H")
        animator3.setObjectValues("", "C")
        animator4.setObjectValues("", "H")

        animator1.addUpdateListener { animation ->
            letter1.text = animation.animatedValue as String
        }
        animator2.addUpdateListener { animation ->
            letter2.text = animation.animatedValue as String
        }
        animator3.addUpdateListener { animation ->
            letter3.text = animation.animatedValue as String
        }
        animator4.addUpdateListener { animation ->
            letter4.text = animation.animatedValue as String
        }

        animator0 = ObjectAnimator
            .ofFloat(
                root, "alpha", 0f, 1f
            ).setDuration(3_000)


        animator1.setEvaluator { fraction, _, _ ->
            // fraction: 0 - начало анимации, 1 - конец
            val letterPosition = (fraction * letterList1.size).toInt()
            if (letterPosition > letterList1.size - 1) {
                letterList1[letterList1.size - 1]
            } else {
                letterList1[letterPosition]
            }
        }
        animator2.setEvaluator { fraction, _, _ ->
            // fraction: 0 - начало анимации, 1 - конец
            val letterPosition = (fraction * letterList2.size).toInt()
            if (letterPosition > letterList2.size - 1) {
                letterList2[letterList1.size - 1]
            } else {
                letterList2[letterPosition]
            }
        }
        animator3.setEvaluator { fraction, _, _ ->
            // fraction: 0 - начало анимации, 1 - конец
            val letterPosition = (fraction * letterList3.size).toInt()
            if (letterPosition > letterList3.size - 1) {
                letterList3[letterList1.size - 1]
            } else {
                letterList3[letterPosition]
            }
        }
        animator4.setEvaluator { fraction, _, _ ->
            // fraction: 0 - начало анимации, 1 - конец
            val letterPosition = (fraction * letterList4.size).toInt()
            if (letterPosition > letterList4.size - 1) {
                letterList4[letterList4.size - 1]
            } else {
                letterList4[letterPosition]
            }
        }

        animator5 = ObjectAnimator
            .ofFloat(
                letter5, "alpha", 0f, 1f
            ).setDuration(3_000)

        animator6 = ObjectAnimator
            .ofFloat(
                root, "alpha", 1f, 0f
            ).setDuration(3_000)

        startAnimation()
    }

    fun startAnimation() {
        animator0.start()
        animator0.doOnEnd {
            animator1.start()
            animator1.doOnEnd {
                animator2.start()
                animator2.doOnEnd {
                    animator3.start()
                    animator3.doOnEnd {
                        animator4.start()
                        animator4.doOnEnd {
                            animator5.start()
                            animator5.doOnEnd {
                                animator6.start()
                                animator6.doOnEnd {
                                    startActivity(
                                        Intent(this, MainMenuActivity::class.java)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}