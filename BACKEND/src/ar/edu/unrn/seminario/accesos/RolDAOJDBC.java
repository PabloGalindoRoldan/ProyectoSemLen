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
import ar.edu.unrn.seminario.exception.PersistenceException;

public class RolDAOJDBC implements RolDAO {

    private static final Logger logger = Logger.getLogger(RolDAOJDBC.class.getName());

    private static final String INSERT_SQL = "INSERT INTO roles (codigo, nombre, activo) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE roles SET nombre = ?, activo = ? WHERE codigo = ?";
    private static final String DELETE_SQL = "DELETE FROM roles WHERE codigo = ?";
    private static final String SELECT_BY_ID = "SELECT codigo, nombre, activo FROM roles WHERE codigo = ?";
    private static final String SELECT_ALL = "SELECT codigo, nombre, activo FROM roles";
    private static final String SELECT_ALL_ACTIVE = "SELECT codigo, nombre, activo FROM roles WHERE activo = 1";

    @Override
    public void create(Rol rol) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, rol.getCodigo());
            ps.setString(2, rol.getNombre());
            ps.setBoolean(3, rol.isActivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating rol: " + (rol == null ? null : rol.getCodigo()), e);
            throw new PersistenceException("Error creando rol", e);
        }
    }

    @Override
    public void update(Rol rol) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, rol.getNombre());
            ps.setBoolean(2, rol.isActivo());
            ps.setInt(3, rol.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating rol: " + (rol == null ? null : rol.getCodigo()), e);
            throw new PersistenceException("Error actualizando rol", e);
        }
    }

    @Override
    public void remove(Integer codigo) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting rol: " + codigo, e);
            throw new PersistenceException("Error eliminando rol", e);
        }
    }

    @Override
    public void remove(Rol rol) throws Exception {
        remove(rol.getCodigo());
    }

    @Override
    public Rol find(Integer codigo) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol(rs.getInt("codigo"), rs.getString("nombre"));
                    if (rs.getBoolean("activo")) rol.activar(); else rol.desactivar();
                    return rol;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding rol: " + codigo, e);
            throw new PersistenceException("Error encontrando rol", e);
        }
        return null;
    }

    @Override
    public List<Rol> findAll() throws Exception {
        List<Rol> resultados = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Rol rol = new Rol(rs.getInt("codigo"), rs.getString("nombre"));
                if (rs.getBoolean("activo")) rol.activar(); else rol.desactivar();
                resultados.add(rol);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all roles", e);
            throw new PersistenceException("Error encontrando todos los roles", e);
        }
        return resultados;
    }

    @Override
    public List<Rol> findAllActive() throws Exception {
        List<Rol> resultados = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL_ACTIVE); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Rol rol = new Rol(rs.getInt("codigo"), rs.getString("nombre"));
                rol.activar();
                resultados.add(rol);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding active roles", e);
            throw new PersistenceException("Error finding active roles", e);
        }
        return resultados;
    }
}