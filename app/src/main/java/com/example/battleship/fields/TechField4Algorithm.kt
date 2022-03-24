package com.example.battleship.fields

import com.example.battleship.boats.Boat
import com.example.battleship.coordinates.Coordinate

class TechField4Algorithm() : TechField(null) {
    constructor(
        scoredList: ArrayList<Coordinate>,
        failList: ArrayList<Coordinate>,
        boatList: Map<Int, Boat>
    ) : this() {
        this.scoredList = scoredList.clone() as ArrayList<Coordinate>
        this.failList = failList.clone() as ArrayList<Coordinate>
        this.boatList = boatList.toMutableMap()

    }
}