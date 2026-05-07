package Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "criterios_clases", foreignKeys = {
        @ForeignKey(entity = Clase.class, parentColumns = "id", childColumns = "idClase", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = CriterioEvaluacion.class, parentColumns = "id", childColumns = "idCriterio", onDelete = ForeignKey.CASCADE)
})
public class ClasesCriterios {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int idClase;
    private int idCriterio;

    public ClasesCriterios(int idClase, int idCriterio) {
        this.idClase = idClase;
        this.idCriterio = idCriterio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public int getIdCriterio() {
        return idCriterio;
    }

    public void setIdCriterio(int idCriterio) {
        this.idCriterio = idCriterio;
    }
}
