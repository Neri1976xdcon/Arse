package com.example.arse;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import Adapters.CriterioClaseAdapter;
import Adapters.CriterioPagerAdapter;
import BD.BaseDat;
import DAO.AlumnoDAO;
import DAO.AulaDAO;
import DAO.ClaseDAO;
import DAO.CriterioDAO;
import DAO.GrupoDAO;
import DAO.TemaDAO;
import Entity.Alumno;
import Entity.Aula;
import Entity.Clase;
import Entity.CriterioEvaluacion;
import Entity.Grupo;
import Entity.Tema;

public class InterfazClase extends AppCompatActivity {

    public static final String EXTRA_ID_CLASE =
            "idclase";

    private int idClase;
    private int idGrupo;

    private BaseDat bd;
    private ClaseDAO claseDao;
    private GrupoDAO grupoDao;
    private AulaDAO aulaDao;
    private TemaDAO temaDao;
    private CriterioDAO criterioDao;

    private final Executor executor =
            Executors.newSingleThreadExecutor();


    private AppCompatButton btnRegresar;

    private TextView txtNombre;
    private TextView txtGrupo;
    private TextView txtAula;

    private LinearLayout cabecera;
    private LinearLayout grupoContenedor;
    private LinearLayout aulaContenedor;

    private ImageView iconoGrupo;
    private ImageView iconoAula;

    private AlumnoDAO alumnoDao;
    private ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_interfaz_clase
        );


        btnRegresar =
                findViewById(
                        R.id.btnRegresar
                );

        txtNombre =
                findViewById(
                        R.id.nombreClaseInterfaz
                );

        txtGrupo =
                findViewById(
                        R.id.grupoClaseIntefaz
                );

        txtAula =
                findViewById(
                        R.id.aulaClaseIntefaz
                );

        cabecera =
                findViewById(
                        R.id.cabeceraClase
                );

        grupoContenedor =
                findViewById(
                        R.id.infoGrupoClase
                );

        aulaContenedor =
                findViewById(
                        R.id.infoAulaClase
                );

        iconoGrupo =
                findViewById(
                        R.id.infoIconoGrupo
                );

        iconoAula =
                findViewById(
                        R.id.infoIconoAula
                );

        btnRegresar.setOnClickListener(
                v -> finish()
        );

        viewPager =
                findViewById(
                        R.id.viewPagerCriterios
                );

        bd =
                BaseDat.getInstance(this);

        claseDao =
                bd.claseDao();

        grupoDao =
                bd.grupoDao();

        aulaDao =
                bd.aulaDao();

        temaDao =
                bd.temaDao();
        criterioDao = bd.criterioDao();

        alumnoDao        = bd.alumnoDao();

        idClase =
                getIntent().getIntExtra(
                        EXTRA_ID_CLASE,
                        -1
                );


        if (idClase == -1) {

            finish();
            return;

        }

        cargarClase();
        configurarBotones();
        actualizarIndicadores();
    }

    private void cargarClase() {

        executor.execute(() -> {

            Clase clase =
                    claseDao.obtenerClasePorId(
                            idClase
                    );

            if (clase == null)
                return;

            Grupo grupo =
                    grupoDao.obtenerGrupo(
                            clase.getIdGrupo()
                    );

            if (grupo != null) {
                idGrupo = grupo.getId();
            } else {
                idGrupo = -1;
            }

            String nombreGrupo =
                    grupo != null
                            ? grupo.getNombre()
                            : "Sin grupo";

            Aula aula = null;

            if (grupo != null) {

                aula =
                        aulaDao.obtenerAula(
                                grupo.getAulaId()
                        );

            }

            String nombreAula =
                    aula != null
                            ? aula.getNombre()
                            : "Sin aula";

            Tema tema =
                    temaDao.obtenerTemaItem(
                            clase.getIdTema()
                    );

            idGrupo = clase.getIdGrupo();
            List<Alumno> alumnos = alumnoDao.obtenerAlumnosGrupo(idGrupo);

            List<String> fechas = Arrays.asList("04","06","11","13","18","20","25","27");

            runOnUiThread(() -> {

                if (isFinishing())
                    return;

                txtNombre.setText(
                        clase.getNombre()
                );

                txtGrupo.setText(
                        nombreGrupo
                );

                txtAula.setText(
                        nombreAula
                );

                if (tema != null) {

                    aplicarTema(
                            tema
                    );

                    inicializarCriterios(
                            idClase,
                            obtenerColorSeguro(
                                    tema.getColorFondo(),
                                    R.color.blanco
                            )
                    );

                }


            });

        });

    }

    private void aplicarTema(
            Tema tema
    ) {

        int colorPrincipal =
                obtenerColorSeguro(
                        tema.getColorPrincipal(),
                        R.color.azulArse
                );

        int colorFondo =
                obtenerColorSeguro(
                        tema.getColorFondo(),
                        R.color.blanco
                );

        int colorRelleno =
                obtenerColorSeguro(
                        tema.getColorRelleno(),
                        R.color.negro
                );

        btnRegresar.setBackgroundTintList(
                ColorStateList.valueOf(
                        colorFondo
                )
        );

        btnRegresar.setTextColor(
                colorRelleno
        );

        aplicarColorFondo(
                cabecera,
                colorPrincipal
        );

        aplicarColorFondo(
                grupoContenedor,
                colorFondo
        );

        iconoGrupo.setImageTintList(
                ColorStateList.valueOf(
                        colorRelleno
                )
        );

        txtGrupo.setTextColor(
                colorRelleno
        );

        aplicarColorFondo(
                aulaContenedor,
                colorFondo
        );

        iconoAula.setImageTintList(
                ColorStateList.valueOf(
                        colorRelleno
                )
        );

        txtAula.setTextColor(
                colorRelleno
        );
    }

    private void aplicarColorFondo(
            View view,
            int color){

        Drawable drawable =
                view.getBackground()
                        .getConstantState()
                        .newDrawable()
                        .mutate();

        if(drawable instanceof GradientDrawable){

            GradientDrawable fondo =
                    (GradientDrawable) drawable;

            fondo.setColor(color);

            view.setBackground(fondo);
        }
    }


    private int obtenerColorSeguro(
            String color,
            int colorDefault
    ) {

        try {

            return Color.parseColor(
                    color
            );

        } catch (Exception e) {

            return ContextCompat.getColor(
                    this,
                    colorDefault
            );
        }

    }

    private void inicializarCriterios(
            int idClase,
            int colorFondo
    ) {

        RecyclerView rvCriterios =
                findViewById(
                        R.id.rvCriteriosClase
                );

        TextView txtNombreCriterio =
                findViewById(
                        R.id.nombreCriterioClase
                );

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

        rvCriterios.setLayoutManager(
                layoutManager
        );

        executor.execute(() -> {

            List<CriterioEvaluacion> lista =
                    criterioDao.criteriosClase(
                            idClase
                    );

            runOnUiThread(() -> {

                if (lista == null || lista.isEmpty())
                    return;

                CriterioPagerAdapter pagerAdapter =
                        new CriterioPagerAdapter(
                                this,
                                lista,
                                idGrupo,
                                idClase
                        );

                viewPager.setAdapter(
                        pagerAdapter
                );

                CriterioClaseAdapter adapter =
                        new CriterioClaseAdapter(
                                this,
                                lista,
                                colorFondo,
                                true,
                                criterio -> {

                                    txtNombreCriterio.setText(
                                            criterio.getNombre()
                                    );

                                    int posicion =
                                            lista.indexOf(
                                                    criterio
                                            );

                                    viewPager.setCurrentItem(
                                            posicion
                                    );

                                }
                        );

                rvCriterios.setAdapter(
                        adapter
                );

                txtNombreCriterio.setText(
                        lista.get(0).getNombre()
                );

                viewPager.registerOnPageChangeCallback(
                        new ViewPager2.OnPageChangeCallback() {

                            @Override
                            public void onPageSelected(
                                    int position
                            ) {
                                super.onPageSelected(position);

                                rvCriterios.scrollToPosition(
                                        position
                                );
                            }
                        }
                );

                viewPager.registerOnPageChangeCallback(
                        new ViewPager2.OnPageChangeCallback() {

                            @Override
                            public void onPageSelected(int position) {

                                super.onPageSelected(position);

                                adapter.seleccionarPosicion(position);

                                rvCriterios.smoothScrollToPosition(position);

                            }

                        }
                );

            });

        });


    }



    private View filaActiva = null;
    private int colorFilaActiva = Color.WHITE;

    private void resaltarFila(View fila, int colorOriginal) {

        if (filaActiva != null && filaActiva != fila) {
            filaActiva.setBackgroundColor(colorFilaActiva);
        }

        if (filaActiva == fila) {
            fila.setBackgroundColor(colorOriginal);
            filaActiva = null;
        } else {
            colorFilaActiva = colorOriginal;
            fila.setBackgroundColor(
                    Color.parseColor("#D6E8FB")
            );
            filaActiva = fila;
        }
    }

    private void configurarBotones() {

        LinearLayout btnCalendario =
                findViewById(R.id.btnCalendarioCumplimiento);

        btnCalendario.setOnClickListener(view -> {

            Intent abrirCalendario =
                    new Intent(
                            InterfazClase.this,
                            Calendario.class
                    );

            abrirCalendario.putExtra(
                    "idClase",
                    idClase
            );

            startActivity(abrirCalendario);
        });

        LinearLayout btnBajoCumplimiento =
                findViewById(
                        R.id.btnBajoCumplimiento
                );

        btnBajoCumplimiento.setOnClickListener(v -> {

            Intent bajoCump =
                    new Intent(
                            this,
                            BajoCumplimiento.class
                    );

            bajoCump.putExtra(
                    "idClase",
                    idClase
            );

            startActivity(bajoCump);
        });
    }


    public void actualizarIndicadores() {

        TextView porcentaje =
                findViewById(
                        R.id.porcentajeCumplimientoReciente
                );

        TextView numAlumnosRiesgo =
                findViewById(
                        R.id.numeroAlumnosRiesgo
                );

        new Thread(() -> {

            float porcentajeNumero =
                    bd.asistenciaAlumnoDao()
                            .obtenerPorcentajeUltimaSesionConAsistencia(
                                    idClase
                            );

            int criticos =
                    bd.asistenciaAlumnoDao()
                            .obtenerCantidadAlumnosCriticos(
                                    idClase,
                                    70
                            );

            int riesgo =
                    bd.asistenciaAlumnoDao()
                            .obtenerCantidadAlumnosRiesgo(
                                    idClase,
                                    70,
                                    80
                            );

            runOnUiThread(() -> {

                porcentaje.setText(
                        ((int) porcentajeNumero) + "%"
                );

                numAlumnosRiesgo.setText(
                        String.valueOf(
                                criticos + riesgo
                        )
                );
            });

        }).start();
    }



}