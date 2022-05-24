package com.example.battleship

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.battleship.record.RecordAdapter
import com.example.battleship.record.RecordHelper
import com.example.battleship.record.database

class RecordActivity : AppCompatActivity() {

    lateinit var recordList: RecyclerView
    lateinit var adapter: RecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_table)

        recordList = findViewById(R.id.record_list)
        recordList.layoutManager = LinearLayoutManager(this)
        recordList.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        adapter = RecordAdapter(null)
        recordList.adapter = adapter

//        Thread.sleep(2_000)
//        val values = ContentValues()
//        values.put(RecordHelper.COLUMN_NAME, "John Lennon")
//        values.put(RecordHelper.COLUMN_DATE, System.currentTimeMillis())
//        values.put(RecordHelper.COLUMN_TURNS, -29)
//        values.put(RecordHelper.COLUMN_RESULT, 0)
//        database.writableDatabase.insert(
//            RecordHelper.TABLE_RECORDS,
//            null, // Параметр - возможность использвоания null значений
//            values
//        )
        updateCursor()
    }

    private fun updateCursor() {
        val orderBy =
            "${RecordHelper.COLUMN_RESULT} DESC," +
                    "${RecordHelper.COLUMN_TURNS}," +
                    "${RecordHelper.COLUMN_DATE}"
        val cursor = database.readableDatabase.query(
            RecordHelper.TABLE_RECORDS,
            null,
            null,
            null,
            null,
            null,
            orderBy
        )
        adapter.updateCursor(cursor)
    }
}