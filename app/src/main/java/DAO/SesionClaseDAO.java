package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Clases.AlumnoAsistencia;
import Entity.SesionClase;

@Dao
public interface SesionClaseDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertar(SesionClase sesionClase);

    @Update
    void actualizarSesion(SesionClase sesion);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertarLista(List<SesionClase> sesiones);

    @Query("SELECT * FROM sesiones WHERE id = :idSesion")
    SesionClase obtenerSesion(int idSesion);

    @Query("SELECT * FROM sesiones WHERE idClase = :idClase AND fecha = :fecha LIMIT 1")
    SesionClase obtenerSesionPorFecha(int idClase, String fecha);

    @Query("SELECT * FROM sesiones WHERE idClase = :idClase ORDER BY fecha ASC")
    List<SesionClase> obtenerSesionesPorClase(int idClase);

    @Query("UPDATE sesiones SET tomada = :tomada WHERE id = :idSesion")
    void actualizarEstadoSesion(int idSesion, boolean tomada);

    @Query("DELETE FROM sesiones WHERE id = :idSesion")
    void eliminarSesion(int idSesion);

    @Query("DELETE FROM sesiones WHERE idClase = :idClase AND tomada = 0")
    void eliminarSesionesNoTomadas(int idClase);

    @Query("SELECT DISTINCT strftime('%m-%Y', fecha) FROM sesiones ORDER BY fecha")
    List<String> obtenerMeses();

    @Query("SELECT * FROM sesiones WHERE strftime('%m-%Y', fecha)=:mes ORDER BY fecha")
    List<SesionClase> obtenerSesionesMes(String mes);

    @Query("SELECT ( SUM(CASE WHEN asistencia = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) FROM asistencias a INNER JOIN sesiones s ON s.id = a.idSesion WHERE strftime('%m-%Y', s.fecha)=:mes")
    float obtenerPorcentajeMes(String mes);

    @Query("SELECT DISTINCT strftime('%m-%Y', fecha) FROM sesiones WHERE idClase=:idClase ORDER BY fecha")
    List<String> obtenerMesesClase(int idClase);

    @Query("SELECT * FROM sesiones WHERE idClase=:idClase AND strftime('%m-%Y', fecha)=:mes ORDER BY fecha")
    List<SesionClase> obtenerSesionesMesClase(
            int idClase,
            String mes
    );

}
