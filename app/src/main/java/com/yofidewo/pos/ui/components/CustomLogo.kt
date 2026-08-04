package com.yofidewo.pos.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun CustomLogo(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap? = null
) {
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Logo Outlet",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        val orangeColor = Color(0xFFFF6600)
        val blueColor = Color(0xFF2563EB)

        Canvas(modifier = modifier.size(100.dp)) {
        val w = size.width
        val h = size.height

        // Orange Awning (Top)
        val awningPath = Path().apply {
            moveTo(w * 0.15f, h * 0.4f)
            lineTo(w * 0.25f, h * 0.15f)
            lineTo(w * 0.75f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.4f)
            close()
        }
        drawPath(path = awningPath, color = orangeColor)
        
        // Awning Scallops
        drawCircle(color = orangeColor, radius = w * 0.1f, center = Offset(w * 0.25f, h * 0.4f))
        drawCircle(color = orangeColor, radius = w * 0.1f, center = Offset(w * 0.5f, h * 0.4f))
        drawCircle(color = orangeColor, radius = w * 0.1f, center = Offset(w * 0.75f, h * 0.4f))

        // Leaf (Top Right)
        val leafPath = Path().apply {
            moveTo(w * 0.75f, h * 0.12f)
            quadraticBezierTo(w * 0.8f, h * 0.02f, w * 0.9f, h * 0.05f)
            quadraticBezierTo(w * 0.9f, h * 0.15f, w * 0.75f, h * 0.12f)
            close()
        }
        drawPath(path = leafPath, color = orangeColor)

        // Blue Abacus (Middle)
        drawRect(color = blueColor, topLeft = Offset(w * 0.15f, h * 0.45f), size = Size(w * 0.1f, h * 0.25f))
        drawRect(color = blueColor, topLeft = Offset(w * 0.75f, h * 0.45f), size = Size(w * 0.1f, h * 0.25f))
        
        // Abacus rods
        drawLine(color = blueColor, start = Offset(w * 0.25f, h * 0.5f), end = Offset(w * 0.75f, h * 0.5f), strokeWidth = w * 0.02f)
        drawLine(color = blueColor, start = Offset(w * 0.25f, h * 0.65f), end = Offset(w * 0.75f, h * 0.65f), strokeWidth = w * 0.02f)
        
        // Abacus beads (Orange on top left, Blue on top right)
        drawRoundRect(color = orangeColor, topLeft = Offset(w * 0.28f, h * 0.46f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))
        drawRoundRect(color = orangeColor, topLeft = Offset(w * 0.38f, h * 0.46f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))
        drawRoundRect(color = blueColor, topLeft = Offset(w * 0.58f, h * 0.46f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))
        drawRoundRect(color = blueColor, topLeft = Offset(w * 0.68f, h * 0.46f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))

        // Abacus beads (Blue on bottom left, Orange on bottom right)
        drawRoundRect(color = blueColor, topLeft = Offset(w * 0.28f, h * 0.61f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))
        drawRoundRect(color = blueColor, topLeft = Offset(w * 0.38f, h * 0.61f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))
        drawRoundRect(color = orangeColor, topLeft = Offset(w * 0.58f, h * 0.61f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))
        drawRoundRect(color = blueColor, topLeft = Offset(w * 0.68f, h * 0.61f), size = Size(w * 0.08f, h * 0.08f), cornerRadius = CornerRadius(w * 0.04f))

        // Smiling Face (Bottom)
        val facePath = Path().apply {
            moveTo(w * 0.15f, h * 0.72f)
            cubicTo(w * 0.15f, h * 0.95f, w * 0.85f, h * 0.95f, w * 0.85f, h * 0.72f)
            close()
        }
        drawPath(path = facePath, color = orangeColor)
        
        // Smile curve
        val smilePath = Path().apply {
            moveTo(w * 0.35f, h * 0.8f)
            quadraticBezierTo(w * 0.5f, h * 0.9f, w * 0.65f, h * 0.8f)
        }
        drawPath(path = smilePath, color = Color.White, style = Stroke(width = w * 0.04f))

        // Dollar Coin (Bottom Right)
        drawCircle(color = blueColor, radius = w * 0.15f, center = Offset(w * 0.85f, h * 0.85f))
        drawCircle(color = Color.White, radius = w * 0.13f, center = Offset(w * 0.85f, h * 0.85f), style = Stroke(width = w * 0.02f))
        // Simple dollar sign approximations
        drawLine(color = Color.White, start = Offset(w * 0.85f, h * 0.75f), end = Offset(w * 0.85f, h * 0.95f), strokeWidth = w * 0.02f)
        val sPath = Path().apply {
            moveTo(w * 0.9f, h * 0.78f)
            quadraticBezierTo(w * 0.8f, h * 0.75f, w * 0.8f, h * 0.82f)
            quadraticBezierTo(w * 0.9f, h * 0.88f, w * 0.9f, h * 0.92f)
            quadraticBezierTo(w * 0.8f, h * 0.95f, w * 0.78f, h * 0.92f)
        }
        drawPath(path = sPath, color = Color.White, style = Stroke(width = w * 0.03f))
    }
}
}
