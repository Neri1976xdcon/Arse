package BD;
// TODO: Optimizar tiempos de respuesta SQL
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import Adapters.AlumnoAgregar;
import Adapters.SesionClaseHorario;
import Clases.Alumno;
import Clases.CriterioEvaluacion;
import Clases.Dia;
import Clases.Tema;

public class ProcesosDAO {
    private final SQLiteDatabase db;

    public ProcesosDAO(Context context) {
        BaseDatos helper = new BaseDatos(context);
        db = helper.getReadableDatabase();
    }

    public List<SesionClaseHorario> obtenerDias(){
        List<SesionClaseHorario> diashorario = new ArrayList<>();
        String consulta = "SELECT ID_DIA, NOMBRE FROM dias";

        Cursor cur = db.rawQuery(consulta, null);

        if(cur.moveToFirst()){
            do{
                Dia dia = new Dia(
                        cur.getInt(0), cur.getString(1)
                );
                SesionClaseHorario sesion = new SesionClaseHorario(dia);
                diashorario.add(sesion);
            } while(cur.moveToNext());
        }
        cur.close();
        return diashorario;

    }

    public List<CriterioEvaluacion> obtenerCriteriosExistentes() {
        List<CriterioEvaluacion> criterios = new ArrayList<>();

        Cursor cur = db.rawQuery(
                "SELECT ID_CRITERIO, NOMBRE, ICONO FROM criterios_evaluacion",
                null
        );

        if (cur.moveToFirst()) {
            do {
                CriterioEvaluacion criterio = new CriterioEvaluacion(
                        cur.getInt(cur.getColumnIndexOrThrow("ID_CRITERIO")),
                        cur.getString(cur.getColumnIndexOrThrow("NOMBRE")),
                        cur.getInt(cur.getColumnIndexOrThrow("ICONO"))
                );
                criterios.add(criterio);
            } while (cur.moveToNext());
        }

        cur.close();
        return criterios;
    }

    public List<Tema> obtenerTemas() {
        List<Tema> temas = new ArrayList<>();

        Cursor cur = db.rawQuery(
                "SELECT ID_TEMA, NOMBRE, COLOR_PRINCIPAL FROM temas WHERE ID_TEMA > 1",
                null
        );

        if (cur.moveToFirst()) {
            do {
                int id = cur.getInt(cur.getColumnIndexOrThrow("ID_TEMA"));
                String nombre = cur.getString(cur.getColumnIndexOrThrow("NOMBRE"));

                String colorPrincipalStr =
                        cur.getString(cur.getColumnIndexOrThrow("COLOR_PRINCIPAL"));

                int colorPrincipal = Color.parseColor(colorPrincipalStr);

                Tema tema = new Tema(id, nombre, colorPrincipal);
                temas.add(tema);

            } while (cur.moveToNext());
        }

        cur.close();
        return temas;
    }


    public List<Alumno> alumnosRegistrados(){
        List<Alumno> alumnos = new ArrayList<>();

        Cursor cur = db.rawQuery("SELECT ID_ALUMNO, NOMBRE FROM ALUMNOS", null);

        if(cur.moveToNext()){
            do{
                Alumno alumno = new Alumno(
                        cur.getInt(cur.getColumnIndexOrThrow("ID_ALUMNO")), cur.getString(cur.getColumnIndexOrThrow("NOMBRE"))
                );

                alumnos.add(alumno);
            } while(cur.moveToNext());

        } else{
            return null;
        }

        return alumnos;
    }


    public boolean existeAlumno(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return false; // 👈 No existe, no consultes BD
        }

        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM alumnos WHERE nombre = ? LIMIT 1",
                new String[]{ nombre }
        );

        boolean existe = cursor.moveToFirst();
        cursor.close();

        return existe;
    }

    public long registrarAlumno(String nombre) {

        ContentValues values = new ContentValues();
        values.put("NOMBRE", nombre);

        return db.insert("alumnos", null, values);
    }

    public List<Alumno> obtenerAlumnos(String filtro, boolean ordenDesc) {

        List<Alumno> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT ID_ALUMNO, UPPER(NOMBRE) AS NOMBRE FROM alumnos"
        );

        List<String> args = new ArrayList<>();

        // 🔎 Filtro por texto
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(" WHERE NOMBRE LIKE ?");
            args.add("%" + filtro + "%");
        }

        // 🔁 Ordenamiento
        sql.append(" ORDER BY NOMBRE ");
        sql.append(ordenDesc ? "DESC" : "ASC");

        Cursor c = db.rawQuery(
                sql.toString(),
                args.isEmpty() ? null : args.toArray(new String[0])
        );

        while (c.moveToNext()) {
            lista.add(new Alumno(
                    c.getInt(c.getColumnIndexOrThrow("ID_ALUMNO")),
                    c.getString(c.getColumnIndexOrThrow("NOMBRE"))
            ));
        }

        c.close();
        return lista;
    }


    public List<AlumnoAgregar> obtenerAlumnosAgregar(String filtro, boolean ordenDesc) {

        List<AlumnoAgregar> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT ID_ALUMNO, UPPER(NOMBRE) AS NOMBRE FROM alumnos"
        );

        List<String> args = new ArrayList<>();

        // 🔎 Filtro por texto
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(" WHERE NOMBRE LIKE ?");
            args.add("%" + filtro + "%");
        }

        // 🔁 Ordenamiento
        sql.append(" ORDER BY NOMBRE ");
        sql.append(ordenDesc ? "DESC" : "ASC");

        Cursor c = db.rawQuery(
                sql.toString(),
                args.isEmpty() ? null : args.toArray(new String[0])
        );

        while (c.moveToNext()) {
            lista.add(new AlumnoAgregar(
                    c.getInt(c.getColumnIndexOrThrow("ID_ALUMNO")),
                    c.getString(c.getColumnIndexOrThrow("NOMBRE"))
            ));
        }

        c.close();
        return lista;
    }



    public boolean hayAlumnosRegistrados() {

        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM alumnos LIMIT 1",
                null
        );

        boolean hayAlumnos = cursor.moveToFirst();
        cursor.close();

        return hayAlumnos;
    }








}
