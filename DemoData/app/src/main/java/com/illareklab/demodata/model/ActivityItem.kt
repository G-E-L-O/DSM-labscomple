package com.illareklab.demodata.model

sealed class ActivityItem {
    abstract val id: Long
    abstract val timestamp: Long

    data class GpsGoogle(
        override val id: Long,
        override val timestamp: Long,
        val latitud: Double,
        val longitud: Double,
        val proveedor: String = "Google Play Services"
    ) : ActivityItem()

    data class GpsSensors(
        override val id: Long,
        override val timestamp: Long,
        val latitud: Double,
        val longitud: Double,
        val proveedor: String = "Internal Sensors"
    ) : ActivityItem()

    data class Photo(
        override val id: Long,
        override val timestamp: Long,
        val rutaArchivo: String
    ) : ActivityItem()

    data class Video(
        override val id: Long,
        override val timestamp: Long,
        val rutaArchivo: String,
        val duracionMs: Long
    ) : ActivityItem()

    data class Audio(
        override val id: Long,
        override val timestamp: Long,
        val rutaArchivo: String,
        val duracionMs: Long,
        val formato: String
    ) : ActivityItem()
}
