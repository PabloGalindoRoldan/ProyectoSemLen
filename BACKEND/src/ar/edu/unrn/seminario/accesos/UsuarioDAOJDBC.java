package ar.edu.unrn.seminario.accesos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ar.edu.unrn.seminario.modelo.Rol;
import ar.edu.unrn.seminario.modelo.Usuario;
import ar.edu.unrn.seminario.exception.PersistenceException;

public class UsuarioDAOJDBC implements UsuarioDAO {

    // Logger para registrar errores y mensajes informativos
    private static final Logger logger = Logger.getLogger(UsuarioDAOJDBC.class.getName());

    // Consultas SQL preparadas (evita errores de tipeo y ayuda a mantener orden)
    private static final String INSERT_SQL = "INSERT INTO usuarios (usuario, contrasena, nombre, email, rol_codigo, activo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE usuarios SET contrasena = ?, nombre = ?, email = ?, rol_codigo = ?, activo = ? WHERE usuario = ?";
    private static final String DELETE_SQL = "DELETE FROM usuarios WHERE usuario = ?";
    private static final String SELECT_BY_ID = "SELECT u.usuario, u.contrasena, u.nombre, u.email, u.rol_codigo, r.nombre AS rol_nombre, u.activo FROM usuarios u LEFT JOIN roles r ON u.rol_codigo = r.codigo WHERE u.usuario = ?";
    private static final String SELECT_ALL = "SELECT u.usuario, u.contrasena, u.nombre, u.email, u.rol_codigo, r.nombre AS rol_nombre, u.activo FROM usuarios u LEFT JOIN roles r ON u.rol_codigo = r.codigo";

    @Override
    public void create(Usuario usuario) throws Exception {
        // Inserta un nuevo usuario en la base de datos
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getEmail());

            Rol r = usuario.getRol();
            if (r != null && r.getCodigo() != null)
                ps.setInt(5, r.getCodigo());
            else
                ps.setNull(5, java.sql.Types.INTEGER);

            ps.setBoolean(6, usuario.isActivo());
            ps.executeUpdate();

        } catch (SQLException e) {
            // Loguea el error y lanza una excepción personalizada
            logger.log(Level.SEVERE, "Error al crear el usuario: " + (usuario == null ? null : usuario.getUsuario()), e);
            throw new PersistenceException("Error al crear el usuario en la base de datos.", e);
        }
    }

    @Override
    public void update(Usuario usuario) throws Exception {
        // Actualiza los datos de un usuario existente
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, usuario.getContrasena());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getEmail());

            Rol r = usuario.getRol();
            if (r != null && r.getCodigo() != null)
                ps.setInt(4, r.getCodigo());
            else
                ps.setNull(4, java.sql.Types.INTEGER);

            ps.setBoolean(5, usuario.isActivo());
            ps.setString(6, usuario.getUsuario());
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar el usuario: " + (usuario == null ? null : usuario.getUsuario()), e);
            throw new PersistenceException("Error al actualizar el usuario en la base de datos.", e);
        }
    }

    @Override
    public void remove(String username) throws Exception {
        // Elimina un usuario por su nombre de usuario
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar el usuario: " + username, e);
            throw new PersistenceException("Error al eliminar el usuario de la base de datos.", e);
        }
    }

    @Override
    public void remove(Usuario usuario) throws Exception {
        // Sobrecarga: elimina pasando el objeto usuario directamente
        remove(usuario.getUsuario());
    }

    @Override
    public Usuario find(String username) throws Exception {
        // Busca un usuario por su nombre de usuario (clave primaria)
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Mapea los datos obtenidos desde la base al objeto Usuario
                    String u = rs.getString("usuario");
                    String contrasena = rs.getString("contrasena");
                    String nombre = rs.getString("nombre");
                    String email = rs.getString("email");
                    Integer rolCodigo = rs.getObject("rol_codigo") == null ? null : rs.getInt("rol_codigo");
                    String rolNombre = rs.getString("rol_nombre");
                    boolean activo = rs.getBoolean("activo");

                    Rol rol = new Rol();
                    rol.setCodigo(rolCodigo);
                    if (rolNombre != null) rol.setNombre(rolNombre);

                    Usuario usuario = new Usuario(u, contrasena, nombre, email, rol);
                    if (activo) usuario.activar();

                    return usuario;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar el usuario: " + username, e);
            throw new PersistenceException("Error al buscar el usuario en la base de datos.", e);
        }

        return null; // Si no lo encuentra, devuelve null
    }

    @Override
    public List<Usuario> findAll() throws Exception {
        // Devuelve la lista completa de usuarios
        List<Usuario> resultados = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Crea un nuevo objeto Usuario por cada fila del resultado
                String u = rs.getString("usuario");
                String contrasena = rs.getString("contrasena");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                Integer rolCodigo = rs.getObject("rol_codigo") == null ? null : rs.getInt("rol_codigo");
                String rolNombre = rs.getString("rol_nombre");
                boolean activo = rs.getBoolean("activo");

                Rol rol = new Rol();
                rol.setCodigo(rolCodigo);
                if (rolNombre != null) rol.setNombre(rolNombre);

                Usuario usuario = new Usuario(u, contrasena, nombre, email, rol);
                if (activo) usuario.activar();

                resultados.add(usuario);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener la lista de usuarios", e);
            throw new PersistenceException("Error al recuperar todos los usuarios desde la base de datos.", e);
        }

        return resultados;
    }

}
