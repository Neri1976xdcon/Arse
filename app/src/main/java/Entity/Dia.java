package Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dias")
public class Dia {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nombre;

    public Dia(String nombre) {
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
