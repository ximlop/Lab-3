package modelo;

import enums.Zona;

public class Diagnostico extends Orden {

    public static final double HORAS_MAXIMAS = 1.5;

    public Diagnostico(String codigo, String nombreCliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > HORAS_MAXIMAS) {
            throw new IllegalArgumentException("Un diagnóstico no puede superar "
                    + HORAS_MAXIMAS + " horas estimadas.");
        }
    }

    @Override
    public double calcularCosto() {
        return getZona() == Zona.METROPOLITANA ? 150.0 : 225.0;
    }

    @Override
    public int diasAtencion() {
        return getZona() == Zona.METROPOLITANA ? 1 : 2;
    }

    @Override
    public int visitasPermitidas() {
        return 3;
    }

    @Override
    public String getCategoria() {
        return "Diagnóstico";
    }
}
