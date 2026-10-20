package controlador;
import enums.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.*;


public class ControladorSistema {

    private final List<Orden> ordenes = new ArrayList<>();
    private final List<Cuadrilla> cuadrillas = new ArrayList<>();
    private final List<Jornada> jornadas = new ArrayList<>();
    private int siguienteNumeroJornada = 1;

    public ControladorSistema() {
        cargarDatosIniciales();
    }

   
    private void cargarDatosIniciales() {
        ordenes.add(new Diagnostico("DIA-001", "María Pérez", "5a avenida 10-20 zona 1", 1.0, Zona.METROPOLITANA));
        ordenes.add(new Diagnostico("DIA-002", "Carlos Ruiz", "Aldea El Chato, Mixco", 1.5, Zona.PERIFERICA));

       
        ordenes.add(new Instalacion("INS-001", "Ana López", "12 calle 3-45 zona 10", 2.5, Zona.METROPOLITANA, 5));
      
        ordenes.add(new Instalacion("INS-002", "Luis Morales", "Km 18 carretera a El Salvador", 2.5, Zona.PERIFERICA, 2));

        ordenes.add(new InstalacionElectricaCertificada("ELE-001", "Rosa Méndez", "7a avenida 5-12 zona 9", 2.5, Zona.METROPOLITANA, 5, 2000.0));
        ordenes.add(new InstalacionElectricaCertificada("ELE-002", "Jorge Castillo", "Aldea San José Pinula", 2.0, Zona.PERIFERICA, 3, 1500.0));

        ordenes.add(new MantenimientoAire("AIR-001", "Elena Sosa", "3a calle 8-15 zona 14", 2.0, Zona.METROPOLITANA, 12000));
        ordenes.add(new MantenimientoAire("AIR-002", "Pedro Gómez", "Diagonal 6 12-30 zona 10", 3.0, Zona.METROPOLITANA, 24000));

        cuadrillas.add(new Cuadrilla("CUA-001", 8.0, true));
        cuadrillas.add(new Cuadrilla("CUA-002", 6.0, false));
        cuadrillas.add(new Cuadrilla("CUA-003", 4.0, false));
    }

    
    public boolean existeCodigoOrden(String codigo) {
        return buscarOrdenOpcional(codigo) != null;
    }

    private Orden buscarOrdenOpcional(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (Orden orden : ordenes) {
            if (orden.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return orden;
            }
        }
        return null;
    }

    public Orden buscarOrden(String codigo) {
        Orden orden = buscarOrdenOpcional(codigo);
        if (orden == null) {
            throw new IllegalArgumentException("No existe una orden con el código '" + codigo + "'.");
        }
        return orden;
    }

    public boolean existeCodigoCuadrilla(String codigo) {
        return buscarCuadrillaOpcional(codigo) != null;
    }

    private Cuadrilla buscarCuadrillaOpcional(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return cuadrilla;
            }
        }
        return null;
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla cuadrilla = buscarCuadrillaOpcional(codigo);
        if (cuadrilla == null) {
            throw new IllegalArgumentException("No existe una cuadrilla con el código '" + codigo + "'.");
        }
        return cuadrilla;
    }

    public Jornada buscarJornada(int numero) {
        for (Jornada jornada : jornadas) {
            if (jornada.getNumero() == numero) {
                return jornada;
            }
        }
        throw new IllegalArgumentException("No existe la jornada número " + numero + ".");
    }

    
    public boolean cuadrillaLibre(Cuadrilla cuadrilla) {
        for (Jornada jornada : jornadas) {
            if (jornada.getCuadrilla() == cuadrilla && jornada.getEstado() != EstadoJornada.CERRADA) {
                return false;
            }
        }
        return true;
    }

   
    public void registrarOrden(Orden orden) {
        if (existeCodigoOrden(orden.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una orden con el código '" + orden.getCodigo() + "'.");
        }
        orden.marcarCobrada();
        ordenes.add(orden);
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (existeCodigoCuadrilla(cuadrilla.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una cuadrilla con el código '" + cuadrilla.getCodigo() + "'.");
        }
        cuadrillas.add(cuadrilla);
    }

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        if (!cuadrillaLibre(cuadrilla)) {
            throw new IllegalStateException("La cuadrilla " + cuadrilla.getCodigo()
                    + " ya tiene una jornada sin cerrar.");
        }
        Jornada jornada = new Jornada(siguienteNumeroJornada, cuadrilla);
        siguienteNumeroJornada++;
        jornadas.add(jornada);
        return jornada;
    }

    public void asignarOrden(int numeroJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(numeroJornada);
        Orden orden = buscarOrden(codigoOrden);
        jornada.agregarOrden(orden);
    }

    public void retirarOrden(int numeroJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(numeroJornada);
        Orden orden = buscarOrden(codigoOrden);
        jornada.retirarOrden(orden);
    }

    public void iniciarJornada(int numeroJornada) {
        buscarJornada(numeroJornada).iniciar();
    }

    public void registrarResultado(int numeroJornada, String codigoOrden, boolean completada) {
        Jornada jornada = buscarJornada(numeroJornada);
        Orden orden = buscarOrden(codigoOrden);
        jornada.registrarResultado(orden, completada);
    }

    

    public List<Orden> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }

    public List<Cuadrilla> getCuadrillas() {
        return Collections.unmodifiableList(cuadrillas);
    }

    public List<Jornada> getJornadas() {
        return Collections.unmodifiableList(jornadas);
    }

    public List<Jornada> getJornadasAbiertas() {
        List<Jornada> abiertas = new ArrayList<>();
        for (Jornada jornada : jornadas) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                abiertas.add(jornada);
            }
        }
        return abiertas;
    }

    
    public Map<String, Map<EstadoOrden, Integer>> contarPorCategoriaYEstado() {
        Map<String, Map<EstadoOrden, Integer>> conteo = new LinkedHashMap<>();
        for (Orden orden : ordenes) {
            Map<EstadoOrden, Integer> porEstado = conteo.get(orden.getCategoria());
            if (porEstado == null) {
                porEstado = new EnumMap<>(EstadoOrden.class);
                for (EstadoOrden estado : EstadoOrden.values()) {
                    porEstado.put(estado, 0);
                }
                conteo.put(orden.getCategoria(), porEstado);
            }
            porEstado.put(orden.getEstado(), porEstado.get(orden.getEstado()) + 1);
        }
        return conteo;
    }

    public double calcularIngresoPorRegistro() {
        double total = 0;
        for (Orden orden : ordenes) {
            total += orden.calcularIngresoRegistro();
        }
        return total;
    }

    public double calcularIngresoPorVisitasAdicionales() {
        double total = 0;
        for (Orden orden : ordenes) {
            total += orden.calcularCargoVisitasAdicionales();
        }
        return total;
    }

    public double calcularIngresoTotal() {
        return calcularIngresoPorRegistro() + calcularIngresoPorVisitasAdicionales();
    }

   
    public Map<String, Double> calcularIngresoPorCategoria() {
        Map<String, Double> ingresos = new LinkedHashMap<>();
        for (Orden orden : ordenes) {
            double actual = ingresos.containsKey(orden.getCategoria()) ? ingresos.get(orden.getCategoria()) : 0.0;
            ingresos.put(orden.getCategoria(),
                    actual + orden.calcularIngresoRegistro() + orden.calcularCargoVisitasAdicionales());
        }
        return ingresos;
    }
}
