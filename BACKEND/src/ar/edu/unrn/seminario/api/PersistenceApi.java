package ar.edu.unrn.seminario.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unrn.seminario.accesos.*;
import ar.edu.unrn.seminario.dto.*;
import ar.edu.unrn.seminario.exception.DomainValidationException;
import ar.edu.unrn.seminario.modelo.*;

public class PersistenceApi implements IApi {

    private UsuarioDAO usuarioDAO = new UsuarioDAOJDBC();
    private RolDAOJDBC rolDAO = new RolDAOJDBC();

    // Capas DAO para las demás entidades
    private PedidoDAO pedidoDAO = new PedidoDAOJDBC();
    private OrdenRetiroDAO ordenDAO = new OrdenRetiroDAOJDBC();
    private VisitaDAO visitaDAO = new VisitaDAOJDBC();
    private DonacionDAO donacionDAO = new DonacionDAOJDBC();

    public PersistenceApi() {
    }

    // ==================== USUARIOS ====================

    @Override
    public void registrarUsuario(String username, String password, String nombre, String email, Integer rol) {
        try {
            Rol r = null;
            if (rol != null) {
                r = new Rol();
                r.setCodigo(rol);
            }
            Usuario usuario = new Usuario(username, password, nombre, email, r);
            usuario.validate(); // validamos antes de guardar
            usuario.activar();
            usuarioDAO.create(usuario);
        } catch (DomainValidationException dve) {
            throw dve; // las validaciones de dominio las dejamos pasar
        } catch (Exception e) {
            // Error genérico
            throw new RuntimeException("Error al registrar el usuario", e);
        }
    }

    @Override
    public UsuarioDTO obtenerUsuario(String username) {
        try {
            Usuario u = usuarioDAO.find(username);
            if (u == null) return null;
            String rolNombre = u.getRol() != null ? u.getRol().getNombre() : null;
            return new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(), rolNombre, u.isActivo(), u.obtenerEstado());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el usuario", e);
        }
    }

    @Override
    public void eliminarUsuario(String username) {
        try {
            usuarioDAO.remove(username);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el usuario", e);
        }
    }

    @Override
    public List<RolDTO> obtenerRoles() {
        try {
            List<RolDTO> res = new ArrayList<>();
            List<Rol> roles = rolDAO.findAll();
            for (Rol r : roles)
                res.add(new RolDTO(r.getCodigo(), r.getNombre(), r.isActivo()));
            return res;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los roles", e);
        }
    }

    @Override
    public List<RolDTO> obtenerRolesActivos() {
        try {
            List<RolDTO> res = new ArrayList<>();
            List<Rol> roles = rolDAO.findAllActive();
            for (Rol r : roles)
                res.add(new RolDTO(r.getCodigo(), r.getNombre(), r.isActivo()));
            return res;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los roles activos", e);
        }
    }

    @Override
    public void guardarRol(Integer codigo, String descripcion, boolean estado) {
        try {
            Rol existing = rolDAO.find(codigo);
            if (existing == null) {
                Rol r = new Rol(codigo, descripcion);
                if (!estado) r.desactivar();
                rolDAO.create(r);
            } else {
                existing.setNombre(descripcion);
                if (estado) existing.activar(); else existing.desactivar();
                rolDAO.update(existing);
            }
        } catch (DomainValidationException dve) {
            throw dve;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el rol", e);
        }
    }

    @Override
    public RolDTO obtenerRolPorCodigo(Integer codigo) {
        try {
            Rol r = rolDAO.find(codigo);
            if (r == null) return null;
            return new RolDTO(r.getCodigo(), r.getNombre(), r.isActivo());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el rol por código", e);
        }
    }

    @Override
    public void activarRol(Integer codigo) {
        try {
            Rol r = rolDAO.find(codigo);
            if (r != null) {
                r.activar();
                rolDAO.update(r);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al activar el rol", e);
        }
    }

    @Override
    public void desactivarRol(Integer codigo) {
        try {
            Rol r = rolDAO.find(codigo);
            if (r != null) {
                r.desactivar();
                rolDAO.update(r);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al desactivar el rol", e);
        }
    }

    @Override
    public List<UsuarioDTO> obtenerUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            List<UsuarioDTO> dtos = new ArrayList<>();
            for (Usuario u : usuarios) {
                String rolNombre = u.getRol() != null ? u.getRol().getNombre() : null;
                dtos.add(new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(), rolNombre, u.isActivo(), u.obtenerEstado()));
            }
            return dtos;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los usuarios", e);
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
            throw new RuntimeException("Error al activar el usuario", e);
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
            throw new RuntimeException("Error al desactivar el usuario", e);
        }
    }

    // ==================== PEDIDOS ====================

    @Override
    public void crearPedidoDonacion(Integer id, String descripcion, String observaciones, boolean necesitaVehiculo,
            String donanteUsername, List<DonacionDTO> donaciones, boolean activo) {
        try {
            Usuario donante = donanteUsername == null ? null : new Usuario(donanteUsername, null, null, null, null);
            List<Donacion> donList = new ArrayList<>();
            if (donaciones != null) {
                for (DonacionDTO d : donaciones)
                    donList.add(new Donacion(ar.edu.unrn.seminario.modelo.TipoDonacion.valueOf(d.getTipoDonacion()), d.getCategoria(), d.getPuntaje()));
            }
            PedidoDonacion pedido = new PedidoDonacion(id == null ? 0 : id, descripcion, observaciones, necesitaVehiculo, donante, donList);
            pedido.validate();
            pedidoDAO.create(pedido);
        } catch (DomainValidationException dve) {
            throw dve;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el pedido de donación", e);
        }
    }

    @Override
    public List<PedidoDonacionDTO> obtenerPedidosDonacion() {
        try {
            List<PedidoDonacionDTO> result = new ArrayList<>();
            List<PedidoDonacion> pedidos = pedidoDAO.findAll();
            for (PedidoDonacion p : pedidos) {
                List<DonacionDTO> dd = new ArrayList<>();
                if (p.getDonaciones() != null) {
                    for (Donacion d : p.getDonaciones())
                        dd.add(new DonacionDTO(d.getTipoDonacion().name(), d.getCategoria(), d.getPuntaje()));
                }
                String donante = p.getDonante() == null ? null : p.getDonante().getUsuario();
                result.add(new PedidoDonacionDTO(
                    p.getId(),
                    p.getDescripcion(),
                    p.getObservaciones(),
                    p.necesitaVehiculo(),
                    donante,
                    p.getFechaCreacion(),
                    dd,
                    true
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los pedidos de donación", e);
        }
    }


    @Override
    public PedidoDonacionDTO obtenerPedidoDonacionPorId(Integer id) {
        try {
            PedidoDonacion p = pedidoDAO.findById(id);
            if (p == null) return null;
            List<DonacionDTO> dd = new ArrayList<>();
            if (p.getDonaciones() != null)
                for (Donacion d : p.getDonaciones())
                    dd.add(new DonacionDTO(d.getTipoDonacion().name(), d.getCategoria(), d.getPuntaje()));
            String donante = p.getDonante() == null ? null : p.getDonante().getUsuario();
            return new PedidoDonacionDTO(
            	    p.getId(),
            	    p.getDescripcion(),
            	    p.getObservaciones(),
            	    p.necesitaVehiculo(),
            	    donante,
            	    p.getFechaCreacion(),  
            	    dd,
            	    true           
            	);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el pedido por ID", e);
        }
    }

    @Override
    public void eliminarPedidoDonacion(Integer id) {
        try {
            pedidoDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el pedido de donación", e);
        }
    }

    // ==================== ORDENES DE RETIRO ====================

    @Override
    public void crearOrdenRetiro(Integer id, Integer pedidoId, String voluntarioUsername, String estado) {
        try {
            PedidoDonacion pedido = pedidoDAO.findById(pedidoId);
            if (pedido == null)
                throw new RuntimeException("No se encontró el pedido con ID: " + pedidoId);

            Usuario voluntario = null;
            try {
                voluntario = usuarioDAO.find(voluntarioUsername);
            } catch (Exception e) {
                // ignoramos si no se encuentra
            }

            OrdenRetiro orden = new OrdenRetiro(id == null ? 0 : id, pedido, voluntario);
            try {
                orden.validate();
            } catch (NoSuchMethodError ignore) {
            }
            ordenDAO.create(orden);
        } catch (DomainValidationException dve) {
            throw dve;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la orden de retiro", e);
        }
    }

    @Override
    public List<OrdenRetiroDTO> obtenerOrdenesRetiro() {
        try {
            List<OrdenRetiroDTO> result = new ArrayList<>();
            List<OrdenRetiro> ordenes = ordenDAO.findAll();
            for (OrdenRetiro o : ordenes) {
                OrdenRetiroDTO dto = new OrdenRetiroDTO(
                	    o.getIdOrdenes(),
                	    o.getPedido() == null ? null : o.getPedido().getId(),
                	    o.getVoluntario() == null ? null : o.getVoluntario().getUsuario(),
                	    o.getEstado() == null ? null : o.getEstado().name(),
                	    o.getFechaGeneracion()
                	);
                if (o.getVisitas() != null) {
                    for (Visita v : o.getVisitas()) dto.addVisita(mapVisitaToDTO(v));
                }
                result.add(dto);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las órdenes de retiro", e);
        }
    }

    @Override
    public OrdenRetiroDTO obtenerOrdenRetiroPorId(Integer id) {
        try {
            OrdenRetiro o = ordenDAO.findById(id);
            if (o == null) return null;
            OrdenRetiroDTO dto = new OrdenRetiroDTO(
            	    o.getIdOrdenes(),
            	    o.getPedido() == null ? null : o.getPedido().getId(),
            	    o.getVoluntario() == null ? null : o.getVoluntario().getUsuario(),
            	    o.getEstado() == null ? null : o.getEstado().name(),
            	    o.getFechaGeneracion()
            	);
            if (o.getVisitas() != null)
                for (Visita v : o.getVisitas())
                    dto.addVisita(mapVisitaToDTO(v));
            return dto;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener la orden por ID", e);
        }
    }

    @Override
    public void eliminarOrdenRetiro(Integer id) {
        try {
            ordenDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la orden de retiro", e);
        }
    }

    // ==================== VISITAS ====================

    @Override
    public void crearVisita(VisitaDTO visita) {
        try {
            Visita v = new Visita(visita.getId() == null ? 0 : visita.getId(), visita.getVisitante(), visita.getFechaHora(),
                    visita.getMotivo(), visita.isConfirmada(), visita.getCantidadBienesRecogidos(), null,
                    visita.getObservaciones(), null, visita.isVisitaFinal());

            // si viene con una orden asociada, la seteamos
            if (visita.getOrdenRetiroId() != null)
                v.setOrdenRetiro(new ar.edu.unrn.seminario.modelo.OrdenRetiro(visita.getOrdenRetiroId(), null, null));

            // agregamos los artículos recogidos si hay
            if (visita.getArticulosRecogidos() != null) {
                List<ar.edu.unrn.seminario.modelo.Articulo> arts = new ArrayList<>();
                for (ArticuloDTO a : visita.getArticulosRecogidos())
                    arts.add(new ar.edu.unrn.seminario.modelo.Articulo(a.getNombre(), a.getCantidad(),
                            a.getTipoDonacion() == null ? null : ar.edu.unrn.seminario.modelo.TipoDonacion.valueOf(a.getTipoDonacion())));
                v.setArticulosRecogidos(arts);
            }

            try { v.validate(); } catch (NoSuchMethodError ignore) {}
            visitaDAO.create(v);
        } catch (DomainValidationException dve) {
            throw dve;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la visita", e);
        }
    }

    @Override
    public List<VisitaDTO> obtenerVisitas() {
        try {
            List<VisitaDTO> result = new ArrayList<>();
            List<Visita> visitas = visitaDAO.findAll();
            for (Visita v : visitas) result.add(mapVisitaToDTO(v));
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las visitas", e);
        }
    }

    @Override
    public VisitaDTO obtenerVisitaPorId(Integer id) {
        try {
            Visita v = visitaDAO.findById(id);
            if (v == null) return null;
            return mapVisitaToDTO(v);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener la visita por ID", e);
        }
    }

    @Override
    public void cancelarVisita(Integer id) {
        try {
            visitaDAO.cancelar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar la visita", e);
        }
    }

    // ==================== HELPERS ====================

    private VisitaDTO mapVisitaToDTO(Visita v) {
        List<ArticuloDTO> articulos = new ArrayList<>();
        if (v.getArticulosRecogidos() != null) {
            for (ar.edu.unrn.seminario.modelo.Articulo a : v.getArticulosRecogidos())
                articulos.add(new ArticuloDTO(a.getNombre(), a.getCantidad(), a.getTipo() == null ? null : a.getTipo().name()));
        }
        Integer ordenId = v.getOrdenRetiro() == null ? null : v.getOrdenRetiro().getIdOrdenes();
        return new VisitaDTO(v.getId(), v.getVisitante(), v.getFechaHora(), v.getMotivo(), v.isConfirmada(),
                v.getCantidadBienesRecogidos(), articulos, v.getObservaciones(), ordenId, v.isVisitaFinal());
    }
}
