package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import Entity.Alumno;
import Entity.GrupoAlumno;

@Dao
public interface GrupoAlumnoDAO {
    @Insert
    void insertar(GrupoAlumno grupoAlumno);

    @Query("SELECT * FROM grupos_alumnos WHERE grupoId = :idGrupo")
    List<GrupoAlumno> obtenerAlumnosGrupo(int idGrupo);

    @Query("SELECT COUNT(*) FROM grupos_alumnos WHERE grupoId = :idGrupo")
    int totalAlumnosGrupo(int idGrupo);

    @Query("DELETE FROM grupos_alumnos WHERE grupoId = :grupoId AND alumnoId = :alumnoId")
    void eliminarRelacion(int grupoId, int alumnoId);

}
