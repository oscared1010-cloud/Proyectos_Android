package com.example.a500conexionapi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeScreen(
    todayEarthquakes: List<EarthquakeFeature>,
    weekEarthquakes: List<EarthquakeFeature>,
    byRegion: Map<String, List<EarthquakeFeature>>,
    isLoading: Boolean
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hoy", "Semana", "Regiones")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("USGS Terremotos") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    0 -> EarthquakeList(todayEarthquakes)
                    1 -> EarthquakeList(weekEarthquakes)
                    2 -> RegionList(byRegion)
                }
            }
        }
    }
}

@Composable
fun EarthquakeList(earthquakes: List<EarthquakeFeature>) {
    if (earthquakes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay datos disponibles")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(earthquakes) { earthquake ->
                EarthquakeItem(earthquake)
            }
        }
    }
}

@Composable
fun RegionList(byRegion: Map<String, List<EarthquakeFeature>>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        byRegion.forEach { (region, earthquakes) ->
            item {
                Text(
                    text = region,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(earthquakes) { earthquake ->
                EarthquakeItem(earthquake)
            }
        }
    }
}

@Composable
fun EarthquakeItem(earthquake: EarthquakeFeature) {
    val properties = earthquake.properties
    val magnitudeColor = when {
        properties.mag >= 7.0 -> Color(0xFFD32F2F)
        properties.mag >= 6.0 -> Color(0xFFF57C00)
        properties.mag >= 4.0 -> Color(0xFFFBC02D)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(magnitudeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%.1f", properties.mag),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = properties.place,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(properties.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
