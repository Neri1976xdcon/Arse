package Clases;

public class GrupoItem {
    private int id;
    private String nombre;
    private String aula;
    private int totalAlumnos;

    public GrupoItem(int id, String nombre, String aula, int totalAlumnos) {
        this.id = id;
        this.nombre = nombre;
        this.aula = aula;
        this.totalAlumnos = totalAlumnos;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getAula() { return aula; }
    public int getTotalAlumnos() { return totalAlumnos; }
}
