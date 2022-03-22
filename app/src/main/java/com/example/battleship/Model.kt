package com.example.battleship

import android.content.res.Resources
import boats.BoatFactory
import boats.BoatInstaller
import com.example.battleship.seabutton.HumanButton
import coordinates.HumanCoordGetterController
import fields.TechField
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Model() {
    companion object {
        private var model: Model? = null
        fun getInstance(): Model {
            createInstance()
            return model!!
        }
        private fun createInstance(){
            if(model == null){
                model = Model()
            }
        }

    }


    var isPopupOnScreen = false



//    val robotInstaller = BoatInstaller(robotBoatFactory, false, coordGetterController)
//    companion object {
//        val humanTechField = TechField()
//        val humanBoatFactory = BoatFactory(humanTechField)
//        val humanInstaller = BoatInstaller(humanBoatFactory, true)
//        val robotTechField = TechField()
//        val robotBoatFactory = BoatFactory(robotTechField)
//        val robotInstaller = BoatInstaller(robotBoatFactory, false)
//
////        val robotTurns = Turn(humanTechField, false, View.humanLabel2)
////        val humanTurns = Turn(robotTechField, true, View.robotLabel2)
////        val turnSequence = TurnSequence(listOf(robotTurns, humanTurns))
//
//        fun start(welcomeText: TextView) {
//            humanTechField.buttonMap = HumanButton.buttonMap
////            robotTechField.buttonMap = SeaController.robotButtonMap
//            MainScope().launch {
//                humanInstaller.installAllHuman(listOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14),welcomeText)
////                View.boatInstallFinished()
//
//                robotInstaller.installAllRobot(listOf(41, 31, 32, 21, 22, 23, 11, 12, 13, 14))
////                turnSequence.start()
//            }
//        }
//    }
}