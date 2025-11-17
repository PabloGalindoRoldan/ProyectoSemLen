package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Articulo;
import java.sql.SQLException;
import java.util.List;

public interface ArticuloDAO {
    void insertar(Articulo articulo, int visitaId) throws SQLException;
    List<Articulo> listarPorVisita(int visitaId) throws SQLException;
    void eliminarPorOrden(int ordenId) throws SQLException;
}
