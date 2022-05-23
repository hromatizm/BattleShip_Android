package com.example.battleship.record

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.battleship.R
import java.util.*

class RecordAdapter(private var cursor: Cursor?) : RecyclerView.Adapter<RecordHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.record_line_view, parent, false)
        return RecordHolder(view)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        val c = cursor
        if (c != null) {
            c.moveToPosition(position)
            val recordLine = RecordLine(
                number = position + 1,
                name = c.getString(c.getColumnIndex(RecordHelper.COLUMN_NAME)),
                date = Date(c.getLong(c.getColumnIndex(RecordHelper.COLUMN_DATE))),
                turns = c.getInt(c.getColumnIndex(RecordHelper.COLUMN_TURNS)),
                result = c.getInt(c.getColumnIndex(RecordHelper.COLUMN_RESULT))
            )
            holder.bind(recordLine)
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    // Чтобы сразу и обновлять курсор, и перерисовывать интерфейс
    fun updateCursor(cursor: Cursor?) {
        this.cursor = cursor
        notifyDataSetChanged()
    }

    fun getCursor() = cursor
}