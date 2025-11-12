package ar.edu.unrn.seminario.modelo;

import ar.edu.unrn.seminario.exception.DomainValidationException;

public class Articulo {
    private String nombre;
    private int cantidad;
    private TipoDonacion tipo;

    public Articulo(String nombre, int cantidad) {
        if (nombre == null || nombre.trim().isEmpty()) throw new DomainValidationException("Articulo.nombre is required");
        if (cantidad < 0) throw new DomainValidationException("Articulo.cantidad cannot be negative");
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public Articulo(String nombre, int cantidad, TipoDonacion tipo) {
        if (nombre == null || nombre.trim().isEmpty()) throw new DomainValidationException("Articulo.nombre is required");
        if (cantidad < 0) throw new DomainValidationException("Articulo.cantidad cannot be negative");
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) throw new DomainValidationException("Articulo.nombre is required");
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 0) throw new DomainValidationException("Articulo.cantidad cannot be negative");
        this.cantidad = cantidad;
    }

    public TipoDonacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoDonacion tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
        result = prime * result + cantidad;
        result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
        Articulo other = (Articulo) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        if (cantidad != other.cantidad)
            return false;
        if (tipo != other.tipo)
            return false;
        return true;
    }
}