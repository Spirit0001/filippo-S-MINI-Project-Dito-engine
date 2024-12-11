package com.innoveworkshop.gametest.engine

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.pow

class Circle(x: Float, y: Float, var radius: Float, color: Int) : GameObject(x, y), Caged {
    // Set up the paint.
    var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawCircle(position.x, position.y, radius, paint)
    }

    override fun hitLeftWall(): Boolean {
        return (position.x - radius) <= gameSurface!!.width
    }

    override fun hitRightWall(): Boolean {
        return (position.x + radius) >= gameSurface!!.width
    }

    override val isFloored: Boolean
        get() = (position.y + radius) >= gameSurface!!.height
<<<<<<< Updated upstream
=======

    fun Circle.collidesWith(rect: Rectangle): Boolean {
        val circleDistanceX = Math.abs(this.position.x - rect.position.x - rect.width / 2)
        val circleDistanceY = Math.abs(this.position.y - rect.position.y - rect.height / 2)

        if (circleDistanceX > (rect.width / 2 + this.radius)) return false
        if (circleDistanceY > (rect.height / 2 + this.radius)) return false

        if (circleDistanceX <= (rect.width / 2)) return true
        if (circleDistanceY <= (rect.height / 2)) return true

        val cornerDistanceSq = ((circleDistanceX - rect.width / 2).toDouble().pow(2.0) +
                (circleDistanceY - rect.height / 2).toDouble().pow(2.0))

        return cornerDistanceSq <= this.radius.toDouble().pow(2.0)
    }







>>>>>>> Stashed changes
}
