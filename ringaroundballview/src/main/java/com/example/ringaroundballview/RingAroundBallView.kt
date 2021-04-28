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
val concFactor : Float = 3.9f
val deg : Float = 360f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")


fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawRingAroundBall(scale : Float, w : Float, h : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val size : Float = Math.min(w, h) / sizeFactor
    val r : Float = size / 2
    val concSize : Float = Math.min(w, h) / concFactor
    val sf : Float = scale.sinify()
    val sf1 : Float = scale.divideScale(0, parts)
    val sf2 : Float = scale.divideScale(1, parts)
    val sf3 : Float = scale.divideScale(2, parts)
    val sf4 : Float = scale.divideScale(3, parts)
    save()
    translate(w / 2, h / 2)
    paint.style = Paint.Style.FILL
    drawCircle((-w / 2 - r) * (1 - sf1), (-h / 2 - r) * sf4, r, paint)
    paint.style = Paint.Style.STROKE
    drawArc(
        RectF(-concSize / 2, -concSize / 2, concSize / 2, concSize / 2),
        deg * sf3,
        deg * (sf2 - sf3),
        false,
        paint
    )
    restore()
}

fun Canvas.drawRABNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawRingAroundBall(scale, w, h, paint)
}

