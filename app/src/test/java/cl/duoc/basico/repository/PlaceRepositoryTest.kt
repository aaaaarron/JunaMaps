package cl.duoc.basico.repository

import android.content.Context
import android.content.res.AssetManager
import cl.duoc.basico.model.Place
import io.mockk.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class PlaceRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: PlaceRepository
    private lateinit var assetManager: AssetManager

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        assetManager = mockk(relaxed = true)
        every { context.assets } returns assetManager
        repository = PlaceRepository(context)
    }

    @After
    fun tearDown() = clearAllMocks()

    @Test
    fun `loadPlacesFromAssets debe cargar y parsear JSON correctamente`() {
        val jsonData = """[{"id":1,"name":"Test Place","lat":-33.39,"lng":-71.15,"address":"Test","acceptsJunaeb":true}]"""
        val inputStream = ByteArrayInputStream(jsonData.toByteArray())

        every { assetManager.open(any()) } returns inputStream

        mockkConstructor(JSONArray::class)
        val mockArray = mockk<JSONArray>()
        every { anyConstructed<JSONArray>().length() } returns 1

        val mockObject = mockk<JSONObject>()
        every { anyConstructed<JSONArray>().getJSONObject(0) } returns mockObject

        every { mockObject.optInt("id", any()) } returns 1
        every { mockObject.optString("name", any()) } returns "Test Place"
        every { mockObject.optDouble("lat", any()) } returns -33.39
        every { mockObject.optDouble("lng", any()) } returns -71.15
        every { mockObject.optString("address", any()) } returns "Test"
        every { mockObject.optBoolean("acceptsJunaeb", any()) } returns true

        val places = repository.loadPlacesFromAssets()

        assertEquals(1, places.size)
        assertEquals("Test Place", places[0].name)
        assertEquals(-33.39, places[0].lat, 0.0001)
        assertTrue(places[0].acceptsJunaeb)
    }

    @Test
    fun `loadPlacesFromAssets con JSON vacio debe retornar lista vacia`() {
        val jsonData = "[]"
        val inputStream = ByteArrayInputStream(jsonData.toByteArray())

        every { assetManager.open(any()) } returns inputStream

        mockkConstructor(JSONArray::class)
        every { anyConstructed<JSONArray>().length() } returns 0

        val places = repository.loadPlacesFromAssets()

        assertTrue(places.isEmpty())
    }

    @Test
    fun `loadPlacesFromAssets debe manejar errores correctamente`() {
        every { assetManager.open(any()) } throws Exception("File not found")
        val places = repository.loadPlacesFromAssets()
        assertTrue(places.isEmpty())
    }
}
