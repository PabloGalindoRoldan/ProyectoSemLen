package ar.edu.unrn.seminario.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenRetiro {
    private Integer idOrdenes;
    private LocalDateTime fechaGeneracion;
    private EstadoOrden estado;
    private List<Visita> visitas = new ArrayList<>();
    private PedidoDonacion pedido;
    private Usuario voluntario;

    public OrdenRetiro(Integer idOrdenes, PedidoDonacion pedido, Usuario voluntario) {
        this.idOrdenes = idOrdenes;
        this.pedido = pedido;
        this.voluntario = voluntario;
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = EstadoOrden.PENDIENTE;
    }

    public OrdenRetiro(Integer idOrdenes, PedidoDonacion pedido, Usuario voluntario, LocalDateTime fechaGeneracion, EstadoOrden estado, List<Visita> visitas) {
        this.idOrdenes = idOrdenes;
        this.pedido = pedido;
        this.voluntario = voluntario;
        this.fechaGeneracion = fechaGeneracion != null ? fechaGeneracion : LocalDateTime.now();
        this.estado = estado != null ? estado : EstadoOrden.PENDIENTE;
        if (visitas != null) this.visitas.addAll(visitas);
    }

    public Integer getIdOrdenes() {
        return idOrdenes;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public List<Visita> getVisitas() {
        return new ArrayList<>(this.visitas);
    }

    public void setVisitas(List<Visita> visitas) {
        this.visitas.clear();
        if (visitas != null) this.visitas.addAll(visitas);
    }

    public void addVisita(Visita visita) {
        if (visita != null) this.visitas.add(visita);
    }

    public void removeVisita(Visita visita) {
        this.visitas.remove(visita);
    }

    public PedidoDonacion getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDonacion pedido) {
        this.pedido = pedido;
    }

    public Usuario getVoluntario() {
        return voluntario;
    }

    public void setVoluntario(Usuario voluntario) {
        this.voluntario = voluntario;
    }

    // state transition helpers
    public void iniciar() {
        this.estado = EstadoOrden.EN_EJECUCION;
    }

    public void completar() {
        this.estado = EstadoOrden.COMPLETADO;
    }

    public void marcarPendiente() {
        this.estado = EstadoOrden.PENDIENTE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idOrdenes == null) ? 0 : idOrdenes.hashCode());
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
        if (idOrdenes == null) {
            if (other.idOrdenes != null)
                return false;
        } else if (!idOrdenes.equals(other.idOrdenes))
            return false;
        return true;
    }
}