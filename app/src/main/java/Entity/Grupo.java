package Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "grupos",
        foreignKeys = @ForeignKey(
                entity = Aula.class,
                parentColumns = "id",
                childColumns = "aulaId",
                onDelete = ForeignKey.CASCADE))
public class Grupo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nombre;
    private int aulaId;

    public Grupo(String nombre, int aulaId) {
        this.nombre = nombre;
        this.aulaId = aulaId;
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

    public int getAulaId() {
        return aulaId;
    }

    public void setAulaId(int aulaId) {
        this.aulaId = aulaId;
    }
}
