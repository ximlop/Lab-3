package modelo;
import enums.Zona;


public class Instalacion extends Orden {

    public static final double HORAS_POR_PUNTO = 0.75;
    public static final double COSTO_HORA_FACTURABLE = 90.0;

    private final int cantidadPuntos;

    public Instalacion(String codigo, String nombreCliente, String direccion,
                       double horasEstimadas, Zona zona, int cantidadPuntos) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona);
        if (cantidadPuntos <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser un entero positivo.");
        }
        this.cantidadPuntos = cantidadPuntos;
    }

    public double horasPorPuntos() {
        return cantidadPuntos * HORAS_POR_PUNTO;
    }

  
    public double horasFacturables() {
        return Math.max(getHorasEstimadas(), horasPorPuntos());
    }

    @Override
    public double calcularCosto() {
        
        double horasCobradas = Math.ceil(horasFacturables() - 1e-9);
        return tarifaBase() + horasCobradas * COSTO_HORA_FACTURABLE;
    }

    @Override
    public int diasAtencion() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }

    @Override
    public int visitasPermitidas() {
        return 3;
    }

    @Override
    public String getCategoria() {
        return "Instalación";
    }

    @Override
    protected String detalleEspecifico() {
        return "Puntos: " + cantidadPuntos;
    }

    public int getCantidadPuntos() {
        return cantidadPuntos;
    }
}
