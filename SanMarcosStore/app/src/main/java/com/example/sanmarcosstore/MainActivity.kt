package com.example.sanmarcosstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sanmarcosstore.ui.theme.SanMarcosStoreTheme
import com.example.sanmarcosstore.ui.navigation.AppNavigation
import com.example.sanmarcosstore.ui.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StoreViewModel = viewModel()
            val darkMode by viewModel.darkMode.collectAsState()

            SanMarcosStoreTheme(darkTheme = darkMode, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),

                    color = MaterialTheme.colorScheme.background
                ) {

                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
