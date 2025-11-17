package com.example.mediacontrol.kotlin.mainClasses

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.mediacontrol.R
import com.example.mediacontrol.reusables.background.PageBackground
import androidx.core.content.edit

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
            if (layoutId != null) {
                ShowButtonFromXml(
                    Integer.parseInt(layoutId),
                    width = width,
                    height = height,
                    opacity = opacity
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = "Width",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = width,
                onValueChange = {
                    width = it
                    prefs.edit { putFloat("WIDTH", width) }
                },
                valueRange = 40f..200f,
                modifier = Modifier.padding(20.dp)
            )
            Text(
                text = "Height",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = height,
                onValueChange = {
                    height = it
                    prefs.edit { putFloat("HEIGHT", height)
                    }
                },
                valueRange = 40f..200f,
                modifier = Modifier.padding(20.dp)
            )

            Text(
                text = "Opacity ",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = opacity,
                onValueChange = {
                    opacity = it
                    prefs.edit { putFloat("OPACITY", opacity)
                    }
                },
                valueRange = 0.1f..1f,
                modifier = Modifier.padding(20.dp)
            )
        }
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
            .size(width = width.dp, height = height.dp).alpha(opacity)
    )
}
