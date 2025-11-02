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
    private static final String SELECT_BY_ID = "SELECT usuario, contrasena, nombre, email, rol_codigo, activo FROM usuarios WHERE usuario = ?";
    private static final String SELECT_ALL = "SELECT usuario, contrasena, nombre, email, rol_codigo, activo FROM usuarios";

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
            throw new Exception("Error creating usuario", e);
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
            throw new Exception("Error updating usuario", e);
        }
    }

    @Override
    public void remove(String username) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error deleting usuario", e);
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
                    boolean activo = rs.getBoolean("activo");
                    Rol rol = new Rol();
                    rol.setCodigo(rolCodigo);
                    Usuario usuario = new Usuario(u, contrasena, nombre, email, rol);
                    if (activo) usuario.activar();
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error finding usuario", e);
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
                boolean activo = rs.getBoolean("activo");
                Rol rol = new Rol();
                rol.setCodigo(rolCodigo);
                Usuario usuario = new Usuario(u, contrasena, nombre, email, rol);
                if (activo) usuario.activar();
                resultados.add(usuario);
            }
        } catch (SQLException e) {
            throw new Exception("Error finding all usuarios", e);
        }
        return resultados;
    }
}
