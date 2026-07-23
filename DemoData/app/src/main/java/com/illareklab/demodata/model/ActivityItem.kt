package com.illareklab.demodata.model

sealed class ActivityItem {
    abstract val id: Long
    abstract val timestamp: Long
    abstract val isRemote: Boolean

    data class GpsGoogle(
        override val id: Long,
        override val timestamp: Long,
        val latitud: Double,
        val longitud: Double,
        override val isRemote: Boolean = false,
        val proveedor: String = "Google Play Services"
    ) : ActivityItem()

    data class GpsSensors(
        override val id: Long,
        override val timestamp: Long,
        val latitud: Double,
        val longitud: Double,
        override val isRemote: Boolean = false,
        val proveedor: String = "Internal Sensors"
    ) : ActivityItem()

    data class Photo(
        override val id: Long,
        override val timestamp: Long,
        val rutaArchivo: String,
        override val isRemote: Boolean = false
    ) : ActivityItem()

    data class Video(
        override val id: Long,
        override val timestamp: Long,
        val rutaArchivo: String,
        val duracionMs: Long,
        override val isRemote: Boolean = false
    ) : ActivityItem()

    data class Audio(
        override val id: Long,
        override val timestamp: Long,
        val rutaArchivo: String,
        val duracionMs: Long,
        val formato: String,
        override val isRemote: Boolean = false
    ) : ActivityItem()
}
