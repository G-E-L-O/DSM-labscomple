package com.illareklab.demodata

import android.app.Application
import com.illareklab.demodata.data.local.AppDatabase
import com.illareklab.demodata.data.repository.GpsRepository
import com.illareklab.demodata.data.session.SessionManager

class DemoDataApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var gpsRepository: GpsRepository
        private set

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.obtenerBaseDatos(this)
        gpsRepository = GpsRepository(database.gpsGoogleDao(), database.gpsSensorsDao())
        sessionManager = SessionManager(this)
    }
}
