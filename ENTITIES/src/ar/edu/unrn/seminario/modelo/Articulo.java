package ar.edu.unrn.seminario.modelo;

import ar.edu.unrn.seminario.exception.DomainValidationException;

public class Articulo {
    // Atributos principales del artículo
    private String nombre;       
    private int cantidad;        
    private TipoDonacion tipo;   

    public Articulo(String nombre, int cantidad) {
        // Si el nombre es nulo o vacío, tiramos una excepción
        if (nombre == null || nombre.trim().isEmpty())
            throw new DomainValidationException("El nombre del artículo es obligatorio");

        // Si la cantidad es menor que cero, no tiene sentido, también error
        if (cantidad < 0)
            throw new DomainValidationException("La cantidad del artículo no puede ser negativa");

        this.nombre = nombre;
        this.cantidad = cantidad;
    }


    public Articulo(String nombre, int cantidad, TipoDonacion tipo) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new DomainValidationException("El nombre del artículo es obligatorio");
        if (cantidad < 0)
            throw new DomainValidationException("La cantidad del artículo no puede ser negativa");

        this.nombre = nombre;
        this.cantidad = cantidad;
        this.tipo = tipo;
    }

    // --- Getters y Setters ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        // Validamos otra vez, por si alguien intenta dejarlo vacío después de crear el objeto
        if (nombre == null || nombre.trim().isEmpty())
            throw new DomainValidationException("El nombre del artículo es obligatorio");
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        // Mismo control que en el constructor
        if (cantidad < 0)
            throw new DomainValidationException("La cantidad del artículo no puede ser negativa");
        this.cantidad = cantidad;
    }

    public TipoDonacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoDonacion tipo) {
        this.tipo = tipo;
    }

    // --- Métodos utilitarios ---

    @Override
    public int hashCode() {
        // Se genera un número único en base a nombre, cantidad y tipo.
        // Sirve cuando el objeto se usa en estructuras como HashSet o HashMap.
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
