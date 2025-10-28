package ar.edu.unrn.seminario.modelo;
import java.time.LocalDateTime;

public class PedidoDonacion {
    private Integer id;
    private String descripcion;
    private String solicitante;
    private String observaciones;
    private boolean necesitaVehiculo;
    private LocalDateTime fechaCreacion;
    private Usuario donante; 
    
	public PedidoDonacion(Integer id, String descripcion, String solicitante, String observaciones, boolean necesitaVehiculo, Usuario donante) {
		this.id = id;
		this.descripcion = descripcion;
		this.solicitante = solicitante;
		this.observaciones = observaciones;
		this.necesitaVehiculo = necesitaVehiculo;
		this.fechaCreacion = LocalDateTime.now();
		this.donante = donante;
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
 
	public boolean necesitaVehiculo() {
		return necesitaVehiculo;
	}

	public Usuario getDonante() {
		return donante;
	}

	public void setDonante(Usuario donante) {
		this.donante = donante;
	}
	
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
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
        PedidoDonacion other = (PedidoDonacion) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}