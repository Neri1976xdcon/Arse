package Clases;

import Entity.Clase;

public class ClaseItem {

    private Clase clase;
    private String nombreGrupo;
    private String nombreAula;
    private String colorPrincipal;
    private String colorFondo;

    public ClaseItem(
            Clase clase,
            String nombreGrupo,
            String nombreAula,
            String colorPrincipal,
            String colorFondo
    ) {

        this.clase = clase;
        this.nombreGrupo = nombreGrupo;
        this.nombreAula = nombreAula;
        this.colorPrincipal = colorPrincipal;
        this.colorFondo = colorFondo;
    }

    public Clase getClase() {
        return clase;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public String getNombreAula() {
        return nombreAula;
    }

    public String getColorPrincipal() {
        return colorPrincipal;
    }

    public String getColorFondo() {
        return colorFondo;
    }
}
