package com.example.battleship

import android.app.Application
import android.content.res.Resources
import android.util.Log
import boats.BoatFactory
import fields.TechField

class MyApplication : Application() {

    companion object {
        private lateinit var appInstance: MyApplication
        fun getAppInstance() : MyApplication {

               Log.d("zzz", "null")

           return appInstance
        }
    }

    val displayWidth = Resources.getSystem().displayMetrics.widthPixels.toString()
    val displayHeight = Resources.getSystem().displayMetrics.heightPixels.toString()
    val humanTechField = TechField()
    val humanBoatFactory = BoatFactory(humanTechField)
    val robotTechField = TechField()
    val robotBoatFactory = BoatFactory(robotTechField)


    override fun onCreate() {
        appInstance = this
        Log.d("zzz", "onCreate1")
        super.onCreate()
        Log.d("zzz", "onCreate2")

        Log.d("zzz", "onCreate3")
    }
}