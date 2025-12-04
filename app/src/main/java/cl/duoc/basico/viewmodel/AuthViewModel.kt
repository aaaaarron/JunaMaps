package cl.duoc.basico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.basico.model.Usuario
import cl.duoc.basico.model.UsuarioDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _errorMessage.value = "Todos los campos son obligatorios"
                return@launch
            }

            val usuario = usuarioDao.login(email, password)
            if (usuario != null) {
                _currentUser.value = usuario
                _isLoggedIn.value = true
                _errorMessage.value = null
                onSuccess()
            } else {
                _errorMessage.value = "Email o contraseña incorrectos"
            }
        }
    }

    fun register(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        confirmPassword: String,
        telefono: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (nombre.isBlank() || apellido.isBlank() || email.isBlank() || password.isBlank()) {
                _errorMessage.value = "Todos los campos son obligatorios"
                return@launch
            }

            if (!isValidEmail(email)) {
                _errorMessage.value = "Email inválido"
                return@launch
            }

            if (password.length < 6) {
                _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
                return@launch
            }

            if (password != confirmPassword) {
                _errorMessage.value = "Las contraseñas no coinciden"
                return@launch
            }

            val existingUser = usuarioDao.getUsuarioByEmail(email)
            if (existingUser != null) {
                _errorMessage.value = "Este email ya está registrado"
                return@launch
            }

            usuarioDao.insert(
                Usuario(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    password = password,
                    telefono = telefono
                )
            )

            _errorMessage.value = null
            onSuccess()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return regex.matches(email)
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun reset() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _errorMessage.value = null
    }
}
