package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import Entity.ClasesCriterios;

@Dao
public interface CriterioClaseDAO {
    @Insert
    void insertar(ClasesCriterios claseCriterios);

    @Query("SELECT * FROM criterios_clases WHERE idClase = :claseId")
    List<ClasesCriterios> criteriosClase(int claseId);
}
