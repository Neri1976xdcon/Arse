package Clases;

import Entity.Alumno;

public class AlumnoCumplimiento {

    private Alumno alumno;
    private int porcentaje;

    public AlumnoCumplimiento(
            Alumno alumno,
            int porcentaje
    ) {
        this.alumno = alumno;
        this.porcentaje = porcentaje;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public int getPorcentaje() {
        return porcentaje;
    }
}