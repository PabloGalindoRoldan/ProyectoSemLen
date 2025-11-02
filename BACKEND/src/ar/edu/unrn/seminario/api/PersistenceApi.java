package ar.edu.unrn.seminario.api;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unrn.seminario.accesos.UsuarioDAO;
import ar.edu.unrn.seminario.accesos.UsuarioDAOJDBC;
import ar.edu.unrn.seminario.dto.RolDTO;
import ar.edu.unrn.seminario.dto.UsuarioDTO;
import ar.edu.unrn.seminario.dto.PedidoDonacionDTO;
import ar.edu.unrn.seminario.dto.DonacionDTO;
import ar.edu.unrn.seminario.dto.OrdenRetiroDTO;
import ar.edu.unrn.seminario.dto.VisitaDTO;
import ar.edu.unrn.seminario.modelo.Rol;
import ar.edu.unrn.seminario.modelo.Usuario;

public class PersistenceApi implements IApi {

    private UsuarioDAO usuarioDAO = new UsuarioDAOJDBC();

    // Roles persistence not implemented in this exercise

    @Override
    public void registrarUsuario(String username, String password, String email, String nombre, Integer rol) {
        try {
            Rol r = null;
            if (rol != null) {
                r = new Rol();
                r.setCodigo(rol);
            }
            Usuario usuario = new Usuario(username, password, nombre, email, r);
            // by default new users are active
            usuario.activar();
            usuarioDAO.create(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Error registering usuario", e);
        }
    }

    @Override
    public UsuarioDTO obtenerUsuario(String username) {
        try {
            Usuario u = usuarioDAO.find(username);
            if (u == null) return null;
            String rolNombre = u.getRol() != null ? String.valueOf(u.getRol().getCodigo()) : null;
            return new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(), rolNombre, u.isActivo(), u.obtenerEstado());
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining usuario", e);
        }
    }

    @Override
    public void eliminarUsuario(String username) {
        try {
            usuarioDAO.remove(username);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting usuario", e);
        }
    }

    @Override
    public List<RolDTO> obtenerRoles() {
        throw new UnsupportedOperationException("obtenerRoles not implemented in PersistenceApi");
    }

    @Override
    public List<RolDTO> obtenerRolesActivos() {
        throw new UnsupportedOperationException("obtenerRolesActivos not implemented in PersistenceApi");
    }

    @Override
    public void guardarRol(Integer codigo, String descripcion, boolean estado) {
        throw new UnsupportedOperationException("guardarRol not implemented in PersistenceApi");
    }

    @Override
    public RolDTO obtenerRolPorCodigo(Integer codigo) {
        throw new UnsupportedOperationException("obtenerRolPorCodigo not implemented in PersistenceApi");
    }

    @Override
    public void activarRol(Integer codigo) {
        throw new UnsupportedOperationException("activarRol not implemented in PersistenceApi");
    }

    @Override
    public void desactivarRol(Integer codigo) {
        throw new UnsupportedOperationException("desactivarRol not implemented in PersistenceApi");
    }

    @Override
    public List<UsuarioDTO> obtenerUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            List<UsuarioDTO> dtos = new ArrayList<>();
            for (Usuario u : usuarios) {
                String rolNombre = u.getRol() != null ? String.valueOf(u.getRol().getCodigo()) : null;
                dtos.add(new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(), rolNombre, u.isActivo(), u.obtenerEstado()));
            }
            return dtos;
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining usuarios", e);
        }
    }

    @Override
    public void activarUsuario(String username) {
        try {
            Usuario u = usuarioDAO.find(username);
            if (u != null) {
                u.activar();
                usuarioDAO.update(u);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error activating usuario", e);
        }
    }

    @Override
    public void desactivarUsuario(String username) {
        try {
            Usuario u = usuarioDAO.find(username);
            if (u != null) {
                u.desactivar();
                usuarioDAO.update(u);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deactivating usuario", e);
        }
    }

    // Pedidos de donacion - not implemented here
    @Override
    public void crearPedidoDonacion(Integer id, String descripcion, String observaciones, boolean necesitaVehiculo,
            String donanteUsername, List<DonacionDTO> donaciones, boolean activo) {
        throw new UnsupportedOperationException("crearPedidoDonacion not implemented in PersistenceApi");
    }

    @Override
    public List<PedidoDonacionDTO> obtenerPedidosDonacion() {
        throw new UnsupportedOperationException("obtenerPedidosDonacion not implemented in PersistenceApi");
    }

    @Override
    public PedidoDonacionDTO obtenerPedidoDonacionPorId(Integer id) {
        throw new UnsupportedOperationException("obtenerPedidoDonacionPorId not implemented in PersistenceApi");
    }

    @Override
    public void eliminarPedidoDonacion(Integer id) {
        throw new UnsupportedOperationException("eliminarPedidoDonacion not implemented in PersistenceApi");
    }

    // Ordenes de retiro - not implemented
    @Override
    public void crearOrdenRetiro(Integer id, Integer pedidoId, String voluntarioUsername, String estado) {
        throw new UnsupportedOperationException("crearOrdenRetiro not implemented in PersistenceApi");
    }

    @Override
    public List<OrdenRetiroDTO> obtenerOrdenesRetiro() {
        throw new UnsupportedOperationException("obtenerOrdenRetiro not implemented in PersistenceApi");
    }

    @Override
    public OrdenRetiroDTO obtenerOrdenRetiroPorId(Integer id) {
        throw new UnsupportedOperationException("obtenerOrdenRetiroPorId not implemented in PersistenceApi");
    }

    @Override
    public void eliminarOrdenRetiro(Integer id) {
        throw new UnsupportedOperationException("eliminarOrdenRetiro not implemented in PersistenceApi");
    }

    // Visitas - not implemented
    @Override
    public void crearVisita(VisitaDTO visita) {
        throw new UnsupportedOperationException("crearVisita not implemented in PersistenceApi");
    }

    @Override
    public List<VisitaDTO> obtenerVisitas() {
        throw new UnsupportedOperationException("obtenerVisitas not implemented in PersistenceApi");
    }

    @Override
    public VisitaDTO obtenerVisitaPorId(Integer id) {
        throw new UnsupportedOperationException("obtenerVisitaPorId not implemented in PersistenceApi");
    }

    @Override
    public void cancelarVisita(Integer id) {
        throw new UnsupportedOperationException("cancelarVisita not implemented in PersistenceApi");
    }
}
