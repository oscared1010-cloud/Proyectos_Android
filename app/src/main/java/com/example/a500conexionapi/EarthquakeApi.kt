package com.example.a500conexionapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthquakeApiService {
    @GET("query")
    suspend fun getEarthquakes(
        @Query("format") format: String = "geojson",
        @Query("starttime") startTime: String? = null,
        @Query("endtime") endTime: String? = null,
        @Query("minmagnitude") minMagnitude: Double? = null,
        @Query("orderby") orderBy: String = "time",
        @Query("limit") limit: Int = 100
    ): EarthquakeResponse
}

object EarthquakeRetrofit {
    // Usando la URL base proporcionada por el usuario
    private const val BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/"

    val api: EarthquakeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EarthquakeApiService::class.java)
    }
}
