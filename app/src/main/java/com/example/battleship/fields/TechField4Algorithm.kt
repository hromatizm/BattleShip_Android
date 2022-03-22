package fields

import boats.Boat
import com.example.battleship.coordinates.Coordinate

class TechField4Algorithm() : TechField() {
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