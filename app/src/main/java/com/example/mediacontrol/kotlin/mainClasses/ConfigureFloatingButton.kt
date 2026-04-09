package com.example.mediacontrol.kotlin.mainClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.mediacontrol.R
import com.example.mediacontrol.reusables.background.PageBackground
import androidx.core.content.edit
import java.awt.font.NumericShaper

@Composable
fun ConfigureFloatingButton(
    navController: NavController,
    layoutId: String?,
    sizePrefButtonName: String?,
) {
    val context = LocalContext.current
    var width by remember { mutableStateOf(60f) }
    var height by remember { mutableStateOf(60f) }
    var opacity by remember { mutableStateOf(0.5f) }
    val prefs = context.getSharedPreferences(sizePrefButtonName, Context.MODE_PRIVATE)

    PageBackground {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            height = prefs.getFloat("HEIGHT",90f)
            width = prefs.getFloat("WIDTH",90f)
            opacity = prefs.getFloat("OPACITY",0.5f)
            Column {

                Box(
                    modifier = Modifier
                        .size(250.dp)   // fixed preview area
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ){
                    if (layoutId != null) {
                        ShowButtonFromXml(
                            Integer.parseInt(layoutId),
                            width = width,
                            height = height,
                            opacity = opacity
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            CustomSlider(
                "WIDTH", width, {
                    width = it
                    prefs.edit { putFloat("WIDTH", width) }
                }, valueRange = 40f..200f,
                divisionFactor = 400f
            )

            CustomSlider(
                "HEIGHT", height, {
                    height = it
                    prefs.edit { putFloat("HEIGHT", height) }
                }, valueRange = 40f..200f,
                divisionFactor = 400f
            )

            CustomSlider(
                "OPACITY", opacity, {
                    opacity = it
                    prefs.edit { putFloat("OPACITY", opacity) }
                }, valueRange = 0f..1f,
                divisionFactor = 1f
            )

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    text:String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    divisionFactor: Float
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        // Label + Value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
            val percentage = ((value - valueRange.start) /
                    (valueRange.endInclusive - valueRange.start)) * 100
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            valueRange = valueRange,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),

            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF1E1E1E),
                activeTrackColor = Color(0xFF1E1E1E),
                inactiveTrackColor = Color(0xFFE0E0E0)
            ),

            thumb = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color(0xFF1E1E1E), CircleShape)
                )
            },

            track = { sliderPositions ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF1E1E1E))
                    )
                }
            }
        )
    }
}

@Composable
fun ShowButtonFromXml(layoutId: Int, width: Float, height: Float,opacity:Float) {
    AndroidView(
        factory = { context ->
            LayoutInflater.from(context)
                .inflate(layoutId, null) as FrameLayout
        }, modifier = Modifier
            .padding(10.dp)
            .size(width = width.dp, height = height.dp)
            .alpha(opacity)
    )
}
