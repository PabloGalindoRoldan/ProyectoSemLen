package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Articulo;
import ar.edu.unrn.seminario.modelo.Visita;
import ar.edu.unrn.seminario.exception.PersistenceException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisitaDAOJDBC implements VisitaDAO {

    // Logger: sirve para registrar mensajes y errores en consola
    private static final Logger logger = Logger.getLogger(VisitaDAOJDBC.class.getName());

    // Consultas SQL predefinidas
    private static final String INSERT_VISITA = "INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_VISITAS = "SELECT id, visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal FROM visitas";
    private static final String SELECT_VISITA_BY_ID = "SELECT id, visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal FROM visitas WHERE id = ?";
    private static final String SELECT_VISITAS_BY_ORDEN = "SELECT id, visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal FROM visitas WHERE orden_retiro_id = ?";
    private static final String DELETE_VISITA = "DELETE FROM visitas WHERE id = ?";
    private static final String UPDATE_CANCEL = "UPDATE visitas SET confirmada = 0 WHERE id = ?";
    private static final String INSERT_ARTICULO = "INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ARTICULOS_BY_VISITA = "SELECT nombre, cantidad, tipoDonacion FROM articulos WHERE visita_id = ?";
    private static final String DELETE_ARTICULOS_BY_VISITA = "DELETE FROM articulos WHERE visita_id = ?";

    @Override
    public int create(Visita visita) throws SQLException {
        try {
            // Abrimos conexión con la base
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false); // desactivamos el autocommit por si algo falla

                // Insertamos la visita
                try (PreparedStatement ps = conn.prepareStatement(INSERT_VISITA, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, visita.getVisitante());
                    ps.setTimestamp(2, visita.getFechaHora() == null ? Timestamp.valueOf(LocalDateTime.now()) : Timestamp.valueOf(visita.getFechaHora()));
                    ps.setString(3, visita.getMotivo());
                    ps.setBoolean(4, visita.isConfirmada());
                    ps.setInt(5, visita.getCantidadBienesRecogidos());
                    ps.setString(6, visita.getObservaciones());

                    // Si la orden de retiro no existe, ponemos NULL
                    if (visita.getOrdenRetiro() == null || visita.getOrdenRetiro().getIdOrdenes() == null)
                        ps.setNull(7, java.sql.Types.INTEGER);
                    else
                        ps.setInt(7, visita.getOrdenRetiro().getIdOrdenes());

                    ps.setBoolean(8, visita.isVisitaFinal());
                    ps.executeUpdate();

                    // Recuperamos el ID generado automáticamente
                    int generatedId = 0;
                    try (ResultSet gk = ps.getGeneratedKeys()) {
                        if (gk.next()) generatedId = gk.getInt(1);
                    }

                    int visitaId = generatedId != 0 ? generatedId : (visita.getId() == null ? 0 : visita.getId());

                    // Si hay artículos asociados, los insertamos
                    if (visita.getArticulosRecogidos() != null && !visita.getArticulosRecogidos().isEmpty()) {
                        try (PreparedStatement ps2 = conn.prepareStatement(INSERT_ARTICULO)) {
                            for (Articulo a : visita.getArticulosRecogidos()) {
                                ps2.setInt(1, visitaId);
                                ps2.setString(2, a.getNombre());
                                ps2.setInt(3, a.getCantidad());
                                ps2.setString(4, a.getTipo() == null ? null : a.getTipo().name());
                                ps2.addBatch(); // los acumulamos
                            }
                            ps2.executeBatch(); // y los ejecutamos todos juntos
                        }
                    }

                    // Si la visita fue final, actualizamos el estado de la orden
                    if (visita.isVisitaFinal() && visita.getOrdenRetiro() != null && visita.getOrdenRetiro().getIdOrdenes() != null) {
                        try (PreparedStatement psUpd = conn.prepareStatement("UPDATE ordenes_retiro SET estado = 'COMPLETADO' WHERE id = ?")) {
                            psUpd.setInt(1, visita.getOrdenRetiro().getIdOrdenes());
                            psUpd.executeUpdate();
                        }
                    }
                    
                 // Si la visita NO es final y pertenece a una orden, verificar si debe pasar a EN_EJECUCION
                    if (!visita.isVisitaFinal() && visita.getOrdenRetiro() != null && visita.getOrdenRetiro().getIdOrdenes() != null) {

                        int ordenId = visita.getOrdenRetiro().getIdOrdenes();

                        // Contar visitas de la orden
                        int cantidadVisitas = 0;
                        try (PreparedStatement psCount = conn.prepareStatement(
                                "SELECT COUNT(*) FROM visitas WHERE orden_retiro_id = ?")) {
                            psCount.setInt(1, ordenId);
                            try (ResultSet rsCount = psCount.executeQuery()) {
                                if (rsCount.next()) {
                                    cantidadVisitas = rsCount.getInt(1);
                                }
                            }
                        }

                        // Si hay al menos 1 visital, pasar a EN_EJECUCION
                        if (cantidadVisitas > 0) {
                            try (PreparedStatement psUpd = conn.prepareStatement(
                                    "UPDATE ordenes_retiro SET estado = 'EN_EJECUCION' WHERE id = ?")) {
                                psUpd.setInt(1, ordenId);
                                psUpd.executeUpdate();
                            }
                        }
                    }


                    conn.commit(); // confirmamos todo
                    return visitaId;
                }
            }
        } catch (SQLException ex) {
            // Registramos el error y lanzamos una excepción personalizada
            logger.log(Level.SEVERE, "Error al crear la visita", ex);
            throw new PersistenceException("Error al crear la visita", ex);
        }
    }

    @Override
    public List<Visita> findAll() throws SQLException {
        try {
            List<Visita> result = new ArrayList<>();
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SELECT_VISITAS);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Visita v = mapVisita(rs);
                    // Cargamos los artículos asociados a esa visita
                    loadArticulosForVisita(conn, v);
                    result.add(v);
                }
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener las visitas", e);
            throw new PersistenceException("Error al obtener las visitas", e);
        }
    }

    @Override
    public Visita findById(int id) throws SQLException {
        try {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SELECT_VISITA_BY_ID)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Visita v = mapVisita(rs);
                        loadArticulosForVisita(conn, v);
                        return v;
                    }
                }
            }
            return null; // si no se encuentra la visita
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar la visita con id=" + id, e);
            throw new PersistenceException("Error al buscar la visita con id=" + id, e);
        }
    }

    @Override
    public List<Visita> listarPorOrden(int ordenId) throws SQLException {
        try {
            List<Visita> result = new ArrayList<>();
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SELECT_VISITAS_BY_ORDEN)) {
                ps.setInt(1, ordenId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Visita v = mapVisita(rs);
                        loadArticulosForVisita(conn, v);
                        result.add(v);
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar las visitas de la orden=" + ordenId, e);
            throw new PersistenceException("Error al listar las visitas de la orden=" + ordenId, e);
        }
    }

    @Override
    public void cancelar(int id) throws SQLException {
        try {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(UPDATE_CANCEL)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al cancelar la visita id=" + id, e);
            throw new PersistenceException("Error al cancelar la visita id=" + id, e);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try {
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                // Borramos primero los artículos relacionados
                try (PreparedStatement psDelArt = conn.prepareStatement(DELETE_ARTICULOS_BY_VISITA)) {
                    psDelArt.setInt(1, id);
                    psDelArt.executeUpdate();
                }
                // Luego la visita
                try (PreparedStatement psDelVis = conn.prepareStatement(DELETE_VISITA)) {
                    psDelVis.setInt(1, id);
                    psDelVis.executeUpdate();
                }
                conn.commit();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar la visita id=" + id, e);
            throw new PersistenceException("Error al eliminar la visita id=" + id, e);
        }
    }

    // Método auxiliar que traduce el ResultSet a un objeto Visita
    private Visita mapVisita(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String visitante = rs.getString("visitante");
        Timestamp t = rs.getTimestamp("fechaHora");
        LocalDateTime fechaHora = t == null ? null : t.toLocalDateTime();
        String motivo = rs.getString("motivo");
        boolean confirmada = rs.getBoolean("confirmada");
        int cantidad = rs.getInt("cantidadBienesRecogidos");
        String observaciones = rs.getString("observaciones");
        Integer ordenId = rs.getObject("orden_retiro_id") == null ? null : rs.getInt("orden_retiro_id");
        boolean visitaFinal = rs.getBoolean("visitaFinal");

        Visita v;
        if (ordenId == null) {
            v = new Visita(id, visitante, fechaHora, motivo, confirmada, cantidad, null, observaciones, null, visitaFinal);
        } else {
            // Creamos una OrdenRetiro “placeholder” solo con el ID
            ar.edu.unrn.seminario.modelo.OrdenRetiro ord = new ar.edu.unrn.seminario.modelo.OrdenRetiro(ordenId, null, null);
            v = new Visita(id, visitante, fechaHora, motivo, confirmada, cantidad, null, observaciones, ord, visitaFinal);
        }
        return v;
    }

    // Carga los artículos asociados a una visita
    private void loadArticulosForVisita(Connection conn, Visita v) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ARTICULOS_BY_VISITA)) {
            ps.setInt(1, v.getId());
            try (ResultSet rs2 = ps.executeQuery()) {
                List<Articulo> articulos = new ArrayList<>();
                while (rs2.next()) {
                    Articulo a = new Articulo(rs2.getString("nombre"), rs2.getInt("cantidad"));
                    String tipo = rs2.getString("tipoDonacion");
                    if (tipo != null) {
                        try {
                            a.setTipo(ar.edu.unrn.seminario.modelo.TipoDonacion.valueOf(tipo));
                        } catch (IllegalArgumentException ex) {
                            // Si no reconoce el tipo, lo ignoramos (por las dudas)
                        }
                    }
                    articulos.add(a);
                }
                v.setArticulosRecogidos(articulos);
            }
        }
    }
}

