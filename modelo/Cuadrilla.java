package modelo;


public class Cuadrilla {

    private final String codigo;
    private final double capacidadMaximaHoras;
    private final boolean certificadaRefrigeracion;

    public Cuadrilla(String codigo, double capacidadMaximaHoras, boolean certificadaRefrigeracion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de la cuadrilla no puede estar vacío.");
        }
        if (Double.isNaN(capacidadMaximaHoras) || Double.isInfinite(capacidadMaximaHoras)
                || capacidadMaximaHoras <= 0) {
            throw new IllegalArgumentException("La capacidad máxima de horas debe ser un número mayor que cero.");
        }
        this.codigo = codigo.trim();
        this.capacidadMaximaHoras = capacidadMaximaHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadMaximaHoras() {
        return capacidadMaximaHoras;
    }

    public boolean isCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    public String describir() {
        return String.format(java.util.Locale.US,
                "[%s] Capacidad: %.2f h | Certificada en refrigeración: %s",
                codigo, capacidadMaximaHoras, certificadaRefrigeracion ? "Sí" : "No");
    }
}
