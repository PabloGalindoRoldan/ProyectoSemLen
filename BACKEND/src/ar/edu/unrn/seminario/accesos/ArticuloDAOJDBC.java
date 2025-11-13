package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Articulo;
import ar.edu.unrn.seminario.exception.PersistenceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArticuloDAOJDBC implements ArticuloDAO {

    private static final Logger logger = Logger.getLogger(ArticuloDAOJDBC.class.getName());

    private static final String INSERT = "INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_VISITA = "SELECT id, nombre, cantidad, tipoDonacion FROM articulos WHERE visita_id = ?";
    private static final String DELETE_BY_ORDEN = "DELETE a FROM articulos a JOIN visitas v ON a.visita_id = v.id WHERE v.orden_retiro_id = ?";

    @Override
    public void insertar(Articulo articulo, int visitaId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, visitaId);
            ps.setString(2, articulo.getNombre());
            ps.setInt(3, articulo.getCantidad());
            ps.setString(4, articulo.getTipo() == null ? null : articulo.getTipo().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                	//se puede obtener el id generado si es necesario
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error insertando articulo en visitaId=" + visitaId, e);
            throw new PersistenceException("Error insertando articulo en visitaId=" + visitaId, e);
        }
    }

    @Override
    public List<Articulo> listarPorVisita(int visitaId) throws SQLException {
        List<Articulo> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_VISITA)) {
            ps.setInt(1, visitaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombre = rs.getString("nombre");
                    int cantidad = rs.getInt("cantidad");
                    String tipo = rs.getString("tipoDonacion");
                    Articulo a = new Articulo(nombre, cantidad);
                    if (tipo != null) {
                        try {
                            ar.edu.unrn.seminario.modelo.TipoDonacion t = ar.edu.unrn.seminario.modelo.TipoDonacion.valueOf(tipo);
                            a.setTipo(t);
                        } catch (IllegalArgumentException ex) {
                            // ignore unknown
                        }
                    }
                    result.add(a);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error listando los articulos de visitaId=" + visitaId, e);
            throw new PersistenceException("Error listando los articulos de visitaId=" + visitaId, e);
        }
        return result;
    }

    @Override
    public void eliminarPorOrden(int ordenId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE_BY_ORDEN)) {
            ps.setInt(1, ordenId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error borrando articulos para ordenId=" + ordenId, e);
            throw new PersistenceException("Error borrando articulos para ordenId=" + ordenId, e);
        }
    }
}