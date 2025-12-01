package cl.duoc.basico.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cl.duoc.basico.model.Usuario
import cl.duoc.basico.model.UsuarioDao
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        usuarioDao = mockk(relaxed = true)
        viewModel = AuthViewModel(usuarioDao)
    }

    @After
    fun tearDown() {
        viewModel.reset()
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `login con credenciales correctas debe ser exitoso`() = runTest {
        val email = "test@test.com"
        val password = "123456"
        val usuario = Usuario(1, "Test", "User", email, password, "")

        coEvery { usuarioDao.login(email, password) } returns usuario

        var loginSuccess = false

        viewModel.login(email, password) {
            loginSuccess = true
        }
        advanceUntilIdle()

        assertTrue(loginSuccess)
        assertTrue(viewModel.isLoggedIn.first())
        assertEquals(usuario, viewModel.currentUser.first())
        assertNull(viewModel.errorMessage.first())

        coVerify { usuarioDao.login(email, password) }
    }

    @Test
    fun `login con credenciales incorrectas debe fallar`() = runTest {
        val email = "test@test.com"
        val password = "wrong"

        coEvery { usuarioDao.login(email, password) } returns null

        var loginSuccess = false

        viewModel.login(email, password) {
            loginSuccess = true
        }
        advanceUntilIdle()

        assertFalse(loginSuccess)
        assertFalse(viewModel.isLoggedIn.first())
        assertEquals("Email o contraseña incorrectos", viewModel.errorMessage.first())
    }

    @Test
    fun `login con campos vacíos debe mostrar error`() = runTest {
        var loginSuccess = false

        viewModel.login("", "") {
            loginSuccess = true
        }
        advanceUntilIdle()

        assertFalse(loginSuccess)
        assertEquals("Todos los campos son obligatorios", viewModel.errorMessage.first())
    }

    @Test
    fun `registro exitoso debe crear usuario`() = runTest {
        val nombre = "Juan"
        val apellido = "Pérez"
        val email = "juan@test.com"
        val password = "123456"
        val confirmPassword = "123456"
        val telefono = "+56912345678"

        coEvery { usuarioDao.getUsuarioByEmail(email) } returns null
        coEvery { usuarioDao.insert(any()) } returns 1L

        var registerSuccess = false

        viewModel.register(nombre, apellido, email, password, confirmPassword, telefono) {
            registerSuccess = true
        }
        advanceUntilIdle()

        assertTrue(registerSuccess)
        assertNull(viewModel.errorMessage.first())
        coVerify { usuarioDao.insert(any()) }
    }

    @Test
    fun `registro con email inválido debe mostrar error`() = runTest {
        var registerSuccess = false

        viewModel.register("Juan", "Pérez", "invalid-email", "123456", "123456", "") {
            registerSuccess = true
        }
        advanceUntilIdle()

        assertFalse(registerSuccess)
        assertEquals("Email inválido", viewModel.errorMessage.first())
    }

    @Test
    fun `registro con contraseña corta debe mostrar error`() = runTest {
        var registerSuccess = false

        viewModel.register("Juan", "Pérez", "juan@test.com", "123", "123", "") {
            registerSuccess = true
        }
        advanceUntilIdle()

        assertFalse(registerSuccess)
        assertEquals("La contraseña debe tener al menos 6 caracteres", viewModel.errorMessage.first())
    }

    @Test
    fun `registro con contraseñas que no coinciden debe mostrar error`() = runTest {
        var registerSuccess = false

        viewModel.register("Juan", "Pérez", "juan@test.com", "123456", "654321", "") {
            registerSuccess = true
        }
        advanceUntilIdle()

        assertFalse(registerSuccess)
        assertEquals("Las contraseñas no coinciden", viewModel.errorMessage.first())
    }

    @Test
    fun `registro con email duplicado debe mostrar error`() = runTest {
        val email = "existing@test.com"
        val existingUser = Usuario(1, "Existing", "User", email, "123456", "")

        coEvery { usuarioDao.getUsuarioByEmail(email) } returns existingUser

        var registerSuccess = false

        viewModel.register("Juan", "Pérez", email, "123456", "123456", "") {
            registerSuccess = true
        }
        advanceUntilIdle()

        assertFalse(registerSuccess)
        assertEquals("Este email ya está registrado", viewModel.errorMessage.first())
    }

    @Test
    fun `logout debe limpiar sesión`() = runTest {
        val usuario = Usuario(1, "Test", "User", "test@test.com", "123456", "")

        coEvery { usuarioDao.login(usuario.email, usuario.password) } returns usuario

        viewModel.login(usuario.email, usuario.password) {}
        advanceUntilIdle()

        viewModel.logout()
        advanceUntilIdle()

        assertFalse(viewModel.isLoggedIn.first())
        assertNull(viewModel.currentUser.first())
        assertNull(viewModel.errorMessage.first())
    }

    @Test
    fun `clearError debe limpiar mensaje de error`() = runTest {
        viewModel.login("", "") {}
        advanceUntilIdle()

        viewModel.clearError()
        advanceUntilIdle()

        assertNull(viewModel.errorMessage.first())
    }
}
