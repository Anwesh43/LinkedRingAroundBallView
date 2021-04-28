package com.example.ringaroundballview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.content.Context

val colors : Array<Int> = arrayOf(
    "",
    "",
    "",
    "",
    ""
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 4
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val deg : Float = 360f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")

