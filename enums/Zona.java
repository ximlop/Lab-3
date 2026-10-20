package enums;


public enum Zona {
    METROPOLITANA("Metropolitana"),
    PERIFERICA("Periférica");

    private final String descripcion;

    Zona(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
