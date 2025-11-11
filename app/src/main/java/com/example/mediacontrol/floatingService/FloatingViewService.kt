package com.example.mediacontrol.floatingService

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.mediacontrol.R

class FloatingViewService : Service() {

    private val prefs by lazy { getSharedPreferences("BUTTONS_VISIBLE", Context.MODE_PRIVATE) }
    private var windowManager: WindowManager? = null
    private var volumeUpView: View? = null
    private var volumeDownView: View? = null
    private var muteView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceSafe()

        // Inflate views
        volumeUpView = inflateViews(R.layout.volume_up_button)
        volumeDownView = inflateViews(R.layout.volume_down_button)
        muteView = inflateViews(R.layout.mute_button)


        // Add views based on saved visibility
        if (prefs.getBoolean("VOLUME_UP", false)) {
            addButtonsToScreen(0,200,volumeUpView,R.id.volumeUp)
        }

        if (prefs.getBoolean("VOLUME_DOWN", false)) {
            addButtonsToScreen(0,500,volumeDownView,R.id.volumeDown)
        }

        if(prefs.getBoolean("MUTE", false)){
            addButtonsToScreen(0, 800, muteView, R.id.mute)
        }

    }

    private fun inflateViews(resource: Int): View {
        return LayoutInflater.from(this).inflate(resource, null)
    }


    private fun addButtonsToScreen(positionX:Int, positionY: Int, item:View?, id:Int){
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.LEFT
            x = positionX
            y = positionY
        }

        windowManager?.addView(item, params)
        clickAction(item!!, id, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.keySet()?.forEach { key ->
            val value = intent.extras?.getBoolean(key, false) ?: false

            when (key) {
                "VOLUME_UP" -> {
                    if (value) {
                        if (volumeUpView?.isAttachedToWindow == false) {
                            addButtonsToScreen(0,200,volumeUpView,R.id.volumeUp)
                        }
                    } else {
                        if (volumeUpView?.isAttachedToWindow == true) {
                            windowManager?.removeView(volumeUpView)
                        }
                    }
                }

                "VOLUME_DOWN" -> {
                    if (value) {
                        if (volumeDownView?.isAttachedToWindow == false) {
                            addButtonsToScreen(0,500,volumeDownView,R.id.volumeDown)
                        }
                    } else {
                        if (volumeDownView?.isAttachedToWindow == true) {
                            windowManager?.removeView(volumeDownView)
                        }
                    }
                }

                "MUTE" -> {
                    if (value) {
                        if (muteView?.isAttachedToWindow == false) {
                            addButtonsToScreen(0,800,muteView,R.id.mute)
                        }
                    } else {
                        if (muteView?.isAttachedToWindow == true) {
                            windowManager?.removeView(muteView)
                        }
                    }
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (volumeUpView?.isAttachedToWindow == true) windowManager?.removeView(volumeUpView)
            if (volumeDownView?.isAttachedToWindow == true) windowManager?.removeView(volumeDownView)
            if (muteView?.isAttachedToWindow == true) windowManager?.removeView(muteView)
        } catch (_: Exception) {
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickAction(view: View, id: Int, params: WindowManager.LayoutParams) {
        view.findViewById<View>(id)?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                event ?: return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val xDiff = (event.rawX - initialTouchX).toInt()
                        val yDiff = (event.rawY - initialTouchY).toInt()
                        if (xDiff < 10 && yDiff < 10) {
                            val audioManager =
                                getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            when (id) {
                                R.id.volumeUp -> audioManager.adjustStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    AudioManager.ADJUST_RAISE,
                                    AudioManager.FLAG_SHOW_UI
                                )

                                R.id.volumeDown -> audioManager.adjustStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    AudioManager.ADJUST_LOWER,
                                    AudioManager.FLAG_SHOW_UI
                                )

                                R.id.mute -> audioManager.adjustStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    AudioManager.ADJUST_MUTE,
                                    AudioManager.FLAG_SHOW_UI
                                )
                            }
                        }
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(view, params)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun startForegroundServiceSafe() {
        val channelId = "com.example.mediacontrol.overlay"
        val channelName = "Floating Overlay Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val notification = NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setContentTitle("App running in background")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()

            startForeground(1, notification)
        } else {
            startForeground(1, Notification())
        }
    }
}
