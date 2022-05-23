package com.example.battleship.record

import java.util.*

class RecordLine(
    val number: Int,
    val name: String,
    val date: Date,
    val turns: Int,
    val result: Int // 1 - Win, 0 - Fail
)