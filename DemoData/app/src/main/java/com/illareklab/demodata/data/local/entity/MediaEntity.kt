package com.illareklab.demodata.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val rutaArchivo: String,
    val tipo: String,
    val tamanoBytes: Long,
    val duracionMs: Long? = null,
    val anchoPx: Int? = null,
    val altoPx: Int? = null,
    val timestamp: Long
)

enum class MediaType { PHOTO, VIDEO }
