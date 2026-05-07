package BD;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "aulas")
public class Aula {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nombre;

    public Aula(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}
