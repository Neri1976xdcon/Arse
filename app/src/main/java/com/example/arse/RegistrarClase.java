package com.example.arse;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.CriteriosAdapter;
import Adapters.GrupoAdapter;
import Adapters.HorarioAdapter;
import Adapters.SesionClaseHorario;
import Adapters.TemaAdapter;
import BD.BaseDat;
import BD.ProcesosDAO;
import Clases.GrupoItem;
import Clases.HorarioDia;
import DAO.AulaDAO;
import DAO.ClaseDAO;
import DAO.CriterioClaseDAO;
import DAO.CriterioDAO;
import DAO.DiaDAO;
import DAO.GrupoAlumnoDAO;
import DAO.GrupoDAO;
import DAO.HorarioDAO;
import Entity.Aula;
import Entity.Clase;
import Entity.ClasesCriterios;
import Entity.CriterioEvaluacion;
import Entity.Dia;
import Entity.Grupo;
import Entity.Horario;
import Entity.Tema;

public class RegistrarClase extends AppCompatActivity {

    ProcesosDAO proDao;

    private BaseDat bd;
    private DiaDAO diaDao;
    private GrupoDAO grupoDao;
    private AulaDAO aulaDao;
    private GrupoAlumnoDAO grupoAlumnoDAO;
    private CriterioDAO criterioDao;
    private ClaseDAO claseDao;
    private HorarioDAO horarioDao;

    private HorarioAdapter adapterHorario;
    private CriteriosAdapter adapterCriterios;
    private TemaAdapter adapterTema;

    private GrupoItem grupoSeleccionado = null;

    private EditText etAsignatura;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registrar_clase);

        inicializarBaseDatos();
        inicializarVistas();
        cargarDias();
        cargarTemas();
        colocarCriterios();
    }


    private void inicializarBaseDatos() {
        bd = BaseDat.getInstance(this);
        diaDao = bd.diaDao();
        grupoDao = bd.grupoDao();
        aulaDao = bd.aulaDao();
        grupoAlumnoDAO = bd.grupoAlumnoDao();
        criterioDao = bd.criterioDao();
        claseDao = bd.claseDao();
        horarioDao = bd.horarioDao();
    }

    private void inicializarVistas() {

        proDao = new ProcesosDAO(this);
        etAsignatura = findViewById(R.id.asignatura);

        findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());

        RecyclerView rvHorario = findViewById(R.id.rvHorario);
        rvHorario.setLayoutManager(new LinearLayoutManager(this));
        adapterHorario = new HorarioAdapter(new ArrayList<Dia>());
        rvHorario.setAdapter(adapterHorario);

        findViewById(R.id.crearGrupoInterfaz)
                .setOnClickListener(v ->
                        startActivity(new Intent(this, Grupos.class)));

        findViewById(R.id.seleccionarGrupo)
                .setOnClickListener(v -> mostrarDialogoSeleccionGrupo());

        findViewById(R.id.registrarClase)
                .setOnClickListener(v -> validarYRegistrar());

        LinearLayout btnRegistrar = findViewById(R.id.registrarClase);
        GradientDrawable fondo = (GradientDrawable) btnRegistrar.getBackground().mutate();
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

    }

    private void cargarDias() {
        executor.execute(() -> {
            List<Dia> dias = diaDao.obtenerDias();
            runOnUiThread(() -> adapterHorario.actualizarLista(dias));
        });
    }

    private void colocarCriterios() {

        RecyclerView recycler = findViewById(R.id.rvCriterios);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setNestedScrollingEnabled(false);

        adapterCriterios = new CriteriosAdapter(new ArrayList<>());
        recycler.setAdapter(adapterCriterios);

        executor.execute(() -> {
            List<CriterioEvaluacion> lista = criterioDao.obtenerCriteriosRegistrados();
            runOnUiThread(() -> adapterCriterios.actualizarLista(lista));
        });
    }

    private void cargarTemas() {

        RecyclerView rvTemas = findViewById(R.id.rvTemas);
        rvTemas.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        adapterTema = new TemaAdapter(new ArrayList<>());
        rvTemas.setAdapter(adapterTema);

        executor.execute(() -> {
            List<Tema> temas = bd.temaDao().obtenerTemasDisponibles();
            runOnUiThread(() -> adapterTema.actualizarLista(temas));
        });
    }

    private void validarYRegistrar() {

        boolean valido = true;

        String nombre = etAsignatura
                .getText()
                .toString()
                .trim();

        TextView mensajeAsignatura =
                findViewById(R.id.mensajeAsignaturaClase);

        TextView mensajeGrupo =
                findViewById(R.id.mensajeGrupoClase);

        TextView mensajeHorario =
                findViewById(R.id.mensajeHorarioClase);

        TextView mensajeCriterios =
                findViewById(R.id.mensajeCriteriosClase);

        boolean hayErroresLocales =
                adapterHorario
                        .validarTodosLosHorarios();

        if (hayErroresLocales) {

            mensajeHorario.setVisibility(
                    View.VISIBLE
            );

            mensajeHorario.setText(
                    "⚠️ Corrige los horarios marcados"
            );

            return;
        }

        if (nombre.isEmpty()) {

            etAsignatura.setError(
                    "Campo obligatorio"
            );

            mensajeAsignatura
                    .setVisibility(
                            View.VISIBLE
                    );

            mensajeAsignatura
                    .setText(
                            "⚠️ Ingresa un nombre de asignatura"
                    );

            valido = false;

        } else {

            etAsignatura.setError(
                    null
            );

            mensajeAsignatura
                    .setVisibility(
                            View.GONE
                    );
        }

        if (grupoSeleccionado == null) {

            mensajeGrupo
                    .setVisibility(
                            View.VISIBLE
                    );

            mensajeGrupo
                    .setText(
                            "⚠️ Selecciona un grupo"
                    );

            valido = false;

        } else {

            mensajeGrupo
                    .setVisibility(
                            View.GONE
                    );
        }

        List<HorarioDia> horariosRegistrados =
                adapterHorario
                        .obtenerHorariosRegistrados();

        if (horariosRegistrados == null
                || horariosRegistrados.isEmpty()) {

            mensajeHorario
                    .setVisibility(
                            View.VISIBLE
                    );

            mensajeHorario
                    .setText(
                            "⚠️ Registra al menos un horario"
                    );

            valido = false;

        } else {

            mensajeHorario
                    .setVisibility(
                            View.GONE
                    );
        }

        List<CriterioEvaluacion> criterios =
                adapterCriterios
                        .getSeleccionados();

        if (criterios == null
                || criterios.isEmpty()) {

            mensajeCriterios
                    .setVisibility(
                            View.VISIBLE
                    );

            mensajeCriterios
                    .setText(
                            "⚠️ Selecciona al menos un criterio"
                    );

            valido = false;

        } else {

            mensajeCriterios
                    .setVisibility(
                            View.GONE
                    );
        }

        if (!valido) {
            return;
        }

        executor.execute(() -> {

            Map<Integer, String> erroresEmpalme =
                    new HashMap<>();

            List<HorarioDia> horariosValidos =
                    new ArrayList<>();

            for (HorarioDia h :
                    horariosRegistrados) {

                if (h.estaVacio())
                    continue;

                String inicio =
                        h.obtenerInicioCompleto();

                String fin =
                        h.obtenerFinCompleto();

                boolean empalme =
                        horarioDao
                                .existeEmpalme(
                                        h.getDiaId(),
                                        inicio,
                                        fin
                                );

                if (empalme) {

                    erroresEmpalme.put(
                            h.getDiaId(),
                            "⚠️ EMPALME CON OTRO HORARIO"
                    );

                } else {

                    horariosValidos.add(
                            h
                    );
                }
            }

            runOnUiThread(() -> {
                if (!erroresEmpalme.isEmpty()) {

                    adapterHorario
                            .setErroresExternos(
                                    erroresEmpalme
                            );

                    mensajeHorario
                            .setVisibility(
                                    View.VISIBLE
                            );

                    mensajeHorario
                            .setText(
                                    "⚠️ Hay empalmes en los horarios"
                            );

                    return;
                }

                adapterHorario
                        .setErroresExternos(
                                new HashMap<>()
                        );

                executor.execute(() -> {

                    boolean existe =
                            claseDao
                                    .existeClase(
                                            nombre,
                                            grupoSeleccionado
                                                    .getId()
                                    );

                    runOnUiThread(() -> {

                        if (existe) {

                            mensajeAsignatura
                                    .setVisibility(
                                            View.VISIBLE
                                    );

                            mensajeAsignatura
                                    .setText(
                                            "⚠️ Ya existe una clase con ese nombre en este grupo"
                                    );

                            return;
                        }

                        if (adapterHorario
                                .hayErrores()) {

                            mensajeHorario
                                    .setVisibility(
                                            View.VISIBLE
                                    );

                            mensajeHorario
                                    .setText(
                                            "⚠️ Corrige los horarios antes de registrar"
                                    );

                            return;
                        }

                        registrarClase(
                                nombre,
                                horariosValidos,
                                criterios
                        );

                    });

                });

            });

        });
    }

    private void registrarClase(String nombre,
                                List<HorarioDia> horarios,
                                List<CriterioEvaluacion> criterios) {
        executor.execute(() -> {

            try {

                int idTema;
                if (adapterTema.getTemaSeleccionado() != null) {
                    idTema = adapterTema.getTemaSeleccionado().getId();
                } else {
                    idTema = 1;
                }

                bd.runInTransaction(() -> {

                    int idClase = (int) claseDao.insertar(
                            new Clase(
                                    nombre,
                                    grupoSeleccionado.getId(),
                                    idTema
                            )
                    );

                    for (HorarioDia h : horarios) {
                        horarioDao.insertar(
                                new Horario(
                                        idClase,
                                        h.getDiaId(),
                                        h.obtenerInicioCompleto(),
                                        h.obtenerFinCompleto()
                                )
                        );
                    }

                    CriterioClaseDAO criterioClaseDAO = bd.criterios_clasesDao();

                    for (CriterioEvaluacion c : criterios) {
                        criterioClaseDAO.insertar(
                                new ClasesCriterios(
                                        idClase,
                                        c.getId()
                                )
                        );
                    }
                });

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "✅ Clase registrada correctamente",
                            Toast.LENGTH_LONG).show();
                    finish();
                });

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(() ->
                        Toast.makeText(this,
                                "❌ Error al registrar la clase",
                                Toast.LENGTH_LONG).show());
            }
        });
    }


    private void mostrarDialogoSeleccionGrupo() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.ventana_seleccionar_grupo);

        RecyclerView rv = dialog.findViewById(R.id.rvSeleccionargrupo);
        rv.setLayoutManager(new LinearLayoutManager(this));

        GrupoAdapter adapter = new GrupoAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        executor.execute(() -> {

            List<Grupo> grupos = grupoDao.obtenerGrupos();
            List<GrupoItem> lista = new ArrayList<>();

            for (Grupo g : grupos) {

                Aula aula = aulaDao.obtenerAula(g.getAulaId());
                String nombreAula = aula != null ? aula.getNombre() : "SIN AULA";

                int total = grupoAlumnoDAO.totalAlumnosGrupo(g.getId());

                lista.add(new GrupoItem(
                        g.getId(),
                        g.getNombre(),
                        nombreAula,
                        total
                ));
            }

            runOnUiThread(() -> adapter.actualizarLista(lista));
        });

        adapter.setOnGrupoSeleccionadoListener(grupo -> {
            grupoSeleccionado = grupo;
            actualizarBotonGrupo(grupo);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void actualizarBotonGrupo(GrupoItem grupo) {

        TextView texto = findViewById(R.id.textoSeleccionGrupo);

        if (grupo == null) {
            texto.setText("SELECCIONAR GRUPO");
        } else {
            texto.setText(grupo.getNombre() + " (" + grupo.getAula() + ")");
        }
    }
}