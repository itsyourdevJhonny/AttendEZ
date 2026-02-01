package com.project.attendez.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import com.project.attendez.ui.theme.BackgroundGradient

fun Modifier.drawGradient(): Modifier {
    return graphicsLayer(alpha = 0.99f)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(
                    brush = BackgroundGradient,
                    blendMode = BlendMode.SrcAtop
                )
            }
        }
}