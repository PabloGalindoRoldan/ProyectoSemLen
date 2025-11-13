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

    // Sentencias SQL (bastante estándar)
    private static final String INSERT_SQL = "INSERT INTO roles (codigo, nombre, activo) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE roles SET nombre = ?, activo = ? WHERE codigo = ?";
    private static final String DELETE_SQL = "DELETE FROM roles WHERE codigo = ?";
    private static final String SELECT_BY_ID = "SELECT codigo, nombre, activo FROM roles WHERE codigo = ?";
    private static final String SELECT_ALL = "SELECT codigo, nombre, activo FROM roles";
    private static final String SELECT_ALL_ACTIVE = "SELECT codigo, nombre, activo FROM roles WHERE activo = 1";

    @Override
    public void create(Rol rol) throws Exception {
        // Inserta un nuevo rol en la base de datos
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setInt(1, rol.getCodigo());
            ps.setString(2, rol.getNombre());
            ps.setBoolean(3, rol.isActivo());
            ps.executeUpdate();

        } catch (SQLException e) {
            // Error al crear el rol
            logger.log(Level.SEVERE, "Error al crear el rol: " + (rol == null ? null : rol.getCodigo()), e);
            throw new PersistenceException("Error al crear el rol", e);
        }
    }

    @Override
    public void update(Rol rol) throws Exception {
        // Actualiza los datos del rol
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, rol.getNombre());
            ps.setBoolean(2, rol.isActivo());
            ps.setInt(3, rol.getCodigo());
            ps.executeUpdate();

        } catch (SQLException e) {
            // Algo falló al actualizar el rol
            logger.log(Level.SEVERE, "Error al actualizar el rol: " + (rol == null ? null : rol.getCodigo()), e);
            throw new PersistenceException("Error al actualizar el rol", e);
        }
    }

    @Override
    public void remove(Integer codigo) throws Exception {
        // Borra el rol según su código
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, codigo);
            ps.executeUpdate();

        } catch (SQLException e) {
            // Error al eliminar el rol
            logger.log(Level.SEVERE, "Error al eliminar el rol con código: " + codigo, e);
            throw new PersistenceException("Error al eliminar el rol", e);
        }
    }

    @Override
    public void remove(Rol rol) throws Exception {
        // Sobrecarga que elimina el rol directamente desde el objeto
        remove(rol.getCodigo());
    }

    @Override
    public Rol find(Integer codigo) throws Exception {
        // Busca un rol por su código
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si lo encuentra, crea el objeto Rol con los datos
                    Rol rol = new Rol(rs.getInt("codigo"), rs.getString("nombre"));
                    if (rs.getBoolean("activo"))
                        rol.activar();
                    else
                        rol.desactivar();
                    return rol;
                }
            }

        } catch (SQLException e) {
            // Error al buscar un rol en la BD
            logger.log(Level.SEVERE, "Error al buscar el rol con código: " + codigo, e);
            throw new PersistenceException("Error al buscar el rol", e);
        }

        // Si no lo encuentra, devuelve null (ojo con esto en la capa de servicio)
        return null;
    }

    @Override
    public List<Rol> findAll() throws Exception {
        // Devuelve todos los roles, activos o no
        List<Rol> resultados = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol(rs.getInt("codigo"), rs.getString("nombre"));
                if (rs.getBoolean("activo"))
                    rol.activar();
                else
                    rol.desactivar();
                resultados.add(rol);
            }

        } catch (SQLException e) {
            // Error general al traer todos los roles
            logger.log(Level.SEVERE, "Error al obtener todos los roles", e);
            throw new PersistenceException("Error al obtener todos los roles", e);
        }

        return resultados;
    }

    @Override
    public List<Rol> findAllActive() throws Exception {
        // Devuelve únicamente los roles activos
        List<Rol> resultados = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_ACTIVE);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol(rs.getInt("codigo"), rs.getString("nombre"));
                rol.activar(); // como el WHERE ya filtra activos, lo marcamos directamente
                resultados.add(rol);
            }

        } catch (SQLException e) {
            // Error al buscar los roles activos
            logger.log(Level.SEVERE, "Error al obtener los roles activos", e);
            throw new PersistenceException("Error al obtener los roles activos", e);
        }

        return resultados;
    }
}
