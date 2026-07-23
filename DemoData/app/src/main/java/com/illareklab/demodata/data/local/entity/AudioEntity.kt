package com.illareklab.demodata.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio")
data class AudioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val rutaArchivo: String,
    val duracionMs: Long,
    val tamanoBytes: Long,
    val formato: String,
    val timestamp: Long
)
