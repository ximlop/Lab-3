package modelo;
import enums.Zona;



public class MantenimientoAire extends Orden {

    public static final int BTU_MINIMO = 9000;
    public static final int BTU_MAXIMO = 60000;
    public static final double COSTO_HORA = 110.0;
    public static final double CARGO_GAS = 80.0;

    private final int capacidadBTU;

    public MantenimientoAire(String codigo, String nombreCliente, String direccion, double horasEstimadas, Zona zona, int capacidadBTU) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new IllegalArgumentException(
                    "El mantenimiento de aire acondicionado solo se acepta en la zona metropolitana.");
        }
        if (capacidadBTU < BTU_MINIMO || capacidadBTU > BTU_MAXIMO) {
            throw new IllegalArgumentException("La capacidad en BTU debe estar entre "
                    + BTU_MINIMO + " y " + BTU_MAXIMO + ".");
        }
        this.capacidadBTU = capacidadBTU;
    }

    @Override
    public double calcularCosto() {
        return tarifaBase() + COSTO_HORA * getHorasEstimadas() + CARGO_GAS;
    }

    @Override
    public int diasAtencion() {
        return 2;
    }

    @Override
    public int visitasPermitidas() {
        return 1;
    }

    @Override
    public String getCategoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public boolean requiereRefrigeracion() {
        return true;
    }

    @Override
    protected String detalleEspecifico() {
        return "Capacidad: " + capacidadBTU + " BTU";
    }

    public int getCapacidadBTU() {
        return capacidadBTU;
    }
}
