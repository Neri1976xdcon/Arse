package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import Entity.Clase;

@Dao
public interface ClaseDAO {
    @Insert
    long insertar(Clase clase);

    @Query("SELECT EXISTS(SELECT 1 FROM clases WHERE nombre = :nombreClase AND idGrupo = :grupoId)")
    boolean existeClase(String nombreClase, int grupoId);

    @Query("SELECT * FROM clases ORDER BY nombre")
    List<Clase> obtenerClasesRegistradas();

    @Query("SELECT COUNT(*) > 0 FROM clases")
    boolean existenClasesRegistradas();

    @Query("SELECT c.* FROM clases c " +
            "INNER JOIN horarios h ON c.id = h.idClase " +
            "WHERE h.idDia = :diaId AND h.hmFin > :horaActual " +
            "ORDER BY h.hmInicio ASC")
    List<Clase> obtenerClasesDia(int diaId, String horaActual);

    @Query("SELECT * FROM clases WHERE id = :idClase")
    Clase obtenerClasePorId(int idClase);

    @Query("SELECT c.* FROM clases c " +
            "INNER JOIN horarios h ON c.id = h.idClase " +
            "INNER JOIN criterios_clases cc ON c.id = cc.idClase " +
            "WHERE h.idDia = :diaActual " +
            "AND :horaActual BETWEEN h.hmInicio AND h.hmFin " +
            "AND cc.idCriterio = 1 " +
            "LIMIT 1")
    Clase obtenerClaseAsistenciaActiva(int diaActual, String horaActual);

    @Query("SELECT EXISTS(SELECT 1 FROM horarios h WHERE h.idClase = :idClase AND h.idDia = :dia AND time(:horaActual) >= time(h.hmInicio) AND time(:horaActual) <= time(h.hmFin))")
    boolean registroActivoClase(int idClase, int dia, String horaActual);
}
