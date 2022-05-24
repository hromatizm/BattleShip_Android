package com.example.battleship.record

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.example.battleship.MyApplication

object RecordSaver {

    @SuppressLint("StaticFieldLeak")
    val app = MyApplication.getAppInstance()

    fun save(ctx: Context) {
        val userName = app.userName
        val gameResult = if (app.isHumanWin) {
            1
        } else {
            0
        }
        val turns = if (app.isHumanWin) {
            app.turnsCounter
        } else {
            -1 * app.turnsCounter
        }
        val values = ContentValues()
        values.put(RecordHelper.COLUMN_NAME, userName)
        values.put(RecordHelper.COLUMN_DATE, System.currentTimeMillis())
        values.put(RecordHelper.COLUMN_TURNS, turns)
        values.put(RecordHelper.COLUMN_RESULT, gameResult)
        ctx.database.writableDatabase.insert(
            RecordHelper.TABLE_RECORDS,
            null, // Параметр - возможность использвоания null значений
            values
        )
    }
}