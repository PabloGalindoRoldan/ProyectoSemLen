package ar.edu.unrn.seminario.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDonacionDTO {
    private Integer id;
    private String descripcion;
    private String solicitante;
    private String observaciones;
    private boolean necesitaVehiculo;
    private String donanteUsername;
    private LocalDateTime fechaCreacion;
    private boolean activo;
    private List<DonacionDTO> donaciones = new ArrayList<>();
    private int puntajeTotal = 0;

    public PedidoDonacionDTO(Integer id, String descripcion, String solicitante, String observaciones,
            boolean necesitaVehiculo, String donanteUsername, List<DonacionDTO> donaciones, boolean activo) {
        this.id = id;
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.observaciones = observaciones;
        this.necesitaVehiculo = necesitaVehiculo;
        this.donanteUsername = donanteUsername;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = activo;

        if (donaciones != null) {
            this.donaciones.addAll(donaciones);
            int total = 0;
            for (DonacionDTO d : donaciones) {
                total += d.getPuntaje();
            }
            this.puntajeTotal = total;
        }
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

    public List<DonacionDTO> getDonaciones() {
        return new ArrayList<>(this.donaciones);
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setDonanteUsername(String donanteUsername) {
        this.donanteUsername = donanteUsername;
    }
}