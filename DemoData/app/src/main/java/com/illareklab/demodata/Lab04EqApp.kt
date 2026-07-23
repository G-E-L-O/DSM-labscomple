package com.illareklab.demodata

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import com.illareklab.demodata.data.local.AppDatabase
import com.illareklab.demodata.data.local.FileStorageManager
import com.illareklab.demodata.data.repository.AudioRepository
import com.illareklab.demodata.data.repository.GpsRepository
import com.illareklab.demodata.data.repository.MediaRepository
import com.illareklab.demodata.data.session.SessionManager

class DemoDataApp : Application(), ImageLoaderFactory {

    lateinit var database: AppDatabase
        private set

    lateinit var gpsRepository: GpsRepository
        private set

    lateinit var sessionManager: SessionManager
        private set

    lateinit var fileStorage: FileStorageManager
        private set

    lateinit var mediaRepository: MediaRepository
        private set

    lateinit var audioRepository: AudioRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.obtenerBaseDatos(this)
        gpsRepository = GpsRepository(database.gpsGoogleDao(), database.gpsSensorsDao())
        sessionManager = SessionManager(this)

        fileStorage = FileStorageManager(this)
        mediaRepository = MediaRepository(database.mediaDao(), fileStorage)
        audioRepository = AudioRepository(database.audioDao(), fileStorage)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }
}
