package BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arse.R;

import java.util.ArrayList;
import java.util.List;

import Clases.CriterioEvaluacion;

public class BaseDatos extends SQLiteOpenHelper {

    private static final String baseDatos = "registros";
    private static final int version = 6;

    // Nombres tablas
    public final String tabla_dias = "dias";
    public final String tabla_criterios = "criterios_evaluacion";
    public final String tabla_temas = "temas";
    public final String tabla_alumnos = "alumnos";
    public final String tabla_aulas = "aulas";


    // Crear tablas
    private final String crear_tabla_dias = "CREATE TABLE "+
            tabla_dias+" (" +
            "ID_DIA INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NOMBRE TEXT NOT NULL);";

    private final String crear_tabla_criterios = "CREATE TABLE "+
            tabla_criterios+" (" +
            "ID_CRITERIO INTEGER PRIMARY KEY AUTOINCREMENT," +
            "NOMBRE TEXT NOT NULL," +
            "ICONO INTEGER NOT NULL);";

    private final String crear_tabla_temas = "CREATE TABLE "+
            tabla_temas+" (" +
            "ID_TEMA INTEGER PRIMARY KEY AUTOINCREMENT," +
            "NOMBRE TEXT NOT NULL," +
            "COLOR_PRINCIPAL TEXT NOT NULL, " +
            "COLOR_FONDO TEXT NOT NULL, " +
            "COLOR_OSCURO TEXT NOT NULL, " +
            "COLOR_ADICIONAL TEXT, " +
            "COLOR_OPCIONAL TEXT);";

    private final String crear_tabla_alumnos = "CREATE TABLE "+tabla_alumnos+" (" +
            "ID_ALUMNO INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NOMBRE TEXT NOT NULL);";

    private final String crear_tabla_aulas = "CREATE TABLE "+tabla_aulas+" (" +
            "ID_AULA INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NOMBRE TEXT NOT NULL);";

    public BaseDatos(Context context){
        super(context, baseDatos, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase bd){
        bd.execSQL(crear_tabla_dias);
        bd.execSQL(crear_tabla_criterios);
        bd.execSQL(crear_tabla_temas);
        bd.execSQL(crear_tabla_alumnos);
        definirDias(bd);
        definirCriteriosEvaluacion(bd);
        definirTemas(bd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int oldVersion, int newVersion) {
        bd.execSQL("DROP TABLE IF EXISTS " + tabla_dias);
        bd.execSQL("DROP TABLE IF EXISTS " + tabla_criterios);
        bd.execSQL("DROP TABLE IF EXISTS " + tabla_temas);
        bd.execSQL("DROP TABLE IF EXISTS "+tabla_alumnos);
        onCreate(bd);
    }


    public void definirDias(SQLiteDatabase bd){
        List<String> dias = new ArrayList<>();
        dias.add("Lunes");
        dias.add("Martes");
        dias.add("Miércoles");
        dias.add("Jueves");
        dias.add("Viernes");

        for(String dia: dias){
            ContentValues datos = new ContentValues();
            datos.put("nombre", dia);
            bd.insert(tabla_dias, null, datos);
        }
    }

    public void definirCriteriosEvaluacion(SQLiteDatabase bd){
        List<CriterioEvaluacion> criterios = new ArrayList<>();
        criterios.add(new CriterioEvaluacion("ASISTENCIA", R.drawable.icono_cumplimiento));
        criterios.add(new CriterioEvaluacion("ACTIVIDADES", R.drawable.icono_actividades));
        criterios.add(new CriterioEvaluacion("TAREAS", R.drawable.icono_tareas));
        criterios.add(new CriterioEvaluacion("PRÁCTICAS", R.drawable.icono_practicas));
        criterios.add(new CriterioEvaluacion("PROYECTOS", R.drawable.icono_proyectos));
        criterios.add(new CriterioEvaluacion("EXÁMENES", R.drawable.icono_examenes));
        criterios.add(new CriterioEvaluacion("PARTICIPACIONES", R.drawable.icono_participaciones));
        criterios.add(new CriterioEvaluacion("SANCIONES", R.drawable.icono_sanciones));

        for(CriterioEvaluacion criterio : criterios){
            ContentValues datos = new ContentValues();
            datos.put("NOMBRE", criterio.getNombre());
            datos.put("ICONO", criterio.getIcono());
            bd.insert(tabla_criterios, null, datos);
        }
    }

    public void definirTemas(SQLiteDatabase bd){
        insertarTema("Gris", "#363C40", "#D9F1FF", "#6C787F", "#C3D8E5", "#818F98", bd);
        insertarTema("Café", "#593825", "#F5E9D9", "#632924", "#8C6B4D", "#510900", bd);
        insertarTema("Rojo", "#F2274C", "#FDDAB2", "#590A10", "#F23D4C", "#DB0D38", bd);
        insertarTema("Rosa", "#D81A4D", "#FDB4EF", "#BF3467", "#F27272", "#F2133C", bd);
        insertarTema("Naranja", "#F26430", "#F2D5A0", "#F22F1D", "#F26241", "#F25430", bd);
        insertarTema("Amarillo", "#7B8020", "#F9FF8D", "#7D8047", "#F5FF41", "#C4CC34", bd);
        insertarTema("Verde", "#304009", "#EEFCA9", "#5F7F13", "#83CC61", "#ACE522", bd);
        insertarTema("Menta", "#02735E", "#B8FFF7", "#018D66", "#66CCA4", "#01503A", bd);
        insertarTema("Azul Claro", "#1767C2", "#DBF2FF", "#004C7F", "#2E91CC", "#2192BF", bd);
        insertarTema("Azul Rey", "#0A0D40", "#6D7ADE", "#024873", "#505AA4", "#04ADBF", bd);
        insertarTema("Morado", "#5B2CBF", "#E7D5F2", "#8500B3", "#9857F2", "#80077B", bd);
    }

    private void insertarTema(
            String nombre,
            String principal,
            String fondo,
            String oscuro,
            String adicional,
            String opcional,
            SQLiteDatabase bd
    ) {
        ContentValues tema = new ContentValues();
        tema.put("NOMBRE", nombre);
        tema.put("COLOR_PRINCIPAL", principal);
        tema.put("COLOR_FONDO", fondo);
        tema.put("COLOR_OSCURO", oscuro);
        tema.put("COLOR_ADICIONAL", adicional);
        tema.put("COLOR_OPCIONAL", opcional);

        // 👇 TABLA CORRECTA
        bd.insert(tabla_temas, null, tema);
    }





}
