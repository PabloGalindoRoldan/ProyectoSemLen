package ar.edu.unrn.seminario.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenRetiroDTO {
    private Integer idOrdenes;
    private LocalDateTime fechaGeneracion;
    private String estado;
    private List<VisitaDTO> visitas = new ArrayList<>();
    private Integer pedidoId;
    private String voluntarioUsername;

    public OrdenRetiroDTO(Integer idOrdenes, Integer pedidoId, String voluntarioUsername, String estado) {
        this.idOrdenes = idOrdenes;
        this.pedidoId = pedidoId;
        this.voluntarioUsername = voluntarioUsername;
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = estado;
    }

    public Integer getIdOrdenes() {
        return idOrdenes;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<VisitaDTO> getVisitas() {
        return new ArrayList<>(this.visitas);
    }

    public void addVisita(VisitaDTO visita) {
        if (visita != null) this.visitas.add(visita);
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public String getVoluntarioUsername() {
        return voluntarioUsername;
    }

    public void setVoluntarioUsername(String voluntarioUsername) {
        this.voluntarioUsername = voluntarioUsername;
    }
}
