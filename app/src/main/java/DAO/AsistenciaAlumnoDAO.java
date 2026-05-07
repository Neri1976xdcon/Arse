package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Clases.AlumnoAsistencia;
import Entity.AsistenciaAlumno;

@Dao
public interface AsistenciaAlumnoDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertar(AsistenciaAlumno asistenciaAlumno);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertarLista(List<AsistenciaAlumno> lista);

    @Query("SELECT * FROM asistencias WHERE idSesion = :sesionId AND idAlumno = :alumnoId")
    AsistenciaAlumno buscarAsistenciaAlumnoClase(int sesionId, int alumnoId);

    @Query("SELECT * FROM asistencias WHERE idAlumno = :alumnoId AND idSesion IN (SELECT id FROM sesiones WHERE idClase = :claseId)")
    List<AsistenciaAlumno> obtenerAsistenciasAlumnoClase(int alumnoId, int claseId);

    @Query("SELECT * FROM asistencias WHERE idSesion = :sesionId")
    List<AsistenciaAlumno> obtenerPorSesion(int sesionId);

    @Update
    void actualizarAsistencia(AsistenciaAlumno asistenciaAlumno);

    @Update
    void actualizarLista(List<AsistenciaAlumno> lista);

    @Query("UPDATE asistencias SET asistencia = :valor WHERE idAlumno = :alumnoId AND idSesion = :sesionId")
    void actualizarDirecto(int alumnoId, int sesionId, boolean valor);

    @Query("DELETE FROM asistencias WHERE idSesion = :sesionId")
    void eliminarPorSesion(int sesionId);


    @Query("SELECT (SUM(CASE WHEN a.asistencia = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) FROM asistencias a INNER JOIN sesiones s ON s.id = a.idSesion WHERE s.idClase=:idClase  AND strftime('%m-%Y', s.fecha)=:mes")
    Float obtenerPorcentajeMesClase(int idClase, String mes);

    @Query("SELECT (SUM(CASE WHEN asistencia = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) FROM asistencias WHERE idSesion IN (:idsSesiones)")
    float obtenerPorcentajeSemana(List<Integer> idsSesiones);

    @Query("SELECT al.id AS id, al.nombre AS nombre, a.asistencia AS asistencia FROM alumnos al INNER JOIN asistencias a ON al.id = a.idAlumno WHERE a.idSesion=:idSesion ORDER BY al.nombre")
    List<AlumnoAsistencia> obtenerAlumnosSesion(
            int idSesion
    );

    @Query("SELECT CASE WHEN COUNT(*) = 0 THEN 0 ELSE (SUM(CASE WHEN a.asistencia = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) END FROM asistencias a WHERE a.idSesion = ( " +
            "SELECT s.id FROM sesiones s INNER JOIN asistencias a2 ON a2.idSesion = s.id WHERE s.idClase = :idClase ORDER BY date(s.fecha) DESC LIMIT 1)")
    float obtenerPorcentajeUltimaSesionConAsistencia(int idClase);

    @Query("SELECT CASE WHEN COUNT(*) = 0 THEN 0 ELSE (SUM( CASE WHEN asistencia = 1 THEN 1 ELSE 0 END"+
    ") * 100.0 / COUNT(*)) END FROM asistencias WHERE idSesion = :idSesion")
    Float obtenerPorcentajeSesion(int idSesion);

    @Query(
            "SELECT COUNT(*) " +
                    "FROM asistencias aa " +
                    "INNER JOIN sesiones sc " +
                    "ON aa.idSesion = sc.id " +
                    "WHERE aa.idAlumno = :idAlumno " +
                    "AND sc.idClase = :idClase"
    )
    int obtenerTotalAsistenciasClase(
            int idAlumno,
            int idClase
    );

    @Query(
            "SELECT COUNT(*) " +
                    "FROM asistencias aa " +
                    "INNER JOIN sesiones sc " +
                    "ON aa.idSesion = sc.id " +
                    "WHERE aa.idAlumno = :idAlumno " +
                    "AND sc.idClase = :idClase " +
                    "AND aa.asistencia = 1"
    )
    int obtenerTotalPresentesClase(
            int idAlumno,
            int idClase
    );

    @Query(
            "SELECT * " +
                    "FROM asistencias " +
                    "WHERE idAlumno = :idAlumno " +
                    "AND idSesion = :idSesion " +
                    "LIMIT 1"
    )
    AsistenciaAlumno obtenerAsistenciaSesionAlumno(
            int idAlumno,
            int idSesion
    );

    @Query(
            "SELECT * " +
                    "FROM asistencias " +
                    "WHERE idSesion = :idSesion"
    )
    List<AsistenciaAlumno> obtenerAsistenciasSesion(
            int idSesion
    );

    @Query(
            "SELECT EXISTS(" +
                    "SELECT 1 FROM asistencias " +
                    "WHERE idAlumno = :idAlumno " +
                    "AND idSesion = :idSesion" +
                    ")"
    )
    boolean existeAsistencia(
            int idAlumno,
            int idSesion
    );

    @Query(
            "SELECT COUNT(*) " +
                    "FROM asistencias aa " +
                    "INNER JOIN sesiones sc " +
                    "ON aa.idSesion = sc.id " +
                    "WHERE aa.idAlumno = :idAlumno " +
                    "AND sc.idClase = :idClase " +
                    "AND aa.asistencia = 0"
    )
    int obtenerTotalFaltasClase(
            int idAlumno,
            int idClase
    );

    @Query(
            "SELECT CASE " +
                    "WHEN COUNT(*) = 0 THEN 0 " +
                    "ELSE CAST((SUM(CASE WHEN aa.asistencia = 1 THEN 1 ELSE 0 END) * 100.0) / COUNT(*) AS INTEGER) " +
                    "END " +
                    "FROM asistencias aa " +
                    "INNER JOIN sesiones sc " +
                    "ON aa.idSesion = sc.id " +
                    "WHERE aa.idAlumno = :idAlumno " +
                    "AND sc.idClase = :idClase"
    )
    int obtenerPorcentajeAsistenciaAlumno(
            int idAlumno,
            int idClase
    );

    @Query(
            "SELECT COUNT(*) " +
                    "FROM asistencias " +
                    "WHERE idSesion = :idSesion"
    )
    int obtenerTotalSesion(int idSesion);

    @Query(
            "SELECT COUNT(*) " +
                    "FROM asistencias " +
                    "WHERE idSesion = :idSesion " +
                    "AND asistencia = 1"
    )
    int obtenerPresentesSesion(int idSesion);

    @Query(
            "SELECT COUNT(*) FROM (" +
                    "SELECT aa.idAlumno, " +
                    "CAST((SUM(CASE WHEN aa.asistencia = 1 THEN 1 ELSE 0 END) * 100.0) / COUNT(*) AS INTEGER) AS porcentaje " +
                    "FROM asistencias aa " +
                    "INNER JOIN sesiones sc " +
                    "ON aa.idSesion = sc.id " +
                    "WHERE sc.idClase = :idClase " +
                    "GROUP BY aa.idAlumno " +
                    "HAVING porcentaje < :minimoAsistencia" +
                    ")"
    )
    int obtenerCantidadAlumnosCriticos(
            int idClase,
            int minimoAsistencia
    );

    @Query(
            "SELECT COUNT(*) FROM (" +
                    "SELECT aa.idAlumno, " +
                    "CAST((SUM(CASE WHEN aa.asistencia = 1 THEN 1 ELSE 0 END) * 100.0) / COUNT(*) AS INTEGER) AS porcentaje " +
                    "FROM asistencias aa " +
                    "INNER JOIN sesiones sc " +
                    "ON aa.idSesion = sc.id " +
                    "WHERE sc.idClase = :idClase " +
                    "GROUP BY aa.idAlumno " +
                    "HAVING porcentaje >= :minimoAsistencia " +
                    "AND porcentaje <= :maximoRiesgo" +
                    ")"
    )
    int obtenerCantidadAlumnosRiesgo(
            int idClase,
            int minimoAsistencia,
            int maximoRiesgo
    );

    @Query("SELECT EXISTS(SELECT 1 FROM asistencias WHERE idSesion = :idSesion)")
    boolean existeAsistenciaSesion(int idSesion);

    @Query("SELECT asistencia FROM asistencias WHERE idAlumno = :idAlumno AND idSesion = :idSesion LIMIT 1")
    Boolean obtenerAsistenciaAlumnoSesion(int idAlumno, int idSesion);
}
