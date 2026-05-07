package Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "clases",
        foreignKeys = {
                @ForeignKey(entity = Grupo.class, parentColumns = "id", childColumns = "idGrupo", onDelete =  ForeignKey.CASCADE),
                @ForeignKey(entity = Tema.class, parentColumns = "id", childColumns = "idTema", onDelete = ForeignKey.SET_NULL)
        },
        indices = {
                @Index("idGrupo"),
                @Index("idTema")
        }
)
public class Clase {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nombre;
    private int idGrupo;
    private Integer idTema;

    public Clase(String nombre, int idGrupo, int idTema) {
        this.nombre = nombre;
        this.idGrupo = idGrupo;
        this.idTema = idTema;
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

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

}
