package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.semantics.*
import com.example.ui.theme.MyApplicationTheme

// ============================================================
// String Resources — Home Screen ID / EN
// ============================================================
private val homeStrings = mapOf(
    "id" to mapOf(
        "switch_activate" to "Aktifkan Layanan Melayang",
        "state_active" to "Aktif",
        "state_inactive" to "Nonaktif",
        "status_bubble" to "STATUS GELEMBUNG",
        "volume_music" to "Volume Aliran Musik",
        "volume_down" to "Volume Turun",
        "volume_up" to "Volume Naik",
        "volume_sync" to "Tersinkronisasi penuh dengan volume media default ponsel Anda.",
        "bubble_size" to "Ukuran Gelembung Melayang",
        "bubble_size_desc" to "Sesuaikan diameter gelembung melayang agar pas dan nyaman saat Anda gunakan.",
        "bubble_opacity" to "Transparansi Gelembung Melayang",
        "bubble_opacity_desc" to "Atur transparansi gelembung (10% - 100%) agar menyatu secara alami dengan wallpaper layar Anda.",
        "system_access" to "AKSES & PERIZINAN SISTEM",
        "overlay_title" to "Tampilkan di Atas Aplikasi Lain",
        "overlay_desc" to "Diperlukan untuk memunculkan gelembung melayang di atas aplikasi lain.",
        "overlay_granted" to "DIIZINKAN",
        "overlay_grant" to "IZINKAN",
        "service_title" to "Sistem Latar Belakang (Service)",
        "service_desc" to "Menjaga gelembung melayang tetap siaga melayani Anda setiap saat.",
        "service_active" to "AKTIF",
        "service_support" to "MENDUKUNG",
        "notif_title" to "Izin Notifikasi",
        "notif_desc" to "Diperlukan untuk memunculkan panel kontrol cepat pada laci notifikasi.",
        "notif_granted" to "DIIZINKAN",
        "notif_grant" to "IZINKAN",
        "tips_icon" to "Tips",
        "gesture_guide" to "Panduan Gestur Gelembung",
        "gesture_swipe" to "Geser Gelembung",
        "gesture_swipe_desc" to "Sentuh dan geser lingkaran melayang ke posisi mana pun di layar Anda.",
        "gesture_single" to "Ketuk Sekali",
        "gesture_single_desc" to "Memunculkan widget pengatur volume default bawaan ponsel Anda.",
        "gesture_double" to "Ketuk Dua Kali",
        "gesture_double_desc" to "Membisukan (Mute) atau membunyikan kembali (Unmute) suara ponsel seketika.",
        "gesture_long" to "Tekan Lama (0.5s)",
        "gesture_long_desc" to "Membuka kembali menu pengaturan utama aplikasi ini dari layar mana saja.",
        "gesture_auto" to "Redup Otomatis",
        "gesture_auto_desc" to "Gelembung akan meredup hingga 30% dari keburaman utama jika didiamkan selama 5 detik agar tersamar sempurna.",
        "version" to "Versi 4.0.0",
    ),
    "en" to mapOf(
        "switch_activate" to "Enable Floating Service",
        "state_active" to "Active",
        "state_inactive" to "Inactive",
        "status_bubble" to "BUBBLE STATUS",
        "volume_music" to "Music Stream Volume",
        "volume_down" to "Volume Down",
        "volume_up" to "Volume Up",
        "volume_sync" to "Fully synchronized with your phone's default media volume.",
        "bubble_size" to "Floating Bubble Size",
        "bubble_size_desc" to "Adjust the bubble diameter to fit comfortably on your screen.",
        "bubble_opacity" to "Floating Bubble Opacity",
        "bubble_opacity_desc" to "Set bubble opacity (10% - 100%) to blend naturally with your wallpaper.",
        "system_access" to "SYSTEM ACCESS & PERMISSIONS",
        "overlay_title" to "Display Over Other Apps",
        "overlay_desc" to "Required to show the floating bubble over other applications.",
        "overlay_granted" to "GRANTED",
        "overlay_grant" to "ALLOW",
        "service_title" to "Background System (Service)",
        "service_desc" to "Keeps the floating bubble ready to serve you at all times.",
        "service_active" to "ACTIVE",
        "service_support" to "SUPPORTED",
        "notif_title" to "Notification Permission",
        "notif_desc" to "Required to show quick control panel in the notification drawer.",
        "notif_granted" to "GRANTED",
        "notif_grant" to "ALLOW",
        "tips_icon" to "Tips",
        "gesture_guide" to "Bubble Gesture Guide",
        "gesture_swipe" to "Swipe Bubble",
        "gesture_swipe_desc" to "Touch and drag the floating circle to any position on your screen.",
        "gesture_single" to "Single Tap",
        "gesture_single_desc" to "Opens your phone's default volume control widget.",
        "gesture_double" to "Double Tap",
        "gesture_double_desc" to "Mute or unmute your phone's sound instantly.",
        "gesture_long" to "Long Press (0.5s)",
        "gesture_long_desc" to "Reopens this app's main settings from anywhere.",
        "gesture_auto" to "Auto Dim",
        "gesture_auto_desc" to "Bubble dims to 30% opacity after 5 seconds of inactivity for a subtle effect.",
        "version" to "Version 4.0.0",
    )
)

private fun getHomeString(ctx: Context, key: String): String {
    val lang = ctx.getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
        .getString("language", "id") ?: "id"
    return homeStrings[lang]?.get(key) ?: homeStrings["id"]?.get(key) ?: key
}

class MainActivity : ComponentActivity() {

    private lateinit var audioManager: AudioManager

    // Observable volume states in compose
    private val currentVolume = mutableIntStateOf(0)
    private val maxVolume = mutableIntStateOf(15)
    private val bubbleSize = mutableIntStateOf(54)
    private val bubbleOpacity = mutableIntStateOf(100)

    // Observable service isRunning state
    private val serviceRunning = mutableStateOf(false)

    // Observable permission statuses
    private val overlayGranted = mutableStateOf(false)
    private val notificationGranted = mutableStateOf(false)

    // Settings dialog state
    private val showSettings = mutableStateOf(false)

    // Language state for bilingual UI
    private val currentLang = mutableStateOf("id")

    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                val streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
                if (streamType == AudioManager.STREAM_MUSIC) {
                    syncVolumeState()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        syncVolumeState()
        currentLang.value = getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
            .getString("language", "id") ?: "id"

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_app_logo),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Text(
                                        text = "Float Volume",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp,
                                        letterSpacing = 0.5.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            actions = {
                                IconButton(onClick = { showSettings.value = true }) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_menu_preferences),
                                        contentDescription = "Settings",
                                        tint = Color(0xFFD0BCFF)
                                    )
                                }
                            }
                            )
                        }
                    ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        DashboardScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp)
                                .verticalScroll(rememberScrollState()),
                            lang = currentLang.value,
                            currentVolume = currentVolume.intValue,
                            maxVolume = maxVolume.intValue,
                            bubbleSize = bubbleSize.intValue,
                            onBubbleSizeChange = { newSize ->
                                bubbleSize.intValue = newSize
                                getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putInt("bubble_size", newSize)
                                    .apply()
                                // Notify service if running
                                if (serviceRunning.value) {
                                    val intent = Intent(this@MainActivity, VolumeFloatingService::class.java).apply {
                                        action = VolumeFloatingService.ACTION_UPDATE_SIZE
                                    }
                                    startService(intent)
                                }
                            },
                            bubbleOpacity = bubbleOpacity.intValue,
                            onBubbleOpacityChange = { newOpacity ->
                                bubbleOpacity.intValue = newOpacity
                                getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putInt("bubble_opacity", newOpacity)
                                    .apply()
                                // Notify service if running
                                if (serviceRunning.value) {
                                    val intent = Intent(this@MainActivity, VolumeFloatingService::class.java).apply {
                                        action = VolumeFloatingService.ACTION_UPDATE_OPACITY
                                    }
                                    startService(intent)
                                }
                            },
                            overlayGranted = overlayGranted.value,
                            notificationGranted = notificationGranted.value,
                            isServiceRunning = serviceRunning.value,
                            onVolumeChange = { progress ->
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                                syncVolumeState()
                            },
                            onVolumeAdjust = { direction ->
                                audioManager.adjustStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    direction,
                                    AudioManager.FLAG_SHOW_UI
                                )
                                syncVolumeState()
                            },
                            onRequestOverlayPermission = {
                                requestOverlayPermission()
                            },
                            onRequestNotificationPermission = {
                                requestNotificationPermission()
                            },
                            onToggleService = {
                                toggleService()
                            }
                        )
                    }

                    if (showSettings.value) {
                        SettingsDialog(
                            onDismiss = { showSettings.value = false }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
        syncServiceState()
        syncVolumeState()
        bubbleSize.intValue = getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE).getInt("bubble_size", 54)
        bubbleOpacity.intValue = getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE).getInt("bubble_opacity", 100)
        currentLang.value = getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
            .getString("language", "id") ?: "id"
        
        // Dynamic volume broad receiver sync
        @Suppress("DEPRECATION")
        registerReceiver(
            volumeReceiver,
            IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        )
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(volumeReceiver)
        } catch (e: Exception) {
            // Safety
        }
    }

    private fun syncVolumeState() {
        currentVolume.intValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        maxVolume.intValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    private fun syncServiceState() {
        serviceRunning.value = VolumeFloatingService.isRunning
    }

    private fun checkPermissions() {
        overlayGranted.value = Settings.canDrawOverlays(this)
        notificationGranted.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Automatic compatibility
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            startActivity(intent)
        }
    }

    private fun toggleService() {
        val prefs = getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
        if (VolumeFloatingService.isRunning) {
            stopService(Intent(this, VolumeFloatingService::class.java))
            prefs.edit().putBoolean("service_enabled", false).apply()
        } else {
            if (Settings.canDrawOverlays(this)) {
                val serviceIntent = Intent(this, VolumeFloatingService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
                prefs.edit().putBoolean("service_enabled", true).apply()
            }
        }
        // Small delay to allow running state boolean to reflect
        handler.postDelayed({
            syncServiceState()
        }, 150)
    }

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
}

@Composable
fun CustomElegantSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = checked, label = "SwitchState")
    val thumbOffset by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessMedium) },
        label = "ThumbOffset"
    ) { state ->
        if (state) 24.dp else 4.dp
    }
    
    val trackColor by animateColorAsState(
        targetValue = if (checked) Color(0xFFD0BCFF) else Color(0xFF49454F),
        animationSpec = tween(durationMillis = 200),
        label = "TrackColor"
    )
    val thumbColor = if (checked) Color(0xFF381E72) else Color(0xFFE6E1E5)
    
    Box(
        modifier = modifier
            .width(56.dp)
            .height(32.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                onClickLabel = "Enable Floating Service",
                role = Role.Switch
            ) { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
            .semantics {
                stateDescription = if (checked) "Active" else "Inactive"
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(24.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    lang: String,
    currentVolume: Int,
    maxVolume: Int,
    bubbleSize: Int,
    onBubbleSizeChange: (Int) -> Unit,
    bubbleOpacity: Int,
    onBubbleOpacityChange: (Int) -> Unit,
    overlayGranted: Boolean,
    notificationGranted: Boolean,
    isServiceRunning: Boolean,
    onVolumeChange: (Int) -> Unit,
    onVolumeAdjust: (Int) -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onToggleService: () -> Unit
) {
    val ctx = LocalContext.current
    Column(
        modifier = modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Service Status Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4F378B)
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "BUBBLE STATUS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = Color(0xFFEADDFF)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isServiceRunning) "Active" else "Inactive",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
                
                // Beautiful Animated Custom Switch Customizer
                CustomElegantSwitch(
                    checked = isServiceRunning,
                    onCheckedChange = { 
                        if (overlayGranted) {
                            onToggleService()
                        } else {
                            onRequestOverlayPermission()
                        }
                    }
                )
            }
        }

        // Mixer Level (STREAM_MUSIC)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2B2930)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Music Stream Volume",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE6E1E5)
                    )
                    val pct = if (maxVolume > 0) (currentVolume * 100) / maxVolume else 0
                    Text(
                        text = "$pct%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD0BCFF)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onVolumeAdjust(AudioManager.ADJUST_LOWER) },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFD0BCFF))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_volume_mute),
                            contentDescription = "Volume Down"
                        )
                    }

                    // Highly themed Elegant Dark styled slider
                    Slider(
                        value = currentVolume.toFloat(),
                        onValueChange = { onVolumeChange(it.toInt()) },
                        valueRange = 0f..maxVolume.toFloat(),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFD0BCFF),
                            activeTrackColor = Color(0xFFD0BCFF),
                            inactiveTrackColor = Color(0xFF49454F)
                        )
                    )

                    IconButton(
                        onClick = { onVolumeAdjust(AudioManager.ADJUST_RAISE) },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFD0BCFF))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_volume_control_icon),
                            contentDescription = "Volume Up"
                        )
                    }
                }
                
                Text(
                    text = "Fully synchronized with your phone's default media volume.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCAC4D0),
                    fontSize = 12.sp
                )
            }
        }

        // Ukuran Lingkaran Melayang (Sizing Customizer Card)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2B2930)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Floating Bubble Size",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE6E1E5)
                    )
                    Text(
                        text = "$bubbleSize dp",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD0BCFF)
                    )
                }

                Slider(
                    value = bubbleSize.toFloat(),
                    onValueChange = { onBubbleSizeChange(it.toInt()) },
                    valueRange = 36f..76f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFD0BCFF),
                        activeTrackColor = Color(0xFFD0BCFF),
                        inactiveTrackColor = Color(0xFF49454F)
                    )
                )

                Text(
                    text = "Adjust the bubble diameter to fit comfortably on your screen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCAC4D0),
                    fontSize = 12.sp
                )
            }
        }

        // Transparansi Lingkaran Melayang (Opacity Customizer Card)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2B2930)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Floating Bubble Opacity",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE6E1E5)
                    )
                    Text(
                        text = "$bubbleOpacity%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD0BCFF)
                    )
                }

                Slider(
                    value = bubbleOpacity.toFloat(),
                    onValueChange = { onBubbleOpacityChange(it.toInt()) },
                    valueRange = 10f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFD0BCFF),
                        activeTrackColor = Color(0xFFD0BCFF),
                        inactiveTrackColor = Color(0xFF49454F)
                    )
                )

                Text(
                    text = "Set bubble opacity (10% - 100%) to blend naturally with your wallpaper.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCAC4D0),
                    fontSize = 12.sp
                )
            }
        }


        // Permissions Status Group Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2B2930)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "SYSTEM ACCESS & PERMISSIONS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFFD0BCFF)
                )

                // Item 1: Overlay Permission
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Display Over Other Apps",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE6E1E5)
                        )
                        Text(
                            text = "Required to show the floating bubble over other applications.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCAC4D0),
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    if (overlayGranted) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1a331c), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "GRANTED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFb5e6b7)
                            )
                        }
                    } else {
                        Button(
                            onClick = onRequestOverlayPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4e2525)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = "ALLOW",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFf5c2c2)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF49454F), thickness = 1.dp)

                // Item 2: Foreground Service Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Background System (Service)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE6E1E5)
                        )
                        Text(
                            text = "Keeps the floating bubble ready to serve you at all times.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCAC4D0),
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    if (isServiceRunning) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1a331c), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFb5e6b7)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF33221a), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SUPPORTED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFe6c8b5)
                            )
                        }
                    }
                }

                // Android 13+ Notification item if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    HorizontalDivider(color = Color(0xFF49454F), thickness = 1.dp)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notification Permission",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE6E1E5)
                            )
                            Text(
                                text = "Required to show quick control panel in the notification drawer.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCAC4D0),
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        if (notificationGranted) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1a331c), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "GRANTED",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFb5e6b7)
                                )
                            }
                        } else {
                            Button(
                                onClick = onRequestNotificationPermission,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4e2525)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = "ALLOW",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFf5c2c2)
                                )
                            }
                        }
                    }
                }
            }
        }


        // Help/Gestures Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2B2930)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Tips",
                        tint = Color(0xFFD0BCFF)
                    )
                    Text(
                        text = "Bubble Gesture Guide",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE6E1E5)
                    )
                }

                val tipsId = listOf(
                    "<b>Geser Gelembung</b>: Sentuh dan geser lingkaran melayang ke posisi mana pun di layar Anda.",
                    "<b>Ketuk Sekali</b>: Memunculkan widget pengatur volume default bawaan ponsel Anda.",
                    "<b>Ketuk Dua Kali</b>: Membisukan (Mute) atau membunyikan kembali (Unmute) suara ponsel seketika.",
                    "<b>Tekan Lama (0.5s)</b>: Membuka kembali menu pengaturan utama aplikasi ini dari layar mana saja.",
                    "<b>Redup Otomatis</b>: Gelembung akan meredup hingga 30% dari keburaman utama jika didiamkan selama 5 detik agar tersamar sempurna."
                )
                val tipsEn = listOf(
                    "<b>Swipe Bubble</b>: Touch and drag the floating circle to any position on your screen.",
                    "<b>Single Tap</b>: Opens your phone's default volume control widget.",
                    "<b>Double Tap</b>: Mute or unmute your phone's sound instantly.",
                    "<b>Long Press (0.5s)</b>: Reopens this app's main settings from anywhere.",
                    "<b>Auto Dim</b>: Bubble dims to 30% opacity after 5 seconds of inactivity for a subtle effect."
                )
                val tips = if (lang == "en") tipsEn else tipsId

                tips.forEach { htmlText ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD0BCFF)
                        )
                        Text(
                            text = android.text.Html.fromHtml(htmlText, android.text.Html.FROM_HTML_MODE_LEGACY).toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFCAC4D0)
                        )
                    }
                }
            }
        }

        // Footer
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (lang == "en") "Version 4.0.0" else "Versi 4.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF49454F),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
    }
}

