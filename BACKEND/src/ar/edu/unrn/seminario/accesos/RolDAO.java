package ar.edu.unrn.seminario.accesos;

import java.util.List;

import ar.edu.unrn.seminario.modelo.Rol;

public interface RolDAO {
    void create(Rol rol) throws Exception;
    void update(Rol rol) throws Exception;
    void remove(Integer codigo) throws Exception;
    void remove(Rol rol) throws Exception;
    Rol find(Integer codigo) throws Exception;
    List<Rol> findAll() throws Exception;
    List<Rol> findAllActive() throws Exception;
}
