package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CubeCyan
import com.example.ui.theme.CubeElectricBlue
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CubeIsometricIcon(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    animated: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cube_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val effectiveAlpha = if (animated) glowAlpha else 0.9f

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0E1726),
                        Color(0xFF090D16)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        CubeCyan.copy(alpha = effectiveAlpha * 0.8f),
                        CubeElectricBlue.copy(alpha = 0.25f)
                    )
                ),
                shape = RoundedCornerShape(size * 0.28f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.62f)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = (w.coerceAtMost(h) / 2f) * 0.92f

            // 6 outer vertices of an isometric hexagon
            val pts = (0 until 6).map { i ->
                val angleRad = Math.toRadians((i * 60.0) - 90.0)
                Offset(
                    x = cx + (r * cos(angleRad)).toFloat(),
                    y = cy + (r * sin(angleRad)).toFloat()
                )
            }

            val hexPath = Path().apply {
                moveTo(pts[0].x, pts[0].y)
                for (i in 1 until 6) {
                    lineTo(pts[i].x, pts[i].y)
                }
                close()
            }

            // Subtle top face highlight
            val topFace = Path().apply {
                moveTo(cx, cy)
                lineTo(pts[5].x, pts[5].y)
                lineTo(pts[0].x, pts[0].y)
                lineTo(pts[1].x, pts[1].y)
                close()
            }
            drawPath(
                path = topFace,
                color = CubeCyan.copy(alpha = 0.16f * effectiveAlpha)
            )

            // Outer hexagon stroke
            drawPath(
                path = hexPath,
                color = CubeCyan.copy(alpha = effectiveAlpha),
                style = Stroke(
                    width = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Inner Y edges connecting center to vertices 1, 3, 5
            val innerStroke = 1.6.dp.toPx()
            drawLine(
                color = CubeCyan.copy(alpha = effectiveAlpha),
                start = Offset(cx, cy),
                end = pts[5],
                strokeWidth = innerStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = CubeCyan.copy(alpha = effectiveAlpha),
                start = Offset(cx, cy),
                end = pts[1],
                strokeWidth = innerStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = CubeElectricBlue.copy(alpha = effectiveAlpha * 0.9f),
                start = Offset(cx, cy),
                end = pts[3],
                strokeWidth = innerStroke,
                cap = StrokeCap.Round
            )

            // Center core dot
            drawCircle(
                color = Color.White,
                radius = 1.8.dp.toPx(),
                center = Offset(cx, cy)
            )
        }
    }
}

@Composable
fun CubeHeroEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 88.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_emblem_glow")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_scale"
    )

    Box(
        modifier = modifier.size(size * 1.25f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CubeCyan.copy(alpha = 0.24f * haloScale),
                        CubeElectricBlue.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                ),
                radius = (this.size.minDimension / 2f) * haloScale
            )
        }

        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CubeCyan.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.35f),
                            CubeElectricBlue.copy(alpha = 0.65f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_cube_emblem_1793201682203),
                contentDescription = "Cube AI futuristic holographic emblem",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
