package com.example.arse;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

import Adapters.CumplimientoAlumnoAdapter;
import Adapters.SemanaAdapter;
import BD.BaseDat;
import Clases.AlumnoCumplimiento;
import Clases.SemanaCalendario;
import Entity.Alumno;
import Entity.SesionClase;

public class BajoCumplimiento extends AppCompatActivity {
    private static final int MINIMO_ASISTENCIA = 70;

    private BaseDat baseDatos;

    private int idClase;

    private int idAlumnoSeleccionado = -1;

    private RecyclerView rvAlumnosRiesgo;
    private RecyclerView rvAlumnosCritico;
    private RecyclerView rvSemanasAlumnoCalendario;

    private TextView numeroAlumnosRiesgo;
    private TextView numeroAlumnosCriticos;

    private Spinner seleccionMes;

    private AppCompatButton btnRegresar;

    private CumplimientoAlumnoAdapter adapterRiesgo;
    private CumplimientoAlumnoAdapter adapterCritico;

    private SemanaAdapter semanaAdapter;

    private List<SemanaCalendario> listaSemanas =
            new ArrayList<>();

    private List<String> listaMesesBD =
            new ArrayList<>();

    private List<String> listaMesesVisual =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_bajo_cumplimiento
        );

        baseDatos =
                BaseDat.getInstance(this);

        idClase =
                getIntent()
                        .getIntExtra(
                                "idClase",
                                -1
                        );

        inicializarVistas();

        configurarRecyclerViews();

        configurarEventos();

        cargarMeses();

        cargarAlumnosCumplimiento();
    }

    private void inicializarVistas(){

        rvAlumnosRiesgo =
                findViewById(
                        R.id.rvAlumnosRiesgo
                );

        rvAlumnosCritico =
                findViewById(
                        R.id.rvAlumnosCritico
                );

        rvSemanasAlumnoCalendario =
                findViewById(
                        R.id.rvSemanasAlumnoCalendario
                );

        numeroAlumnosRiesgo =
                findViewById(
                        R.id.numeroAlumnosRiesgo
                );

        numeroAlumnosCriticos =
                findViewById(
                        R.id.numeroAlumnosCriticos
                );

        seleccionMes =
                findViewById(
                        R.id.seleccionMes
                );

        btnRegresar =
                findViewById(
                        R.id.btnRegresar
                );
    }

    private void configurarRecyclerViews(){

        rvAlumnosRiesgo.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvAlumnosCritico.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvSemanasAlumnoCalendario
                .setLayoutManager(
                        new LinearLayoutManager(this)
                );
    }

    private void configurarEventos(){

        btnRegresar.setOnClickListener(v -> {

            finish();
        });

        seleccionMes.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        String mes =
                                listaMesesBD.get(position);

                        if(idAlumnoSeleccionado == -1){

                            cargarCalendarioGeneral(mes);

                        }else{

                            cargarCalendarioAlumno(
                                    idAlumnoSeleccionado,
                                    mes
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                    }
                }
        );
    }

    private void cargarMeses(){

        Executors.newSingleThreadExecutor()
                .execute(() -> {

                    listaMesesBD =
                            baseDatos
                                    .sesionClaseDao()
                                    .obtenerMesesClase(
                                            idClase
                                    );

                    listaMesesVisual.clear();

                    for(String mes : listaMesesBD){

                        listaMesesVisual.add(
                                formatearMes(mes)
                        );
                    }

                    runOnUiThread(() -> {

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(
                                        this,
                                        R.layout.item_spinner,
                                        listaMesesVisual
                                );

                        adapter.setDropDownViewResource(
                                R.layout
                                        .item_spinner_dropdown
                        );

                        seleccionMes.setAdapter(
                                adapter
                        );
                    });
                });
    }

    private void cargarAlumnosCumplimiento(){

        Executors.newSingleThreadExecutor()
                .execute(() -> {

                    List<Alumno> alumnos =
                            baseDatos
                                    .alumnoDao()
                                    .obtenerAlumnosGrupo(
                                            baseDatos
                                                    .grupoDao()
                                                    .obtenerIdGrupoClase(
                                                            idClase
                                                    )
                                    );

                    List<AlumnoCumplimiento> riesgo =
                            new ArrayList<>();

                    List<AlumnoCumplimiento> criticos =
                            new ArrayList<>();

                    for(Alumno alumno : alumnos){

                        int total =
                                baseDatos
                                        .asistenciaAlumnoDao()
                                        .obtenerTotalAsistenciasClase(
                                                alumno.getId(),
                                                idClase
                                        );

                        if(total == 0)
                            continue;

                        int porcentaje =
                                calcularPorcentajeAlumno(
                                        alumno.getId()
                                );

                        AlumnoCumplimiento item =
                                new AlumnoCumplimiento(
                                        alumno,
                                        porcentaje
                                );

                        if(porcentaje
                                < MINIMO_ASISTENCIA){

                            criticos.add(item);

                        }else if(
                                porcentaje
                                        <= MINIMO_ASISTENCIA + 10){

                            riesgo.add(item);
                        }
                    }

                    Collections.sort(
                            riesgo,
                            (a,b) ->
                                    a.getPorcentaje()
                                            - b.getPorcentaje()
                    );

                    Collections.sort(
                            criticos,
                            (a,b) ->
                                    a.getPorcentaje()
                                            - b.getPorcentaje()
                    );

                    runOnUiThread(() -> {

                        numeroAlumnosRiesgo.setText(
                                String.valueOf(
                                        riesgo.size()
                                )
                        );

                        numeroAlumnosCriticos.setText(
                                String.valueOf(
                                        criticos.size()
                                )
                        );

                        adapterRiesgo =
                                new CumplimientoAlumnoAdapter(
                                        this,
                                        riesgo,
                                        true,
                                        true
                                );

                        adapterRiesgo
                                .setOnAlumnoClickListener(
                                        alumno -> {

                                            idAlumnoSeleccionado =
                                                    alumno.getId();

                                            adapterRiesgo
                                                    .seleccionarAlumno(
                                                            alumno.getId()
                                                    );

                                            if(adapterCritico != null){

                                                adapterCritico
                                                        .seleccionarAlumno(
                                                                -1
                                                        );
                                            }

                                            cargarCalendarioAlumno(
                                                    alumno.getId(),
                                                    listaMesesBD.get(
                                                            seleccionMes
                                                                    .getSelectedItemPosition()
                                                    )
                                            );
                                        }
                                );

                        rvAlumnosRiesgo.setAdapter(
                                adapterRiesgo
                        );

                        adapterCritico =
                                new CumplimientoAlumnoAdapter(
                                        this,
                                        criticos,
                                        false,
                                        true
                                );

                        adapterCritico
                                .setOnAlumnoClickListener(
                                        alumno -> {

                                            idAlumnoSeleccionado =
                                                    alumno.getId();

                                            adapterCritico
                                                    .seleccionarAlumno(
                                                            alumno.getId()
                                                    );

                                            if(adapterRiesgo != null){

                                                adapterRiesgo
                                                        .seleccionarAlumno(
                                                                -1
                                                        );
                                            }

                                            cargarCalendarioAlumno(
                                                    alumno.getId(),
                                                    listaMesesBD.get(
                                                            seleccionMes
                                                                    .getSelectedItemPosition()
                                                    )
                                            );
                                        }
                                );

                        rvAlumnosCritico.setAdapter(
                                adapterCritico
                        );
                    });
                });
    }

    private int calcularPorcentajeAlumno(
            int idAlumno){

        int total =
                baseDatos
                        .asistenciaAlumnoDao()
                        .obtenerTotalAsistenciasClase(
                                idAlumno,
                                idClase
                        );

        int presentes =
                baseDatos
                        .asistenciaAlumnoDao()
                        .obtenerTotalPresentesClase(
                                idAlumno,
                                idClase
                        );

        if(total == 0)
            return 0;

        return (int)(
                (presentes * 100f)
                        / total
        );
    }

    private void cargarCalendarioGeneral(
            String mes){

        Executors.newSingleThreadExecutor()
                .execute(() -> {

                    List<SesionClase> sesiones =
                            baseDatos
                                    .sesionClaseDao()
                                    .obtenerSesionesMesClase(
                                            idClase,
                                            mes
                                    );

                    for(SesionClase sesion : sesiones){

                        int total =
                                baseDatos
                                        .asistenciaAlumnoDao()
                                        .obtenerTotalSesion(
                                                sesion.getId()
                                        );

                        // SIN ASISTENCIA
                        if(total == 0){

                            sesion.setAsistenciaAlumno(
                                    null
                            );

                        }else{

                            // CON ASISTENCIA
                            sesion.setAsistenciaAlumno(
                                    true
                            );
                        }
                    }

                    listaSemanas =
                            generarSemanasCalendario(
                                    sesiones
                            );

                    runOnUiThread(() -> {

                        semanaAdapter =
                                new SemanaAdapter(
                                        this,
                                        listaSemanas,
                                        true
                                );

                        rvSemanasAlumnoCalendario
                                .setAdapter(
                                        semanaAdapter
                                );
                    });
                });
    }

    private void cargarCalendarioAlumno(
            int idAlumno,
            String mes){

        Executors.newSingleThreadExecutor()
                .execute(() -> {

                    List<SesionClase> sesiones =
                            baseDatos
                                    .sesionClaseDao()
                                    .obtenerSesionesMesClase(
                                            idClase,
                                            mes
                                    );

                    for(SesionClase sesion : sesiones){

                        Boolean asistencia =
                                baseDatos
                                        .asistenciaAlumnoDao()
                                        .obtenerAsistenciaAlumnoSesion(
                                                idAlumno,
                                                sesion.getId()
                                        );

                        sesion.setAsistenciaAlumno(
                                asistencia
                        );
                    }

                    listaSemanas =
                            generarSemanasCalendario(
                                    sesiones
                            );

                    runOnUiThread(() -> {

                        semanaAdapter =
                                new SemanaAdapter(
                                        this,
                                        listaSemanas,
                                        true
                                );

                        rvSemanasAlumnoCalendario
                                .setAdapter(
                                        semanaAdapter
                                );
                    });
                });
    }

    private List<SemanaCalendario>
    generarSemanasCalendario(
            List<SesionClase> sesiones){

        List<SemanaCalendario> semanas =
                new ArrayList<>();

        Map<Integer, SemanaCalendario> mapa =
                new LinkedHashMap<>();

        Calendar calendar =
                Calendar.getInstance();

        for(SesionClase sesion : sesiones){

            try{

                Date fecha =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        ).parse(
                                sesion.getFecha()
                        );

                calendar.setTime(fecha);

                int semana =
                        calendar.get(
                                Calendar.WEEK_OF_YEAR
                        );

                SemanaCalendario item =
                        mapa.get(semana);

                if(item == null){

                    item =
                            new SemanaCalendario(
                                    "SEMANA " + semana,
                                    new ArrayList<>()
                            );

                    mapa.put(
                            semana,
                            item
                    );
                }

                item.getSesiones()
                        .add(sesion);

            }catch (Exception e){

                e.printStackTrace();
            }
        }

        semanas.addAll(
                mapa.values()
        );

        return semanas;
    }

    private String formatearMes(
            String mesBD){

        String[] partes =
                mesBD.split("-");

        String numeroMes =
                partes[0];

        String anio =
                partes[1];

        String nombreMes =
                "";

        switch (numeroMes){

            case "01":
                nombreMes = "ENERO";
                break;

            case "02":
                nombreMes = "FEBRERO";
                break;

            case "03":
                nombreMes = "MARZO";
                break;

            case "04":
                nombreMes = "ABRIL";
                break;

            case "05":
                nombreMes = "MAYO";
                break;

            case "06":
                nombreMes = "JUNIO";
                break;

            case "07":
                nombreMes = "JULIO";
                break;

            case "08":
                nombreMes = "AGOSTO";
                break;

            case "09":
                nombreMes = "SEPTIEMBRE";
                break;

            case "10":
                nombreMes = "OCTUBRE";
                break;

            case "11":
                nombreMes = "NOVIEMBRE";
                break;

            case "12":
                nombreMes = "DICIEMBRE";
                break;
        }

        return nombreMes + " - " + anio;
    }
}