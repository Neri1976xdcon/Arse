package Clases;

import androidx.annotation.ColorInt;

public class Tema {

    private int id;
    private String nombre;

    @ColorInt
    private int colorPrincipal;
    @ColorInt private int colorFondo;
    @ColorInt private int colorOscuro;
    @ColorInt private int colorAdicional;
    @ColorInt private int colorOpcional;

    private boolean seleccionado;
    private boolean habilitado = true;

    public Tema(
            int id,
            String nombre,
            @ColorInt int colorPrincipal,
            @ColorInt int colorFondo,
            @ColorInt int colorOscuro,
            @ColorInt int colorAdicional,
            @ColorInt int colorOpcional
    ) {
        this.id = id;
        this.nombre = nombre;
        this.colorPrincipal = colorPrincipal;
        this.colorFondo = colorFondo;
        this.colorOscuro = colorOscuro;
        this.colorAdicional = colorAdicional;
        this.colorOpcional = colorOpcional;
        this.seleccionado = false;
    }

    public Tema(int id, String nombre, @ColorInt int colorPrincipal) {
        this.id = id;
        this.nombre = nombre;
        this.colorPrincipal = colorPrincipal;
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

    public int getColorPrincipal() {
        return colorPrincipal;
    }

    public void setColorPrincipal(int colorPrincipal) {
        this.colorPrincipal = colorPrincipal;
    }

    public int getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(int colorFondo) {
        this.colorFondo = colorFondo;
    }

    public int getColorOscuro() {
        return colorOscuro;
    }

    public void setColorOscuro(int colorOscuro) {
        this.colorOscuro = colorOscuro;
    }

    public int getColorAdicional() {
        return colorAdicional;
    }

    public void setColorAdicional(int colorAdicional) {
        this.colorAdicional = colorAdicional;
    }

    public int getColorOpcional() {
        return colorOpcional;
    }

    public void setColorOpcional(int colorOpcional) {
        this.colorOpcional = colorOpcional;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
}
