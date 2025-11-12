package ar.edu.unrn.seminario.modelo;

import ar.edu.unrn.seminario.exception.DomainValidationException;

public class Rol {
	private Integer codigo;
	private String nombre;
	private boolean activo;

	public Rol() {

	}

	public Rol(Integer codigo, String nombre) {
		if (codigo == null || codigo <= 0)
			throw new DomainValidationException("Rol.codigo must be positive");
		if (nombre == null || nombre.trim().isEmpty())
			throw new DomainValidationException("Rol.nombre is required");
		super();
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		if (codigo == null || codigo <= 0)
			throw new DomainValidationException("Rol.codigo must be positive");
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		if (nombre == null || nombre.trim().isEmpty())
			throw new DomainValidationException("Rol.nombre is required");
		this.nombre = nombre;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public void activar() {
		this.activo = true;
	}

	public void desactivar() {
		this.activo = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		Rol other = (Rol) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Rol [codigo=" + codigo + ", nombre=" + nombre + ", activo=" + activo + "]";
	}

}