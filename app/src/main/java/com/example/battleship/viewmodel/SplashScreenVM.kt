package com.example.battleship.viewmodel

import android.animation.ValueAnimator
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.properties.Delegates

class SplashScreenVM : ViewModel() {

    val animator0Data = MutableLiveData<Float>()
    var alphaRoot: Float by Delegates.observable(0f) { _, _, new ->
        animator0Data.postValue(new)
    }
    val animator0 = ValueAnimator()

    val animator1Data = MutableLiveData<String>()
    var letter1: String by Delegates.observable(" ") { _, _, new ->
        animator1Data.postValue(new)
    }
    val letterList1 = listOf(" ", "Q", "U", "O", "P", "X", "S")
    val animator1 = ValueAnimator()

    val animator2Data = MutableLiveData<String>()
    var letter2: String by Delegates.observable(" ") { _, _, new ->
        animator2Data.postValue(new)
    }
    val letterList2 = listOf(" ", "Y", "M", "G", "R", "A", "H")
    val animator2 = ValueAnimator()

    val animator3Data = MutableLiveData<String>()
    var letter3: String by Delegates.observable(" ") { _, _, new ->
        animator3Data.postValue(new)
    }
    val letterList3 = listOf(" ", "D", "Y", "M", "G", "R", "C")
    var animator3 = ValueAnimator()

    val animator4Data = MutableLiveData<String>()
    var letter4: String by Delegates.observable(" ") { _, _, new ->
        animator4Data.postValue(new)
    }
    val letterList4 = listOf(" ", "G", "R", "A", "Q", "U", "H")
    val animator4 = ValueAnimator()

    val animator5Data = MutableLiveData<Float>()
    var alphaLetter5: Float by Delegates.observable(0f) { _, _, new ->
        animator5Data.postValue(new)
    }
    val animator5 = ValueAnimator()

    val animator6Data = MutableLiveData<Float>()
    val animator6 = ValueAnimator()

    val isAnimationFinished = MutableLiveData(false)

    init {
        with(animator0) {
            duration = 3_000
            setObjectValues(0f, 1f)
            addUpdateListener { animation ->
                alphaRoot = animation.animatedValue as Float
            }
            setEvaluator { fraction, _, _ -> fraction * 1f }
        }

        with(animator1) {
            duration = 1_000
            setObjectValues("", "S")
            addUpdateListener { animation ->
                letter1 = animation.animatedValue as String
            }
            setEvaluator { fraction, _, _ ->
                val letterPosition = (fraction * letterList1.size).toInt()
                if (letterPosition > letterList1.size - 1) {
                    letterList1[letterList1.size - 1]
                } else {
                    letterList1[letterPosition]
                }
            }
        }

        with(animator2) {
            duration = 1_000
            setObjectValues("", "H")
            addUpdateListener { animation ->
                letter2 = animation.animatedValue as String
            }
            setEvaluator { fraction, _, _ ->
                val letterPosition = (fraction * letterList2.size).toInt()
                if (letterPosition > letterList2.size - 1) {
                    letterList2[letterList2.size - 1]
                } else {
                    letterList2[letterPosition]
                }
            }
        }

        with(animator3) {
            duration = 1_000
            setObjectValues("", "C")
            addUpdateListener { animation ->
                letter3 = animation.animatedValue as String
            }
            setEvaluator { fraction, _, _ ->
                val letterPosition = (fraction * letterList3.size).toInt()
                if (letterPosition > letterList3.size - 1) {
                    letterList3[letterList3.size - 1]
                } else {
                    letterList3[letterPosition]
                }
            }
        }

        with(animator4) {
            duration = 1_000
            setObjectValues("", "H")
            addUpdateListener { animation ->
                letter4 = animation.animatedValue as String
            }
            setEvaluator { fraction, _, _ ->
                val letterPosition = (fraction * letterList4.size).toInt()
                if (letterPosition > letterList4.size - 1) {
                    letterList4[letterList4.size - 1]
                } else {
                    letterList4[letterPosition]
                }
            }
        }

        with(animator5) {
            duration = 3_000
            setObjectValues(0f, 1f)
            addUpdateListener { animation ->
                alphaLetter5 = animation.animatedValue as Float
            }
            setEvaluator { fraction, _, _ -> fraction * 1f }
        }

        with(animator6) {
            duration = 3_000
            setObjectValues(1f, 0f)
            addUpdateListener { animation ->
                alphaRoot = animation.animatedValue as Float
            }
            setEvaluator { fraction, _, _ -> 1f - fraction }
        }

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
                                    isAnimationFinished.postValue(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}