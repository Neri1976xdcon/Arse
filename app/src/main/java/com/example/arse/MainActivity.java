package com.example.arse;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import Adapters.ClaseAdapter;
import BD.BaseDat;
import Clases.ClaseItem;
import DAO.AulaDAO;
import DAO.ClaseDAO;
import DAO.DiaDAO;
import DAO.GrupoDAO;
import DAO.SesionClaseDAO;
import DAO.TemaDAO;
import Entity.Aula;
import Entity.Clase;
import Entity.Dia;
import Entity.Grupo;
import Entity.SesionClase;
import Entity.Tema;

public class MainActivity extends AppCompatActivity {
    private BaseDat bd;
    private DiaDAO diaDao;
    private DrawerLayout drawerLayout;
    private LinearLayout drawer;
    private ImageButton btnFlecha;

    ClaseAdapter adapterClases;
    AulaDAO aulaDao;
    GrupoDAO grupoDao;
    ClaseDAO claseDao;
    SesionClaseDAO sesionClaseDao;
    TemaDAO temaDao;
    int spinnerSeleccionado;

    TextView mensajeClases;

    private Executor executor =
            Executors.newSingleThreadExecutor();

    Spinner opciones;

    RecyclerView rvClases;

    ImageView iconoPalomitaLogo, iconoTacheLogo;
    boolean pendientes;
    LinearLayout asistenciaActiva;
    TextView asistenciaActivaAsignatura;

    private Clase claseActivaGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawer = findViewById(R.id.drawer_derecho);
        btnFlecha = findViewById(R.id.btnFlechaDrawer);

        bd = BaseDat.getInstance(this);
        diaDao = bd.diaDao();
        aulaDao = bd.aulaDao();
        grupoDao = bd.grupoDao();
        claseDao = bd.claseDao();
        temaDao = bd.temaDao();
        sesionClaseDao = bd.sesionClaseDao();

        mensajeClases = findViewById(R.id.mensajeClases);
        rvClases = findViewById(R.id.rvClases);

        rvClases.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvClases.setVisibility(View.GONE);
        mensajeClases.setVisibility(View.GONE);

        configurarDrawer();
        configurarBottomSheet();
        elementos();

        spinnerSeleccionado = 1;
        cargarClases(spinnerSeleccionado);

        opciones = findViewById(R.id.opcionesClases);
        String[] opcionesSpinner = {"HOY", "TODAS"};

        ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                opcionesSpinner
        );

        adapterSpin.setDropDownViewResource(
                R.layout.item_spinner_dropdown
        );

        opciones.setAdapter(adapterSpin);

        opciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSeleccionado = position;
                cargarClases(position); // ← pasar position directo, no spinnerSeleccionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        asistenciaActiva = findViewById(R.id.asistenciaActiva);
        asistenciaActivaAsignatura = findViewById(R.id.asistenciaActivaAsignatura);
        asistenciaActiva.setOnClickListener(view -> {

            if (claseActivaGlobal == null) return;

            Intent intent = new Intent(this, InterfazClase.class);
            intent.putExtra("idclase", claseActivaGlobal.getId());
            startActivity(intent);
        });

        asistenciaActiva.setVisibility(View.GONE);
        iconoTacheLogo = findViewById(R.id.arseIconoTache);
        iconoPalomitaLogo = findViewById(R.id.arseIconoPaloma);

        verificarRegistroAsistenciaActivo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarClases(spinnerSeleccionado);
        verificarRegistroAsistenciaActivo();
    }

    private void pendientes(boolean modo){

        if(modo){
            iconoPalomitaLogo.setVisibility(View.GONE);
            iconoTacheLogo.setVisibility(View.VISIBLE);
        } else{
            iconoPalomitaLogo.setVisibility(View.VISIBLE);
            iconoTacheLogo.setVisibility(View.GONE);
        }


    }

    private void configurarDrawer() {
        drawerLayout.setScrimColor(Color.argb(40, 0, 0, 0));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int anchoDrawer = metrics.widthPixels / 2;

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawer.getLayoutParams();
        params.width = anchoDrawer;
        drawer.setLayoutParams(params);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                btnFlecha.setRotationY(slideOffset * 180f);

                float translation = drawerView.getWidth() * slideOffset;
                btnFlecha.setTranslationX(translation + 8);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                btnFlecha.setRotationY(0f);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                btnFlecha.setRotationY(180f);
            }
        });

        btnFlecha.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(drawer)) {
                drawerLayout.closeDrawer(drawer);
            } else {
                drawerLayout.openDrawer(drawer);
            }
        });
    }

    private void configurarBottomSheet() {
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<LinearLayout> behavior =
                BottomSheetBehavior.from(bottomSheet);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        bottomSheet.getLayoutParams().height = metrics.heightPixels / 2;
        bottomSheet.requestLayout();

        behavior.setPeekHeight(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        150,
                        metrics
                )
        );
        behavior.setFitToContents(true);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    public void elementos(){
        LinearLayout btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(view ->{
            Intent abirInterfazRegistro = new Intent(this, RegistrarClase.class);
            startActivity(abirInterfazRegistro);
                });

        GradientDrawable fondo =
                (GradientDrawable) btnRegistrar.getBackground().mutate();

        fondo.setColor(
                ContextCompat.getColor(
                        this,
                        R.color.grisPalidoOscuro
                )
        );
        btnRegistrar.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    fondo.setColor(
                            ContextCompat.getColor(
                                    view.getContext(),
                                    R.color.azulArse
                            )
                    );
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    fondo.setColor(
                            ContextCompat.getColor(
                                    view.getContext(),
                                    R.color.grisPalidoOscuro
                            )
                    );
                    break;
            }

            return false;
        });

        Button btnGrupos = findViewById(R.id.btnGrupos);
        btnGrupos.setOnClickListener(view ->{
            Intent abrirInterfazGrupos = new Intent(this, Grupos.class);
            startActivity(abrirInterfazGrupos);
        });
    }


    private void cargarClases(int seleccion) {

        Calendar calendario = Calendar.getInstance();
        int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);

        int diaActual;

        switch (diaSemana) {
            case Calendar.MONDAY: diaActual = 1; break;
            case Calendar.TUESDAY: diaActual = 2; break;
            case Calendar.WEDNESDAY: diaActual = 3; break;
            case Calendar.THURSDAY: diaActual = 4; break;
            case Calendar.FRIDAY: diaActual = 5; break;
            case Calendar.SATURDAY: diaActual = 6; break;
            default: diaActual = -1; break;
        }


        executor.execute(() -> {

            List<Clase> clases;
            String mensajeVacio;

            if (seleccion == 0) {

                if (diaActual != -1) {

                    String horaActual = obtenerHoraActual();

                    clases = claseDao.obtenerClasesDia(
                            diaActual,
                            horaActual
                    );

                    mensajeVacio = "Sin clases restantes por hoy";

                } else {

                    clases = new ArrayList<>();
                    mensajeVacio = "SIN CLASES POR HOY";
                }

            } else {

                clases = claseDao.obtenerClasesRegistradas();
                mensajeVacio = "No hay clases registradas";
            }

            if (clases == null || clases.isEmpty()) {

                String finalMensaje = mensajeVacio;

                runOnUiThread(() -> {
                    rvClases.setVisibility(View.GONE);
                    mensajeClases.setVisibility(View.VISIBLE);
                    mensajeClases.setText(finalMensaje);
                });

                return;
            }

            List<ClaseItem> items = new ArrayList<>();

            for (Clase c : clases) {

                String nombreGrupo = grupoDao.obtenerNombreGrupo(c.getIdGrupo());
                Grupo grupo = grupoDao.obtenerGrupo(c.getIdGrupo());

                Aula aula = null;

                if (grupo != null) {
                    aula = aulaDao.obtenerAula(grupo.getAulaId());
                }

                String nombreAula =
                        (aula != null) ? aula.getNombre() : "Sin aula";

                Tema tema = temaDao.obtenerTemaItem(c.getIdTema());

                String colorPrincipal =
                        (tema != null) ? tema.getColorPrincipal() : "#2196F3";

                String colorFondo =
                        (tema != null) ? tema.getColorFondo() : "#FFFFFF";

                items.add(new ClaseItem(
                        c,
                        nombreGrupo,
                        nombreAula,
                        colorPrincipal,
                        colorFondo
                ));
            }

            runOnUiThread(() -> {

                mensajeClases.setVisibility(View.GONE);
                rvClases.setVisibility(View.VISIBLE);

                if (adapterClases == null) {
                    adapterClases = new ClaseAdapter(items);
                    rvClases.setAdapter(adapterClases);
                } else {
                    adapterClases.actualizar(items);
                }

            });

        });
    }

    private void verificarRegistroAsistenciaActivo() {

        executor.execute(() -> {

            String horaActual = obtenerHoraActual();

            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            Calendar calendario = Calendar.getInstance();
            int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);

            int diaActual;

            switch (diaSemana) {
                case Calendar.MONDAY: diaActual = 1; break;
                case Calendar.TUESDAY: diaActual = 2; break;
                case Calendar.WEDNESDAY: diaActual = 3; break;
                case Calendar.THURSDAY: diaActual = 4; break;
                case Calendar.FRIDAY: diaActual = 5; break;
                case Calendar.SATURDAY: diaActual = 6; break;
                default: diaActual = -1; break;
            }

            Clase claseActiva = null;
            String nombreGrupo = null;
            boolean mostrar = false;

            if (diaActual != -1) {

                claseActiva = claseDao.obtenerClaseAsistenciaActiva(
                        diaActual,
                        horaActual
                );

                if (claseActiva != null) {

                    SesionClase sesion = sesionClaseDao
                            .obtenerSesionPorFecha(
                                    claseActiva.getId(),
                                    fechaHoy
                            );

                    if (sesion == null || !sesion.isTomada()) {

                        mostrar = true;

                        nombreGrupo = grupoDao.obtenerNombreGrupo(
                                claseActiva.getIdGrupo()
                        );
                    }
                }
            }

            Clase finalClase = claseActiva;
            String finalGrupo = nombreGrupo;
            boolean finalMostrar = mostrar;

            runOnUiThread(() -> {

                if (finalMostrar && finalClase != null) {

                    asistenciaActiva.setVisibility(View.VISIBLE);
                    pendientes(true);


                    String nombreGrupoSeguro =
                            (finalGrupo != null) ? finalGrupo : "Sin grupo";

                    asistenciaActivaAsignatura.setText(
                            finalClase.getNombre().toUpperCase()
                                    + " (" + nombreGrupoSeguro + ")"
                    );

                    claseActivaGlobal = finalClase;

                } else {

                    asistenciaActiva.setVisibility(View.GONE);
                    pendientes(false);
                    claseActivaGlobal = null;
                }

            });

        });
    }

    private String obtenerHoraActual() {

        SimpleDateFormat sdf =
                new SimpleDateFormat("HH:mm", Locale.getDefault());

        return sdf.format(new Date());
    }


}
