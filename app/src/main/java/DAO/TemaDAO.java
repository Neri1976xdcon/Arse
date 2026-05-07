package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import Entity.Tema;

@Dao
public interface TemaDAO {
    @Insert
    void insertar(Tema tema);

    @Query("SELECT * FROM temas")
    List<Tema> obtenerTemas();

    @Query("SELECT * FROM temas WHERE id NOT IN (SELECT idTema FROM clases WHERE idTema IS NOT NULL) AND id != 1")
    List<Tema> obtenerTemasDisponibles();

    @Query("SELECT * FROM temas WHERE id= :idTema")
    Tema obtenerTemaItem(int idTema);

}
