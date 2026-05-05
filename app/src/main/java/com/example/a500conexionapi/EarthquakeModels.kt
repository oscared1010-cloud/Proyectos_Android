package com.example.a500conexionapi

data class EarthquakeResponse(
    val features: List<EarthquakeFeature>
)

data class EarthquakeFeature(
    val id: String,
    val properties: EarthquakeProperties
)

data class EarthquakeProperties(
    val mag: Double,
    val place: String,
    val time: Long,
    val url: String,
    val title: String
)
