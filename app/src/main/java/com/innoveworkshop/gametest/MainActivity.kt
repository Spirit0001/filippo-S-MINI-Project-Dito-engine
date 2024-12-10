package com.innoveworkshop.gametest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.innoveworkshop.gametest.engine.*


private fun checkWallCollision(circle: Circle, surface: GameSurface) {
    // Check left edge
    if (circle.position.x - circle.radius < 0) {
        circle.position.x = circle.radius
    }

    // Check right edge
    if (circle.position.x + circle.radius > surface.width) {
        circle.position.x = surface.width - circle.radius
    }

    // Check top edge
    if (circle.position.y - circle.radius < 0) {
        circle.position.y = circle.radius
    }

    // Check bottom edge
    if (circle.position.y + circle.radius > surface.height) {
        circle.position.y = surface.height - circle.radius
    }
}

class MainActivity : AppCompatActivity(), SensorEventListener {
    protected var gameSurface: GameSurface? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var circle: Circle? = null
    protected var game: Game? = null

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

                gameSurface?.let { checkWallCollision(this, it) } // Ensure the circle stays within bounds
            }
        }
    }

    // This method is required but not used here
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this implementation
    }

    inner class Game : GameObject() {
        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            // Initialize the circle at the center
            circle = Circle(
                (surface!!.width / 2).toFloat(),
                (surface.height / 2).toFloat(),
                20f,
                25f,
                Color.RED
            )
            surface.addGameObject(circle!!)

            // Add walls
            surface.addGameObject(
                Rectangle(
                    Vector((surface.width / 3).toFloat(), (surface.height / 30).toFloat()),
                    2000f, 5f, Color.BLACK
                )
            )
            surface.addGameObject(
                Rectangle(
                    Vector((surface.width / 3).toFloat(), (surface.height / 1).toFloat()),
                    2000f, 5f, Color.BLACK
                )
            )

            // Add maze elements as needed

            surface.addGameObject( // 1
                RectangleRotated(
                    Vector((surface.width / 2.2).toFloat(), (surface.height /1).toFloat()),
                    90f, 820f, Color. BLACK
                )
            )

            surface.addGameObject( // 2
                RectangleRotated(
                    Vector((surface.width / 1.83).toFloat(), (surface.height /1).toFloat()),
                    90f, 190f, Color. BLACK
                )
            )

            surface.addGameObject( // 3
                RectangleRotated(
                    Vector((surface.width / 1.83).toFloat(), (surface.height /1.215).toFloat()),
                    90f, 500f, Color. BLACK
                )
            )

            surface.addGameObject( // 4
                RectangleRotated(
                    Vector((surface.width / 1.6).toFloat(), (surface.height /1.182).toFloat()),
                    800f, 15f, Color. BLACK
                )
            )

            surface.addGameObject( // 5
                RectangleRotated(
                    Vector((surface.width / 1.6).toFloat(), (surface.height /1.100).toFloat()),
                    800f, 15f, Color. BLACK
                )
            )



        }

        override fun onFixedUpdate() {
            super.onFixedUpdate()
            // Additional physics updates if required
        }
    }
}
