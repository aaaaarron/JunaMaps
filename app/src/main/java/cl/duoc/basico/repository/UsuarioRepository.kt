package cl.duoc.basico.repository

import cl.duoc.basico.model.Usuario
import cl.duoc.basico.model.UsuarioDao

/**
 * Repositorio para operaciones de Usuario usando Room Database
 */
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    suspend fun register(usuario: Usuario): Long {
        return usuarioDao.insert(usuario)
    }

    suspend fun getUsuarioByEmail(email: String): Usuario? {
        return usuarioDao.getUsuarioByEmail(email)
    }

    suspend fun getAllUsuarios(): List<Usuario> {
        return usuarioDao.getAllUsuarios()
    }

    suspend fun updateUsuario(usuario: Usuario) {
        usuarioDao.update(usuario)
    }

    suspend fun deleteUsuario(usuario: Usuario) {
        usuarioDao.delete(usuario)
    }
}
