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
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF1C1B1F))
                                            .border(1.dp, Color(0xFF2B2930), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
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
                            )
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
                onClickLabel = "Aktifkan Layanan Melayang",
                role = Role.Switch
            ) { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
            .semantics {
                stateDescription = if (checked) "Aktif" else "Nonaktif"
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
                        text = "STATUS LAYANAN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = Color(0xFFEADDFF)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isServiceRunning) "Aktif" else "Berhenti",
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
                        text = "Mixer Level (STREAM_MUSIC)",
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
                    text = "Sinkronisasi langsung dengan aliran suara system default.",
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
                        text = "Ukuran Lingkaran Melayang",
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
                    text = "Sesuaikan ukuran lingkaran melayang di layar agar lebih pas dan nyaman digunakankan.",
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
                        text = "Transparansi Lingkaran Melayang",
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
                    text = "Sesuaikan tingkat transparansi tombol melayang (10% - 100%) agar tersamar sempurna di layar Anda.",
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
                    text = "AKSES DAN IZIN SYSTEM",
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
                            text = "Tampil Di Atas Aplikasi Lain",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE6E1E5)
                        )
                        Text(
                            text = "Diperlukan untuk menampilkan tombol lingkaran melayang.",
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
                                text = "DIIZINKAN",
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
                                text = "IZINKAN",
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
                            text = "Layanan Latar Depan (Service)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE6E1E5)
                        )
                        Text(
                            text = "Memastikan tombol melayang tetap aktif berjalan di sistem.",
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
                                text = "AKTIF",
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
                                text = "MENDUKUNG",
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
                                text = "Izin Notifikasi",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE6E1E5)
                            )
                            Text(
                                text = "Diperlukan untuk menampilkan kontrol di baris notifikasi.",
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
                                    text = "DIIZINKAN",
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
                                    text = "IZINKAN",
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
                        text = "Petunjuk Penggunaan Tombol",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE6E1E5)
                    )
                }

                val tips = listOf(
                    "<b>Geser Circle</b>: Sentuh lalu seret lingkaran melayang ke sudut mana pun di layar sesuai keinginan Anda.",
                    "<b>Ketuk Sekali</b>: Menampilkan kontrol widget pengatur suara bawaan (default HP) di layar.",
                    "<b>Ketuk Dua Kali</b>: Senyap (Mute) atau bunyikan kembali (Unmute) aliran suara default HP secara instan.",
                    "<b>Tekan Lama (0.5s)</b>: Membuka dashboard utama Float Volume secara instan dari mana saja.",
                    "<b>Redup Otomatis</b>: Diamkan selama 5 detik untuk meredupkan lingkaran ke 30% dari transparansi aktif Anda secara proporsional."
                )

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

        // Footer Pembuat
        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Projects 10 By Curzy",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD0BCFF)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GitHub",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCAC4D0),
                    modifier = Modifier
                        .clickable { uriHandler.openUri("https://github.com/Curzyori/Float-Volume-10") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF49454F)
                )
                Text(
                    text = "Website",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCAC4D0),
                    modifier = Modifier
                        .clickable { uriHandler.openUri("https://curzy.my.id/") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Text(
                text = "Version 3.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF49454F),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

