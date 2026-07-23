package com.illareklab.demodata.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.illareklab.demodata.data.remote.NetworkConstants
import com.illareklab.demodata.data.remote.RetrofitClient
import com.illareklab.demodata.data.remote.model.GpsSyncRequest
import com.illareklab.demodata.data.repository.AudioRepository
import com.illareklab.demodata.data.repository.GpsRepository
import com.illareklab.demodata.data.repository.MediaRepository
import com.illareklab.demodata.data.session.SessionManager
import com.illareklab.demodata.workers.DelayedNotificationWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

data class SyncCounts(
    val gpsGoogle: Int = 0,
    val gpsSensors: Int = 0,
    val photos: Int = 0,
    val videos: Int = 0,
    val audios: Int = 0,
    val total: Int = 0
)

class SyncViewModel(
    context: Context,
    private val gpsRepository: GpsRepository,
    private val mediaRepository: MediaRepository,
    private val audioRepository: AudioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    private val _lastWorkId = MutableStateFlow<UUID?>(null)
    val lastWorkId = _lastWorkId.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    val syncCounts: StateFlow<SyncCounts> = combine(
        gpsRepository.googleCount,
        gpsRepository.sensorsCount,
        mediaRepository.photoCount,
        mediaRepository.videoCount,
        audioRepository.count
    ) { google, sensors, photos, videos, audios ->
        SyncCounts(
            gpsGoogle = google,
            gpsSensors = sensors,
            photos = photos,
            videos = videos,
            audios = audios,
            total = google + sensors + photos + videos + audios
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SyncCounts()
    )

    fun forceSync(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            try {

                val slug = sessionManager.projectSlug.firstOrNull() ?: NetworkConstants.PROJECT_SLUG

                val googlePoints = gpsRepository.googlePoints.firstOrNull() ?: emptyList()
                if (googlePoints.isNotEmpty()) {
                    val gpsRequests = googlePoints.map {
                        GpsSyncRequest(
                            latitud = it.latitud,
                            longitud = it.longitud,
                            altitud = it.altitud,
                            precision = it.precision,
                            provider = "google_flp",
                            timestamp = it.timestamp
                        )
                    }

                    RetrofitClient.apiService.syncGps(slug, gpsRequests)
                }

                val mediaItems = mediaRepository.allMedia.firstOrNull() ?: emptyList()
                for (item in mediaItems) {
                    val file = File(item.rutaArchivo)
                    if (file.exists()) {
                        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                        val tipo = item.tipo.toRequestBody("text/plain".toMediaTypeOrNull())
                        val ts = item.timestamp.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                        RetrofitClient.apiService.uploadMedia(slug, body, tipo, ts)
                    }
                }

                val audios = audioRepository.allAudios.firstOrNull() ?: emptyList()
                for (audio in audios) {
                    val file = File(audio.rutaArchivo)
                    if (file.exists()) {
                        val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                        val tipo = "AUDIO".toRequestBody("text/plain".toMediaTypeOrNull())
                        val ts = audio.timestamp.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                        RetrofitClient.apiService.uploadMedia(slug, body, tipo, ts)
                    }
                }

                onResult(true, "Sincronización completada con éxito")
            } catch (e: Exception) {
                Log.e("SyncViewModel", "Error en sincronización", e)
                onResult(false, "Error: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun scheduleDelayedNotification(title: String, message: String) {
        val inputData = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .build()

        val notificationRequest = OneTimeWorkRequestBuilder<DelayedNotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        _lastWorkId.value = notificationRequest.id
        workManager.enqueue(notificationRequest)
    }

    fun cancelDelayedNotification() {
        _lastWorkId.value?.let { uuid ->
            workManager.cancelWorkById(uuid)
            _lastWorkId.value = null
        }
    }

    class Factory(
        private val context: Context,
        private val gpsRepository: GpsRepository,
        private val mediaRepository: MediaRepository,
        private val audioRepository: AudioRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SyncViewModel::class.java)) {
                return SyncViewModel(context, gpsRepository, mediaRepository, audioRepository, sessionManager) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
        }
    }
}
