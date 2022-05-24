package com.example.battleship.viewmodel

import android.animation.ValueAnimator
import androidx.core.animation.doOnEnd
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashScreenVM : ViewModel() {

    val animator0Data = MutableLiveData<Float>()
    val animator1Data = MutableLiveData<String>()
    val animator2Data = MutableLiveData<String>()
    val animator3Data = MutableLiveData<String>()
    val animator4Data = MutableLiveData<String>()
    val animator5Data = MutableLiveData<Float>()
    val animator6Data = MutableLiveData<Float>()

    val animator0 = ValueAnimator()
    val animator1 = ValueAnimator()
    val animator2 = ValueAnimator()
    var animator3 = ValueAnimator()
    val animator4 = ValueAnimator()
    val animator5 = ValueAnimator()
    val animator6 = ValueAnimator()

    val letterList1 = listOf(" ", "Q", "U", "O", "P", "X", "S")
    val letterList2 = listOf(" ", "Y", "M", "G", "R", "A", "H")
    val letterList3 = listOf(" ", "D", "Y", "M", "G", "R", "C")
    val letterList4 = listOf(" ", "G", "R", "A", "Q", "U", "H")

    val isAnimationFinished = MutableLiveData(false)

    init {
        fun lettersChanger(list: List<String>, fraction: Float): String {
            val letterPosition = (fraction * list.size).toInt()
            return if (letterPosition > list.size - 1) {
                list[list.size - 1]
            } else {
                list[letterPosition]
            }
        }

        with(animator0) {
            duration = 2_000
            setObjectValues(0f, 1f)
            addUpdateListener { animation ->
                animator0Data.postValue(animation.animatedValue as Float)
            }
        }

        with(animator1) {
            duration = 1_000
            setObjectValues("", "S")
            addUpdateListener { animation ->
                animator1Data.postValue(animation.animatedValue as String)
            }
            setEvaluator { fraction, _, _ ->
                lettersChanger(letterList1, fraction)
            }
        }

        with(animator2) {
            duration = 1_000
            setObjectValues("", "H")
            addUpdateListener { animation ->
                animator2Data.postValue(animation.animatedValue as String)
            }
            setEvaluator { fraction, _, _ ->
                lettersChanger(letterList2, fraction)
            }
        }

        with(animator3) {
            duration = 1_000
            setObjectValues("", "C")
            addUpdateListener { animation ->
                animator3Data.postValue(animation.animatedValue as String)
            }
            setEvaluator { fraction, _, _ ->
                lettersChanger(letterList3, fraction)
            }
        }

        with(animator4) {
            duration = 1_000
            setObjectValues("", "H")
            addUpdateListener { animation ->
                animator4Data.postValue(animation.animatedValue as String)
            }
            setEvaluator { fraction, _, _ ->
                lettersChanger(letterList4, fraction)
            }
        }

        with(animator5) {
            duration = 2_000
            setObjectValues(0f, 1f)
            addUpdateListener { animation ->
                animator5Data.postValue(animation.animatedValue as Float)
            }
        }

        with(animator6) {
            duration = 2_000
            setObjectValues(1f, 0f)
            addUpdateListener { animation ->
                animator6Data.postValue(animation.animatedValue as Float)
            }
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