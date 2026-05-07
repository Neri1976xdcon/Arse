package Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "grupos_alumnos",
        primaryKeys = {"grupoId", "alumnoId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Grupo.class,
                        parentColumns = "id",
                        childColumns = "grupoId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Alumno.class,
                        parentColumns = "id",
                        childColumns = "alumnoId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class GrupoAlumno {

    private int grupoId;
    private int alumnoId;

    public GrupoAlumno(int grupoId, int alumnoId) {
        this.grupoId = grupoId;
        this.alumnoId = alumnoId;
    }

    public int getGrupoId() {
        return grupoId;
    }

    public int getAlumnoId() {
        return alumnoId;
    }
}