package modelo;
import enums.Zona;



public class InstalacionElectricaCertificada extends Instalacion {

    public static final double COSTO_CERTIFICADO = 120.0;
    public static final double PORCENTAJE_GARANTIA = 0.05;

    private final double costoMateriales;

    public InstalacionElectricaCertificada(String codigo, String nombreCliente, String direccion, double horasEstimadas, Zona zona, int cantidadPuntos, double costoMateriales) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona, cantidadPuntos);
        if (Double.isNaN(costoMateriales) || Double.isInfinite(costoMateriales) || costoMateriales <= 0) {
            throw new IllegalArgumentException("El valor de los materiales debe ser un número mayor que cero.");
        }
        this.costoMateriales = costoMateriales;
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + COSTO_CERTIFICADO + costoMateriales * PORCENTAJE_GARANTIA;
    }

    @Override
    public int diasAtencion() {
        return super.diasAtencion() + 2;
    }

    @Override
    public int visitasPermitidas() {
        return 2;
    }

    @Override
    public String getCategoria() {
        return "Instalación eléctrica certificada";
    }

    @Override
    protected String detalleEspecifico() {
        return super.detalleEspecifico() + String.format(java.util.Locale.US,
                " | Materiales: Q%.2f", costoMateriales);
    }

    public double getCostoMateriales() {
        return costoMateriales;
    }
}
