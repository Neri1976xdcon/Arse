package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import Entity.Horario;

@Dao
public interface HorarioDAO {

    @Insert
    void insertar(Horario horario);

    @Query("SELECT COUNT(*) > 0 FROM Horarios WHERE idDia = :diaId AND ((:inicio >= hmInicio AND :inicio < hmFin) OR (:fin > hmInicio AND :fin <= hmFin) OR (hmInicio >= :inicio AND hmInicio < :fin))")
    boolean existeEmpalme(
            int diaId,
            String inicio,
            String fin
    );
}
