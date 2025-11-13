package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Donacion;
import ar.edu.unrn.seminario.modelo.PedidoDonacion;
import ar.edu.unrn.seminario.modelo.Usuario;
import ar.edu.unrn.seminario.exception.PersistenceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;

public class PedidoDAOJDBC implements PedidoDAO {

    private static final Logger logger = Logger.getLogger(PedidoDAOJDBC.class.getName());

    private static final String INSERT_PEDIDO = "INSERT INTO pedidos (id, descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PEDIDOS = "SELECT id, descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total FROM pedidos";
    private static final String SELECT_PEDIDO_BY_ID = "SELECT id, descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total FROM pedidos WHERE id = ?";
    private static final String DELETE_PEDIDO = "DELETE FROM pedidos WHERE id = ?";

    private DonacionDAO donacionDAO = new DonacionDAOJDBC();

    @Override
    public int create(PedidoDonacion pedido) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(INSERT_PEDIDO, Statement.RETURN_GENERATED_KEYS)) {
                int useId = (pedido.getId() == null || pedido.getId() == 0) ? 0 : pedido.getId();
                ps.setInt(1, useId);
                ps.setString(2, pedido.getDescripcion());
                ps.setString(3, pedido.getObservaciones());
                ps.setBoolean(4, pedido.necesitaVehiculo());
                Usuario donante = pedido.getDonante();
                ps.setString(5, donante == null ? null : donante.getUsuario());
                ps.setTimestamp(6, Timestamp.valueOf(pedido.getFechaCreacion()));
                ps.setBoolean(7, true);
                ps.setInt(8, pedido.calcularPuntajeTotal());
                ps.executeUpdate();

                int generatedId = 0;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) generatedId = rs.getInt(1);
                }

                int pedidoId = (generatedId != 0) ? generatedId : (pedido.getId() == null ? 0 : pedido.getId());

                // Inserto las donaciones con la misma conexión para mantener la transacción coherente
                if (pedido.getDonaciones() != null && !pedido.getDonaciones().isEmpty()) {
                    for (Donacion d : pedido.getDonaciones()) {
                        donacionDAO.insertar(conn, d, pedidoId);
                    }
                }

                conn.commit();
                return pedidoId;

            } catch (SQLException ex) {
                try { conn.rollback(); } catch (SQLException r) { logger.log(Level.SEVERE, "Error al hacer rollback", r); }
                // Error al crear el pedido
                logger.log(Level.SEVERE, "Error al crear el pedido", ex);
                throw new PersistenceException("Error al crear el pedido", ex);
            }
        } catch (SQLException e) {
            // Error al obtener la conexión o ejecutar la creación
            logger.log(Level.SEVERE, "Error en create(PedidoDonacion)", e);
            throw new PersistenceException("Error al crear el pedido (problema de conexión)", e);
        }
    }

    @Override
    public List<PedidoDonacion> findAll() throws SQLException {
        List<PedidoDonacion> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PEDIDOS);
             ResultSet rs = ps.executeQuery()) {

        	while (rs.next()) {
        	    Integer id = rs.getInt("id");
        	    String descripcion = rs.getString("descripcion");
        	    String observaciones = rs.getString("observaciones");
        	    boolean necesitaVehiculo = rs.getBoolean("necesitaVehiculo");
        	    String donante = rs.getString("donante_username");
        	    Usuario u = new Usuario(donante, null, null, null, null);
        	    LocalDateTime fecha = rs.getTimestamp("fecha_creacion").toLocalDateTime(); 
        	    List<Donacion> donaciones = donacionDAO.listarPorPedido(id);

        	    PedidoDonacion p = new PedidoDonacion(id, descripcion, observaciones, necesitaVehiculo, u, fecha, donaciones);
        	    result.add(p);
        	}

        } catch (SQLException e) {
            // Algo falló al buscar los pedidos
            logger.log(Level.SEVERE, "Error al obtener la lista de pedidos", e);
            throw new PersistenceException("Error al obtener todos los pedidos", e);
        }

        return result;
    }

    @Override
    public PedidoDonacion findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PEDIDO_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            	    String descripcion = rs.getString("descripcion");
            	    String observaciones = rs.getString("observaciones");
            	    boolean necesitaVehiculo = rs.getBoolean("necesitaVehiculo");
            	    String donante = rs.getString("donante_username");
            	    Usuario u = new Usuario(donante, null, null, null, null);
            	    LocalDateTime fecha = rs.getTimestamp("fecha_creacion").toLocalDateTime();  
            	    List<Donacion> donaciones = donacionDAO.listarPorPedido(id);

            	    return new PedidoDonacion(id, descripcion, observaciones, necesitaVehiculo, u, fecha, donaciones);
            	}
            }
        } catch (SQLException e) {
            // Error al buscar un pedido por ID
            logger.log(Level.SEVERE, "Error al buscar el pedido con id=" + id, e);
            throw new PersistenceException("Error al buscar el pedido con id=" + id, e);
        }

        // Si no lo encontró, devuelve null (ojo con esto en la capa superior)
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Primero borro las donaciones asociadas para evitar errores de FK
                donacionDAO.eliminarPorPedido(conn, id);

                // Luego borro el pedido
                try (PreparedStatement ps = conn.prepareStatement(DELETE_PEDIDO)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                conn.commit();

            } catch (SQLException ex) {
                try { conn.rollback(); } catch (SQLException r) { logger.log(Level.SEVERE, "Error al hacer rollback", r); }
                logger.log(Level.SEVERE, "Error al eliminar el pedido con id=" + id, ex);
                throw new PersistenceException("Error al eliminar el pedido con id=" + id, ex);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error en delete(pedido)", e);
            throw new PersistenceException("Error al eliminar el pedido (problema de conexión)", e);
        }
    }
}
