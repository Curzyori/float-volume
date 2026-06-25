package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// String Resources — ID / EN
// ============================================================
private val strings = mapOf(
    "id" to mapOf(
        "settings_title" to "Pengaturan",
        "language_label" to "Bahasa",
        "github_label" to "GitHub Repo",
        "website_label" to "Website",
        "bmc_label" to "Traktir Kopi",
        "evm_label" to "EVM (ETH / BNB / Polygon)",
        "btc_label" to "BTC",
        "copied" to "Alamat disalin!",
        "copy_failed" to "Gagal menyalin",
        "close" to "Tutup",
    ),
    "en" to mapOf(
        "settings_title" to "Settings",
        "language_label" to "Language",
        "github_label" to "GitHub Repo",
        "website_label" to "Website",
        "bmc_label" to "Buy Me a Coffee",
        "evm_label" to "EVM (ETH / BNB / Polygon)",
        "btc_label" to "BTC",
        "copied" to "Address copied!",
        "copy_failed" to "Failed to copy",
        "close" to "Close",
    )
)

private fun getString(ctx: Context, key: String): String {
    val lang = ctx.getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
        .getString("language", "id") ?: "id"
    return strings[lang]?.get(key) ?: strings["id"]?.get(key) ?: key
}

// ============================================================
// Constants
// ============================================================
private const val GITHUB_URL = "https://github.com/Curzyori/float-volume"
private const val WEBSITE_URL = "https://float-volume.curzy.dev/"

private const val EVM_ADDRESS = "0x54e18F0345a099D9FE6dd0576bb1699733c44735"
private const val BTC_ADDRESS = "bc1q7g5whvwjvrh7mtuap2tu7qh3tyyhvls36cp7fs"

// ============================================================
// Settings Dialog
// ============================================================
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedLang by remember {
        val saved = context.getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
            .getString("language", "id") ?: "id"
        mutableStateOf(saved)
    }

    fun saveLang(lang: String) {
        selectedLang = lang
        context.getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("language", lang)
            .apply()
    }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun copyAddress(address: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("address", address)
        clipboard.setPrimaryClip(clip)
        Toast
            .makeText(context, getString(context, "copied"), Toast.LENGTH_SHORT)
            .apply {
                setGravity(android.view.Gravity.BOTTOM, 0, 200)
                show()
            }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(24.dp),
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF2B2930),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u2699\ufe0f",
                        fontSize = 18.sp
                    )
                    Text(
                        text = getString(context, "settings_title"),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFFE6E1E5)
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = "\u2715",
                        fontSize = 16.sp,
                        color = Color(0xFFCAC4D0)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // --- Language ---
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\ud83c\udf10",
                            fontSize = 16.sp
                        )
                        Text(
                            text = getString(context, "language_label"),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFFCAC4D0)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LanguageChip(
                            label = "EN",
                            selected = selectedLang == "en",
                            onClick = { saveLang("en") }
                        )
                        LanguageChip(
                            label = "ID",
                            selected = selectedLang == "id",
                            onClick = { saveLang("id") }
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF49454F), thickness = 0.5.dp)

                // --- GitHub ---
                LinkRow(
                    icon = "\ud83d\udcbb",
                    label = getString(context, "github_label"),
                    onClick = { openUrl(GITHUB_URL) }
                )

                // --- Website ---
                LinkRow(
                    icon = "\ud83c\udf10",
                    label = getString(context, "website_label"),
                    onClick = { openUrl(WEBSITE_URL) }
                )

                HorizontalDivider(color = Color(0xFF49454F), thickness = 0.5.dp)

                // --- Buy Me a Coffee ---
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = getString(context, "bmc_label"),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color(0xFFCAC4D0)
                    )

                    CryptoAddressCard(
                        label = getString(context, "evm_label"),
                        address = EVM_ADDRESS,
                        onCopy = { copyAddress(EVM_ADDRESS) }
                    )

                    CryptoAddressCard(
                        label = getString(context, "btc_label"),
                        address = BTC_ADDRESS,
                        onCopy = { copyAddress(BTC_ADDRESS) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = getString(context, "close"),
                    color = Color(0xFFD0BCFF)
                )
            }
        }
    )
}

// ============================================================
// Language Toggle Chip
// ============================================================
@Composable
private fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) Color(0xFF4F378B) else Color(0xFF49454F)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (selected) Color.White else Color(0xFFCAC4D0)
        )
    }
}

// ============================================================
// Link Row (GitHub / Website)
// ============================================================
@Composable
private fun LinkRow(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3540))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 16.sp)
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color(0xFFE6E1E5)
            )
        }
        Text(
            text = "\u2197\ufe0f",
            fontSize = 16.sp,
            color = Color(0xFFCAC4D0)
        )
    }
}

// ============================================================
// Crypto Address Card
// ============================================================
@Composable
private fun CryptoAddressCard(
    label: String,
    address: String,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3540))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = Color(0xFFD0BCFF)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = address,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFFCAC4D0),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(32.dp)
            ) {
                Text(
                    text = "📋",
                    fontSize = 16.sp
                )
            }
        }
    }
}
