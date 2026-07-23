package com.illareklab.demodata.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.illareklab.demodata.data.local.entity.GpsSensorsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsSensorsDao {
    @Insert
    suspend fun insert(ubicacion: GpsSensorsEntity)

    @Query("SELECT * FROM gps_sensors_updates ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<GpsSensorsEntity>>

    @Query("SELECT COUNT(*) FROM gps_sensors_updates")
    fun observeCount(): Flow<Int>

    @Query("DELETE FROM gps_sensors_updates")
    suspend fun deleteAll()
}
