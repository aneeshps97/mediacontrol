package com.example.mediacontrol.kotlin.mainClasses

import android.view.View

data class ButtonInfo(
    val key: String,
    val view: View?,
    val viewId: Int,
    val x: Int,
    val y: Int
)