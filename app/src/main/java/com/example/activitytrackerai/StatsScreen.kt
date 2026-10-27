package com.example.activitytrackerai

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.accompanist.permissions.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.max
import kotlin.math.min



enum class StatsTab(val label: String) { Day("D"), Week("W"), Month("M"), Year("Y") }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val activityRecognitionPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Manifest.permission.ACTIVITY_RECOGNITION
    } else null
    val permissionState = activityRecognitionPermission?.let { rememberPermissionState(it) }
    if (permissionState != null) {
        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) permissionState.launchPermissionRequest()
        }
    }
    if (permissionState != null && !permissionState.status.isGranted) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Η εφαρμογή χρειάζεται άδεια για να μετρήσει βήματα.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Δώσε άδεια")
            }
        }
        return
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var allSteps by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    val allStepsPerHour = remember { mutableStateOf<Map<Int, Float>>(emptyMap()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            FirebaseUtils.getAllSteps(userId) { map ->
                allSteps = map
                isLoading = false
            }
        }
    }

    var selectedTab by remember { mutableStateOf(StatsTab.Day) }

    val displayData = remember(selectedTab, allSteps) {
        when (selectedTab) {
            StatsTab.Day -> {
                val hours = (0..23).toList()
                val hourLabels = hours.map { it.toString().padStart(2, '0') }
                val hourSteps = hours.map { allStepsPerHour.value[it] ?: 0f }
                val value = hourSteps.sum().toInt().toString()
                val yLabels = listOf("0", "2.000", "4.000", "6.000")
                ChartUiConfig(value, hourSteps, hourLabels, yLabels)
            }
            StatsTab.Week -> {
                // ΣΩΣΤΟ mapping για Δευτέρα-Κυριακή της τρέχουσας εβδομάδας
                val today = LocalDate.now()
                val startOfWeek = today.with(DayOfWeek.MONDAY)
                val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }
                val barLabels = weekDates.map { it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) }
                val barData = weekDates.map { date ->
                    val key = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    allSteps[key]?.toFloat() ?: 0f
                }
                val avg = if (barData.count { it > 0 } > 0) barData.sum() / barData.count { it > 0 } else 0f
                val value = avg.toInt().toString()
                val yLabels = listOf("0", "2.000", "4.000", "6.000")
                ChartUiConfig(value, barData, barLabels, yLabels)
            }
            StatsTab.Month -> {
                val now = LocalDate.now()
                val selectedMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"))
                val daysInMonth = now.lengthOfMonth()
                val barLabels = (1..daysInMonth).map { it.toString() }
                val barData = (1..daysInMonth).map { day ->
                    val key = selectedMonth + day.toString().padStart(2, '0')
                    allSteps[key]?.toFloat() ?: 0f
                }
                val avg = if (barData.count { it > 0 } > 0) barData.sum() / barData.count { it > 0 } else 0f
                val value = avg.toInt().toString()
                val yLabels = listOf("0", "5.000", "10.000", "15.000")
                ChartUiConfig(value, barData, barLabels, yLabels)
            }
            StatsTab.Year -> {
                val now = LocalDate.now()
                val year = now.year
                val months = (1..12).map { LocalDate.of(year, it, 1) }
                val barLabels = months.map { it.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).take(1) }
                val barData = months.map { m ->
                    val ym = m.format(DateTimeFormatter.ofPattern("yyyyMM"))
                    allSteps.filterKeys { it.startsWith(ym) }.values.sum().toFloat()
                }
                val avg = if (barData.count { it > 0 } > 0) barData.sum() / barData.count { it > 0 } else 0f
                val value = avg.toInt().toString()
                val yLabels = listOf("0", "2.000", "4.000", "6.000")
                ChartUiConfig(value, barData, barLabels, yLabels)
            }
        }
    }

    val rangeLabel = when (selectedTab) {
        StatsTab.Day -> "Today"
        StatsTab.Week -> {
            val today = LocalDate.now()
            val start = today.with(DayOfWeek.MONDAY)
            val end = start.plusDays(6)
            "${start.dayOfMonth}–${end.dayOfMonth} ${end.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} ${end.year}"
        }
        StatsTab.Month -> {
            val now = LocalDate.now()
            val firstOfMonth = now.withDayOfMonth(1)
            "${firstOfMonth.dayOfMonth} ${firstOfMonth.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)}–${now.dayOfMonth} ${now.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} ${now.year}"
        }
        StatsTab.Year -> {
            val now = LocalDate.now()
            val year = now.year
            "Jan $year–Dec $year"
        }
    }

    HealthStatsScreen(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        value = displayData.value,
        valueLabel = "steps",
        rangeLabel = rangeLabel,
        chartConfig = displayData,
        tab = selectedTab
    )
}

data class ChartUiConfig(
    val value: String,
    val barData: List<Float>,
    val barLabels: List<String>,
    val yLabels: List<String>
)

@Composable
fun HealthStatsScreen(
    selectedTab: StatsTab,
    onTabSelected: (StatsTab) -> Unit,
    value: String,
    valueLabel: String,
    rangeLabel: String,
    chartConfig: ChartUiConfig,
    tab: StatsTab
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsTab.values().forEach { t ->
                val selected = t == selectedTab
                Button(
                    onClick = { onTabSelected(t) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) Color(0xFFF3F3F3) else Color.Transparent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(t.label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            if (selectedTab == StatsTab.Day) "TOTAL" else "AVERAGE",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF818181),
            modifier = Modifier.padding(start = 24.dp)
        )
        Spacer(Modifier.height(4.dp))

        Row(Modifier.padding(start = 24.dp), verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(Modifier.width(6.dp))
            Text(
                valueLabel,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF818181),
                modifier = Modifier.padding(bottom = 7.dp)
            )
        }
        Spacer(Modifier.height(4.dp))

        Text(
            rangeLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF818181),
            modifier = Modifier.padding(start = 24.dp)
        )
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 8.dp)
        ) {
            ResponsiveBarChart(
                chartConfig.barData, chartConfig.barLabels, chartConfig.yLabels, Color(0xFFE91E63),
                alternateBarColors = false,
                scrollable = tab == StatsTab.Day || tab == StatsTab.Month,
                barSpacing = if (tab == StatsTab.Day) 16.dp else if (tab == StatsTab.Month) 10.dp else 0.dp
            )
        }
    }
}

@Composable
fun ResponsiveBarChart(
    barData: List<Float>,
    barLabels: List<String>,
    yLabels: List<String>,
    barColor: Color,
    tooltipLabels: List<String>? = null,
    alternateBarColors: Boolean = false,
    scrollable: Boolean = false,
    barSpacing: Dp = 0.dp
) {
    val chartLeftPadding = 40.dp
    val maxValue = (yLabels.last().replace(".", "").replace(",", "").toFloatOrNull()
        ?: barData.maxOrNull() ?: 1f)
    val barWidth = 32.dp
    val chartHeight = 140.dp

    var selectedBar by remember { mutableStateOf<Int?>(null) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    Box(Modifier.fillMaxSize()) {
        // Y axis & grid
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            yLabels.reversed().forEach { yLabel ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        yLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF818181),
                        modifier = Modifier.width(chartLeftPadding)
                    )
                    Box(
                        Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(Color(0xFFE0E0E0))
                    )
                }
            }
        }

        // Bottom: bars + tooltip
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = chartLeftPadding, end = 8.dp, bottom = 4.dp)
        ) {
            // Bars + labels
            Row(
                modifier = if (scrollable)
                    Modifier
                        .horizontalScroll(scrollState)
                        .padding(bottom = 8.dp)
                else
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = if (scrollable)
                    Arrangement.spacedBy(barSpacing)
                else
                    Arrangement.SpaceEvenly
            ) {
                barData.zip(barLabels).forEachIndexed { i, (value, label) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .height(chartHeight + 24.dp)
                            .width(barWidth)
                            .clickable(enabled = value > 0f) {
                                selectedBar = if (selectedBar == i) null else i
                            }
                    ) {
                        Box(
                            Modifier
                                .height(chartHeight)
                                .width(barWidth),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                Modifier
                                    .width(barWidth)
                                    .height((chartHeight * (value / maxValue).coerceIn(0f, 1f)))
                                    .background(
                                        if (alternateBarColors && i % 2 == 1)
                                            Color(0xFFFFB17A)
                                        else
                                            barColor,
                                        RoundedCornerShape(6.dp)
                                    )
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF818181),
                            modifier = Modifier.width(barWidth),
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // --- Tooltip κάτω από το σωστό bar, με clamp στα άκρα ---
            if (selectedBar != null && selectedBar!! in barData.indices) {
                val offsetDp = with(density) {
                    if (scrollable) {
                        val pxPerItem = (barWidth + barSpacing).toPx()
                        val scrollPx = scrollState.value.toFloat()
                        val leftOffsetPx = selectedBar!! * pxPerItem - scrollPx + barWidth.toPx() / 2
                        leftOffsetPx / density.density
                    } else {
                        ((barWidth + barSpacing) * selectedBar!! + barWidth / 2).toPx() / density.density
                    }
                }.dp

                val tooltipMinWidth = 64.dp
                val tooltipHorizontalPadding = 16.dp
                val tooltipTotalWidth = tooltipMinWidth + tooltipHorizontalPadding * 2

                val chartWidthDp = with(density) {
                    (barWidth + barSpacing) * barData.size - barSpacing
                }

                val clampMin = tooltipTotalWidth / 2
                val clampMax = chartWidthDp - tooltipTotalWidth / 2

                val needsClamp = scrollable || barData.size > 10 // ή βάλε ό,τι όριο θες

                val clampedOffsetDp = if (needsClamp)
                    max(clampMin.value, min(clampMax.value, (offsetDp - chartLeftPadding).value)).dp
                else
                    (offsetDp - chartLeftPadding)

                Box(
                    Modifier
                        .offset(x = clampedOffsetDp, y = 36.dp)
                        .zIndex(10f)
                        .align(Alignment.BottomStart)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x22000000), RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = tooltipHorizontalPadding)
                            .widthIn(min = tooltipMinWidth)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            barData[selectedBar!!].toInt().toString(),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center
                        )
                        if (tooltipLabels != null) {
                            Text(
                                tooltipLabels[selectedBar!!],
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}