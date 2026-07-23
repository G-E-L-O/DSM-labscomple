package com.illareklab.demodata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.illareklab.demodata.ui.navigation.AppNavigation
import com.illareklab.demodata.ui.theme.DemoDataTheme
import com.illareklab.demodata.ui.viewmodel.GpsViewModel
import com.illareklab.demodata.ui.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as DemoDataApp

        val gpsViewModel     = GpsViewModel(app.gpsRepository)
        val sessionViewModel = SessionViewModel(app.sessionManager)

        setContent {
            val isDarkModePref by sessionViewModel.isDarkMode.collectAsStateWithLifecycle()
            val usarModoOscuro = isDarkModePref ?: isSystemInDarkTheme()

            DemoDataTheme(darkTheme = usarModoOscuro, dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation(
                        gpsViewModel     = gpsViewModel,
                        sessionViewModel = sessionViewModel
                    )
                }
            }
        }
    }
}
