package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class FrictionFloor(
    position: Vector,
    width: Float,
    height: Float,
    color: Int,
    private val friction: Float // Add a friction parameter
) : Rectangle(position, width, height, color) {

    init {
        require(friction in 0.0..1.0) { "Friction must be between 0 and 1." } // Optional validation
    }

    override fun onFixedUpdate() {
        super.onFixedUpdate()

        applyFrictionToObjects()
    }

    private fun applyFrictionToObjects() {
        // Logic for interacting with objects on the floor and applying friction
        // Example: Update velocities of objects in contact with the floor
    }
}