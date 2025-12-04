package cl.duoc.basico.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.basico.model.CurrentWeather
import cl.duoc.basico.repository.WeatherRepository
import cl.duoc.basico.repository.PlaceRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GoogleMaps() {

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-33.390756, -71.153893), 15f)
    }
    val coroutineScope = rememberCoroutineScope()

    // Weather state
    val weatherRepository = remember { WeatherRepository() }
    var currentWeather by remember { mutableStateOf<CurrentWeather?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getLastLocation(context, fusedLocationClient) { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f))
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Fetch Weather when location is available
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            try {
                currentWeather = weatherRepository.getCurrentWeather(loc.latitude, loc.longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // Usamos PlaceRepository directamente para cargar desde JSON local
        // Esto evita intentar conectar a un servidor Spring Boot apagado (causa de ANR)
        val placeRepository = remember { PlaceRepository(context) }
        var places by remember { mutableStateOf(listOf<cl.duoc.basico.model.Place>()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            try {
                // Cargamos en hilo IO para no bloquear la UI
                val loadedPlaces = withContext(Dispatchers.IO) {
                    placeRepository.loadPlacesFromAssets()
                }
                places = loadedPlaces
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error al cargar lugares: ${e.message}"
                isLoading = false
            }
        }

        // Título
        Text(
            text = "Locales JUNAEB - Curacaví",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Weather Display
        currentWeather?.let { weather ->
            Card(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val icon = when {
                        weather.weathercode <= 1 -> Icons.Default.WbSunny
                        weather.weathercode in 2..49 -> Icons.Default.Cloud
                        else -> Icons.Default.Grain // Lluvia/Nieve/Tormenta
                    }
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = "Clima",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${weather.temperature}°C",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // Mapa
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = userLocation != null),
            uiSettings = MapUiSettings(myLocationButtonEnabled = userLocation != null)
        ) {
            places.filter { it.acceptsJunaeb }.forEach { p ->
                Marker(
                    state = MarkerState(position = LatLng(p.lat, p.lng)),
                    title = p.name,
                    snippet = p.address
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val lugares = places.filter { it.acceptsJunaeb }

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text(text = "Cargando locales...")
            }
            errorMessage != null -> {
                Text(
                    text = "No se pudieron cargar los locales",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            lugares.isEmpty() -> {
                Text(
                    text = "No hay locales disponibles",
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Locales disponibles (${lugares.size}):",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(lugares) { lp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = lp.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📍 ${lp.address}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getLastLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationResult: (Location?) -> Unit
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            onLocationResult(location)
        }
        .addOnFailureListener {
            onLocationResult(null)
        }
}
