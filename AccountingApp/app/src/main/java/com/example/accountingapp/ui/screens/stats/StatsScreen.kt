package com.example.accountingapp.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accountingapp.ui.theme.BrownDark
import com.example.accountingapp.ui.theme.BrownLight
import com.example.accountingapp.ui.theme.BrownMedium
import com.example.accountingapp.ui.theme.ChartColors
import com.example.accountingapp.ui.theme.Cream
import com.example.accountingapp.ui.theme.Mint
import com.example.accountingapp.ui.theme.MintDark
import com.example.accountingapp.ui.theme.Pink
import com.example.accountingapp.ui.theme.PinkDark
import com.example.accountingapp.ui.theme.White
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            MonthSelector(
                year = state.year,
                month = state.month,
                onPrev = { viewModel.previousMonth() },
                onNext = { viewModel.nextMonth() }
            )
        }

        // Income/Expense summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💰", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("收入", fontSize = 13.sp, color = BrownLight)
                        Text(
                            text = "¥${String.format("%.0f", state.totalIncome)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintDark
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💸", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("支出", fontSize = 13.sp, color = BrownLight)
                        Text(
                            text = "¥${String.format("%.0f", state.totalExpense)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PinkDark
                        )
                    }
                }
            }
        }

        // Expense breakdown chart
        if (state.expenseCategories.isNotEmpty()) {
            item {
                SectionTitle("支出分类")
            }
            item {
                DonutChartCard(state.expenseCategories)
            }
            items(state.expenseCategories) { stat ->
                CategoryBar(stat = stat)
            }
        }

        // Income breakdown
        if (state.incomeCategories.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionTitle("收入分类")
            }
            items(state.incomeCategories) { stat ->
                CategoryBar(stat = stat)
            }
        }

        if (state.expenseCategories.isEmpty() && state.incomeCategories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("本月暂无数据", color = BrownLight)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun MonthSelector(
    year: Int,
    month: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "上个月",
                tint = BrownMedium
            )
        }
        Text(
            text = "${year}年${month}月",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.width(140.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onNext) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "下个月",
                tint = BrownMedium
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = BrownDark
    )
}

@Composable
private fun DonutChartCard(stats: List<CategoryStat>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            DonutChart(
                stats = stats.take(8),
                modifier = Modifier.size(180.dp)
            )
        }
    }
}

@Composable
private fun DonutChart(
    stats: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    val total = stats.sumOf { it.total.toDouble() }.toFloat()
    if (total <= 0) return

    Canvas(modifier = modifier) {
        val strokeWidth = 32f
        val halfStroke = strokeWidth / 2
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = Offset(halfStroke, halfStroke)
        val arcSize = Size(radius * 2, radius * 2)

        var startAngle = -90f

        stats.forEachIndexed { index, stat ->
            val sweep = (stat.total / total) * 360f
            val color = ChartColors[index % ChartColors.size]

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                topLeft = topLeft,
                size = arcSize,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun CategoryBar(stat: CategoryStat) {
    val color = ChartColors[
        (kotlin.math.abs(stat.category.id.toInt()) % ChartColors.size)
    ]

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stat.category.emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stat.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrownDark
                )
            }
            Text(
                text = "¥${String.format("%.2f", stat.total)}",
                style = MaterialTheme.typography.bodyMedium,
                color = BrownMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .then(
                    Modifier.fillMaxWidth()
                )
        ) {
            // Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .then(
                        Modifier.fillMaxWidth()
                    )
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawRoundRect(
                        color = Cream,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
                    )
                }
            }
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(stat.percentage.coerceIn(0f, 1f))
                    .height(10.dp)
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawRoundRect(
                        color = color,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${String.format(Locale.US, "%.1f", stat.percentage * 100)}%",
            style = MaterialTheme.typography.labelSmall,
            color = BrownLight
        )
    }
}
