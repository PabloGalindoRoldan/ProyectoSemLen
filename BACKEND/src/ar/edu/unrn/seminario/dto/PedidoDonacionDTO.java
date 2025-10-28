package ar.edu.unrn.seminario.dto;

import java.time.LocalDateTime;

public class PedidoDonacionDTO {
    private Integer id;
    private String descripcion;
    private String solicitante;
    private String observaciones;
    private boolean necesitaVehiculo;
    private String donanteUsername;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public PedidoDonacionDTO(Integer id, String descripcion, String solicitante, String observaciones,
            boolean necesitaVehiculo, String donanteUsername, boolean activo) {
        this.id = id;
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.observaciones = observaciones;
        this.necesitaVehiculo = necesitaVehiculo;
        this.donanteUsername = donanteUsername;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public boolean isNecesitaVehiculo() {
        return necesitaVehiculo;
    }

    public String getDonanteUsername() {
        return donanteUsername;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
    
    public void setDonanteUsername(String donanteUsername) {
        this.donanteUsername = donanteUsername;
    }
}
