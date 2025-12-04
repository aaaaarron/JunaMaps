package cl.duoc.basico.repository

import android.content.Context
import cl.duoc.basico.model.Place
import org.json.JSONArray

class PlaceRepository(private val context: Context) {

    fun loadPlacesFromAssets(fileName: String = "places.json"): List<Place> {
        return try {
            val json = readJson(fileName)
            parsePlaces(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun readJson(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    fun parsePlaces(json: String): List<Place> {
        val arr = JSONArray(json)
        val list = mutableListOf<Place>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                Place(
                    id = obj.optInt("id", i),
                    name = obj.optString("name", ""),
                    lat = obj.optDouble("lat", 0.0),
                    lng = obj.optDouble("lng", 0.0),
                    address = obj.optString("address", ""),
                    acceptsJunaeb = obj.optBoolean("acceptsJunaeb", false)
                )
            )
        }
        return list
    }
}
