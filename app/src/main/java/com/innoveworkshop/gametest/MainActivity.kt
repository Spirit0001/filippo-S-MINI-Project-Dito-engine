package com.innoveworkshop.gametest

import android.graphics.Color
import android.os.Bundle
import android.view.View
<<<<<<< Updated upstream
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.innoveworkshop.gametest.assets.DroppingRectangle
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import com.innoveworkshop.gametest.engine.GameSurface
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class MainActivity : AppCompatActivity() {
    protected var gameSurface: GameSurface? = null
    protected var upButton: Button? = null
    protected var downButton: Button? = null
    protected var leftButton: Button? = null
    protected var rightButton: Button? = null

=======
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.innoveworkshop.gametest.engine.*
import kotlin.math.pow

class MainActivity : AppCompatActivity(), SensorEventListener {
    protected var gameSurface: GameSurface? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var circle: Circle? = null
    private val mazeWalls = mutableListOf<Rectangle>()
>>>>>>> Stashed changes
    protected var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        game = Game()
        gameSurface!!.setRootGameObject(game)

        setupControls()
    }

    private fun setupControls() {
        upButton = findViewById<View>(R.id.up_button) as Button
        upButton!!.setOnClickListener { game!!.circle!!.position.y -= 10f }

        downButton = findViewById<View>(R.id.down_button) as Button
        downButton!!.setOnClickListener { game!!.circle!!.position.y += 10f }

        leftButton = findViewById<View>(R.id.left_button) as Button
        leftButton!!.setOnClickListener { game!!.circle!!.position.x -= 10f }

<<<<<<< Updated upstream
        rightButton = findViewById<View>(R.id.right_button) as Button
        rightButton!!.setOnClickListener { game!!.circle!!.position.x += 10f }
=======
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // This method processes accelerometer data
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0]
            val upDown = event.values[1]

            circle?.apply {
                position.x += -sides * 2  // Move horizontally based on tilt
                position.y += upDown * 2 // Move vertically based on tilt

                gameSurface?.let { checkWallCollision(this, mazeWalls) } // Ensure the circle stays within bounds
            }
        }
    }

    // This method is required but not used here
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this implementation
>>>>>>> Stashed changes
    }

    private fun checkWallCollision(circle: Circle, walls: List<Rectangle>) {
        walls.forEach { wall ->
            if (circle.collidesWith(wall)) {
                // Determine overlap
                val dx = circle.position.x - wall.position.x
                val dy = circle.position.y - wall.position.y

                val overlapX = circle.radius + wall.width / 2 - Math.abs(dx)
                val overlapY = circle.radius + wall.height / 2 - Math.abs(dy)

                if (overlapX > 0 && overlapY > 0) {
                    if (overlapX < overlapY) {
                        // Resolve horizontal collision
                        circle.position.x += if (dx > 0) overlapX else -overlapX
                    } else {
                        // Resolve vertical collision
                        circle.position.y += if (dy > 0) overlapY else -overlapY
                    }
                }
            }
        }
    }

    inner class Game : GameObject() {
        var circle: Circle? = null

        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            circle = Circle(
                (surface!!.width / 2).toFloat(),
                (surface.height / 2).toFloat(),
<<<<<<< Updated upstream
                100f,
=======
                20f,
                50f,
>>>>>>> Stashed changes
                Color.RED
            )
            surface.addGameObject(circle!!)

<<<<<<< Updated upstream
            surface.addGameObject(
                Rectangle(
                    Vector((surface.width / 3).toFloat(), (surface.height / 3).toFloat()),
                    200f, 100f, Color.GREEN
                )
            )

            surface.addGameObject(
                DroppingRectangle(
                    Vector((surface.width / 3).toFloat(), (surface.height / 3).toFloat()),
                    100f, 100f, 10f, Color.rgb(128, 14, 80)
                )
            )
=======
            // Create maze walls based on a simple layout
            val mazeLayout = arrayOf(
                "1111111111",
                "1000000001",
                "1011111101",
                "1010000101",
                "1010111101",
                "1010000001",
                "1111111111"
            )

            val cellWidth = surface.width / mazeLayout[0].length
            val cellHeight = surface.height / mazeLayout.size

            mazeLayout.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, cell ->
                    if (cell == '1') {
                        val wall = Rectangle(
                            Vector((colIndex * cellWidth).toFloat(), (rowIndex * cellHeight).toFloat()),
                            cellWidth.toFloat(),
                            cellHeight.toFloat(),
                            Color.BLACK
                        )
                        mazeWalls.add(wall)
                        surface.addGameObject(wall)
                    }
                }
            }
>>>>>>> Stashed changes
        }

        override fun onFixedUpdate() {
            super.onFixedUpdate()

            if (!circle!!.isFloored && !circle!!.hitRightWall() && !circle!!.isDestroyed) {
                circle!!.setPosition(circle!!.position.x + 1, circle!!.position.y + 1)
            } else {
                circle!!.destroy()
            }
        }
    }
<<<<<<< Updated upstream
}
=======
}

// Extension function to check collision between circle and rectangle
fun Circle.collidesWith(rect: Rectangle): Boolean {
    val circleDistanceX = Math.abs(this.position.x - (rect.position.x + rect.width / 2))
    val circleDistanceY = Math.abs(this.position.y - (rect.position.y + rect.height / 2))

    if (circleDistanceX > (rect.width / 2 + this.radius)) return false
    if (circleDistanceY > (rect.height / 2 + this.radius)) return false

    if (circleDistanceX <= (rect.width / 2)) return true
    if (circleDistanceY <= (rect.height / 2)) return true

    val cornerDistanceSq = ((circleDistanceX - rect.width / 2).toDouble().pow(2.0) +
            (circleDistanceY - rect.height / 2).toDouble().pow(2.0))

    return cornerDistanceSq <= this.radius.toDouble().pow(2.0)
}
>>>>>>> Stashed changes
