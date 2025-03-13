package com.app.mytasks.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.hypot

class CircularRevealShape(
    private val progress: Float,
    private val center: Offset
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val maxRadius = hypot(size.width.toDouble(), size.height.toDouble()).toFloat()
        val currentRadius = maxRadius * progress

        return Outline.Generic(
            Path().apply {
                reset()
                addOval(
                    Rect(
                        left = center.x - currentRadius,
                        top = center.y - currentRadius,
                        right = center.x + currentRadius,
                        bottom = center.y + currentRadius
                    )
                )
            }
        )
    }
}
