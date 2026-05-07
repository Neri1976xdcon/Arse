package Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "asistencias",
        primaryKeys = {"idAlumno", "idSesion"},
        foreignKeys = {
                @ForeignKey(entity = Alumno.class,
                        parentColumns = "id",
                        childColumns = "idAlumno",
                        onDelete = ForeignKey.CASCADE),

                @ForeignKey(entity = SesionClase.class,
                        parentColumns = "id",
                        childColumns = "idSesion",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("idAlumno"), @Index("idSesion")}
)
public class AsistenciaAlumno {

    public int idAlumno;
    public int idSesion;
    public boolean asistencia;
}
