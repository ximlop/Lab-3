import controlador.ControladorSistema;
import enums.EstadoJornada;
import enums.EstadoOrden;
import enums.Zona;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import modelo.*;


public class Main {

    private static final Scanner TECLADO = new Scanner(System.in);
    private static final ControladorSistema CONTROLADOR = new ControladorSistema();

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elija una opción: ");
            System.out.println();
            ejecutar(opcion);
        } while (opcion != 0);
        System.out.println("Hasta pronto.");
    }

    private static void mostrarMenu() {
        System.out.println("\n------ TecniHogar ------");
        System.out.println("1. Cotizar y registrar una orden");
        System.out.println("2. Registrar una cuadrilla");
        System.out.println("3. Crear una jornada");
        System.out.println("4. Asignar una orden a una jornada");
        System.out.println("5. Retirar una orden de una jornada");
        System.out.println("6. Iniciar una jornada");
        System.out.println("7. Registrar resultado de una visita");
        System.out.println("8. Consultar órdenes");
        System.out.println("9. Consultar cuadrillas");
        System.out.println("10. Consultar jornadas");
        System.out.println("11. Reportes");
        System.out.println("0. Salir");
    }

    private static void ejecutar(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    cotizarYRegistrar();
                    break;
                case 2:
                    registrarCuadrilla();
                    break;
                case 3:
                    crearJornada();
                    break;
                case 4:
                    asignarOrden();
                    break;
                case 5:
                    retirarOrden();
                    break;
                case 6:
                    iniciarJornada();
                    break;
                case 7:
                    registrarResultado();
                    break;
                case 8:
                    consultarOrdenes();
                    break;
                case 9:
                    consultarCuadrillas();
                    break;
                case 10:
                    consultarJornadas();
                    break;
                case 11:
                    mostrarReportes();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida. Elija un número del menú.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        }
    }

   
    private static void cotizarYRegistrar() {
        System.out.println("Categorías: 1) Diagnóstico  2) Instalación  3) Instalación eléctrica certificada  4) Mantenimiento de aire acondicionado");
        int categoria = leerEntero("Categoría: ");
        if (categoria < 1 || categoria > 4) {
            System.out.println("Operación rechazada: categoría inválida.");
            return;
        }
        String codigo = leerTexto("Código de la orden: ");
        if (codigo.isEmpty()) {
            System.out.println("Operación rechazada: el código no puede estar vacío.");
            return;
        }
        if (CONTROLADOR.existeCodigoOrden(codigo)) {
            System.out.println("Operación rechazada: ya existe una orden con el código '" + codigo + "'.");
            return;
        }
        String cliente = leerTexto("Nombre del cliente: ");
        String direccion = leerTexto("Dirección: ");
        double horas = leerDecimal("Horas estimadas: ");
        Zona zona = leerZona();

        Orden orden;
        switch (categoria) {
            case 1:
                orden = new Diagnostico(codigo, cliente, direccion, horas, zona);
                break;
            case 2:
                orden = new Instalacion(codigo, cliente, direccion, horas, zona,
                        leerEntero("Cantidad de puntos: "));
                break;
            case 3:
                int puntos = leerEntero("Cantidad de puntos: ");
                double materiales = leerDecimal("Valor declarado de los materiales (Q): ");
                orden = new InstalacionElectricaCertificada(codigo, cliente, direccion, horas, zona,
                        puntos, materiales);
                break;
            default:
                orden = new MantenimientoAire(codigo, cliente, direccion, horas, zona,
                        leerEntero("Capacidad del equipo en BTU: "));
        }

        System.out.println("\n--- Cotización ---");
        System.out.println("Costo: " + dinero(orden.calcularCosto()));
        System.out.println("Tiempo estimado de atención: " + orden.diasAtencion() + " día(s) hábil(es)");
        System.out.println("Visitas permitidas: " + orden.visitasPermitidas());
        String respuesta = leerTexto("¿El cliente acepta la cotización? (s/n): ");
        if (respuesta.equalsIgnoreCase("s")) {
            CONTROLADOR.registrarOrden(orden);
            System.out.println("Orden registrada como pendiente. Se cobró " + dinero(orden.calcularCosto()) + ".");
        } else {
            System.out.println("Cotización descartada. No se registró nada.");
        }
    }

    private static void registrarCuadrilla() {
        String codigo = leerTexto("Código de la cuadrilla: ");
        double capacidad = leerDecimal("Capacidad máxima de horas por jornada: ");
        String certificada = leerTexto("¿Está certificada en refrigeración? (s/n): ");
        Cuadrilla cuadrilla = new Cuadrilla(codigo, capacidad, certificada.equalsIgnoreCase("s"));
        CONTROLADOR.registrarCuadrilla(cuadrilla);
        System.out.println("Cuadrilla registrada.");
    }

    private static void crearJornada() {
        consultarCuadrillas();
        String codigo = leerTexto("Código de la cuadrilla para la jornada: ");
        Jornada jornada = CONTROLADOR.crearJornada(codigo);
        System.out.println("Jornada " + jornada.getNumero() + " creada en preparación para la cuadrilla "
                + jornada.getCuadrilla().getCodigo() + ".");
    }

    private static void asignarOrden() {
        int numero = leerEntero("Número de jornada: ");
        mostrarOrdenesPendientes();
        String codigo = leerTexto("Código de la orden a asignar: ");
        CONTROLADOR.asignarOrden(numero, codigo);
        System.out.println("Orden asignada a la jornada " + numero + ".");
    }

    private static void retirarOrden() {
        int numero = leerEntero("Número de jornada: ");
        Jornada jornada = CONTROLADOR.buscarJornada(numero);
        mostrarOrdenesDeJornada(jornada);
        String codigo = leerTexto("Código de la orden a retirar: ");
        CONTROLADOR.retirarOrden(numero, codigo);
        System.out.println("Orden retirada; vuelve a quedar pendiente.");
    }

    private static void iniciarJornada() {
        int numero = leerEntero("Número de jornada: ");
        Jornada jornada = CONTROLADOR.buscarJornada(numero);
        System.out.println("Órdenes que se atenderán:");
        mostrarOrdenesDeJornada(jornada);
        String confirmar = leerTexto("¿Confirma el inicio de la jornada? (s/n): ");
        if (confirmar.equalsIgnoreCase("s")) {
            CONTROLADOR.iniciarJornada(numero);
            System.out.println("Jornada " + numero + " iniciada; sus órdenes están en atención.");
        } else {
            System.out.println("Inicio cancelado. La jornada sigue en preparación.");
        }
    }

    private static void registrarResultado() {
        int numero = leerEntero("Número de jornada: ");
        Jornada jornada = CONTROLADOR.buscarJornada(numero);
        mostrarOrdenesDeJornada(jornada);
        String codigo = leerTexto("Código de la orden: ");
        System.out.println("Resultado: 1) Completada  2) Visita fallida");
        int resultado = leerEntero("Resultado: ");
        if (resultado != 1 && resultado != 2) {
            System.out.println("Operación rechazada: resultado inválido.");
            return;
        }
        CONTROLADOR.registrarResultado(numero, codigo, resultado == 1);
        Orden orden = CONTROLADOR.buscarOrden(codigo);
        System.out.println("Resultado registrado. La orden quedó '" + orden.getEstado().getDescripcion() + "'.");
        if (jornada.getEstado() == EstadoJornada.CERRADA) {
            System.out.println("La jornada " + numero + " quedó cerrada y la cuadrilla "  + jornada.getCuadrilla().getCodigo() + " está libre.");
        }
    }

 

    private static void consultarOrdenes() {
        System.out.println("--- Órdenes ---");
        for (Orden orden : CONTROLADOR.getOrdenes()) {
            System.out.println(orden.describir());
        }
    }

    private static void mostrarOrdenesPendientes() {
        System.out.println("Órdenes pendientes:");
        boolean hay = false;
        for (Orden orden : CONTROLADOR.getOrdenes()) {
            if (orden.getEstado() == EstadoOrden.PENDIENTE) {
                System.out.println(orden.describir());
                hay = true;
            }
        }
        if (!hay) {
            System.out.println("(no hay órdenes pendientes)");
        }
    }

    private static void mostrarOrdenesDeJornada(Jornada jornada) {
        if (jornada.getOrdenes().isEmpty()) {
            System.out.println("(la jornada no tiene órdenes)");
        }
        for (Orden orden : jornada.getOrdenes()) {
            System.out.println(orden.describir());
        }
    }

    private static void consultarCuadrillas() {
        System.out.println("--- Cuadrillas ---");
        for (Cuadrilla cuadrilla : CONTROLADOR.getCuadrillas()) {
            String disponible = CONTROLADOR.cuadrillaLibre(cuadrilla) ? "Disponible" : "Ocupada";
            System.out.println(cuadrilla.describir() + " | " + disponible);
        }
    }

    private static void consultarJornadas() {
        System.out.println("--- Jornadas ---");
        if (CONTROLADOR.getJornadas().isEmpty()) {
            System.out.println("(no hay jornadas creadas)");
        }
        for (Jornada jornada : CONTROLADOR.getJornadas()) {
            System.out.println(jornada.describir());
            for (Orden orden : jornada.getOrdenes()) {
                System.out.println("    - " + orden.getCodigo() + " (" + orden.getEstado().getDescripcion() + ")");
            }
        }
    }

    private static void mostrarReportes() {
        System.out.println("===== Reporte: órdenes por categoría y estado =====");
        for (Map.Entry<String, Map<EstadoOrden, Integer>> categoria
                : CONTROLADOR.contarPorCategoriaYEstado().entrySet()) {
            StringBuilder linea = new StringBuilder(categoria.getKey() + ": ");
            for (Map.Entry<EstadoOrden, Integer> estado : categoria.getValue().entrySet()) {
                linea.append(estado.getKey().getDescripcion()).append("=").append(estado.getValue()).append("  ");
            }
            System.out.println(linea);
        }

        System.out.println("\n===== Reporte: ingresos =====");
        System.out.println("Ingresos por registro:            " + dinero(CONTROLADOR.calcularIngresoPorRegistro()));
        System.out.println("Ingresos por visitas adicionales: " + dinero(CONTROLADOR.calcularIngresoPorVisitasAdicionales()));
        System.out.println("Ingreso total:                    " + dinero(CONTROLADOR.calcularIngresoTotal()));
        System.out.println("Ingreso por categoría:");
        for (Map.Entry<String, Double> ingreso : CONTROLADOR.calcularIngresoPorCategoria().entrySet()) {
            System.out.println("  " + ingreso.getKey() + ": " + dinero(ingreso.getValue()));
        }

        System.out.println("\n===== Reporte: jornadas sin cerrar =====");
        List<Jornada> abiertas = CONTROLADOR.getJornadasAbiertas();
        if (abiertas.isEmpty()) {
            System.out.println("(no hay jornadas sin cerrar)");
        }
        for (Jornada jornada : abiertas) {
            System.out.println(jornada.describir());
        }
    }

    

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!TECLADO.hasNextLine()) {
            System.out.println("\nFin de la entrada. Hasta pronto.");
            System.exit(0);
        }
        return TECLADO.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escriba un número entero.");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                    throw new NumberFormatException();
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escriba un número (por ejemplo 2.5).");
            }
        }
    }

    private static Zona leerZona() {
        while (true) {
            int opcion = leerEntero("Zona (1 = Metropolitana, 2 = Periférica): ");
            if (opcion == 1) {
                return Zona.METROPOLITANA;
            }
            if (opcion == 2) {
                return Zona.PERIFERICA;
            }
            System.out.println("Entrada inválida: escriba 1 o 2.");
        }
    }

    private static String dinero(double monto) {
        return String.format(Locale.US, "Q%,.2f", monto);
    }
}
