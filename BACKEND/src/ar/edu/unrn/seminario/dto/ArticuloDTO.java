package ar.edu.unrn.seminario.dto;

public class ArticuloDTO {
    private String nombre;
    private int cantidad;
    private String tipoDonacion; 
    public ArticuloDTO(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public ArticuloDTO(String nombre, int cantidad, String tipoDonacion) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.tipoDonacion = tipoDonacion;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getTipoDonacion() {
        return tipoDonacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setTipoDonacion(String tipoDonacion) {
        this.tipoDonacion = tipoDonacion;
    }
}