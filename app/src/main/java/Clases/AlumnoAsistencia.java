package Clases;

public class AlumnoAsistencia {

    private int id;
    private String nombre;
    private boolean asistencia;

    public AlumnoAsistencia(int id, String nombre, boolean asistencia) {
        this.id = id;
        this.nombre = nombre;
        this.asistencia = asistencia;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isAsistencia() {
        return asistencia;
    }
}
