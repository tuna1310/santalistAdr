package com.santalist.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.ui.theme.PipBubbleBgLight

@Composable
fun PipSpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PipBubbleBgLight
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top triangle pointer pointing up towards Pip
        Canvas(modifier = Modifier.size(width = 18.dp, height = 9.dp)) {
            val arrowPath = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(arrowPath, color = backgroundColor, style = Fill)
        }

        // Rounded bubble body
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = backgroundColor,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
