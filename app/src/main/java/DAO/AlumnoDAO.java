    package DAO;

    import androidx.room.Dao;
    import androidx.room.Delete;
    import androidx.room.Insert;
    import androidx.room.OnConflictStrategy;
    import androidx.room.Query;
    import androidx.room.Update;

    import java.util.List;

    import Entity.Alumno;

    @Dao
    public interface AlumnoDAO {

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insertar(Alumno alumno);

        @Query("SELECT * FROM alumnos ORDER BY nombre ASC")
        List<Alumno> obtenerTodosAlumnos();

        @Query("SELECT * FROM alumnos " +
                "WHERE LOWER(nombre) LIKE '%' || LOWER(:texto) || '%' " +
                "ORDER BY nombre ASC")
        List<Alumno> buscarAlumnos(String texto);

        @Query("SELECT * FROM alumnos " +
                "WHERE (:texto IS NULL OR LOWER(nombre) LIKE '%' || LOWER(:texto) || '%') " +
                "ORDER BY " +
                "CASE WHEN :ordenAsc = 1 THEN nombre END ASC, " +
                "CASE WHEN :ordenAsc = 0 THEN nombre END DESC")
        List<Alumno> obtenerAlumnosFiltrados(String texto, boolean ordenAsc);

        @Query("SELECT EXISTS(SELECT 1 FROM alumnos WHERE LOWER(nombre) = LOWER(:alumno))")
        boolean existeAlumno(String alumno);

        @Query("SELECT * FROM alumnos WHERE id = :id LIMIT 1")
        Alumno obtenerAlumnoPorId(int id);

        @Delete
        void eliminar(Alumno alumno);

        @Update
        void actualizar(Alumno alumno);

        @Query("SELECT a.* FROM alumnos a " +
                "INNER JOIN grupos_alumnos ga ON a.id = ga.alumnoId " +
                "WHERE ga.grupoId = :idGrupo ORDER BY a.nombre")
        List<Alumno> obtenerAlumnosGrupo(int idGrupo);

        @Query("SELECT a.* FROM alumnos a " +
                "INNER JOIN grupos_alumnos ga ON a.id = ga.alumnoId " +
                "WHERE ga.grupoId = :idGrupo " +
                "AND (:texto IS NULL OR a.nombre LIKE '%' || :texto || '%')")
        List<Alumno> obtenerAlumnosGrupoFiltrado(int idGrupo, String texto);

        @Query("SELECT a.* FROM alumnos a INNER JOIN grupos_alumnos ga ON a.id = ga.alumnoId WHERE ga.grupoId = :grupoId  AND (:busqueda IS NULL OR a.nombre LIKE '%' || :busqueda || '%') ORDER BY a.nombre COLLATE NOCASE ASC")
        List<Alumno> obtenerAlumnosGrupoFiltradoOrdenadoAsc(int grupoId, String busqueda);


        @Query("SELECT a.* FROM alumnos a INNER JOIN grupos_alumnos ga ON a.id = ga.alumnoId WHERE ga.grupoId = :grupoId  AND (:busqueda IS NULL OR a.nombre LIKE '%' || :busqueda || '%') ORDER BY a.nombre COLLATE NOCASE DESC")
        List<Alumno> obtenerAlumnosGrupoFiltradoOrdenadoDesc(int grupoId, String busqueda);

        @Query("SELECT * FROM alumnos WHERE id NOT IN " +
                "(SELECT alumnoId FROM grupos_alumnos WHERE grupoId = :grupoId) ORDER BY nombre")
        List<Alumno> obtenerAlumnosNoEnGrupo(int grupoId);

        @Query("SELECT * FROM alumnos WHERE id NOT IN " +
                "(SELECT alumnoId FROM grupos_alumnos WHERE grupoId = :grupoId) " +
                "AND nombre LIKE '%' || :texto || '%'")
        List<Alumno> buscarAlumnosNoEnGrupo(int grupoId, String texto);

        @Query("SELECT COUNT(*) FROM alumnos")
        int contarAlumnos();

        @Query("DELETE FROM alumnos")
        void eliminarTodos();

        @Query("DELETE FROM alumnos WHERE id = :id")
        void eliminarAlumno(int id);




    }
