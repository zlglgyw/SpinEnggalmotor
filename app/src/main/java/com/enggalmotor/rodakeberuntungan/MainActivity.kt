package com.enggalmotor.rodakeberuntungan

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.TextUtils
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.enggalmotor.rodakeberuntungan.ui.theme.Gold
import com.enggalmotor.rodakeberuntungan.ui.theme.Navy
import com.enggalmotor.rodakeberuntungan.ui.theme.NavyDark
import com.enggalmotor.rodakeberuntungan.ui.theme.Orange
import com.enggalmotor.rodakeberuntungan.ui.theme.OrangeLight
import com.enggalmotor.rodakeberuntungan.ui.theme.Red
import com.enggalmotor.rodakeberuntungan.ui.theme.RodaKeberuntunganTheme
import com.enggalmotor.rodakeberuntungan.ui.theme.WheelColors
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val PREFS_NAME = "roda_keberuntungan"
private const val KEY_PRIZES = "prizes"
private const val KEY_PIN = "pin"
private const val KEY_LOCK = "lock_enabled"
private const val KEY_THEME = "theme"
private const val DEFAULT_PIN = "1234"
private val DEFAULT_PRIZES =
    listOf("Oli Federal 1 Liter", "Kaos Enggal Motor", "Gelas", "Gantungan Kunci")

private data class ThemePalette(
    val name: String,
    val top: Color,
    val mid: Color,
    val bottom: Color,
    val accent: Color,
    val buttonFrom: Color,
    val buttonTo: Color,
    val dot: Color
)

private val THEMES = listOf(
    ThemePalette(
        "Biru", NavyDark, Navy, Color(0xFF123A6B), Gold, Orange, OrangeLight,
        Color(0xFF1E5AA8)
    ),
    ThemePalette(
        "Merah", Color(0xFF2B0A0A), Color(0xFF7A1010), Color(0xFFA31616),
        Color(0xFFFFD166), Red, Color(0xFFFF6B6B), Color(0xFFC0392B)
    ),
    ThemePalette(
        "Hijau", Color(0xFF06281A), Color(0xFF0B5D3B), Color(0xFF0E7A4A),
        Color(0xFFFFD166), Color(0xFF1E9E5A), Color(0xFF4ADE80), Color(0xFF27AE60)
    ),
    ThemePalette(
        "Ungu", Color(0xFF1B1035), Color(0xFF3B1F6B), Color(0xFF5B2D9E),
        Color(0xFFFFD166), Color(0xFF7C3AED), Color(0xFFA78BFA), Color(0xFF8E44AD)
    )
)

private fun loadPrizes(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val stored = prefs.getString(KEY_PRIZES, null) ?: return DEFAULT_PRIZES
    return stored.split("\n").filter { it.isNotBlank() }
}

private fun savePrizes(context: Context, prizes: List<String>) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_PRIZES, prizes.joinToString("\n"))
        .apply()
}

private fun loadPin(context: Context): String {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_PIN, DEFAULT_PIN) ?: DEFAULT_PIN
}

private fun savePin(context: Context, pin: String) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_PIN, pin)
        .apply()
}

private fun loadLock(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(KEY_LOCK, true)
}

private fun saveLock(context: Context, enabled: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_LOCK, enabled)
        .apply()
}

private fun loadTheme(context: Context): Int {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getInt(KEY_THEME, 0)
}

private fun saveTheme(context: Context, index: Int) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putInt(KEY_THEME, index)
        .apply()
}

/** Short celebration vibration when a winner is drawn. */
private fun vibrateWin(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }
        if (vibrator?.hasVibrator() == true) {
            val pattern = longArrayOf(0, 200, 100, 200, 100, 400)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    } catch (_: Exception) {
        // Vibration is a bonus; never break the draw if it fails.
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RodaKeberuntunganTheme {
                RodaKeberuntunganScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RodaKeberuntunganScreen() {
    val context = LocalContext.current
    var prizes by remember { mutableStateOf(loadPrizes(context)) }
    var themeIndex by remember { mutableStateOf(loadTheme(context)) }
    val palette = THEMES[themeIndex.coerceIn(THEMES.indices)]
    var lockEnabled by remember { mutableStateOf(loadLock(context)) }
    var pin by remember { mutableStateOf(loadPin(context)) }
    var isSpinning by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    var showWinner by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }
    var newPrize by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var pinMsg by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Sound: short beeps only, no audio files (keeps the APK tiny and offline).
    val toneGen = remember {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (_: Exception) {
            null
        }
    }
    DisposableEffect(Unit) { onDispose { toneGen?.release() } }

    // Tick-tick sound while the wheel spins.
    LaunchedEffect(isSpinning) {
        while (isSpinning) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
            } catch (_: Exception) {
            }
            kotlinx.coroutines.delay(160)
        }
    }

    // Persist every change locally (SharedPreferences) — fully offline.
    LaunchedEffect(prizes) { savePrizes(context, prizes) }
    LaunchedEffect(themeIndex) { saveTheme(context, themeIndex) }
    LaunchedEffect(lockEnabled) { saveLock(context, lockEnabled) }

    fun spin() {
        if (isSpinning) return
        if (prizes.size < 2) {
            message = context.getString(R.string.need_two_items)
            return
        }
        message = ""
        val winnerIndex = Random.nextInt(prizes.size)
        val segmentAngle = 360f / prizes.size
        val centerAngle = winnerIndex * segmentAngle + segmentAngle / 2f
        val currentMod = ((rotation.value % 360f) + 360f) % 360f
        // Pointer is at the top (270 degrees in canvas coordinates).
        val delta = ((270f - centerAngle - currentMod) % 360f + 360f) % 360f
        // Longer, slower finish: 7 turns in 6 seconds, for suspense.
        val target = rotation.value + 2520f + delta
        scope.launch {
            isSpinning = true
            rotation.animateTo(
                targetValue = target,
                animationSpec = tween(durationMillis = 6000, easing = FastOutSlowInEasing)
            )
            isSpinning = false
            winner = prizes[winnerIndex]
            showWinner = true
            // Win jingle: 3 short beeps + 1 long beep, plus vibration.
            vibrateWin(context)
            try {
                repeat(3) {
                    toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                    kotlinx.coroutines.delay(200)
                }
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 500)
            } catch (_: Exception) {
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        // Small FAB, 56dp, bottom-right of the SCREEN, separate from PUTAR.
        // Scaffold places it at FabPosition.End; content padding below keeps
        // the PUTAR button clear of it.
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (lockEnabled) {
                        pinInput = ""
                        pinError = ""
                        showPinDialog = true
                    } else {
                        showSheet = true
                    }
                },
                modifier = Modifier.size(56.dp),
                containerColor = palette.mid,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = stringResource(id = R.string.manage_names)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(palette.top, palette.mid, palette.bottom)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // In-app header logo (same image as the launcher icon).
                Image(
                    painter = painterResource(id = R.drawable.logo_enggal_motor),
                    contentDescription = "Logo Enggal Motor",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.title),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.subtitle),
                    color = palette.accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                Wheel(
                    names = prizes,
                    rotationDegrees = rotation.value,
                    modifier = Modifier.size(320.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Big orange gradient PUTAR button with shadow.
                Button(
                    onClick = { spin() },
                    enabled = !isSpinning,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(64.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        palette.buttonFrom,
                                        palette.buttonTo,
                                        palette.buttonFrom
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.spin),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = message,
                        color = palette.accent,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.prizes_count, prizes.size),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Running promo text, like a shop signboard.
                // Native Android marquee (TextView): smooth and continuous.
                MarqueeText(
                    text = stringResource(id = R.string.marquee_text),
                    color = palette.accent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // --- Manage names: small bottom sheet only, never a full page ---
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = palette.mid
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.manage_names),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.prizes_count, prizes.size),
                    color = palette.accent,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newPrize,
                        onValueChange = { newPrize = it },
                        placeholder = { Text(text = stringResource(id = R.string.add_item_hint)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val trimmed = newPrize.trim()
                        if (trimmed.isNotEmpty()) {
                            prizes = prizes + trimmed
                            newPrize = ""
                        }
                    }) {
                        Text(text = stringResource(id = R.string.add))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    itemsIndexed(prizes) { index, prize ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prize,
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                prizes = prizes.toMutableList().also { it.removeAt(index) }
                            }) {
                                Text(text = "Hapus", color = Red)
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = { prizes = emptyList() },
                    enabled = prizes.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.clear_all))
                }

                Spacer(modifier = Modifier.height(20.dp))
                // --- Theme colors ---
                Text(
                    text = stringResource(id = R.string.theme_title),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    THEMES.forEachIndexed { index, theme ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { themeIndex = index }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(theme.dot)
                                    .border(
                                        width = if (index == themeIndex) 3.dp else 1.dp,
                                        color = if (index == themeIndex) {
                                            Color.White
                                        } else {
                                            Color.White.copy(alpha = 0.4f)
                                        },
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = theme.name,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                // --- PIN lock for the settings panel ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.lock_settings),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = lockEnabled,
                        onCheckedChange = { lockEnabled = it }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                newPin = it
                            }
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.new_pin_hint))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (newPin.length == 4) {
                            pin = newPin
                            savePin(context, newPin)
                            newPin = ""
                            pinMsg = context.getString(R.string.pin_saved)
                        } else {
                            pinMsg = context.getString(R.string.pin_four_digits)
                        }
                    }) {
                        Text(text = stringResource(id = R.string.change_pin))
                    }
                }
                if (pinMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pinMsg,
                        color = palette.accent,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // --- PIN entry dialog ---
    if (showPinDialog) {
        Dialog(onDismissRequest = {
            showPinDialog = false
            pinInput = ""
            pinError = ""
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(palette.mid)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.enter_pin),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                pinInput = it
                            }
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.pin_hint))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (pinError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pinError,
                            color = Red,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(onClick = {
                            if (pinInput == pin) {
                                showPinDialog = false
                                pinInput = ""
                                pinError = ""
                                showSheet = true
                            } else {
                                pinError = context.getString(R.string.wrong_pin)
                            }
                        }) {
                            Text(text = stringResource(id = R.string.unlock))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = {
                            showPinDialog = false
                            pinInput = ""
                            pinError = ""
                        }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                }
            }
        }
    }

    // --- Winner popup with confetti ---
    if (showWinner && winner != null) {
        // Blinking winner name.
        val winnerBlink = rememberInfiniteTransition(label = "winnerBlink")
        val winnerAlpha by winnerBlink.animateFloat(
            initialValue = 1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "winnerAlpha"
        )
        Dialog(onDismissRequest = { showWinner = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(palette.mid)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Confetti(modifier = Modifier.fillMaxSize())
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.congrats),
                        color = palette.accent,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.winner_title),
                        color = palette.accent,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = winner!!,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(winnerAlpha)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Button(onClick = {
                            showWinner = false
                            spin()
                        }) {
                            Text(text = stringResource(id = R.string.spin_again))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { showWinner = false }) {
                            Text(text = stringResource(id = R.string.close))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarqueeText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                this.text = text
                setTextColor(color.toArgb())
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setSingleLine(true)
                setHorizontallyScrolling(true)
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
                isSelected = true
                isFocusable = true
                isFocusableInTouchMode = true
            }
        },
        update = { view ->
            view.text = text
            view.setTextColor(color.toArgb())
        },
        modifier = modifier
    )
}

@Composable
private fun Wheel(
    names: List<String>,
    rotationDegrees: Float,
    modifier: Modifier = Modifier
) {
    // Blinking lights around the wheel (two alternating groups).
    val infinite = rememberInfiniteTransition(label = "lights")
    val blink by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationDegrees)
        ) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val count = names.size.coerceAtLeast(1)
            val sweep = 360f / count
            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }
            for (i in names.indices) {
                val color = WheelColors[i % WheelColors.size]
                drawArc(
                    color = color,
                    startAngle = i * sweep,
                    sweepAngle = sweep,
                    useCenter = true,
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                    topLeft = Offset(center.x - radius, center.y - radius)
                )
                // Full name, bold white, readable — never a single letter.
                val name = names[i]
                paint.textSize = when {
                    name.length <= 6 -> 52f
                    name.length <= 10 -> 42f
                    else -> 32f
                }
                val centerAngle = i * sweep + sweep / 2f
                val textRadius = radius * 0.62f
                with(drawContext.canvas) {
                    nativeCanvas.save()
                    nativeCanvas.translate(center.x, center.y)
                    nativeCanvas.rotate(centerAngle)
                    // drawText x is along the radius, y is the baseline offset.
                    nativeCanvas.drawText(name, textRadius, paint.textSize / 3f, paint)
                    nativeCanvas.restore()
                }
            }
            // Black divider lines between the colored segments.
            for (i in 0 until count) {
                val angle = Math.toRadians((i * sweep).toDouble())
                drawLine(
                    color = Color.Black,
                    start = center,
                    end = Offset(
                        x = center.x + radius * cos(angle).toFloat(),
                        y = center.y + radius * sin(angle).toFloat()
                    ),
                    strokeWidth = 6.dp.toPx()
                )
            }
            // Gold border around the wheel.
            drawCircle(
                color = Gold,
                radius = radius,
                center = center,
                style = Stroke(width = 10f)
            )
            drawCircle(
                color = Gold,
                radius = radius * 0.14f,
                center = center
            )
        }
        // Blinking lights (drawn outside the rotating canvas, so they stay put).
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f + 14f
            val center = Offset(size.width / 2f, size.height / 2f)
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30).toDouble())
                val pos = Offset(
                    x = center.x + (radius * cos(angle)).toFloat(),
                    y = center.y + (radius * sin(angle)).toFloat()
                )
                val alpha = if (i % 2 == 0) blink else 1f - blink + 0.25f
                drawCircle(
                    color = Gold.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = 9f,
                    center = pos
                )
            }
        }
        // Pointer at the top.
        Canvas(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.TopCenter)
        ) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width / 2f, size.height)
                lineTo(0f, 0f)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(path = path, color = Red)
        }
    }
}

@Composable
private fun Confetti(modifier: Modifier = Modifier) {
    val pieces = remember {
        val random = Random(42)
        List(120) {
            Triple(
                random.nextFloat(),
                random.nextFloat(),
                WheelColors[random.nextInt(WheelColors.size)]
            )
        }
    }
    Canvas(modifier = modifier) {
        for ((x, y, color) in pieces) {
            drawCircle(
                color = color,
                radius = 8f,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}
