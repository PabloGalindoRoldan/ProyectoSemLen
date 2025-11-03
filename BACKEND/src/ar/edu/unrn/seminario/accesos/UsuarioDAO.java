package ar.edu.unrn.seminario.accesos;

import java.util.List;

import ar.edu.unrn.seminario.modelo.Usuario;

public interface UsuarioDAO {
    void create(Usuario usuario) throws Exception;
    void update(Usuario usuario) throws Exception;
    void remove(String username) throws Exception;
    void remove(Usuario usuario) throws Exception;
    Usuario find(String username) throws Exception;
    List<Usuario> findAll() throws Exception;
}
