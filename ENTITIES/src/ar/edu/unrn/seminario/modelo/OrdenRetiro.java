package ar.edu.unrn.seminario.modelo;

public class OrdenRetiro {
    private Integer id;
    private PedidoDonacion pedido;
    private String fecha;
    private String responsable;
    private boolean activo;

    public OrdenRetiro(Integer id, PedidoDonacion pedido, String fecha, String responsable, boolean activo) {
        this.id = id;
        this.pedido = pedido;
        this.fecha = fecha;
        this.responsable = responsable;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public PedidoDonacion getPedido() {
        return pedido;
    }

    public String getFecha() {
        return fecha;
    }

    public String getResponsable() {
        return responsable;
    }

    public boolean isActivo() {
        return activo;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
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
        OrdenRetiro other = (OrdenRetiro) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
