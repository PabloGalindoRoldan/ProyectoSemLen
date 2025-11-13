package ar.edu.unrn.seminario.modelo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unrn.seminario.exception.DomainValidationException;

public class PedidoDonacion {
    private Integer id;
    private String descripcion;
    private String observaciones;
    private boolean necesitaVehiculo;
    private LocalDateTime fechaCreacion;
    private Usuario donante; 
    private List<Donacion> donaciones = new ArrayList<>();
    
	public PedidoDonacion(Integer id, String descripcion, String observaciones, boolean necesitaVehiculo, Usuario donante) {
		this.id = id;
		this.descripcion = descripcion;
		this.observaciones = observaciones;
		this.necesitaVehiculo = necesitaVehiculo;
		this.fechaCreacion = LocalDateTime.now();
		this.donante = donante;
	}
	
	public PedidoDonacion(Integer id, String descripcion, String observaciones, boolean necesitaVehiculo,
            Usuario donante, LocalDateTime fechaCreacion, List<Donacion> donaciones) {
		this.id = id;
		this.descripcion = descripcion;
		this.observaciones = observaciones;
		this.necesitaVehiculo = necesitaVehiculo;
		this.donante = donante;
		this.fechaCreacion = (fechaCreacion != null) ? fechaCreacion : LocalDateTime.now();
		if (donaciones != null) {
		this.donaciones.addAll(donaciones);
	}
}

	public PedidoDonacion(Integer id, String descripcion, String observaciones, boolean necesitaVehiculo, Usuario donante, List<Donacion> donaciones) {
		this(id, descripcion, observaciones, necesitaVehiculo, donante);
		if (donaciones != null) {
			this.donaciones.addAll(donaciones);
		}
	}

    public Integer getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
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

	public List<Donacion> getDonaciones() {
		return new ArrayList<>(this.donaciones);
	}

	public void setDonaciones(List<Donacion> donaciones) {
		this.donaciones.clear();
		if (donaciones != null) this.donaciones.addAll(donaciones);
	}

	public void addDonacion(Donacion donacion) {
		if (donacion != null) this.donaciones.add(donacion);
	}

	public void removeDonacion(Donacion donacion) {
		this.donaciones.remove(donacion);
	}

	public int calcularPuntajeTotal() {
		int total = 0;
		for (Donacion d : this.donaciones) {
			total += d.getPuntaje();
		}
		return total;
	}
	
	// validation
	public void validate() {
		if (this.descripcion == null || this.descripcion.trim().isEmpty()) throw new DomainValidationException("PedidoDonacion.descripcion es requerido");
		if (this.donaciones != null) {
			for (Donacion d : this.donaciones) {
				if (d == null) throw new DomainValidationException("PedidoDonacion contiene una Donacion nula");
				d.validate();
			}
		}
		int computed = calcularPuntajeTotal();
		if (computed < 0) throw new DomainValidationException("PedidoDonacion.puntaje total no puede ser negativo");
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