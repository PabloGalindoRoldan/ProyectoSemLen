package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Donacion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DonacionDAO {

    void insertar(Donacion donacion, int pedidoId) throws SQLException;

    List<Donacion> listarPorPedido(int pedidoId) throws SQLException;

    Donacion buscarPorId(int id) throws SQLException;

    void eliminar(int id) throws SQLException;

    void actualizar(Donacion donacion) throws SQLException;

    // Connection-aware variants to allow transactional callers to reuse the same Connection
    void insertar(Connection conn, Donacion donacion, int pedidoId) throws SQLException;

    void eliminarPorPedido(Connection conn, int pedidoId) throws SQLException;
}