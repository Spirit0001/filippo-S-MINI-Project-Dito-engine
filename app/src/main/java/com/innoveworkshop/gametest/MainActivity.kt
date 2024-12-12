package com.innoveworkshop.gametest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.innoveworkshop.gametest.assets.FrictionFloor
import com.innoveworkshop.gametest.engine.*



class MainActivity : AppCompatActivity(), SensorEventListener {
    protected var gameSurface: GameSurface? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var circle: Circle? = null
    private val mazeWalls = mutableListOf<Rectangle>()
    protected var game: Game? = null
    private var exitPoint: Rectangle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        game = Game()
        gameSurface!!.setRootGameObject(game)

        setUpSensorStuff()
    }

    private fun setUpSensorStuff() {
        // Initialize SensorManager and Accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Toast.makeText(this, "No accelerometer found on this device.", Toast.LENGTH_LONG).show()
            Log.e("SensorError", "No accelerometer found on this device.")
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

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

                gameSurface?.let {
                    checkWallCollision(this, mazeWalls)
                    checkExitCollision(this, exitPoint)
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun checkWallCollision(circle: Circle, walls: List<Rectangle>) {
        walls.forEach { wall ->
            if (circle.collidesWith(wall)) {
                // Resolve overlap by calculating the nearest edge and pushing the circle out
                val nearestX = circle.position.x.coerceIn(wall.position.x, wall.position.x + wall.width)
                val nearestY = circle.position.y.coerceIn(wall.position.y, wall.position.y + wall.height)

                val dx = circle.position.x - nearestX
                val dy = circle.position.y - nearestY

                if (Math.abs(dx) > Math.abs(dy)) {
                    // Horizontal collision
                    circle.position.x += if (dx > 0) circle.radius - dx else -circle.radius - dx
                } else {
                    // Vertical collision
                    circle.position.y += if (dy > 0) circle.radius - dy else -circle.radius - dy
                }
            }
        }
    }

    private fun checkExitCollision(circle: Circle, exit: Rectangle?) {
        exit?.let {
            if (circle.collidesWith(it)) {
                Toast.makeText(this, "You reached the exit!", Toast.LENGTH_LONG).show()
                Log.d("GameStatus", "Player reached the exit point!")
                circle.destroy()
            }
        }
    }



    inner class Game : GameObject() {
        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            // Center the maze on the screen
            val screenWidth = surface!!.width
            val screenHeight = surface.height

            // Initialize the circle at the center of the screen

            // Create a better and scaled-up maze with an exit point
            val mazeLayout = arrayOf(
                "11111111111111111111111111",
                "100000000010000FFF00000001",
                "10111110101011111011100001",
                "10100010101000011010111111",
                "10100000001111011010100000",
                "10100111101001011000100000",
                "11100100101011011110111111",
                "100000101010100F1010000001",
                "101110100010001F1000011111",
                "101010110110111F1011110000",
                "100010100110100F1000010000",
                "11111110111110111011010000",
                "00000010100000101000011110",
                "00000010001111101FFFF000E1",
                "00000010001000001FFFF11111",
                "00000011111000001111110000",
            )

            val cellWidth = screenWidth / mazeLayout[0].length
            val cellHeight = screenHeight / mazeLayout.size
            val xOffset = (screenWidth - mazeLayout[0].length * cellWidth) / 2
            val yOffset = (screenHeight - mazeLayout.size * cellHeight) / 2

            mazeLayout.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, cell ->
                    val xPosition = xOffset + colIndex * cellWidth
                    val yPosition = yOffset + rowIndex * cellHeight
                    when (cell) {
                        '1' -> {
                            val wall = Rectangle(
                                Vector(xPosition.toFloat(), yPosition.toFloat()),
                                cellWidth.toFloat(),
                                cellHeight.toFloat(),
                                Color.BLACK
                            )
                            mazeWalls.add(wall)
                            surface.addGameObject(wall)
                        }
                        'F'-> {
                            val FrictionFloor = FrictionFloor(
                                Vector(xPosition.toFloat(), yPosition.toFloat()),
                                cellWidth.toFloat(),
                                cellHeight.toFloat(),
                                Color.YELLOW,
                                1f
                            )
                            surface.addGameObject(FrictionFloor)

                        }

                        'E' -> {
                            exitPoint = Rectangle(
                                Vector(xPosition.toFloat(), yPosition.toFloat()),
                                cellWidth.toFloat(),
                                cellHeight.toFloat(),
                                Color.GREEN
                            )
                            surface.addGameObject(exitPoint!!)
                        }
                    }
                }
            }


            circle = Circle(
                (screenWidth / 6.5).toFloat(),
                (screenHeight / 4).toFloat(),
                20f,
                Color.RED,
            )
            surface.addGameObject(circle!!)


        }

        override fun onFixedUpdate() {
            super.onFixedUpdate()

        }
    }
}

// Extension function to check collision between circle and rectangle
fun Circle.collidesWith(rect: Rectangle): Boolean {
    // Calculate the nearest point on the rectangle to the circle's center
    val nearestX = this.position.x.coerceIn(rect.position.x, rect.position.x + rect.width)
    val nearestY = this.position.y.coerceIn(rect.position.y, rect.position.y + rect.height)

    // Calculate the distance from the circle's center to this nearest point
    val dx = this.position.x - nearestX
    val dy = this.position.y - nearestY

    // Check if the distance is less than or equal to the radius (collision occurs)
    return (dx * dx + dy * dy) <= (this.radius * this.radius)
}

