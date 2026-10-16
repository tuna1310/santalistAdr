package com.santalist.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PipPose {
    WAVING,        // Home screen empty
    CHEEK_REST,    // People screen empty
    HUGGING_GIFT,  // Money screen empty
    SLEEPING       // Messages screen empty
}

@Composable
fun PipMascot(
    pose: PipPose,
    modifier: Modifier = Modifier,
    width: Dp = 140.dp,
    height: Dp = 175.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pip_infinite")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pip_bob"
    )
    val shadowScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pip_shadow"
    )

    val outlineColor = Color(0xFF2B2420)
    val ghostWhite = Color.White
    val santaRed = Color(0xFFD63834)
    val blushColor = Color(0xFFFFAAA6)
    val ribbonGold = Color(0xFFFDC02F)
    val sleepZColor = Color(0xFF8A8580)
    val shadowColor = Color(0x1C2B2420)

    Box(
        modifier = modifier.size(width, height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width, height)) {
            val canvasW = size.width
            val canvasH = size.height
            val scale = canvasW / 140f
            val cx = canvasW / 2f
            val cy = canvasH / 2f

            val strokeWidth = 3.8f * scale
            val fineStrokeWidth = 2.4f * scale

            // 1. Shadow underneath (stays on ground, modulated by shadowScale)
            val shadowY = canvasH - (12f * scale)
            val shadowRadiusX = (38f * scale) * shadowScale
            val shadowRadiusY = (7.5f * scale) * shadowScale
            drawOval(
                color = shadowColor,
                topLeft = Offset(cx - shadowRadiusX, shadowY - shadowRadiusY),
                size = Size(shadowRadiusX * 2f, shadowRadiusY * 2f)
            )

            // Everything above ground bobs up and down
            val offsetY = bobOffset * scale

            val tiltAngle = if (pose == PipPose.CHEEK_REST) -6f else 0f

            rotate(degrees = tiltAngle, pivot = Offset(cx, cy + offsetY)) {
                // 2. Pip's Ghost Body
                val bodyTopY = 48f * scale + offsetY
                val bodyBottomY = 126f * scale + offsetY
                val bodyLeftX = cx - 35f * scale
                val bodyRightX = cx + 35f * scale

                val bodyPath = Path().apply {
                    // Start at top dome (arch)
                    moveTo(bodyLeftX, bodyTopY + 15f * scale)
                    // Dome over the head
                    cubicTo(
                        bodyLeftX, bodyTopY - 18f * scale,
                        bodyRightX, bodyTopY - 18f * scale,
                        bodyRightX, bodyTopY + 15f * scale
                    )
                    // Right side down to bottom
                    cubicTo(
                        bodyRightX + 4f * scale, bodyTopY + 45f * scale,
                        bodyRightX + 2f * scale, bodyBottomY - 15f * scale,
                        bodyRightX, bodyBottomY
                    )
                    // Bottom 3 wavy scallops
                    val scallopWidth = (bodyRightX - bodyLeftX) / 3f
                    val s1 = bodyRightX - scallopWidth
                    val s2 = bodyLeftX + scallopWidth

                    // Scallop 3 (right)
                    quadraticTo(
                        bodyRightX - scallopWidth * 0.5f, bodyBottomY + 8f * scale,
                        s1, bodyBottomY
                    )
                    // Scallop 2 (middle)
                    quadraticTo(
                        cx, bodyBottomY + 9f * scale,
                        s2, bodyBottomY
                    )
                    // Scallop 1 (left)
                    quadraticTo(
                        bodyLeftX + scallopWidth * 0.5f, bodyBottomY + 8f * scale,
                        bodyLeftX, bodyBottomY
                    )

                    // Left side up to head
                    cubicTo(
                        bodyLeftX - 2f * scale, bodyBottomY - 15f * scale,
                        bodyLeftX - 4f * scale, bodyTopY + 45f * scale,
                        bodyLeftX, bodyTopY + 15f * scale
                    )
                    close()
                }

                // Fill Ghost Body
                drawPath(path = bodyPath, color = ghostWhite, style = Fill)
                // Stroke Ghost Body
                drawPath(
                    path = bodyPath,
                    color = outlineColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 3. Pose-specific arms & objects behind/on body
                when (pose) {
                    PipPose.WAVING -> {
                        // Left arm (resting on left side)
                        val leftArm = Path().apply {
                            moveTo(bodyLeftX + 3f * scale, 82f * scale + offsetY)
                            quadraticTo(
                                bodyLeftX - 12f * scale, 88f * scale + offsetY,
                                bodyLeftX + 2f * scale, 98f * scale + offsetY
                            )
                        }
                        drawPath(path = leftArm, color = ghostWhite, style = Fill)
                        drawPath(path = leftArm, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))

                        // Right arm (raised waving up-right)
                        val wavingArm = Path().apply {
                            moveTo(bodyRightX - 3f * scale, 76f * scale + offsetY)
                            cubicTo(
                                bodyRightX + 16f * scale, 72f * scale + offsetY,
                                bodyRightX + 22f * scale, 52f * scale + offsetY,
                                bodyRightX + 16f * scale, 48f * scale + offsetY
                            )
                            cubicTo(
                                bodyRightX + 8f * scale, 44f * scale + offsetY,
                                bodyRightX + 2f * scale, 64f * scale + offsetY,
                                bodyRightX - 4f * scale, 86f * scale + offsetY
                            )
                            close()
                        }
                        drawPath(path = wavingArm, color = ghostWhite, style = Fill)
                        drawPath(
                            path = wavingArm,
                            color = outlineColor,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    PipPose.CHEEK_REST -> {
                        // Left hand curled up against cheek
                        val cheekHand = Path().apply {
                            moveTo(bodyLeftX + 4f * scale, 94f * scale + offsetY)
                            cubicTo(
                                bodyLeftX - 10f * scale, 88f * scale + offsetY,
                                bodyLeftX - 10f * scale, 74f * scale + offsetY,
                                cx - 18f * scale, 74f * scale + offsetY
                            )
                            cubicTo(
                                cx - 14f * scale, 78f * scale + offsetY,
                                bodyLeftX + 8f * scale, 88f * scale + offsetY,
                                bodyLeftX + 4f * scale, 94f * scale + offsetY
                            )
                            close()
                        }
                        drawPath(path = cheekHand, color = ghostWhite, style = Fill)
                        drawPath(path = cheekHand, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

                        // Right hand cute nub resting on body
                        val rightArm = Path().apply {
                            moveTo(bodyRightX - 4f * scale, 82f * scale + offsetY)
                            quadraticTo(
                                bodyRightX + 10f * scale, 88f * scale + offsetY,
                                bodyRightX - 2f * scale, 98f * scale + offsetY
                            )
                        }
                        drawPath(path = rightArm, color = ghostWhite, style = Fill)
                        drawPath(path = rightArm, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))
                    }

                    PipPose.HUGGING_GIFT -> {
                        // Red Gift Box held in center
                        val boxW = 38f * scale
                        val boxH = 34f * scale
                        val boxX = cx - boxW / 2f
                        val boxY = 82f * scale + offsetY

                        val boxRect = RoundRect(
                            Rect(boxX, boxY, boxX + boxW, boxY + boxH),
                            CornerRadius(4f * scale, 4f * scale)
                        )
                        val boxPath = Path().apply { addRoundRect(boxRect) }

                        // Draw Red Box
                        drawPath(path = boxPath, color = santaRed, style = Fill)
                        drawPath(path = boxPath, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))

                        // Yellow Ribbon Cross
                        val ribbonW = 7f * scale
                        // Vertical ribbon
                        drawRect(
                            color = ribbonGold,
                            topLeft = Offset(cx - ribbonW / 2f, boxY),
                            size = Size(ribbonW, boxH)
                        )
                        // Horizontal ribbon
                        drawRect(
                            color = ribbonGold,
                            topLeft = Offset(boxX, boxY + boxH / 2f - ribbonW / 2f),
                            size = Size(boxW, ribbonW)
                        )

                        // Ribbon Bow on top
                        drawCircle(
                            color = ribbonGold,
                            radius = 4f * scale,
                            center = Offset(cx, boxY)
                        )

                        // Two cute ghost hands hugging the sides of the gift
                        val leftGrip = Path().apply {
                            moveTo(boxX - 6f * scale, boxY + 8f * scale)
                            cubicTo(
                                boxX + 6f * scale, boxY + 6f * scale,
                                boxX + 8f * scale, boxY + 22f * scale,
                                boxX - 6f * scale, boxY + 24f * scale
                            )
                            close()
                        }
                        drawPath(path = leftGrip, color = ghostWhite, style = Fill)
                        drawPath(path = leftGrip, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))

                        val rightGrip = Path().apply {
                            moveTo(boxX + boxW + 6f * scale, boxY + 8f * scale)
                            cubicTo(
                                boxX + boxW - 6f * scale, boxY + 6f * scale,
                                boxX + boxW - 8f * scale, boxY + 22f * scale,
                                boxX + boxW + 6f * scale, boxY + 24f * scale
                            )
                            close()
                        }
                        drawPath(path = rightGrip, color = ghostWhite, style = Fill)
                        drawPath(path = rightGrip, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))
                    }

                    PipPose.SLEEPING -> {
                        // Two relaxed hands resting slightly
                        val leftArm = Path().apply {
                            moveTo(bodyLeftX + 4f * scale, 86f * scale + offsetY)
                            quadraticTo(
                                bodyLeftX - 8f * scale, 94f * scale + offsetY,
                                bodyLeftX + 6f * scale, 102f * scale + offsetY
                            )
                        }
                        drawPath(path = leftArm, color = ghostWhite, style = Fill)
                        drawPath(path = leftArm, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))

                        val rightArm = Path().apply {
                            moveTo(bodyRightX - 4f * scale, 86f * scale + offsetY)
                            quadraticTo(
                                bodyRightX + 8f * scale, 94f * scale + offsetY,
                                bodyRightX - 6f * scale, 102f * scale + offsetY
                            )
                        }
                        drawPath(path = rightArm, color = ghostWhite, style = Fill)
                        drawPath(path = rightArm, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))

                        // Floating "z" "z" beside Pip's head
                        drawSleepZ(cx + 34f * scale, 58f * scale + offsetY, 10f * scale, sleepZColor, fineStrokeWidth)
                        drawSleepZ(cx + 44f * scale, 44f * scale + offsetY, 14f * scale, sleepZColor, strokeWidth)
                    }
                }

                // 4. Face Features
                // Blush cheeks
                val blushY = 72f * scale + offsetY
                val blushW = 11f * scale
                val blushH = 6.5f * scale
                drawOval(
                    color = blushColor,
                    topLeft = Offset(cx - 22f * scale - blushW / 2f, blushY - blushH / 2f),
                    size = Size(blushW, blushH)
                )
                drawOval(
                    color = blushColor,
                    topLeft = Offset(cx + 22f * scale - blushW / 2f, blushY - blushH / 2f),
                    size = Size(blushW, blushH)
                )

                // Eyes
                val eyeY = 63f * scale + offsetY
                val eyeSpacing = 13f * scale
                if (pose == PipPose.SLEEPING) {
                    // Closed eyes: downward arcs (– – or ˘ ˘)
                    val eyeW = 9f * scale
                    drawArc(
                        color = outlineColor,
                        startAngle = 10f,
                        sweepAngle = 160f,
                        useCenter = false,
                        topLeft = Offset(cx - eyeSpacing - eyeW / 2f, eyeY - 4f * scale),
                        size = Size(eyeW, 8f * scale),
                        style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = outlineColor,
                        startAngle = 10f,
                        sweepAngle = 160f,
                        useCenter = false,
                        topLeft = Offset(cx + eyeSpacing - eyeW / 2f, eyeY - 4f * scale),
                        size = Size(eyeW, 8f * scale),
                        style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round)
                    )
                } else {
                    // Open eyes: round black circles
                    val eyeRadius = 3.6f * scale
                    drawCircle(outlineColor, radius = eyeRadius, center = Offset(cx - eyeSpacing, eyeY))
                    drawCircle(outlineColor, radius = eyeRadius, center = Offset(cx + eyeSpacing, eyeY))

                    // Cute white anime specular dot
                    val glintRadius = 1.1f * scale
                    drawCircle(ghostWhite, radius = glintRadius, center = Offset(cx - eyeSpacing + 1.2f * scale, eyeY - 1.2f * scale))
                    drawCircle(ghostWhite, radius = glintRadius, center = Offset(cx + eyeSpacing + 1.2f * scale, eyeY - 1.2f * scale))
                }

                // Mouth
                val mouthY = 71f * scale + offsetY
                if (pose == PipPose.SLEEPING) {
                    // Small relaxed smile line
                    val mouthPath = Path().apply {
                        moveTo(cx - 3.5f * scale, mouthY)
                        quadraticTo(cx, mouthY + 2f * scale, cx + 3.5f * scale, mouthY)
                    }
                    drawPath(path = mouthPath, color = outlineColor, style = Stroke(fineStrokeWidth, cap = StrokeCap.Round))
                } else {
                    // Cute curved smile ◡
                    val mouthW = 10f * scale
                    drawArc(
                        color = outlineColor,
                        startAngle = 15f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(cx - mouthW / 2f, mouthY - 3f * scale),
                        size = Size(mouthW, 7f * scale),
                        style = Stroke(width = strokeWidth * 0.8f, cap = StrokeCap.Round)
                    )
                }

                // 5. Santa Hat (Red cone + White puffy trim + Pom-pom)
                val hatBaseY = 32f * scale + offsetY
                rotate(degrees = 10f, pivot = Offset(cx, hatBaseY)) {
                    // Red Cone
                    val conePath = Path().apply {
                        moveTo(cx - 24f * scale, hatBaseY + 2f * scale)
                        cubicTo(
                            cx - 20f * scale, hatBaseY - 26f * scale,
                            cx + 12f * scale, hatBaseY - 34f * scale,
                            cx + 38f * scale, hatBaseY - 10f * scale
                        )
                        // Droop tip
                        cubicTo(
                            cx + 46f * scale, hatBaseY - 2f * scale,
                            cx + 42f * scale, hatBaseY + 8f * scale,
                            cx + 36f * scale, hatBaseY + 12f * scale
                        )
                        cubicTo(
                            cx + 28f * scale, hatBaseY + 6f * scale,
                            cx + 22f * scale, hatBaseY + 2f * scale,
                            cx + 22f * scale, hatBaseY + 2f * scale
                        )
                        close()
                    }
                    drawPath(path = conePath, color = santaRed, style = Fill)
                    drawPath(path = conePath, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

                    // White Puffy Pom-pom at tip
                    val pomCenter = Offset(cx + 42f * scale, hatBaseY + 6f * scale)
                    val pomRadius = 8.5f * scale
                    drawCircle(ghostWhite, pomRadius, pomCenter)
                    drawCircle(outlineColor, pomRadius, pomCenter, style = Stroke(strokeWidth))

                    // White Fluffy Hat Trim (Brim)
                    val trimRect = RoundRect(
                        Rect(cx - 28f * scale, hatBaseY - 3f * scale, cx + 28f * scale, hatBaseY + 12f * scale),
                        CornerRadius(8f * scale, 8f * scale)
                    )
                    val trimPath = Path().apply { addRoundRect(trimRect) }
                    drawPath(path = trimPath, color = ghostWhite, style = Fill)
                    drawPath(path = trimPath, color = outlineColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))
                }
            }
        }
    }
}

private fun DrawScope.drawSleepZ(
    startX: Float,
    startY: Float,
    size: Float,
    color: Color,
    strokeWidth: Float
) {
    val zPath = Path().apply {
        moveTo(startX, startY)
        lineTo(startX + size, startY)
        lineTo(startX, startY + size)
        lineTo(startX + size, startY + size)
    }
    drawPath(path = zPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
}
