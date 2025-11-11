package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Donacion;
import ar.edu.unrn.seminario.modelo.TipoDonacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonacionDAOJDBC implements DonacionDAO {

    private static final String INSERT =
        "INSERT INTO donaciones (tipoDonacion, categoria, puntaje, pedido_id) VALUES (?, ?, ?, ?)";

    private static final String SELECT_BY_PEDIDO =
        "SELECT id, tipoDonacion, categoria, puntaje FROM donaciones WHERE pedido_id = ?";

    private static final String DELETE_BY_PEDIDO =
        "DELETE FROM donaciones WHERE pedido_id = ?";

    private static final String UPDATE =
        "UPDATE donaciones SET tipoDonacion = ?, categoria = ?, puntaje = ? WHERE id = ?";

    @Override
    public void insertar(Donacion donacion, int pedidoId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, donacion.getTipoDonacion().name());
            ps.setString(2, donacion.getCategoria());
            ps.setInt(3, donacion.getPuntaje());
            ps.setInt(4, pedidoId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    donacion.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Donacion> listarPorPedido(int pedidoId) throws SQLException {
        List<Donacion> donaciones = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_PEDIDO)) {

            ps.setInt(1, pedidoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donacion d = mapear(rs);
                    donaciones.add(d);
                }
            }
        }
        return donaciones;
    }

    @Override
    public void eliminar(int pedidoId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_PEDIDO)) {
            ps.setInt(1, pedidoId);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Donacion donacion) throws SQLException {
        if (donacion.getId() == 0) {
            throw new IllegalArgumentException("No se puede actualizar una donación sin ID.");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, donacion.getTipoDonacion().name());
            ps.setString(2, donacion.getCategoria());
            ps.setInt(3, donacion.getPuntaje());
            ps.setInt(4, donacion.getId());
            ps.executeUpdate();
        }
    }
    
    @Override
    public Donacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, tipoDonacion, categoria, puntaje FROM donaciones WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                } else {
                	throw new IllegalArgumentException("No se pudo encontrar la donacion con el Id ingresado");
                }
            }
        }
    }
    

    private Donacion mapear(ResultSet rs) throws SQLException {
        TipoDonacion tipo = TipoDonacion.valueOf(rs.getString("tipoDonacion"));
        String categoria = rs.getString("categoria");
        int puntaje = rs.getInt("puntaje");
        Donacion d = new Donacion(tipo, categoria, puntaje);
        d.setId(rs.getInt("id"));
        return d;
    }
}

