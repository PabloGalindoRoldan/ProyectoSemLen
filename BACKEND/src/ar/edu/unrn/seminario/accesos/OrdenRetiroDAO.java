package ar.edu.unrn.seminario.accesos;

import ar.edu.unrn.seminario.modelo.OrdenRetiro;
import java.sql.SQLException;
import java.util.List;

public interface OrdenRetiroDAO {
    int create(OrdenRetiro orden) throws SQLException;
    List<OrdenRetiro> findAll() throws SQLException;
    OrdenRetiro findById(int id) throws SQLException;
    void delete(int id) throws SQLException;
    void update(OrdenRetiro orden) throws SQLException;
}
