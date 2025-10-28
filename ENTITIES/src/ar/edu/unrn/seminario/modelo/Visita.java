package ar.edu.unrn.seminario.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Visita {
    private Integer id;
    private String visitante;
    private LocalDateTime fechaHora;
    private String motivo;
    private boolean confirmada;
    private int cantidadBienesRecogidos;
    private List<Articulo> articulosRecogidos = new ArrayList<>();
    private String observaciones;
    private OrdenRetiro ordenRetiro;
    private boolean visitaFinal;

    public Visita(Integer id, String visitante, LocalDateTime fechaHora, String motivo, boolean confirmada) {
        this.id = id;
        this.visitante = visitante;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.confirmada = confirmada;
    }

    public Visita(Integer id, String visitante, LocalDateTime fechaHora, String motivo, boolean confirmada,
            int cantidadBienesRecogidos, List<Articulo> articulosRecogidos, String observaciones, OrdenRetiro ordenRetiro, boolean visitaFinal) {
        this(id, visitante, fechaHora, motivo, confirmada);
        this.cantidadBienesRecogidos = cantidadBienesRecogidos;
        if (articulosRecogidos != null) this.articulosRecogidos.addAll(articulosRecogidos);
        this.observaciones = observaciones;
        this.ordenRetiro = ordenRetiro;
        this.visitaFinal = visitaFinal;
    }

    public Integer getId() {
        return id;
    }

    public String getVisitante() {
        return visitante;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public int getCantidadBienesRecogidos() {
        return cantidadBienesRecogidos;
    }

    public List<Articulo> getArticulosRecogidos() {
        return new ArrayList<>(this.articulosRecogidos);
    }

    public String getObservaciones() {
        return observaciones;
    }

    public OrdenRetiro getOrdenRetiro() {
        return ordenRetiro;
    }

    public boolean isVisitaFinal() {
        return visitaFinal;
    }

    public void setConfirmada(boolean confirmada) {
        this.confirmada = confirmada;
    }

    public void setCantidadBienesRecogidos(int cantidad) {
        this.cantidadBienesRecogidos = cantidad;
    }

    public void setArticulosRecogidos(List<Articulo> articulos) {
        this.articulosRecogidos.clear();
        if (articulos != null) this.articulosRecogidos.addAll(articulos);
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setOrdenRetiro(OrdenRetiro ordenRetiro) {
        this.ordenRetiro = ordenRetiro;
    }

    public void setVisitaFinal(boolean visitaFinal) {
        this.visitaFinal = visitaFinal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Visita other = (Visita) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}