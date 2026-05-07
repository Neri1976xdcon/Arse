package Clases;

public class CriterioEvaluacion {
    private int id;
    private String nombre;
    private int icono;
    private int pronderacion;
    private boolean seleccionado;

    public CriterioEvaluacion(int id, String nombre, int icono) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.pronderacion = 0;
        this.seleccionado = false;
    }

    public CriterioEvaluacion(String nombre, int icono) {
        this.id = 0;
        this.nombre = nombre;
        this.icono = icono;
        this.pronderacion = 0;
        this.seleccionado = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public int getPronderacion() {
        return pronderacion;
    }

    public void setPronderacion(int pronderacion) {
        this.pronderacion = pronderacion;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
