package com.example.battleship.record

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.battleship.R
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue

class RecordHolder(recordLineView: View) : RecyclerView.ViewHolder(recordLineView) {

    private val number = recordLineView.findViewById<TextView>(R.id.number)
    private val name = recordLineView.findViewById<TextView>(R.id.name)
    private val date = recordLineView.findViewById<TextView>(R.id.date)
    private val turns = recordLineView.findViewById<TextView>(R.id.turns)
    private val result = recordLineView.findViewById<TextView>(R.id.result)
    var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun bind(recordLine: RecordLine) {
        number.text = recordLine.number.toString()
        name.text = recordLine.name
        date.text = sdf.format(recordLine.date)
        // При проигрыше отрицательное число в БД для корректной сортировки, поэтому тут модулю:
        turns.text = recordLine.turns.absoluteValue.toString()
        result.text = when(recordLine.result){
            0 -> "Fail"
            1 -> "Win"
            else -> "No"
        }
    }
}