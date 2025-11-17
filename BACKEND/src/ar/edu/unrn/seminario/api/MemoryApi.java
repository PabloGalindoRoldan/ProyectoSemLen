/*package ar.edu.unrn.seminario.api;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unrn.seminario.dto.RolDTO;
import ar.edu.unrn.seminario.dto.UsuarioDTO;
import ar.edu.unrn.seminario.dto.PedidoDonacionDTO;
import ar.edu.unrn.seminario.dto.DonacionDTO;
import ar.edu.unrn.seminario.dto.OrdenRetiroDTO;
import ar.edu.unrn.seminario.dto.VisitaDTO;
import ar.edu.unrn.seminario.dto.ArticuloDTO;
import ar.edu.unrn.seminario.modelo.Rol;
import ar.edu.unrn.seminario.modelo.Usuario;

public class MemoryApi implements IApi {

	private ArrayList<Rol> roles = new ArrayList();
	private ArrayList<Usuario> usuarios = new ArrayList<>();
	private ArrayList<PedidoDonacionDTO> pedidos = new ArrayList<>();
	private ArrayList<OrdenRetiroDTO> ordenes = new ArrayList<>();
	private ArrayList<VisitaDTO> visitas = new ArrayList<>();
	private int nextPedidoId = 1;
	private int nextOrdenId = 1;
	private int nextVisitaId = 1;

	public MemoryApi() {

		// datos iniciales
		this.roles.add(new Rol(1, "ADMIN"));
		this.roles.add(new Rol(2, "ESTUDIANTE"));
		this.roles.add(new Rol(3, "INVITADO"));
		this.roles.add(new Rol(4, "DONANTE"));
		this.roles.add(new Rol(5, "VOLUNTARIO"));
		//inicializarUsuarios();
	}
/*
	private void inicializarUsuarios() {
		registrarUsuario("admin", "1234", "admin@unrn.edu.ar", "Admin", 1);
		registrarUsuario("ldifabio", "4", "ldifabio@unrn.edu.ar", "Lucas", 2);
		registrarUsuario("bjgorosito", "1234", "bjgorosito@unrn.edu.ar", "Bruno", 3);
		registrarUsuario("pgalindo", "5678", "pablogalindo90@gmail.com", "Pablo", 4);
		registrarUsuario("mvoluntario", "v123", "vol@example.com", "Voluntario", 5);

	}
	
	
	@Override
	public void registrarUsuario(String username, String password, String email, String nombre, Integer rol) {

		Rol role = this.buscarRol(rol);
		Usuario usuario = new Usuario(username, password, nombre, email, role);
		this.usuarios.add(usuario);

	}

	@Override
	public List<UsuarioDTO> obtenerUsuarios() {
		List<UsuarioDTO> dtos = new ArrayList<>();
		for (Usuario u : this.usuarios) {
			dtos.add(new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(),
					u.getRol().getNombre(), u.isActivo(), u.obtenerEstado()));
		}
		return dtos;
	}

	@Override
	public UsuarioDTO obtenerUsuario(String username) {
		for (Usuario u : usuarios) {
			if (u.getUsuario().equals(username)) {
				return new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(), u.getRol().getNombre(), u.isActivo(), u.obtenerEstado());
			}
		}
		return null;
	}

	@Override
	public void eliminarUsuario(String username) {
		Usuario u = buscarUsuario(username);
		if (u != null) this.usuarios.remove(u);
	}

	@Override
	public List<RolDTO> obtenerRoles() {
		List<RolDTO> dtos = new ArrayList<>();
		for (Rol r : this.roles) {
			dtos.add(new RolDTO(r.getCodigo(), r.getNombre()));
		}
		return dtos;
	}

	@Override
	public List<RolDTO> obtenerRolesActivos() {
		List<RolDTO> dtos = new ArrayList<>();
		for (Rol r : this.roles) {
			if (r.isActivo())
				dtos.add(new RolDTO(r.getCodigo(), r.getNombre()));
		}
		return dtos;
	}

	@Override
	public void guardarRol(Integer codigo, String descripcion, boolean estado) {
		Rol rol = new Rol(codigo, descripcion);
		this.roles.add(rol);
	}

	@Override
	public RolDTO obtenerRolPorCodigo(Integer codigo) {
		for (Rol r : roles) {
			if (r.getCodigo().equals(codigo)) return new RolDTO(r.getCodigo(), r.getNombre());
		}
		return null;
	}

	@Override
	public void activarRol(Integer codigo) {
		for (Rol r : roles) if (r.getCodigo().equals(codigo)) r.activar();
	}

	@Override
	public void desactivarRol(Integer codigo) {
		for (Rol r : roles) if (r.getCodigo().equals(codigo)) r.desactivar();
	}

	@Override
	public void activarUsuario(String usuario) {
		Usuario user = this.buscarUsuario(usuario);
		if (user != null) user.activar();
	}

	@Override
	public void desactivarUsuario(String usuario) {
		Usuario user = this.buscarUsuario(usuario);
		if (user != null) user.desactivar();
	}

	private Rol buscarRol(Integer codigo) {
		for (Rol rol : roles) {
			if (rol.getCodigo().equals(codigo))
				return rol;
		}
		return null;
	}

	private Usuario buscarUsuario(String usuario) {
		for (Usuario user : usuarios) {
			if (user.getUsuario().equals(usuario))
				return user;
		}
		return null;
	}

	// ---- Pedidos de donacion ----
	@Override
	public void crearPedidoDonacion(Integer id, String descripcion, String observaciones,
					boolean necesitaVehiculo, String donanteUsername, List<DonacionDTO> donaciones, boolean activo) {
		Usuario donante = buscarUsuario(donanteUsername);
		if (donante == null) {
			throw new IllegalArgumentException("Donante no encontrado: " + donanteUsername);
		}
		if (donante.getRol() == null || !"DONANTE".equals(donante.getRol().getNombre())) {
			throw new IllegalArgumentException("Usuario no tiene rol DONANTE: " + donanteUsername);
		}

		int assignedId = (id == null || id == 0) ? nextPedidoId++ : id;
		PedidoDonacionDTO dto = new PedidoDonacionDTO(assignedId, descripcion, observaciones,
				necesitaVehiculo, donanteUsername, donaciones, activo);
		this.pedidos.add(dto);
	}

	@Override
	public List<PedidoDonacionDTO> obtenerPedidosDonacion() {
		return new ArrayList<>(this.pedidos);
	}

	@Override
	public PedidoDonacionDTO obtenerPedidoDonacionPorId(Integer id) {
		for (PedidoDonacionDTO p : this.pedidos) {
			if (p.getId().equals(id))
				return p;
		}
		return null;
	}

	@Override
	public void eliminarPedidoDonacion(Integer id) {
		PedidoDonacionDTO encontrado = null;
		for (PedidoDonacionDTO p : this.pedidos) {
			if (p.getId().equals(id)) {
				encontrado = p;
				break;
			}
		}
		if (encontrado != null)
			this.pedidos.remove(encontrado);
	}

	// ---- Ordenes de retiro ----
	@Override
	public void crearOrdenRetiro(Integer id, Integer pedidoId, String voluntarioUsername, String estado) {

		PedidoDonacionDTO pedido = obtenerPedidoDonacionPorId(pedidoId);
		if (pedido == null) {
			throw new IllegalArgumentException("Pedido no encontrado: " + pedidoId);
		}
		
		Usuario voluntario = buscarUsuario(voluntarioUsername);
		if (voluntario == null) {
			throw new IllegalArgumentException("Voluntario no encontrado: " + voluntarioUsername);
		}
		if (voluntario.getRol() == null || !"VOLUNTARIO".equals(voluntario.getRol().getNombre())) {
			throw new IllegalArgumentException("Usuario no tiene rol VOLUNTARIO: " + voluntarioUsername);
		}

		int assignedId = (id == null || id == 0) ? nextOrdenId++ : id;
		OrdenRetiroDTO dto = new OrdenRetiroDTO(assignedId, pedidoId, voluntarioUsername, estado == null ? "PENDIENTE" : estado);
		this.ordenes.add(dto);
	}

	@Override
	public List<OrdenRetiroDTO> obtenerOrdenesRetiro() {
		return new ArrayList<>(this.ordenes);
	}

	@Override
	public OrdenRetiroDTO obtenerOrdenRetiroPorId(Integer id) {
		for (OrdenRetiroDTO o : this.ordenes) {
			if (o.getIdOrdenes().equals(id)) return o;
		}
		return null;
	}

	@Override
	public void eliminarOrdenRetiro(Integer id) {
		OrdenRetiroDTO encontrado = null;
		for (OrdenRetiroDTO o : this.ordenes) {
			if (o.getIdOrdenes().equals(id)) {
				encontrado = o; break;
			}
		}
		if (encontrado != null) {
			this.ordenes.remove(encontrado);
			List<VisitaDTO> toRemove = new ArrayList<>();
			for (VisitaDTO v : this.visitas) {
				if (v.getOrdenRetiroId() != null && v.getOrdenRetiroId().equals(id)) toRemove.add(v);
			}
			for (VisitaDTO v : toRemove) this.visitas.remove(v);
		}
	}

	// ---- Visitas ----
	@Override
	public void crearVisita(VisitaDTO visita) {
		if (visita == null) throw new IllegalArgumentException("Visita nula");
		int assignedId = (visita.getId() == null || visita.getId() == 0) ? nextVisitaId++ : visita.getId();
		visita.setId(assignedId);

		if (visita.getOrdenRetiroId() != null) {
			OrdenRetiroDTO orden = obtenerOrdenRetiroPorId(visita.getOrdenRetiroId());
			if (orden == null) throw new IllegalArgumentException("Orden de retiro no encontrada: " + visita.getOrdenRetiroId());

			orden.addVisita(visita);

			if (visita.isVisitaFinal()) {
				orden.setEstado("COMPLETADO");
			}
		}
		this.visitas.add(visita);
	}

	@Override
	public List<VisitaDTO> obtenerVisitas() {
		return new ArrayList<>(this.visitas);
	}

	@Override
	public VisitaDTO obtenerVisitaPorId(Integer id) {
		for (VisitaDTO v : this.visitas) if (v.getId().equals(id)) return v;
		return null;
	}

	@Override
	public void cancelarVisita(Integer id) {
		VisitaDTO v = obtenerVisitaPorId(id);
		if (v != null) v.setConfirmada(false);
	}

}*/