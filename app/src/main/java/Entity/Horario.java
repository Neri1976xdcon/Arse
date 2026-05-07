package Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "horarios",
        foreignKeys = {
                @ForeignKey(entity = Clase.class, parentColumns = "id", childColumns = "idClase", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Dia.class, parentColumns = "id", childColumns = "idDia", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("idClase"),
                @Index("idDia")
        }
)
public class Horario {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int idClase;
    private int idDia;
    private String hmInicio;
    private String hmFin;

    public Horario(int idClase, int idDia, String hmInicio, String hmFin) {
        this.idClase = idClase;
        this.idDia = idDia;
        this.hmInicio = hmInicio;
        this.hmFin = hmFin;
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

    public int getIdDia() {
        return idDia;
    }

    public void setIdDia(int idDia) {
        this.idDia = idDia;
    }

    public String getHmInicio() {
        return hmInicio;
    }

    public void setHmInicio(String hmInicio) {
        this.hmInicio = hmInicio;
    }

    public String getHmFin() {
        return hmFin;
    }

    public void setHmFin(String hmFin) {
        this.hmFin = hmFin;
    }
}
