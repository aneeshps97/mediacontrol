package com.example.mediacontrol.kotlin.mainClasses

import android.view.View

class ButtonViews {
    var volumeUpView: View? = null
    var volumeDownView: View? = null
    var muteView: View? = null
    var playView: View? = null
    var pauseView: View? = null
    var previousView: View? = null
    var nextView: View? = null
    var fastForwardView: View? = null
    var rewindView: View? = null

    fun allViews(): List<View?> = listOf(
        volumeUpView,
        volumeDownView,
        muteView,
        playView,
        pauseView,
        previousView,
        nextView,
        fastForwardView,
        rewindView
    )
}
