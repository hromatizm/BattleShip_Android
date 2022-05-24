package com.example.battleship.record

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE = "battle.db"
private const val DBVERSION = 1

class RecordHelper(ctx: Context) : SQLiteOpenHelper(ctx, DATABASE, null, DBVERSION) {

    companion object {
        const val TABLE_RECORDS = "records"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DATE = "date"
        const val COLUMN_TURNS = "turns"
        const val COLUMN_RESULT = "result"

        private var instance: RecordHelper? = null
        fun getInstance(ctx: Context) = instance ?: RecordHelper(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {

        val create = " create table $TABLE_RECORDS ( " +
                "$COLUMN_ID integer primary key autoincrement , " +
                "$COLUMN_NAME text not null , " +
                "$COLUMN_DATE integer not null , " +
                "$COLUMN_TURNS integer not null, " +
                "$COLUMN_RESULT integer not null); "
        db.execSQL(create)

        db.execSQL(
            " insert into $TABLE_RECORDS (" +
                    "$COLUMN_NAME, $COLUMN_DATE, $COLUMN_TURNS,$COLUMN_RESULT ) " +
                    "values ('Jimmy Hendrix', '${System.currentTimeMillis()}', '42', '1') ; "
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun saveRecord(name: String, ){

    }
}



val Context.database: RecordHelper
    get() = RecordHelper.getInstance(this)