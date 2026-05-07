package Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "sesiones", foreignKeys = {
        @ForeignKey(entity = Clase.class, childColumns = "idClase", parentColumns = "id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Dia.class, childColumns = "idDia", parentColumns = "id")
}, indices = {@Index("idClase"), @Index("idDia")})
public class SesionClase {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int idClase;
    private String fecha;
    private boolean tomada;
    private int idDia;

    @Ignore
    private Boolean asistenciaAlumno;

    public Boolean getAsistenciaAlumno() {
        return asistenciaAlumno;
    }

    public void setAsistenciaAlumno(Boolean asistenciaAlumno) {
        this.asistenciaAlumno = asistenciaAlumno;
    }

    public SesionClase(String fecha, boolean tomada, int idDia) {
        this.fecha = fecha;
        this.tomada = tomada;
        this.idDia = idDia;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isTomada() {
        return tomada;
    }

    public void setTomada(boolean tomada) {
        this.tomada = tomada;
    }

    public int getIdDia() {
        return idDia;
    }

    public void setIdDia(int idDia) {
        this.idDia = idDia;
    }
}
