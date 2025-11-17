package com.example.mediacontrol.kotlin.mainClasses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mediacontrol.R
import com.example.mediacontrol.Routes.Routes
import com.example.mediacontrol.floatingService.FloatingViewService
import com.example.mediacontrol.reusables.background.PageBackground
@Composable
fun ManageFloatingButton(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("BUTTONS_VISIBLE", Context.MODE_PRIVATE)

    val items = remember {
        mutableStateListOf(
            Item(Constants.VOLUME_UP, prefs.getBoolean(Constants.VOLUME_UP, false), R.layout.volume_up_button),
            Item(Constants.VOLUME_DOWN, prefs.getBoolean(Constants.VOLUME_DOWN, false), R.layout.volume_down_button),
            Item(Constants.MUTE, prefs.getBoolean(Constants.MUTE, false), R.layout.mute_button),
            Item(Constants.PLAY, prefs.getBoolean(Constants.PLAY, false), R.layout.play_button),
            Item(Constants.PAUSE, prefs.getBoolean(Constants.PAUSE, false), R.layout.pause_button),
            Item(Constants.PREVIOUS, prefs.getBoolean(Constants.PREVIOUS, false), R.layout.previous_button),
            Item(Constants.NEXT, prefs.getBoolean(Constants.NEXT, false), R.layout.next_button),
            Item(Constants.FAST_FORWARD, prefs.getBoolean(Constants.FAST_FORWARD, false), R.layout.fastfoward_button),
            Item(Constants.REWIND, prefs.getBoolean(Constants.REWIND, false), R.layout.rewind_button)
        )
    }
    val bundle = Bundle().apply {
        items.forEach { item ->
            putBoolean(item.name.replace(" ", "_").uppercase(), item.isSelected)
        }
    }

    val intent = Intent(context, FloatingViewService::class.java).apply {
        putExtras(bundle)
    }
    context.startService(intent)
    PageBackground {
        Column {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        modifier = androidx.compose.ui.Modifier.clickable {
                            // Navigate to "details" page with item id as argument
                            navController.navigate(Routes.configureFLoatingButton+"/${item.layoutId}/${item.name}")
                        }
                    )
                    Switch(
                        checked = item.isSelected,
                        onCheckedChange = { checked ->
                            items[index] = item.copy(isSelected = checked)
                            prefs.edit().putBoolean(item.name.replace(" ", "_").uppercase(), checked).apply()
                        }
                    )
                }
            }
        }
    }
}

data class Item(
    val name: String,
    val isSelected: Boolean,
    val layoutId:Int
)
