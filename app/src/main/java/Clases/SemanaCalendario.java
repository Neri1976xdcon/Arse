package Clases;

import java.util.List;

import Entity.SesionClase;

public class SemanaCalendario {

    private String nombreSemana;
    private List<SesionClase> sesiones;

    public SemanaCalendario(String nombreSemana,
                            List<SesionClase> sesiones) {

        this.nombreSemana = nombreSemana;
        this.sesiones = sesiones;
    }

    public String getNombreSemana() {
        return nombreSemana;
    }

    public List<SesionClase> getSesiones() {
        return sesiones;
    }
}
