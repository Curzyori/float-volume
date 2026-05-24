package com.example

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.NotificationCompat
import kotlin.math.abs

class VolumeFloatingService : Service() {

    companion object {
        const val CHANNEL_ID = "floating_volume_service_channel"
        const val NOTIFICATION_ID = 8877
        const val ACTION_STOP = "com.example.ACTION_STOP"
        const val ACTION_UPDATE_SIZE = "com.example.ACTION_UPDATE_SIZE"
        
        @Volatile
        var isRunning = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var audioManager: AudioManager
    private lateinit var floatingView: View

    private lateinit var rootContainer: LinearLayout
    private lateinit var sliderPanel: LinearLayout
    private lateinit var floatingButton: FrameLayout
    private lateinit var floatingIcon: ImageView
    private lateinit var sliderVolumeIcon: ImageView
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumePercentage: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var isDimmed = false

    // 5-second idle runnable to dim opacity to 30%
    private val dimRunnable = Runnable {
        animateAlpha(0.3f)
        isDimmed = true
        // Automatically collapse the slider panel on idle
        if (sliderPanel.visibility == View.VISIBLE) {
            sliderPanel.visibility = View.GONE
        }
    }

    // Broadcast receiver to sync with physical volume key hits
    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                val streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
                if (streamType == AudioManager.STREAM_MUSIC) {
                    updateSeekBarToSystemVolume()
                    resetIdleTimer()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        setupFloatingView()
        registerVolumeReceiver()

        resetIdleTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopSelf()
            ACTION_UPDATE_SIZE -> {
                val sizeDp = getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE).getInt("bubble_size", 54)
                applyBubbleSize(sizeDp)
            }
        }
        return START_STICKY
    }

    private fun setupFloatingView() {
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_volume_layout, null)

        rootContainer = floatingView.findViewById(R.id.root_container)
        sliderPanel = floatingView.findViewById(R.id.slider_panel)
        floatingButton = floatingView.findViewById(R.id.floating_button)
        floatingIcon = floatingView.findViewById(R.id.floating_icon)
        floatingIcon.visibility = View.GONE
        sliderVolumeIcon = floatingView.findViewById(R.id.slider_volume_icon)
        volumeSeekBar = floatingView.findViewById(R.id.volume_seekbar)
        volumePercentage = floatingView.findViewById(R.id.volume_percentage)

        // Setup clear contrast programmatically
        floatingIcon.setColorFilter(Color.parseColor("#1C1B1F")) // dark charcoal contrast on white button
        sliderVolumeIcon.setColorFilter(Color.WHITE) // crisp white on grey capsule background

        // Configure system layout params
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 400

        // Handle draggable touch behavior for floating round button
        floatingButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var touchStartTime = 0L
            private var isDragging = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                resetIdleTimer()

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        touchStartTime = System.currentTimeMillis()
                        isDragging = false
                        v.scaleX = 0.95f
                        v.scaleY = 0.95f
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY

                        if (abs(dx) > 10 || abs(dy) > 10) {
                            isDragging = true
                            params.x = (initialX + dx).toInt()
                            params.y = (initialY + dy).toInt()
                            try {
                                windowManager.updateViewLayout(floatingView, params)
                            } catch (e: Exception) {
                                // View layout exception safety
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        v.scaleX = 1.0f
                        v.scaleY = 1.0f
                        val duration = System.currentTimeMillis() - touchStartTime
                        val dist = abs(event.rawX - initialTouchX) + abs(event.rawY - initialTouchY)

                        if (!isDragging || (dist < 15 && duration < 250)) {
                            onBubbleClicked()
                        }
                        return true
                    }
                }
                return false
            }
        })

        // Setup custom volume slider seekbar listener
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.max = maxVol
        volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        updateSeekBarToSystemVolume()

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    updateSeekBarToSystemVolume()
                    resetIdleTimer()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                resetIdleTimer()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                resetIdleTimer()
            }
        })

        windowManager.addView(floatingView, params)

        // Apply persisted floating bubble size
        val sizeDp = getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE).getInt("bubble_size", 54)
        applyBubbleSize(sizeDp)
    }

    private fun applyBubbleSize(sizeDp: Int) {
        if (!::floatingButton.isInitialized) return
        val sizePx = (sizeDp * resources.displayMetrics.density).toInt()
        
        val lp = floatingButton.layoutParams
        lp.width = sizePx
        lp.height = sizePx
        floatingButton.layoutParams = lp
        
        floatingButton.requestLayout()
        
        // Ensure WindowManager knows to re-evaluate wrap_content bounds
        try {
            val params = floatingView.layoutParams as? WindowManager.LayoutParams
            if (params != null) {
                windowManager.updateViewLayout(floatingView, params)
            }
        } catch (e: Exception) {
            // Safety
        }
    }

    private fun onBubbleClicked() {
        // Toggle/Show system volume overlay panel (image 3)
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun updateSeekBarToSystemVolume() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        volumeSeekBar.progress = currentVolume

        val pct = if (maxVolume > 0) (currentVolume * 100) / maxVolume else 0
        volumePercentage.text = "$pct%"

        if (currentVolume == 0) {
            floatingIcon.setImageResource(R.drawable.ic_volume_mute)
            sliderVolumeIcon.setImageResource(R.drawable.ic_volume_mute)
        } else {
            floatingIcon.setImageResource(R.drawable.ic_volume_control_icon)
            sliderVolumeIcon.setImageResource(R.drawable.ic_volume_control_icon)
        }
    }

    private fun resetIdleTimer() {
        handler.removeCallbacks(dimRunnable)
        if (isDimmed) {
            animateAlpha(1.0f)
            isDimmed = false
        }
        handler.postDelayed(dimRunnable, 5000)
    }

    private fun animateAlpha(targetAlpha: Float) {
        rootContainer.animate()
            .alpha(targetAlpha)
            .setDuration(250)
            .start()
    }

    private fun registerVolumeReceiver() {
        val filter = IntentFilter()
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // "android.media.VOLUME_CHANGED_ACTION"
            filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        } else {
            filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        }
        registerReceiver(volumeReceiver, filter)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.service_running_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlags)

        val stopSelfIntent = Intent(this, VolumeFloatingService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(this, 1, stopSelfIntent, pendingIntentFlags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_desc))
            .setSmallIcon(R.drawable.ic_volume_control_icon)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop Service",
                stopPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        isRunning = false
        handler.removeCallbacks(dimRunnable)
        try {
            unregisterReceiver(volumeReceiver)
        } catch (e: Exception) {
            // Safety
        }
        try {
            windowManager.removeView(floatingView)
        } catch (e: Exception) {
            // Safety
        }
        super.onDestroy()
    }
}
