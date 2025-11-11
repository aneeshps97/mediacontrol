package com.example.mediacontrol.kotlin.mainClasses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mediacontrol.floatingService.FloatingViewService
import com.example.mediacontrol.reusables.background.PageBackground

@Composable
fun ManageFloatingButton(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("BUTTONS_VISIBLE", Context.MODE_PRIVATE)
    val items = remember {
        mutableStateListOf(
            Item("VOLUME UP", prefs.getBoolean("VOLUME_UP", false)),
            Item("VOLUME DOWN", prefs.getBoolean("VOLUME_DOWN", false)),
            Item("MUTE", prefs.getBoolean("MUTE", false))
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
                    Text(text = item.name)
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
    val isSelected: Boolean
)
