package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import Entity.Aula;

@Dao
public interface AulaDAO {

    @Insert
    long insertar(Aula aula);

    @Query("SELECT * FROM aulas")
    List<Aula> obtenerTodas();

    @Query("SELECT * FROM aulas WHERE id = :idAula")
    Aula obtenerAula(int idAula);

    @Query("SELECT EXISTS(SELECT 1 FROM aulas WHERE LOWER(nombre) = LOWER(:aulaNombre))")
    boolean existeAula(String aulaNombre);
}