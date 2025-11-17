package ar.edu.unrn.seminario.api;

import java.util.List;

import ar.edu.unrn.seminario.dto.RolDTO;
import ar.edu.unrn.seminario.dto.UsuarioDTO;
import ar.edu.unrn.seminario.dto.PedidoDonacionDTO;
import ar.edu.unrn.seminario.dto.DonacionDTO;
import ar.edu.unrn.seminario.dto.OrdenRetiroDTO;
import ar.edu.unrn.seminario.dto.VisitaDTO;

public interface IApi {

	void registrarUsuario(String username, String password, String email, String nombre, Integer rol);

	UsuarioDTO obtenerUsuario(String username);

	void eliminarUsuario(String username);

	List<RolDTO> obtenerRoles();

	List<RolDTO> obtenerRolesActivos();

	void guardarRol(Integer codigo, String descripcion, boolean estado); // crear el objeto de dominio Rol

	RolDTO obtenerRolPorCodigo(Integer codigo); // recuperar el rol almacenado

	void activarRol(Integer codigo); // recuperar el objeto Rol, implementar el comportamiento de estado.

	void desactivarRol(Integer codigo); // recuperar el objeto Rol, imp

	List<UsuarioDTO> obtenerUsuarios(); // recuperar todos los usuarios

	void activarUsuario(String username); // recuperar el objeto Usuario, implementar el comportamiento de estado.

	void desactivarUsuario(String username); // recuperar el objeto Usuario, implementar el comportamiento de estado.

	// Pedidos de donacion
	void crearPedidoDonacion(Integer id, String descripcion, String observaciones, boolean necesitaVehiculo, String donanteUsername, List<DonacionDTO> donaciones, boolean activo);
	List<PedidoDonacionDTO> obtenerPedidosDonacion();
	PedidoDonacionDTO obtenerPedidoDonacionPorId(Integer id);
	void eliminarPedidoDonacion(Integer id);

	// Ordenes de retiro
	void crearOrdenRetiro(Integer id, Integer pedidoId, String voluntarioUsername, String estado);
	List<OrdenRetiroDTO> obtenerOrdenesRetiro();
	OrdenRetiroDTO obtenerOrdenRetiroPorId(Integer id);
	void eliminarOrdenRetiro(Integer id);

	// Visitas
	void crearVisita(VisitaDTO visita);
	List<VisitaDTO> obtenerVisitas();
	VisitaDTO obtenerVisitaPorId(Integer id);
	void cancelarVisita(Integer id);
}