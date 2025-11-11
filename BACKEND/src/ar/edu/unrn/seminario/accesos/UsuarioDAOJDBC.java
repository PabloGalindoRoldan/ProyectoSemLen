package ar.edu.unrn.seminario.accesos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unrn.seminario.modelo.Rol;
import ar.edu.unrn.seminario.modelo.Usuario;

public class UsuarioDAOJDBC implements UsuarioDAO {

    private static final String INSERT_SQL = "INSERT INTO usuarios (usuario, contrasena, nombre, email, rol_codigo, activo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE usuarios SET contrasena = ?, nombre = ?, email = ?, rol_codigo = ?, activo = ? WHERE usuario = ?";
    private static final String DELETE_SQL = "DELETE FROM usuarios WHERE usuario = ?";
    private static final String SELECT_BY_ID = "SELECT u.usuario, u.contrasena, u.nombre, u.email, u.rol_codigo, r.nombre AS rol_nombre, u.activo FROM usuarios u LEFT JOIN roles r ON u.rol_codigo = r.codigo WHERE u.usuario = ?";
    private static final String SELECT_ALL = "SELECT u.usuario, u.contrasena, u.nombre, u.email, u.rol_codigo, r.nombre AS rol_nombre, u.activo FROM usuarios u LEFT JOIN roles r ON u.rol_codigo = r.codigo";

    @Override
    public void create(Usuario usuario) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getEmail());
            Rol r = usuario.getRol();
            if (r != null && r.getCodigo() != null) ps.setInt(5, r.getCodigo()); else ps.setNull(5, java.sql.Types.INTEGER);
            ps.setBoolean(6, usuario.isActivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error creando usuario", e);
        }
    }

    @Override
    public void update(Usuario usuario) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, usuario.getContrasena());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getEmail());
            Rol r = usuario.getRol();
            if (r != null && r.getCodigo() != null) ps.setInt(4, r.getCodigo()); else ps.setNull(4, java.sql.Types.INTEGER);
            ps.setBoolean(5, usuario.isActivo());
            ps.setString(6, usuario.getUsuario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error actualizando usuario", e);
        }
    }

    @Override
    public void remove(String username) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error borrando usuario", e);
        }
    }

    @Override
    public void remove(Usuario usuario) throws Exception {
        remove(usuario.getUsuario());
    }

    @Override
    public Usuario find(String username) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
            throw new Exception("Error encontrando usuario", e);
        }
        return null;
    }

    @Override
    public List<Usuario> findAll() throws Exception {
        List<Usuario> resultados = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
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
            throw new Exception("Error al buscar todos los usuarios", e);
        }
        return resultados;
    }
}