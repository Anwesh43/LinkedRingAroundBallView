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
    "#f44336",
    "#9C27B0",
    "#BF360C",
    "#00C853",
    "#1A237E"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val concFactor : Float = 3.9f
val deg : Float = 360f
val delay : Long = 15
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

class RingAroundBallView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class RABNode(var i : Int, val state : State = State()) {

        private var next : RABNode? = null
        private var prev : RABNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = RABNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRABNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RABNode {
            var curr : RABNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class RingAroundBall(var i : Int) {

        private var curr : RABNode = RABNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : RingAroundBallView) {

        private val rab : RingAroundBall = RingAroundBall(0)
        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            rab.draw(canvas, paint)
            animator.animate {
                rab.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            rab.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : RingAroundBallView {
            val view : RingAroundBallView = RingAroundBallView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
