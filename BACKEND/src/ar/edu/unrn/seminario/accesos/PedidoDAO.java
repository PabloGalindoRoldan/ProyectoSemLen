package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.PedidoDonacion;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {
    int create(PedidoDonacion pedido) throws SQLException; 
    List<PedidoDonacion> findAll() throws SQLException;
    PedidoDonacion findById(int id) throws SQLException;
    void delete(int id) throws SQLException;
}
