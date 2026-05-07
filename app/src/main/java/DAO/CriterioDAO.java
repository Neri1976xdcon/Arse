package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import Entity.CriterioEvaluacion;

@Dao
public interface CriterioDAO {
    @Insert
    void insertar(CriterioEvaluacion criterio);

    @Query("SELECT * FROM criterios")
    List<CriterioEvaluacion> obtenerCriteriosRegistrados();

    @Query("SELECT * FROM criterios WHERE id IN (SELECT idCriterio FROM criterios_clases WHERE idClase = :claseId)")
    List<CriterioEvaluacion> criteriosClase(int claseId);
}
