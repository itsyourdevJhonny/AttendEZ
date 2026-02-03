package com.project.attendez.ui.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.attendez.R
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.Typography
import com.project.attendez.ui.util.drawGradient

@Composable
fun EventHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                colorFilter = ColorFilter.tint(color = BluePrimary),
                modifier = Modifier.size(28.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "A",
                    style = Typography.headlineMedium.copy(
                        brush = BackgroundGradient,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                )

                Text(
                    text = "ttend",
                    style = Typography.titleLarge.copy(
                        brush = BackgroundGradient,
                        fontWeight = FontWeight.Black
                    )
                )

                Text(
                    text = "EZ",
                    style = Typography.headlineMedium.copy(
                        brush = BackgroundGradient,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier.drawGradient()
        )
    }
}