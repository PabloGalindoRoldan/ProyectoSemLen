package ar.edu.unrn.seminario.modelo;

import ar.edu.unrn.seminario.exception.DomainValidationException;

public class Donacion {
    private TipoDonacion tipoDonacion;
    private String categoria;
    private int puntaje;
    private int id;

    public Donacion(TipoDonacion tipoDonacion, String categoria, int puntaje) {
        this.tipoDonacion = tipoDonacion;
        this.categoria = categoria;
        this.puntaje = puntaje;
    }

    // --- Getters y Setters ---

    public TipoDonacion getTipoDonacion() {
        return tipoDonacion;
    }

    public void setTipoDonacion(TipoDonacion tipoDonacion) {
        this.tipoDonacion = tipoDonacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void validate() {
        if (this.tipoDonacion == null)
            throw new DomainValidationException("El tipo de donación es obligatorio");
        if (this.categoria == null || this.categoria.trim().isEmpty())
            throw new DomainValidationException("La categoría de la donación es obligatoria");
        if (this.puntaje < 0)
            throw new DomainValidationException("El puntaje de la donación no puede ser negativo");
    }

    // hashCode y equals para comparar objetos Donacion
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categoria == null) ? 0 : categoria.hashCode());
        result = prime * result + ((tipoDonacion == null) ? 0 : tipoDonacion.hashCode());
        result = prime * result + puntaje;
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
        Donacion otro = (Donacion) obj;

        if (categoria == null) {
            if (otro.categoria != null)
                return false;
        } else if (!categoria.equals(otro.categoria))
            return false;

        if (tipoDonacion != otro.tipoDonacion)
            return false;

        if (puntaje != otro.puntaje)
            return false;

        return true;
    }
}
