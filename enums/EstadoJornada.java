package enums;


public enum EstadoJornada {
    EN_PREPARACION("En preparación"),
    EN_ATENCION("En atención"),
    CERRADA("Cerrada");

    private final String descripcion;

    EstadoJornada(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
