package ar.edu.unrn.seminario.dto;

public class DonacionDTO {
    private String tipoDonacion;
    private String categoria;
    private int puntaje;

    public DonacionDTO(String tipoDonacion, String categoria, int puntaje) {
        this.tipoDonacion = tipoDonacion;
        this.categoria = categoria;
        this.puntaje = puntaje;
    }

    public String getTipoDonacion() {
        return tipoDonacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setTipoDonacion(String tipoDonacion) {
        this.tipoDonacion = tipoDonacion;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
}