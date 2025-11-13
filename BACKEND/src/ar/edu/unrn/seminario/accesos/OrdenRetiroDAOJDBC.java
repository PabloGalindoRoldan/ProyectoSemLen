package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.OrdenRetiro;
import ar.edu.unrn.seminario.modelo.EstadoOrden;
import ar.edu.unrn.seminario.modelo.PedidoDonacion;
import ar.edu.unrn.seminario.modelo.Visita;
import ar.edu.unrn.seminario.modelo.Usuario;
import ar.edu.unrn.seminario.exception.PersistenceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;

public class OrdenRetiroDAOJDBC implements OrdenRetiroDAO {

    private static final Logger logger = Logger.getLogger(OrdenRetiroDAOJDBC.class.getName());

    private static final String INSERT_ORDEN = "INSERT INTO ordenes_retiro (id, pedido_id, voluntario_username, fecha_generacion, estado) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ORDENES = "SELECT id, pedido_id, voluntario_username, fecha_generacion, estado FROM ordenes_retiro";
    private static final String SELECT_ORDEN_BY_ID = "SELECT id, pedido_id, voluntario_username, fecha_generacion, estado FROM ordenes_retiro WHERE id = ?";
    private static final String DELETE_ORDEN = "DELETE FROM ordenes_retiro WHERE id = ?";
    private static final String UPDATE_ORDEN = "UPDATE ordenes_retiro SET estado = ?, voluntario_username = ? WHERE id = ?";

    private VisitaDAO visitaDAO = new VisitaDAOJDBC();

    @Override
    public int create(OrdenRetiro orden) throws SQLException {
        try {
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_ORDEN, Statement.RETURN_GENERATED_KEYS)) {
                int useId = orden.getIdOrdenes() == null ? 0 : orden.getIdOrdenes();
                ps.setInt(1, useId);
                ps.setInt(2, orden.getPedido() == null ? 0 : orden.getPedido().getId());
                ps.setString(3, orden.getVoluntario() == null ? null : orden.getVoluntario().getUsuario());
                ps.setTimestamp(4, orden.getFechaGeneracion() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(orden.getFechaGeneracion()));
                ps.setString(5, orden.getEstado() == null ? "PENDIENTE" : orden.getEstado().name());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
                return useId;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating orden retiro", e);
            throw new PersistenceException("Error creating orden retiro", e);
        }
    }

    @Override
    public List<OrdenRetiro> findAll() throws SQLException {
        List<OrdenRetiro> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ORDENES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer pedidoId = rs.getInt("pedido_id");
                String voluntarioUsername = rs.getString("voluntario_username");
                String estadoStr = rs.getString("estado");
                Timestamp ts = rs.getTimestamp("fecha_generacion");
                LocalDateTime fecha = (ts != null) ? ts.toLocalDateTime() : null;

                // Crear objetos relacionados (placeholder)
                PedidoDonacion pedido = new PedidoDonacion(
                    pedidoId, null, null, false,
                    new Usuario(null, null, null, null, null)
                );
                Usuario voluntario = new Usuario(voluntarioUsername, null, null, null, null);

                // Convertir estado con seguridad
                EstadoOrden estado = EstadoOrden.PENDIENTE;
                if (estadoStr != null) {
                    try {
                        estado = EstadoOrden.valueOf(estadoStr);
                    } catch (IllegalArgumentException e) {
                        logger.warning("Estado inválido en BD para orden id=" + id + ": " + estadoStr);
                    }
                }

                // Cargar visitas
                List<Visita> visitas = visitaDAO.listarPorOrden(id);

                // Crear la orden completa con fecha y estado
                OrdenRetiro orden = new OrdenRetiro(id, pedido, voluntario, fecha, estado, visitas);
                result.add(orden);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding ordenes retiro", e);
            throw new PersistenceException("Error finding ordenes retiro", e);
        }
        return result;
    }

    @Override
    public OrdenRetiro findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ORDEN_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer pedidoId = rs.getInt("pedido_id");
                    String voluntarioUsername = rs.getString("voluntario_username");
                    String estadoStr = rs.getString("estado");
                    Timestamp ts = rs.getTimestamp("fecha_generacion");
                    LocalDateTime fecha = (ts != null) ? ts.toLocalDateTime() : null;

                    PedidoDonacion pedido = new PedidoDonacion(
                        pedidoId, null, null, false,
                        new Usuario(null, null, null, null, null)
                    );
                    Usuario voluntario = new Usuario(voluntarioUsername, null, null, null, null);

                    EstadoOrden estado = EstadoOrden.PENDIENTE;
                    if (estadoStr != null) {
                        try {
                            estado = EstadoOrden.valueOf(estadoStr);
                        } catch (IllegalArgumentException e) {
                            logger.warning("Estado inválido en BD para orden id=" + id + ": " + estadoStr);
                        }
                    }

                    List<Visita> visitas = visitaDAO.listarPorOrden(id);
                    return new OrdenRetiro(id, pedido, voluntario, fecha, estado, visitas);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding orden retiro by id=" + id, e);
            throw new PersistenceException("Error finding orden retiro by id=" + id, e);
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        try {
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE_ORDEN)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting orden retiro id=" + id, e);
            throw new PersistenceException("Error deleting orden retiro id=" + id, e);
        }
    }

    @Override
    public void update(OrdenRetiro orden) throws SQLException {
        try {
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_ORDEN)) {
                ps.setString(1, orden.getEstado() == null ? "PENDIENTE" : orden.getEstado().name());
                ps.setString(2, orden.getVoluntario() == null ? null : orden.getVoluntario().getUsuario());
                ps.setInt(3, orden.getIdOrdenes());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating orden retiro id=" + (orden == null ? null : orden.getIdOrdenes()), e);
            throw new PersistenceException("Error updating orden retiro", e);
        }
    }
}