package modelo;

import enums.EstadoJornada;
import enums.EstadoOrden;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class Jornada {

    private final int numero;
    private final Cuadrilla cuadrilla;
    private final List<Orden> ordenes;
    private final Set<Orden> conResultado;
    private EstadoJornada estado;

    public Jornada(int numero, Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new IllegalArgumentException("La jornada necesita una cuadrilla.");
        }
        if (numero <= 0) {
            throw new IllegalArgumentException("El número de jornada debe ser un entero positivo.");
        }
        this.numero = numero;
        this.cuadrilla = cuadrilla;
        this.ordenes = new ArrayList<>();
        this.conResultado = new HashSet<>();
        this.estado = EstadoJornada.EN_PREPARACION;
    }

   
    public void agregarOrden(Orden orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION, "asignar órdenes");
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("La orden " + orden.getCodigo()
                    + " no se puede asignar: está en estado '" + orden.getEstado().getDescripcion()
                    + "' (solo se asignan órdenes pendientes, no asignadas a otra jornada).");
        }
        if (orden.requiereRefrigeracion() && !cuadrilla.isCertificadaRefrigeracion()) {
            throw new IllegalStateException("La orden " + orden.getCodigo()
                    + " requiere una cuadrilla certificada en refrigeración y la cuadrilla "
                    + cuadrilla.getCodigo() + " no lo está.");
        }
        double horasConNueva = calcularHorasAsignadas() + orden.getHorasEstimadas();
        if (horasConNueva > cuadrilla.getCapacidadMaximaHoras() + 1e-9) {
            throw new IllegalStateException(String.format(java.util.Locale.US,
                    "La orden %s excede la capacidad de la cuadrilla %s (%.2f h con la orden, máximo %.2f h).",
                    orden.getCodigo(), cuadrilla.getCodigo(), horasConNueva,
                    cuadrilla.getCapacidadMaximaHoras()));
        }
        orden.asignar();
        ordenes.add(orden);
    }

    public void retirarOrden(Orden orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION, "retirar órdenes");
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo()
                    + " no está asignada a la jornada " + numero + ".");
        }
        orden.retirar();
        ordenes.remove(orden);
    }

    
    public void iniciar() {
        exigirEstado(EstadoJornada.EN_PREPARACION, "iniciar");
        if (ordenes.isEmpty()) {
            throw new IllegalStateException("No se puede iniciar una jornada vacía.");
        }
        for (Orden orden : ordenes) {
            orden.iniciarVisita();
        }
        estado = EstadoJornada.EN_ATENCION;
    }

    public void registrarResultado(Orden orden, boolean completada) {
        exigirEstado(EstadoJornada.EN_ATENCION, "registrar resultados");
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo()
                    + " no pertenece a la jornada " + numero + ".");
        }
        if (conResultado.contains(orden)) {
            throw new IllegalStateException("La orden " + orden.getCodigo()
                    + " ya tiene resultado registrado en esta jornada.");
        }
        if (completada) {
            orden.registrarCompletada();
        } else {
            orden.registrarVisitaFallida();
        }
        conResultado.add(orden);
        if (conResultado.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
        }
    }

    public boolean tieneResultado(Orden orden) {
        return conResultado.contains(orden);
    }

    public double calcularHorasAsignadas() {
        double total = 0;
        for (Orden orden : ordenes) {
            total += orden.getHorasEstimadas();
        }
        return total;
    }

    public double calcularCapacidadDisponible() {
        return cuadrilla.getCapacidadMaximaHoras() - calcularHorasAsignadas();
    }

    private void exigirEstado(EstadoJornada requerido, String accion) {
        if (estado != requerido) {
            throw new IllegalStateException("No se puede " + accion + " en la jornada " + numero
                    + ": está '" + estado.getDescripcion() + "' y debe estar '"
                    + requerido.getDescripcion() + "'.");
        }
    }

    public String describir() {
        return String.format(java.util.Locale.US,
                "Jornada %d | Cuadrilla: %s | Estado: %s | Horas asignadas: %.2f | Capacidad disponible: %.2f | Órdenes: %d",
                numero, cuadrilla.getCodigo(), estado.getDescripcion(),
                calcularHorasAsignadas(), calcularCapacidadDisponible(), ordenes.size());
    }

    public int getNumero() {
        return numero;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public List<Orden> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }
}
