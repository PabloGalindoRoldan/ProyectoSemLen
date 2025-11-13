package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.Visita;
import java.sql.SQLException;
import java.util.List;

public interface VisitaDAO {
    int create(Visita visita) throws SQLException;
    List<Visita> findAll() throws SQLException;
    Visita findById(int id) throws SQLException;
    List<Visita> listarPorOrden(int ordenId) throws SQLException;
    void cancelar(int id) throws SQLException;
    void delete(int id) throws SQLException;
}
