package ar.edu.unrn.seminario.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VisitaDTO {
    private Integer id;
    private String visitante;
    private LocalDateTime fechaHora;
    private String motivo;
    private boolean confirmada;

    private int cantidadBienesRecogidos;
    private List<ArticuloDTO> articulosRecogidos = new ArrayList<>();
    private String observaciones;
    private Integer ordenRetiroId;
    private boolean visitaFinal;

    public VisitaDTO(Integer id, String visitante, LocalDateTime fechaHora, String motivo, boolean confirmada,
            int cantidadBienesRecogidos, List<ArticuloDTO> articulosRecogidos, String observaciones, Integer ordenRetiroId, boolean visitaFinal) {
        this.id = id;
        this.visitante = visitante;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.confirmada = confirmada;
        this.cantidadBienesRecogidos = cantidadBienesRecogidos;
        if (articulosRecogidos != null) this.articulosRecogidos.addAll(articulosRecogidos);
        this.observaciones = observaciones;
        this.ordenRetiroId = ordenRetiroId;
        this.visitaFinal = visitaFinal;
    }

    public Integer getId() { return id; }
    public String getVisitante() { return visitante; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
    public boolean isConfirmada() { return confirmada; }
    public int getCantidadBienesRecogidos() { return cantidadBienesRecogidos; }
    public List<ArticuloDTO> getArticulosRecogidos() { return new ArrayList<>(this.articulosRecogidos); }
    public String getObservaciones() { return observaciones; }
    public Integer getOrdenRetiroId() { return ordenRetiroId; }
    public boolean isVisitaFinal() { return visitaFinal; }

    public void setId(Integer id) { this.id = id; }
    public void setOrdenRetiroId(Integer ordenRetiroId) { this.ordenRetiroId = ordenRetiroId; }

    public void setConfirmada(boolean confirmada) { this.confirmada = confirmada; }
    public void setCantidadBienesRecogidos(int cantidad) { this.cantidadBienesRecogidos = cantidad; }
    public void setArticulosRecogidos(List<ArticuloDTO> articulos) { this.articulosRecogidos.clear(); if (articulos != null) this.articulosRecogidos.addAll(articulos); }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setVisitaFinal(boolean visitaFinal) { this.visitaFinal = visitaFinal; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}