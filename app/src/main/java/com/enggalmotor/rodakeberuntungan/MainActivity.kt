package com.enggalmotor.rodakeberuntungan

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private const val KEY_NAMES = "names"
private val DEFAULT_NAMES = listOf("Budi", "Andi", "Sari", "Joko", "Dewi", "Rina")

private fun loadNames(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val stored = prefs.getString(KEY_NAMES, null) ?: return DEFAULT_NAMES
    return stored.split("\n").filter { it.isNotBlank() }
}

private fun saveNames(context: Context, names: List<String>) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_NAMES, names.joinToString("\n"))
        .apply()
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
    var names by remember { mutableStateOf(loadNames(context)) }
    var isSpinning by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    var showWinner by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Persist every change locally (SharedPreferences) — fully offline.
    LaunchedEffect(names) { saveNames(context, names) }

    fun spin() {
        if (isSpinning) return
        if (names.size < 2) {
            message = context.getString(R.string.need_two_names)
            return
        }
        message = ""
        val winnerIndex = Random.nextInt(names.size)
        val segmentAngle = 360f / names.size
        val centerAngle = winnerIndex * segmentAngle + segmentAngle / 2f
        val currentMod = ((rotation.value % 360f) + 360f) % 360f
        // Pointer is at the top (270 degrees in canvas coordinates).
        val delta = ((270f - centerAngle - currentMod) % 360f + 360f) % 360f
        val target = rotation.value + 1800f + delta
        scope.launch {
            isSpinning = true
            rotation.animateTo(
                targetValue = target,
                animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
            )
            isSpinning = false
            winner = names[winnerIndex]
            showWinner = true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        // Small FAB, 56dp, bottom-right of the SCREEN, separate from PUTAR.
        // Scaffold places it at FabPosition.End; content padding below keeps
        // the PUTAR button clear of it.
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                modifier = Modifier.size(56.dp),
                containerColor = Navy,
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
                        colors = listOf(NavyDark, Navy, Color(0xFF123A6B))
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
                    color = Gold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                Wheel(
                    names = names,
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
                                    colors = listOf(Orange, OrangeLight, Orange)
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
                        color = Gold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.names_count, names.size),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }

    // --- Manage names: small bottom sheet only, never a full page ---
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Navy
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
                    text = stringResource(id = R.string.names_count, names.size),
                    color = Gold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = { Text(text = stringResource(id = R.string.add_name_hint)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val trimmed = newName.trim()
                        if (trimmed.isNotEmpty()) {
                            names = names + trimmed
                            newName = ""
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
                    itemsIndexed(names) { index, name ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                names = names.toMutableList().also { it.removeAt(index) }
                            }) {
                                Text(text = "Hapus", color = Red)
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = { names = emptyList() },
                    enabled = names.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.clear_all))
                }
            }
        }
    }

    // --- Winner popup with confetti ---
    if (showWinner && winner != null) {
        Dialog(onDismissRequest = { showWinner = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Navy)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Confetti(modifier = Modifier.fillMaxSize())
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.winner_title),
                        color = Gold,
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
                        textAlign = TextAlign.Center
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
        List(60) {
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
