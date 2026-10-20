package enums;


public enum EstadoOrden {
    PENDIENTE("Pendiente"),
    ASIGNADA("Asignada"),
    EN_ATENCION("En atención"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoOrden(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
