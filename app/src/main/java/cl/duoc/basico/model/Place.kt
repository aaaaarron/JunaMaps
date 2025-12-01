package cl.duoc.basico.model

/**
 * Modelo de datos para un local comercial que puede aceptar el beneficio JUNAEB.
 * 
 * @property id Identificador único del lugar
 * @property name Nombre del establecimiento
 * @property lat Latitud de la ubicación
 * @property lng Longitud de la ubicación
 * @property address Dirección completa del local
 * @property acceptsJunaeb Indica si el local acepta el beneficio JUNAEB
 */
data class Place(
    val id: Int,
    val name: String,
    val lat: Double,
    val lng: Double,
    val address: String,
    val acceptsJunaeb: Boolean
)
