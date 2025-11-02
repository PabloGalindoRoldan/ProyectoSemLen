package ar.edu.unrn.seminario.modelo;

public class Donacion {
    private TipoDonacion tipoDonacion;
    private String categoria;
    private int puntaje;

    public Donacion(TipoDonacion tipoDonacion, String categoria, int puntaje) {
        this.tipoDonacion = tipoDonacion;
        this.categoria = categoria;
        this.puntaje = puntaje;
    }

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

    /*@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categoria == null) ? 0 : categoria.hashCode());
        result = prime * result + ((tipoDonacion == null) ? 0 : tipoDonacion.hashCode());
        result = prime * result + puntaje;
        return result;
    }*/

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
