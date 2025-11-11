package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Donacion;
import java.sql.SQLException;
import java.util.List;

public interface DonacionDAO {

    void insertar(Donacion donacion, int pedidoId) throws SQLException;

    List<Donacion> listarPorPedido(int pedidoId) throws SQLException;

    Donacion buscarPorId(int id) throws SQLException;

    void eliminar(int id) throws SQLException;

    void actualizar(Donacion donacion) throws SQLException;
}