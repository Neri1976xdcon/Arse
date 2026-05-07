package BD;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import DAO.AlumnoDAO;
import DAO.AsistenciaAlumnoDAO;
import DAO.AulaDAO;
import DAO.ClaseDAO;
import DAO.CriterioClaseDAO;
import DAO.CriterioDAO;
import DAO.DiaDAO;
import DAO.GrupoAlumnoDAO;
import DAO.GrupoDAO;
import DAO.HorarioDAO;
import DAO.SesionClaseDAO;
import DAO.TemaDAO;
import Entity.Alumno;
import Entity.AsistenciaAlumno;
import Entity.Aula;
import Entity.Clase;
import Entity.ClasesCriterios;
import Entity.CriterioEvaluacion;
import Entity.Dia;
import Entity.Grupo;
import Entity.GrupoAlumno;
import Entity.Horario;
import Entity.SesionClase;
import Entity.Tema;

import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {Alumno.class, Dia.class, CriterioEvaluacion.class, Tema.class, Clase.class, Grupo.class, Aula.class, GrupoAlumno.class, Horario.class, ClasesCriterios.class, SesionClase.class, AsistenciaAlumno.class}, version = 1)
public abstract class BaseDat extends RoomDatabase {
    private static BaseDat INSTANCE;
    public abstract AlumnoDAO alumnoDao();
    public abstract DiaDAO diaDao();
    public abstract CriterioDAO criterioDao();
    public abstract TemaDAO temaDao();
    public abstract AulaDAO aulaDao();
    public abstract GrupoDAO grupoDao();
    public abstract GrupoAlumnoDAO grupoAlumnoDao();
    public abstract ClaseDAO claseDao();
    public abstract HorarioDAO horarioDao();
    public abstract CriterioClaseDAO criterios_clasesDao();
    public abstract SesionClaseDAO sesionClaseDao();

    public abstract AsistenciaAlumnoDAO asistenciaAlumnoDao();

    public static synchronized BaseDat getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            BaseDat.class,
                            "arsebd"
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);

            Executors.newSingleThreadExecutor().execute(() -> {
                BaseDat database = INSTANCE;

                if (database != null) {

                    DiaDAO diaDao = database.diaDao();
                    CriterioDAO criterioDao = database.criterioDao();
                    TemaDAO temaDao = database.temaDao();

                    diaDao.insertar(new Dia("Lunes"));
                    diaDao.insertar(new Dia("Martes"));
                    diaDao.insertar(new Dia("Miércoles"));
                    diaDao.insertar(new Dia("Jueves"));
                    diaDao.insertar(new Dia("Viernes"));
                    diaDao.insertar(new Dia("Sábado"));

                    criterioDao.insertar(new CriterioEvaluacion("ASISTENCIA", "icono_asistencia"));
                    criterioDao.insertar(new CriterioEvaluacion("ACTIVIDADES", "icono_actividades"));
                    criterioDao.insertar(new CriterioEvaluacion("TAREAS", "icono_tareas"));
                    criterioDao.insertar(new CriterioEvaluacion("PRÁCTICAS", "icono_practicas"));
                    criterioDao.insertar(new CriterioEvaluacion("PROYECTOS", "icono_proyectos"));
                    criterioDao.insertar(new CriterioEvaluacion("EXÁMENES", "icono_examenes"));
                    criterioDao.insertar(new CriterioEvaluacion("PARTICIPACIONES", "icono_participaciones"));
                    criterioDao.insertar(new CriterioEvaluacion("SANCIONES", "icono_sanciones"));

                    temaDao.insertar(new Tema("Gris",
                            "#2F3437",
                            "#F1F3F4",
                            "#1F2326",
                            "#4A4F54",
                            "#E0E3E5"
                    ));

                    temaDao.insertar(new Tema("Café",
                            "#5A3825",
                            "#F4E6DC",
                            "#3B2418",
                            "#8C4A2F",
                            "#EAD2C2"
                    ));

                    temaDao.insertar(new Tema("Rojo",
                            "#D7263D",
                            "#FFE5E8",
                            "#8B0F1A",
                            "#FF3B5C",
                            "#FFD1D6"
                    ));

                    temaDao.insertar(new Tema("Rosa",
                            "#C2185B",
                            "#FFE4F0",
                            "#7A0F3A",
                            "#E91E63",
                            "#FFD1E6"
                    ));

                    temaDao.insertar(new Tema("Naranja",
                            "#E65100",
                            "#FFF3E0",
                            "#A63A00",
                            "#FF6D00",
                            "#FFE0B2"
                    ));

                    temaDao.insertar(new Tema("Amarillo",
                            "#F9A825",
                            "#FFFDE7",
                            "#C17900",
                            "#FFB300",
                            "#FFF59D"
                    ));

                    temaDao.insertar(new Tema("Verde",
                            "#2E7D32",
                            "#E8F5E9",
                            "#1B5E20",
                            "#43A047",
                            "#C8E6C9"
                    ));

                    temaDao.insertar(new Tema("Menta",
                            "#00897B",
                            "#E0F2F1",
                            "#004D40",
                            "#00BFA5",
                            "#B2DFDB"
                    ));

                    temaDao.insertar(new Tema("Azul Claro",
                            "#1976D2",
                            "#E3F2FD",
                            "#0D47A1",
                            "#2196F3",
                            "#BBDEFB"
                    ));

                    temaDao.insertar(new Tema("Azul Rey",
                            "#283593",
                            "#E8EAF6",
                            "#1A237E",
                            "#3949AB",
                            "#C5CAE9"
                    ));

                    temaDao.insertar(new Tema("Morado",
                            "#6A1B9A",
                            "#F3E5F5",
                            "#4A148C",
                            "#8E24AA",
                            "#E1BEE7"
                    ));
                }
            });
        }
    };
}