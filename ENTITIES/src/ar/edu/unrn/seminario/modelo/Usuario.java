package ar.edu.unrn.seminario.modelo;

import ar.edu.unrn.seminario.exception.DomainValidationException;

public class Usuario {
	private String usuario;
	private String contrasena;
	private String nombre;
	private String email;
	private Rol rol;
	private boolean activo;

	public Usuario(String usuario, String contrasena, String nombre, String email, Rol rol) {

		this.usuario = usuario;
		this.contrasena = contrasena;
		this.nombre = nombre;
		this.email = email;
		this.rol = rol;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		if (usuario == null || usuario.trim().isEmpty()) throw new DomainValidationException("Usuario.usuario es requerido");
		this.usuario = usuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		if (contrasena == null || contrasena.trim().isEmpty()) throw new DomainValidationException("Usuario.contrasena es requerido");
		this.contrasena = contrasena;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		if (nombre == null || nombre.trim().isEmpty()) throw new DomainValidationException("Usuario.nombre es requerido");
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email.trim().isEmpty()) {
			throw new DomainValidationException("Usuario.email invalido");
		}
		if (email != null && !email.trim().isEmpty()) {
			if (!email.contains("@")) throw new DomainValidationException("Usuario.email invalido");
		}
		this.email = email;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public boolean isActivo() {
		return activo;
	}

	public String obtenerEstado() {
		return isActivo() ? "ACTIVO" : "INACTIVO";
	}

	public void activar() {
		if (!isActivo())
			this.activo = true;
	}

	public void desactivar() {
		if (isActivo())
			this.activo = false;
	}

	// metodo de validacion de entidades
	public void validate() {
		System.out.println(this.usuario + " " + this.contrasena + " " + this.nombre + " " + this.email);
		if (this.usuario == null || this.usuario.trim().isEmpty()) throw new DomainValidationException("Usuario.usuario es requerido");
		if (this.contrasena == null || this.contrasena.trim().isEmpty()) throw new DomainValidationException("Usuario.contrasena es requerido");
		if (this.nombre == null || this.nombre.trim().isEmpty()) throw new DomainValidationException("Usuario.nombre es requerido");
		if (this.email != null && !this.email.trim().isEmpty() && !this.email.contains("@")) throw new DomainValidationException("Usuario.email invalid");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}

}