package com.example.study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.data.*
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.view.SpatialAnalyticsViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpatialAnalyticsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpatialAnalyticsViewModel = viewModel()
) {
    val locations by viewModel.allLocations.collectAsState(initial = emptyList())
    val comparison by viewModel.locationComparison.collectAsState(initial = null)
    var selectedLocation by remember { mutableStateOf<FavoriteLocation?>(null) }
    val locationReport by viewModel.locationReport.collectAsState(initial = null)
    
    LaunchedEffect(Unit) {
        viewModel.loadLocationComparison()
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analytics Espaciais",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAnalytics() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumo Geral
            comparison?.let { comp ->
                item {
                    OverallSummaryCard(comparison = comp)
                }
            }
            
            // Seleção de Localização
            item {
                LocationSelector(
                    locations = locations,
                    selectedLocation = selectedLocation,
                    onLocationSelected = { location ->
                        selectedLocation = location
                        viewModel.loadLocationReport(location.id)
                    }
                )
            }
            
            // Relatório da Localização Selecionada
            locationReport?.let { report ->
                item {
                    LocationPerformanceCard(report = report)
                }
                item {
                    ProductivityInsightsCard(insights = report.insights)
                }
            }
            
            // Lista de Todas as Localizações
            item {
                AllLocationsAnalyticsCard(
                    locations = locations,
                    onLocationClick = { location ->
                        selectedLocation = location
                        viewModel.loadLocationReport(location.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun OverallSummaryCard(
    comparison: LocationComparison,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Resumo Geral",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Locais Ativos",
                    value = comparison.locations.size.toString(),
                    icon = Icons.Default.LocationOn
                )
                
                comparison.bestPerformingLocation?.let { bestLocation ->
                    StatItem(
                        label = "Melhor Local",
                        value = bestLocation.locationName,
                        icon = Icons.Default.Star
                    )
                }
            }
            
            if (comparison.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recomendação Principal:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = comparison.recommendations.first().message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LocationSelector(
    locations: List<FavoriteLocation>,
    selectedLocation: FavoriteLocation?,
    onLocationSelected: (FavoriteLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Selecionar Localização",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                locations.forEach { location ->
                    LocationSelectorItem(
                        location = location,
                        isSelected = selectedLocation?.id == location.id,
                        onClick = { onLocationSelected(location) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationSelectorItem(
    location: FavoriteLocation,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${location.studySessionCount} sessões • ${String.format("%.1f", location.averagePerformance)}% performance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LocationPerformanceCard(
    report: LocationPerformanceReport,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Performance - ${report.locationName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PerformanceMetric(
                    label = "Sessões",
                    value = report.totalSessions.toString(),
                    icon = Icons.Default.Event
                )
                
                PerformanceMetric(
                    label = "Performance",
                    value = "${String.format("%.1f", report.averagePerformance)}%",
                    icon = Icons.Default.TrendingUp
                )
                
                PerformanceMetric(
                    label = "Duração Média",
                    value = "${String.format("%.0f", report.averageDuration)}min",
                    icon = Icons.Default.Schedule
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tendência de Performance
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (report.performanceTrend) {
                        PerformanceTrend.IMPROVING -> Icons.Default.TrendingUp
                        PerformanceTrend.DECLINING -> Icons.Default.TrendingDown
                        PerformanceTrend.STABLE -> Icons.Default.TrendingFlat
                        PerformanceTrend.INSUFFICIENT_DATA -> Icons.Default.Help
                    },
                    contentDescription = null,
                    tint = when (report.performanceTrend) {
                        PerformanceTrend.IMPROVING -> SuccessColor
                        PerformanceTrend.DECLINING -> MaterialTheme.colorScheme.error
                        PerformanceTrend.STABLE -> MaterialTheme.colorScheme.primary
                        PerformanceTrend.INSUFFICIENT_DATA -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (report.performanceTrend) {
                        PerformanceTrend.IMPROVING -> "Performance melhorando"
                        PerformanceTrend.DECLINING -> "Performance declinando"
                        PerformanceTrend.STABLE -> "Performance estável"
                        PerformanceTrend.INSUFFICIENT_DATA -> "Dados insuficientes"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PerformanceMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProductivityInsightsCard(
    insights: List<ProductivityInsight>,
    modifier: Modifier = Modifier
) {
    if (insights.isEmpty()) return
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Insights de Produtividade",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            insights.forEach { insight ->
                InsightItem(insight = insight)
                if (insight != insights.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun InsightItem(
    insight: ProductivityInsight,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (insight.insightType) {
                        InsightType.BEST_TIME_TO_STUDY -> Icons.Default.Schedule
                        InsightType.MOST_PRODUCTIVE_LOCATION -> Icons.Default.LocationOn
                        InsightType.OPTIMAL_DURATION -> Icons.Default.Timer
                        InsightType.BEST_CARD_TYPES -> Icons.Default.Quiz
                        InsightType.PERFORMANCE_TREND -> Icons.Default.TrendingUp
                        InsightType.CONSISTENCY_SCORE -> Icons.Default.Assessment
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = insight.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = insight.recommendation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Confiança: ${String.format("%.0f", insight.confidence * 100)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AllLocationsAnalyticsCard(
    locations: List<FavoriteLocation>,
    onLocationClick: (FavoriteLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Todas as Localizações",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                locations.forEach { location ->
                    LocationAnalyticsItem(
                        location = location,
                        onClick = { onLocationClick(location) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationAnalyticsItem(
    location: FavoriteLocation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${location.studySessionCount} sessões",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.1f", location.averagePerformance)}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        location.averagePerformance >= 80 -> SuccessColor
                        location.averagePerformance >= 60 -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
                Text(
                    text = "Performance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
