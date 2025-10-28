package ar.edu.unrn.seminario.modelo;

public class Visita {
    private Integer id;
    private String visitante;
    private String fecha;
    private String motivo;
    private boolean confirmada;

    public Visita(Integer id, String visitante, String fecha, String motivo, boolean confirmada) {
        this.id = id;
        this.visitante = visitante;
        this.fecha = fecha;
        this.motivo = motivo;
        this.confirmada = confirmada;
    }

    public Integer getId() {
        return id;
    }

    public String getVisitante() {
        return visitante;
    }

    public String getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public void confirmar() {
        this.confirmada = true;
    }

    public void cancelar() {
        this.confirmada = false;
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
