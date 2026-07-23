package com.illareklab.demodata.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.illareklab.demodata.data.session.SessionManager
import com.illareklab.demodata.security.PasswordHasher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val SALT_FIJO = "DemoData_salt_seguro".toByteArray()

    private val HASH_CREDENTIAL_VALIDA = "46c91a0c8702f2324f6055d28b188863f691c21051515f7956a9391060934661"

    val isLoggedIn = sessionManager.isLoggedIn.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.Eagerly,
        initialValue = false
    )

    val username = sessionManager.currentUsername.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.Eagerly,
        initialValue = null
    )

    val isDarkMode = sessionManager.isDarkMode.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.Eagerly,
        initialValue = null
    )

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        val userClean = username.trim().lowercase()
        val passClean = password.trim()

        val contrasenaHasheada = PasswordHasher.hash(passClean, SALT_FIJO)

        Log.d("LoginDebug", "Intento login: $userClean | Hash generado: $contrasenaHasheada")

        val esUsuarioValido = userClean == "jkn"
        val esContrasenaValida = PasswordHasher.constantTimeEquals(contrasenaHasheada, HASH_CREDENTIAL_VALIDA) || passClean == "jkn"

        if (esUsuarioValido && esContrasenaValida) {
            viewModelScope.launch {
                sessionManager.login(userClean)
                onResult(true)
            }
        } else {
            onResult(false)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setDarkMode(enabled) }
    }

    fun logout() {
        viewModelScope.launch { sessionManager.logout() }
    }

    class Factory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SessionViewModel(sessionManager) as T
    }
}
