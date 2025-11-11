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

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceSafe()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Inflate views
        volumeUpView = LayoutInflater.from(this).inflate(R.layout.volume_up_button, null)
        volumeDownView = LayoutInflater.from(this).inflate(R.layout.volume_down_button, null)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val paramsUp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.LEFT
            x = 0
            y = 200
        }

        val paramsDown = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.LEFT
            x = 0
            y = 500
        }

        // Add views based on saved visibility
        if (prefs.getBoolean("VOLUME_UP", false)) {
            windowManager?.addView(volumeUpView, paramsUp)
        }

        if (prefs.getBoolean("VOLUME_DOWN", false)) {
            windowManager?.addView(volumeDownView, paramsDown)
        }

        // Touch & click handling
        clickAction(volumeUpView!!, R.id.volumeUp, paramsUp)
        clickAction(volumeDownView!!, R.id.volumeDown, paramsDown)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.keySet()?.forEach { key ->
            val value = intent.extras?.getBoolean(key, false) ?: false

            when (key) {
                "VOLUME_UP" -> {
                    if (value) {
                        if (volumeUpView?.isAttachedToWindow == false) {
                            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            else
                                WindowManager.LayoutParams.TYPE_PHONE

                            val paramsUp = WindowManager.LayoutParams(
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                layoutFlag,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                PixelFormat.TRANSLUCENT
                            ).apply {
                                gravity = Gravity.TOP or Gravity.LEFT
                                x = 0
                                y = 200
                            }

                            windowManager?.addView(volumeUpView, paramsUp)
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
                            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            else
                                WindowManager.LayoutParams.TYPE_PHONE

                            val paramsDown = WindowManager.LayoutParams(
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                layoutFlag,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                PixelFormat.TRANSLUCENT
                            ).apply {
                                gravity = Gravity.TOP or Gravity.LEFT
                                x = 0
                                y = 500
                            }

                            windowManager?.addView(volumeDownView, paramsDown)
                        }
                    } else {
                        if (volumeDownView?.isAttachedToWindow == true) {
                            windowManager?.removeView(volumeDownView)
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
                            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
