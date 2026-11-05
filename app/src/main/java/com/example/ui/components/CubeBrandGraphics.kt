package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.ClaudeAmber
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.theme.ClaudeTerracottaLight

/**
 * Claude-inspired warm terracotta asterism / geometric symbol.
 */
@Composable
fun ClaudeAsterismIcon(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    tint: Color = ClaudeTerracotta
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            val strokeWidth = (w * 0.13f).coerceAtLeast(2.dp.toPx())

            // 4 intersecting organic rays creating Claude's signature spark
            // Vertical bar
            drawLine(
                color = tint,
                start = Offset(cx, h * 0.12f),
                end = Offset(cx, h * 0.88f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            // Horizontal bar
            drawLine(
                color = tint,
                start = Offset(w * 0.12f, cy),
                end = Offset(w * 0.88f, cy),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            // Diagonal 1
            drawLine(
                color = tint.copy(alpha = 0.82f),
                start = Offset(w * 0.24f, h * 0.24f),
                end = Offset(w * 0.76f, h * 0.76f),
                strokeWidth = strokeWidth * 0.82f,
                cap = StrokeCap.Round
            )
            // Diagonal 2
            drawLine(
                color = tint.copy(alpha = 0.82f),
                start = Offset(w * 0.76f, h * 0.24f),
                end = Offset(w * 0.24f, h * 0.76f),
                strokeWidth = strokeWidth * 0.82f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Clean isometric cube logo with warm terracotta & amber tones.
 */
@Composable
fun CubeIsometricIcon(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    animated: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(size * 0.28f)
            ),
        contentAlignment = Alignment.Center
    ) {
        ClaudeAsterismIcon(
            size = size * 0.82f,
            tint = ClaudeTerracotta
        )
    }
}

/**
 * Claude-inspired warm welcome hero emblem.
 */
@Composable
fun CubeHeroEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 76.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(ClaudeTerracotta.copy(alpha = 0.10f))
                .border(
                    width = 1.5.dp,
                    color = ClaudeTerracotta.copy(alpha = 0.35f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            ClaudeAsterismIcon(
                size = size * 0.75f,
                tint = ClaudeTerracotta
            )
        }
    }
}
