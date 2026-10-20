package modelo;
import enums.EstadoOrden;
import enums.Zona;



public abstract class Orden {

    public static final double CARGO_VISITA_ADICIONAL = 40.0;

    private final String codigo;
    private final String nombreCliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int cantidadVisitas;
    private boolean cobrada;

    
    protected Orden(String codigo, String nombreCliente, String direccion,
                    double horasEstimadas, Zona zona) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        if (Double.isNaN(horasEstimadas) || Double.isInfinite(horasEstimadas) || horasEstimadas <= 0) {
            throw new IllegalArgumentException("Las horas estimadas deben ser un número mayor que cero.");
        }
        if (zona == null) {
            throw new IllegalArgumentException("La zona es obligatoria.");
        }
        this.codigo = codigo.trim();
        this.nombreCliente = nombreCliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.cantidadVisitas = 0;
        this.cobrada = false;
    }

    
    public abstract double calcularCosto();

    public abstract int diasAtencion();

   
    public abstract int visitasPermitidas();

    public abstract String getCategoria();

    public boolean requiereRefrigeracion() {
        return false;
    }

  
    protected String detalleEspecifico() {
        return "";
    }

   
    protected double tarifaBase() {
        return zona == Zona.METROPOLITANA ? 100.0 : 175.0;
    }


    public void asignar() {
        exigirEstado(EstadoOrden.PENDIENTE, "asignar");
        estado = EstadoOrden.ASIGNADA;
    }

    public void retirar() {
        exigirEstado(EstadoOrden.ASIGNADA, "retirar");
        estado = EstadoOrden.PENDIENTE;
    }

    public void iniciarVisita() {
        exigirEstado(EstadoOrden.ASIGNADA, "iniciar la visita de");
        estado = EstadoOrden.EN_ATENCION;
        cantidadVisitas++;
    }

    public void registrarCompletada() {
        exigirEstado(EstadoOrden.EN_ATENCION, "completar");
        estado = EstadoOrden.COMPLETADA;
    }

    public void registrarVisitaFallida() {
        exigirEstado(EstadoOrden.EN_ATENCION, "registrar visita fallida en");
        if (cantidadVisitas >= visitasPermitidas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    public void marcarCobrada() {
        this.cobrada = true;
    }

    private void exigirEstado(EstadoOrden requerido, String accion) {
        if (estado != requerido) {
            throw new IllegalStateException("No se puede " + accion + " la orden " + codigo + ": está en estado '" + estado.getDescripcion() + "' y debe estar en '" + requerido.getDescripcion() + "'.");
        }
    }

    

    public double calcularIngresoRegistro() {
        return cobrada ? calcularCosto() : 0.0;
    }

    
    public double calcularCargoVisitasAdicionales() {
        return Math.max(0, cantidadVisitas - 1) * CARGO_VISITA_ADICIONAL;
    }

  

    public String describir() {
        String detalle = detalleEspecifico();
        return String.format(java.util.Locale.US,
                "[%s] %s | Cliente: %s | Dirección: %s | Zona: %s | Horas est.: %.2f%s%n"
                + "      Estado: %s | Visitas: %d de %d | Costo: Q%.2f | Atención: %d día(s)",
                codigo, getCategoria(), nombreCliente, direccion, zona.getDescripcion(),
                horasEstimadas, detalle.isEmpty() ? "" : " | " + detalle,
                estado.getDescripcion(), cantidadVisitas, visitasPermitidas(),
                calcularCosto(), diasAtencion());
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getCantidadVisitas() {
        return cantidadVisitas;
    }

    public boolean isCobrada() {
        return cobrada;
    }
}
