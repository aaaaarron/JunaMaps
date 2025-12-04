package cl.duoc.basico.repository

import cl.duoc.basico.model.CurrentWeather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
        
class WeatherRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherService::class.java)

    suspend fun getCurrentWeather(lat: Double, lng: Double): CurrentWeather? {
        return try {
            val response = service.getWeather(lat, lng)
            if (response.isSuccessful) {
                response.body()?.currentWeather
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
