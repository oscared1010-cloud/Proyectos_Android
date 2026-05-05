package com.example.a500conexionapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a500conexionapi.ui.theme._500ConexionApiTheme
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _500ConexionApiTheme {
                val viewModel: EarthquakeViewModel = viewModel()
                EarthquakeScreen(
                    todayEarthquakes = viewModel.todayEarthquakes,
                    weekEarthquakes = viewModel.weekEarthquakes,
                    byRegion = viewModel.byRegion,
                    isLoading = viewModel.isLoading
                )
            }
        }
    }
}

class EarthquakeViewModel : ViewModel() {
    var todayEarthquakes by mutableStateOf<List<EarthquakeFeature>>(emptyList())
    var weekEarthquakes by mutableStateOf<List<EarthquakeFeature>>(emptyList())
    var byRegion by mutableStateOf<Map<String, List<EarthquakeFeature>>>(emptyMap())
    
    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchAllData()
    }

    private fun fetchAllData() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Obtenemos los datos usando el método query con un límite de 100
                val response = EarthquakeRetrofit.api.getEarthquakes(limit = 100)
                val allFeatures = response.features

                val now = System.currentTimeMillis()
                val oneDayMillis = 24 * 60 * 60 * 1000L

                // 1. Filtrar Hoy
                todayEarthquakes = allFeatures.filter { 
                    (now - it.properties.time) < oneDayMillis 
                }.take(20)

                // 2. Filtrar Semana
                weekEarthquakes = allFeatures.take(50)

                // 3. Agrupar por Región (Lógica simplificada por nombre de lugar)
                byRegion = allFeatures.groupBy { feature ->
                    val place = feature.properties.place.lowercase()
                    when {
                        place.contains("california") || place.contains("alaska") || place.contains("mexico") || place.contains("chile") -> "América"
                        place.contains("japan") || place.contains("indonesia") || place.contains("philippines") || place.contains("china") -> "Asia"
                        place.contains("italy") || place.contains("greece") || place.contains("turkey") || place.contains("iceland") -> "Europa"
                        place.contains("fiji") || place.contains("new zealand") || place.contains("tonga") -> "Oceanía"
                        else -> "Otros"
                    }
                }.filter { it.key != "Otros" } // Solo mostramos regiones principales identificadas

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
