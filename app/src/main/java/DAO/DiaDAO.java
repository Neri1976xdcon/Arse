package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import Entity.Dia;

@Dao
public interface DiaDAO {
    @Insert
    long insertar(Dia dia);

    @Query("SELECT * FROM DIAS")
    List<Dia> obtenerDias();
}
