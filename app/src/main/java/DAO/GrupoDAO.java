package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import Entity.Grupo;

@Dao
public interface GrupoDAO {
    @Insert
    long insertar(Grupo grupo);

    @Query("SELECT * FROM grupos")
    List<Grupo> obtenerGrupos();

    @Query("SELECT COUNT(*) FROM grupos")
    int contarGrupos();

    @Query("SELECT nombre FROM grupos WHERE id= :idGrupo")
    String obtenerNombreGrupo(int idGrupo);

    @Query("SELECT * FROM grupos WHERE id= :idGrupo")
    Grupo obtenerGrupo(int idGrupo);

    @Query("SELECT id FROM grupos WHERE id= (SELECT idGrupo FROM clases WHERE id = :claseId)")
    int obtenerIdGrupoClase(int claseId);

    @Query("SELECT EXISTS(SELECT 1 FROM grupos WHERE LOWER(nombre) = LOWER(:aulaNombre))")
    boolean existeGrupo(String aulaNombre);

}
