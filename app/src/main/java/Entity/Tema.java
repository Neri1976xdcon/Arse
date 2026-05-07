package Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "temas")
public class Tema {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nombre;
    private String colorPrincipal;
    private String colorFondo;
    private String colorRelleno;
    private String colorElemento;
    private String colorColumna;

    public Tema(String nombre, String colorPrincipal, String colorFondo, String colorRelleno, String colorElemento, String colorColumna) {
        this.nombre = nombre;
        this.colorPrincipal = colorPrincipal;
        this.colorFondo = colorFondo;
        this.colorRelleno = colorRelleno;
        this.colorElemento = colorElemento;
        this.colorColumna = colorColumna;
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

    public String getColorPrincipal() {
        return colorPrincipal;
    }

    public void setColorPrincipal(String colorPrincipal) {
        this.colorPrincipal = colorPrincipal;
    }

    public String getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(String colorFondo) {
        this.colorFondo = colorFondo;
    }

    public String getColorRelleno() {
        return colorRelleno;
    }

    public void setColorRelleno(String colorRelleno) {
        this.colorRelleno = colorRelleno;
    }

    public String getColorElemento() {
        return colorElemento;
    }

    public void setColorElemento(String colorElemento) {
        this.colorElemento = colorElemento;
    }

    public String getColorColumna() {
        return colorColumna;
    }

    public void setColorColumna(String colorColumna) {
        this.colorColumna = colorColumna;
    }
}
