package com.example.arse;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Adapters.CumplimientoAlumnoAdapter;
import Adapters.SemanaAdapter;
import BD.BaseDat;
import Clases.AlumnoAsistencia;
import Clases.SemanaCalendario;
import DAO.AlumnoDAO;
import DAO.AsistenciaAlumnoDAO;
import DAO.SesionClaseDAO;
import Entity.SesionClase;

public class Calendario extends AppCompatActivity {

    private Spinner seleccionMes;

    private RecyclerView rvSemanasCalendario;
    private RecyclerView rvAlumnos;

    private EditText buscadorAlumnos;

    private TextView porcentajeCumplimientoGeneral;
    private TextView textoCumplimientoGeneral;

    private TextView porcentaje;
    private TextView textoIndicadorPorcentaje;

    private TextView mensajeVacio;

    private BaseDat baseDatos;

    private SesionClaseDAO sesionClaseDao;
    private AsistenciaAlumnoDAO asistenciaAlumnoDao;
    private AlumnoDAO alumnoDao;

    private SemanaAdapter semanaAdapter;
    private CumplimientoAlumnoAdapter alumnoAdapter;

    private int idClase;

    private List<String> listaMeses;
    private AppCompatButton conAsistencia;
    private AppCompatButton sinAsistencia;
    private List<String> listaMesesBD;
    private List<String> listaMesesVisual;
    private int idSesionSeleccionada = -1;

    private boolean mostrarAsistencias = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_calendario);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        idClase = getIntent()
                .getIntExtra("idClase", -1);

        inicializarVistas();

        inicializarBaseDatos();

        inicializarRecyclerViews();

        cargarMeses();

        eventos();
        actualizarBotonesFiltro();
    }

    private void inicializarVistas(){

        seleccionMes =
                findViewById(R.id.seleccionMes);

        rvSemanasCalendario =
                findViewById(R.id.rvSemanasCalendario);

        rvAlumnos =
                findViewById(R.id.rvAlumnos);

        buscadorAlumnos =
                findViewById(R.id.buscadorAlumnos);

        porcentajeCumplimientoGeneral =
                findViewById(
                        R.id.porcentajeCumplimientoGeneral
                );

        textoCumplimientoGeneral =
                findViewById(
                        R.id.textoCumplimientoGeneral
                );

        porcentaje =
                findViewById(R.id.porcentaje);

        textoIndicadorPorcentaje =
                findViewById(
                        R.id.textoIndicadorPorcentaje
                );

        conAsistencia =
                findViewById(R.id.conAsistencia);

        sinAsistencia =
                findViewById(R.id.sinAsistencia);

        mensajeVacio =
                findViewById(R.id.mensajeVacio);
    }

    private void inicializarBaseDatos(){

        baseDatos = BaseDat.getInstance(this);

        sesionClaseDao =
                baseDatos.sesionClaseDao();

        asistenciaAlumnoDao =
                baseDatos.asistenciaAlumnoDao();

        alumnoDao =
                baseDatos.alumnoDao();
    }

    private void inicializarRecyclerViews(){

        rvSemanasCalendario.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvAlumnos.setLayoutManager(
                new LinearLayoutManager(this)
        );

        semanaAdapter = new SemanaAdapter(
                this,
                new ArrayList<>(),

                new SemanaAdapter.OnDiaClickListener() {

                    @Override
                    public void onDiaClick(
                            SesionClase sesion) {

                        cargarAlumnosSesion(
                                sesion.getId()
                        );

                        actualizarPorcentajeSesion(
                                sesion.getId()
                        );
                    }

                    @Override
                    public void onSemanaClick(
                            List<SesionClase> sesiones) {

                        actualizarPorcentajeSemana(
                                sesiones
                        );

                        mostrarMensajeSemana();
                    }
                }
        );

        alumnoAdapter =
                new CumplimientoAlumnoAdapter(
                        this,
                        new ArrayList<>()
                );

        rvSemanasCalendario.setAdapter(
                semanaAdapter
        );

        rvAlumnos.setAdapter(
                alumnoAdapter
        );
    }

    private void actualizarBotonesFiltro(){

        if(mostrarAsistencias){

            conAsistencia.setSelected(true);
            sinAsistencia.setSelected(false);

        }else{

            conAsistencia.setSelected(false);
            sinAsistencia.setSelected(true);
        }
    }

    private void eventos(){

        AppCompatButton btnRegreso = findViewById(R.id.btnRegresar);
        btnRegreso.setOnClickListener(vi -> finish());

        buscadorAlumnos.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {

                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        alumnoAdapter.filtrar(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {

                    }
                }
        );

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

                        cargarCalendarioMes(mes);
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                    }
                }
        );

        conAsistencia.setOnClickListener(v -> {

            mostrarAsistencias = true;

            actualizarBotonesFiltro();

            if(idSesionSeleccionada != -1){

                alumnoAdapter.filtrarAsistencia(true);
            }
        });

        sinAsistencia.setOnClickListener(v -> {

            mostrarAsistencias = false;

            actualizarBotonesFiltro();

            if(idSesionSeleccionada != -1){

                alumnoAdapter.filtrarAsistencia(false);
            }
        });
    }

    private void cargarMeses(){


        new Thread(() -> {

            listaMesesBD =
                    sesionClaseDao.obtenerMesesClase(idClase);

            listaMesesVisual = new ArrayList<>();

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

                seleccionMes.setAdapter(adapter);
            });

        }).start();
    }

    private void cargarCalendarioMes(String mes){

        new Thread(() -> {

            List<SesionClase> sesiones =
                    sesionClaseDao
                            .obtenerSesionesMesClase(
                                    idClase,
                                    mes
                            );

            List<SemanaCalendario> semanas =
                    generarSemanas(sesiones);

            Float porcentajeGeneral =
                    asistenciaAlumnoDao
                            .obtenerPorcentajeMesClase(
                                    idClase,
                                    mes
                            );

            runOnUiThread(() -> {

                actualizarIndicadorGeneral(
                        porcentajeGeneral
                );

                semanaAdapter.actualizarLista(semanas);
                idSesionSeleccionada = -1;

                if(!semanas.isEmpty()){

                    semanaAdapter.seleccionarUltimaSesion();

                }else{

                    mostrarMensajeSemana();
                }
            });

        }).start();
    }

    private void mostrarMensajeSemana(){

        mensajeVacio.setVisibility(View.VISIBLE);

        rvAlumnos.setVisibility(View.GONE);

        mensajeVacio.setText(
                "SELECCIONE UN DÍA PARA VER LOS ALUMNOS"
        );
    }

    private void mostrarRecyclerAlumnos(){

        mensajeVacio.setVisibility(View.GONE);

        rvAlumnos.setVisibility(View.VISIBLE);
    }

    private void cargarAlumnosSesion(
            int idSesion){

        idSesionSeleccionada = idSesion;

        new Thread(() -> {

            List<AlumnoAsistencia> alumnos =
                    asistenciaAlumnoDao
                            .obtenerAlumnosSesion(
                                    idSesion
                            );

            runOnUiThread(() -> {

                mostrarRecyclerAlumnos();

                alumnoAdapter.actualizarLista(alumnos);

                alumnoAdapter.filtrarAsistencia(
                        mostrarAsistencias
                );
            });

        }).start();
    }

    private void actualizarPorcentajeSemana(
            List<SesionClase> sesiones){

        new Thread(() -> {

            List<Integer> ids = new ArrayList<>();

            for(SesionClase sesion : sesiones){

                ids.add(sesion.getId());
            }

            float porcentajeSemana =
                    asistenciaAlumnoDao
                            .obtenerPorcentajeSemana(
                                    ids
                            );

            runOnUiThread(() -> {

                actualizarIndicadorSemana(
                        porcentajeSemana
                );
            });

        }).start();
    }

    private void actualizarIndicadorGeneral(
            Float porcentajeValor){

        GradientDrawable fondo =
                new GradientDrawable();

        fondo.setShape(
                GradientDrawable.RECTANGLE
        );

        fondo.setCornerRadius(30f);

        if(porcentajeValor == null){

            porcentajeCumplimientoGeneral
                    .setText("--");

            textoCumplimientoGeneral
                    .setTextColor(
                            getColor(
                                    R.color.grisPalidoOscuro
                            )
                    );

            fondo.setColor(
                    getColor(
                            R.color.grisPalidoClaro
                    )
            );

            porcentajeCumplimientoGeneral
                    .setBackground(fondo);

            return;
        }

        int color =
                obtenerColorPorcentaje(
                        porcentajeValor
                );

        porcentajeCumplimientoGeneral
                .setText(
                        porcentajeValor.intValue()
                                + "%"
                );

        fondo.setColor(color);

        porcentajeCumplimientoGeneral
                .setBackground(fondo);

        textoCumplimientoGeneral
                .setTextColor(color);
    }

    private void actualizarIndicadorSemana(
            Float porcentajeValor){

        if(porcentajeValor == null){

            porcentaje.setText("--");

            textoIndicadorPorcentaje.setTextColor(
                    getColor(
                            R.color.grisPalidoOscuro
                    )
            );

            GradientDrawable fondo =
                    (GradientDrawable)
                            ContextCompat.getDrawable(
                                            this,
                                            R.drawable.fondo_redondeado
                                    )
                                    .getConstantState()
                                    .newDrawable()
                                    .mutate();

            fondo.setColor(
                    getColor(
                            R.color.grisPalidoClaro
                    )
            );

            porcentaje.setBackground(fondo);

            FrameLayout.LayoutParams params =
                    (FrameLayout.LayoutParams)
                            porcentaje.getLayoutParams();

            params.width =
                    FrameLayout.LayoutParams.WRAP_CONTENT;

            porcentaje.setLayoutParams(params);

            porcentaje.invalidate();

            return;
        }

        int color =
                obtenerColorPorcentaje(
                        porcentajeValor
                );

        porcentaje.setText(
                porcentajeValor.intValue()
                        + "%"
        );

        textoIndicadorPorcentaje.setTextColor(
                color
        );

        View contenedor =
                findViewById(
                        R.id.contenedorPorcentaje
                );

        GradientDrawable fondo =
                new GradientDrawable();

        fondo.setShape(
                GradientDrawable.RECTANGLE
        );

        fondo.setCornerRadius(30f);

        fondo.setColor(color);

        porcentaje.setBackground(fondo);

        contenedor.post(() -> {

            FrameLayout.LayoutParams params =
                    (FrameLayout.LayoutParams)
                            porcentaje.getLayoutParams();

            int anchoContenedor =
                    contenedor.getWidth();

            params.width =
                    Math.max(
                            120,
                            (int)(
                                    anchoContenedor
                                            * (porcentajeValor / 100f)
                            )
                    );

            porcentaje.setLayoutParams(params);

            porcentaje.requestLayout();
        });
    }

    private void aplicarColorBoton(
            AppCompatButton boton,
            int colorRes){

        GradientDrawable fondo =
                new GradientDrawable();

        fondo.setShape(
                GradientDrawable.RECTANGLE
        );

        fondo.setCornerRadius(30f);

        fondo.setColor(
                ContextCompat.getColor(
                        this,
                        colorRes
                )
        );

        boton.setBackground(fondo);
    }

    private int obtenerColorPorcentaje(
            float porcentaje){

        if(porcentaje < 50)
            return getColor(R.color.rojoError);

        else if(porcentaje < 70)
            return getColor(R.color.naranjaOscuro);

        else if(porcentaje < 80)
            return getColor(R.color.amarilloOscuro);

        else if(porcentaje < 90)
            return getColor(R.color.azulArse);

        else
            return getColor(R.color.verdeDrawer);
    }

    private List<SemanaCalendario> generarSemanas(
            List<SesionClase> sesiones){

        List<SemanaCalendario> semanas =
                new ArrayList<>();

        int contador = 1;

        for(int i = 0;
            i < sesiones.size();
            i += 5){

            int fin =
                    Math.min(i + 5,
                            sesiones.size());

            semanas.add(
                    new SemanaCalendario(
                            "Semana " + contador,
                            sesiones.subList(i, fin)
                    )
            );

            contador++;
        }

        return semanas;
    }

    private String formatearMes(String mesBD){

        String[] partes = mesBD.split("-");

        String numeroMes = partes[0];
        String anio = partes[1];

        String nombreMes = "";

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

    private void actualizarPorcentajeSesion(
            int idSesion){

        new Thread(() -> {

            float porcentajeSesion =
                    asistenciaAlumnoDao
                            .obtenerPorcentajeSesion(
                                    idSesion
                            );

            runOnUiThread(() -> {

                actualizarIndicadorSemana(
                        porcentajeSesion
                );
            });

        }).start();
    }
}