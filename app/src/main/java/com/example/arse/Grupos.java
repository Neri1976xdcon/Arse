package com.example.arse;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import Adapters.AlumnoAdapter;
import Adapters.AlumnoAgregarAdapter;
import Adapters.GrupoAdapter;
import BD.BaseDat;
import Clases.GrupoItem;
import DAO.AulaDAO;
import DAO.GrupoAlumnoDAO;
import DAO.GrupoDAO;
import Entity.Alumno;
import DAO.AlumnoDAO;
import Entity.Aula;
import Entity.Grupo;
import Entity.GrupoAlumno;

public class Grupos extends AppCompatActivity {

    private BaseDat bd;
    private AlumnoDAO alumnoDao;

    private String alumnoBusqueda = null;
    private boolean ordenAscendente = true;

    private EditText buscador;
    private RecyclerView rvAlumnos;
    private TextView mensajeSinAlumnos;
    private AlumnoAdapter alumnoAdapter;

    private RecyclerView rvGrupos;
    private GrupoAdapter grupoAdapter;

    private GrupoDAO grupoDao;
    private AulaDAO aulaDao;
    private GrupoAlumnoDAO grupoAlumnoDAO;

    private TextView mensajeSinGrupos;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnableBusqueda;
    List<Grupo> grupos;

    private Integer grupoSeleccionadoId = null; // null = mostrar todos
    private boolean modoEliminar = false;
    Runnable runnableAula;
    Runnable runnableGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grupos);

        bd = BaseDat.getInstance(this);
        alumnoDao = bd.alumnoDao();
        grupoDao = bd.grupoDao();
        aulaDao = bd.aulaDao();
        grupoAlumnoDAO = bd.grupoAlumnoDao();

        LinearLayout crearGrupo = findViewById(R.id.crearGrupo);
        LinearLayout btnMostrarTodos = findViewById(R.id.mostrarTodosAlumnos);

        buscador = findViewById(R.id.buscadorAlumnos);
        rvAlumnos = findViewById(R.id.rvAlumnos);
        mensajeSinAlumnos = findViewById(R.id.mensajeSinAlumnos);

        rvGrupos = findViewById(R.id.rvGrupos);
        mensajeSinGrupos = findViewById(R.id.mensajeSinGrupos);

        AppCompatButton regresar = findViewById(R.id.btnRegresar);
        AppCompatButton mas = findViewById(R.id.masAlumnos);
        ImageButton btnOrdenar = findViewById(R.id.ordenarAlumnos);

        rvAlumnos.setLayoutManager(new LinearLayoutManager(this));
        rvAlumnos.setHasFixedSize(true);
        rvAlumnos.setItemAnimator(null);

        alumnoAdapter = new AlumnoAdapter(new ArrayList<>());
        rvAlumnos.setAdapter(alumnoAdapter);
        alumnoAdapter.setOnAlumnoAccionListener((alumno, accion) -> {
            mostrarDialogoRemoverAlumno(alumno, accion);
        });

        rvGrupos.setLayoutManager(new LinearLayoutManager(this));
        rvGrupos.setHasFixedSize(true);
        rvGrupos.setItemAnimator(null);

        grupoAdapter = new GrupoAdapter(new ArrayList<>());
        rvGrupos.setAdapter(grupoAdapter);

        grupoAdapter.setOnGrupoSeleccionadoListener(grupo -> {

            if (grupo == null) {
                grupoSeleccionadoId = null;
                actualizarFiltroVisual(true);
            } else {
                grupoSeleccionadoId = grupo.getId();
                actualizarFiltroVisual(false);
            }

            mostrarAlumnos();
        });

        crearGrupo.setOnClickListener(v -> mostrarDialogoCrearGrupo());
        regresar.setOnClickListener(v -> finish());
        mas.setOnClickListener(v -> {

            if (grupoSeleccionadoId == null) {
                mostrarVentanaRegistrarAlumno();
            } else {
                mostrarDialogoAgregarAlumnosGrupo(grupoSeleccionadoId);
            }

        });

        btnMostrarTodos.setOnClickListener(v -> {
            grupoSeleccionadoId = null;
            grupoAdapter.limpiarSeleccion();
            actualizarFiltroVisual(true);
            mostrarAlumnos();
        });

        configurarBuscador();
        configurarOrdenamiento(btnOrdenar);

        grupoSeleccionadoId = null;
        actualizarFiltroVisual(true);

        AppCompatButton btnRemover = findViewById(R.id.removerAlumno);

        btnRemover.setOnClickListener(v -> {

            modoEliminar = !modoEliminar;

            btnRemover.setSelected(modoEliminar);
            btnRemover.setAlpha(modoEliminar ? 1f : 0.5f);

            actualizarModoEliminarUI();
        });

        validarEstadoGrupos();
        cargarAlumnos();
    }


    private void actualizarFiltroVisual(boolean todosSeleccionado) {

        LinearLayout btnMostrarTodos = findViewById(R.id.mostrarTodosAlumnos);
        AppCompatButton mas = findViewById(R.id.masAlumnos);

        btnMostrarTodos.setSelected(todosSeleccionado);
    }


    private void actualizarModoEliminarUI() {

        boolean esGrupo = grupoSeleccionadoId != null;

        if (modoEliminar) {

            if (esGrupo) {
                alumnoAdapter.setModoAccion("QUITAR");
            } else {
                alumnoAdapter.setModoAccion("ELIMINAR");
            }

        } else {
            alumnoAdapter.setModoAccion(null);
        }
    }


    private void validarEstadoGrupos() {

        new Thread(() -> {

            int total = grupoDao.contarGrupos();

            runOnUiThread(() -> {

                if (total > 0) {
                    rvGrupos.setVisibility(View.VISIBLE);
                    mensajeSinGrupos.setVisibility(View.GONE);

                    mostrarGrupos();
                } else {
                    rvGrupos.setVisibility(View.GONE);
                    mensajeSinGrupos.setVisibility(View.VISIBLE);
                }

            });

        }).start();
    }

    private void cargarAlumnos() {
        mostrarAlumnos();
    }

    private void mostrarGrupos() {

        new Thread(() -> {

            List<Grupo> grupos = grupoDao.obtenerGrupos();
            List<GrupoItem> listaFinal = new ArrayList<>();

            for (Grupo g : grupos) {

                Aula aula = aulaDao.obtenerAula(g.getAulaId());
                String nombreAula = aula != null ? aula.getNombre() : "SIN AULA";

                int total = grupoAlumnoDAO.totalAlumnosGrupo(g.getId());

                listaFinal.add(new GrupoItem(
                        g.getId(),
                        g.getNombre(),
                        nombreAula,
                        total
                ));
            }

            runOnUiThread(() -> {
                grupoAdapter.actualizarLista(listaFinal);

                if (grupoSeleccionadoId != null) {
                    grupoAdapter.seleccionarGrupoPorId(grupoSeleccionadoId);
                } else {
                    actualizarFiltroVisual(true); // TODOS seleccionado
                }
            });

        }).start();
    }

    private void mostrarAlumnos() {

        new Thread(() -> {

            List<Alumno> alumnos = new ArrayList<>();

            try {

                if (grupoSeleccionadoId == null) {

                    alumnos = alumnoDao.obtenerAlumnosFiltrados(
                            alumnoBusqueda,
                            ordenAscendente
                    );

                } else {

                    if (ordenAscendente) {
                        alumnos = alumnoDao.obtenerAlumnosGrupoFiltradoOrdenadoAsc(
                                grupoSeleccionadoId,
                                alumnoBusqueda
                        );
                    } else {
                        alumnos = alumnoDao.obtenerAlumnosGrupoFiltradoOrdenadoDesc(
                                grupoSeleccionadoId,
                                alumnoBusqueda
                        );
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Alumno> finalAlumnos = alumnos;

            runOnUiThread(() -> {

                alumnoAdapter.actualizarLista(finalAlumnos);

                if (finalAlumnos == null || finalAlumnos.isEmpty()) {
                    mensajeSinAlumnos.setVisibility(View.VISIBLE);
                    rvAlumnos.setVisibility(View.GONE);
                } else {
                    mensajeSinAlumnos.setVisibility(View.GONE);
                    rvAlumnos.setVisibility(View.VISIBLE);
                }

            });

        }).start();
    }

    private void configurarBuscador() {

        LinearLayout contenedor = findViewById(R.id.contenedorBuscador);

        buscador.setOnFocusChangeListener((view, focused) -> {
            GradientDrawable fondo =
                    (GradientDrawable) contenedor.getBackground().mutate();

            fondo.setColor(
                    ContextCompat.getColor(
                            this,
                            focused ? R.color.amarilloClaro : R.color.grisPalidoClaro
                    )
            );
        });

        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

                if (runnableBusqueda != null) {
                    handler.removeCallbacks(runnableBusqueda);
                }

                runnableBusqueda = () -> {

                    alumnoBusqueda = normalizar(editable.toString().trim());

                    if (alumnoBusqueda.isEmpty()) {
                        alumnoBusqueda = null;
                    }

                    mostrarAlumnos();
                };

                handler.postDelayed(runnableBusqueda, 300);
            }

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
        });
    }

    private void configurarOrdenamiento(ImageButton btnOrdenar) {

        btnOrdenar.setOnClickListener(v -> {

            ordenAscendente = !ordenAscendente;

            btnOrdenar.animate()
                    .rotationX(ordenAscendente ? 0f : 180f)
                    .setDuration(300)
                    .start();

            mostrarAlumnos();
        });
    }

    public String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }

    public String quitarAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .trim();
    }

    private void mostrarDialogoCrearGrupo() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.ventana_crear_grupo);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window != null) {

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int ancho = (int) (metrics.widthPixels * 0.85);

            window.setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        AulaDAO aulaDao = bd.aulaDao();
        GrupoDAO grupoDao = bd.grupoDao();
        GrupoAlumnoDAO grupoAlumnoDAO = bd.grupoAlumnoDao();
        AlumnoDAO alumnoDao = bd.alumnoDao();

        EditText etGrupo = dialog.findViewById(R.id.nuevoGrupo);
        EditText etNuevaAula = dialog.findViewById(R.id.etNuevaAula);
        Spinner spinnerAulas = dialog.findViewById(R.id.spinnerAulas);

        RadioButton rbNueva = dialog.findViewById(R.id.opcionNuevaAula);
        RadioButton rbSeleccionar = dialog.findViewById(R.id.opcionSeleccionarAula);

        RecyclerView rvAlumnos = dialog.findViewById(R.id.rvAgregarAlumnosGrupo);
        EditText buscador = dialog.findViewById(R.id.buscadorAlumnos);

        AppCompatButton btnGuardar = dialog.findViewById(R.id.registrarGrupo);
        AppCompatButton btnCancelar = dialog.findViewById(R.id.cancelarRegistroGrupo);

        etNuevaAula.setEnabled(false);
        spinnerAulas.setEnabled(false);

        rvAlumnos.setLayoutManager(new LinearLayoutManager(this));

        AlumnoAgregarAdapter adapter = new AlumnoAgregarAdapter(this, new ArrayList<>());
        rvAlumnos.setAdapter(adapter);

        new Thread(() -> {
            List<Alumno> lista = alumnoDao.obtenerTodosAlumnos();

            runOnUiThread(() -> adapter.actualizarLista(lista));
        }).start();

        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String texto = s.toString().trim();

                new Thread(() -> {

                    List<Alumno> filtrados = texto.isEmpty()
                            ? alumnoDao.obtenerTodosAlumnos()
                            : alumnoDao.buscarAlumnos(texto);

                    runOnUiThread(() -> adapter.actualizarLista(filtrados));

                }).start();
            }

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        final List<Aula>[] listaAulas = new List[]{new ArrayList<>()};

        new Thread(() -> {

            listaAulas[0] = aulaDao.obtenerTodas();

            runOnUiThread(() -> {

                if (listaAulas[0].isEmpty()) {

                    List<String> lista = new ArrayList<>();
                    lista.add("SIN AULAS");

                    ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            lista
                    );

                    spinnerAulas.setAdapter(adapterSpinner);
                    spinnerAulas.setEnabled(false);

                } else {

                    List<String> nombres = new ArrayList<>();
                    for (Aula a : listaAulas[0]) {
                        nombres.add(a.getNombre());
                    }

                    ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            nombres
                    );

                    spinnerAulas.setAdapter(adapterSpinner);
                }

            });

        }).start();

        rbNueva.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etNuevaAula.setEnabled(true);
                spinnerAulas.setEnabled(false);
            }
        });

        rbSeleccionar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etNuevaAula.setEnabled(false);
                spinnerAulas.setEnabled(true);
            }
        });

        etNuevaAula.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String aulaIngresada = s.toString().trim();

                if (runnableAula != null) {
                    handler.removeCallbacks(runnableAula);
                }

                runnableAula = () -> {

                    if (aulaIngresada.isEmpty()) {
                        etNuevaAula.setError(null);
                        return;
                    }

                    new Thread(() -> {

                        boolean existe = aulaDao.existeAula(aulaIngresada);

                        runOnUiThread(() -> {

                            if (existe) {
                                etNuevaAula.setError("YA EXISTE ESA AULA");
                            } else {
                                etNuevaAula.setError(null);
                            }

                        });

                    }).start();

                };

                handler.postDelayed(runnableAula, 400);
            }

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        etGrupo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String grupoIngresado = s.toString().trim();

                if (runnableGrupo != null) {
                    handler.removeCallbacks(runnableGrupo);
                }

                runnableGrupo = () -> {

                    if (grupoIngresado.isEmpty()) {
                        etGrupo.setError(null);
                        return;
                    }

                    new Thread(() -> {

                        boolean existe = grupoDao.existeGrupo(grupoIngresado);

                        runOnUiThread(() -> {

                            if (existe) {
                                etGrupo.setError("YA EXISTE ESE GRUPO");
                            } else {
                                etGrupo.setError(null);
                            }

                        });

                    }).start();

                };

                handler.postDelayed(runnableGrupo, 400);
            }

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnGuardar.setOnClickListener(v -> {

            String nombreGrupo = etGrupo.getText().toString().trim();

            if (nombreGrupo.isEmpty()) {
                etGrupo.setError("INGRESA EL GRUPO");
                return;
            }

            if (rbNueva.isChecked()) {
                String nombreAula = etNuevaAula.getText().toString().trim();

                if (nombreAula.isEmpty()) {
                    etNuevaAula.setError("INGRESA AULA");
                    return;
                }
            }

            new Thread(() -> {

                boolean grupoExiste = grupoDao.existeGrupo(nombreGrupo);

                boolean aulaExiste = false;
                String nombreAula = etNuevaAula.getText().toString().trim();

                if (rbNueva.isChecked()) {
                    aulaExiste = aulaDao.existeAula(nombreAula);
                }

                boolean finalAulaExiste = aulaExiste;

                runOnUiThread(() -> {

                    if (grupoExiste) {
                        etGrupo.setError("YA EXISTE ESE GRUPO");
                    }

                    if (rbNueva.isChecked() && finalAulaExiste) {
                        etNuevaAula.setError("YA EXISTE ESA AULA");
                    }

                });

                if (grupoExiste || (rbNueva.isChecked() && aulaExiste)) {
                    return;
                }

                int idAula = -1;

                if (rbNueva.isChecked()) {

                    Aula aula = new Aula(nombreAula);
                    long id = aulaDao.insertar(aula);
                    idAula = (int) id;

                } else {

                    int pos = spinnerAulas.getSelectedItemPosition();

                    if (!listaAulas[0].isEmpty()) {
                        idAula = listaAulas[0].get(pos).getId();
                    }
                }

                Grupo grupo = new Grupo(nombreGrupo, idAula);
                long idGrupo = grupoDao.insertar(grupo);

                List<Integer> alumnosSeleccionados = adapter.getAlumnosSeleccionados();

                for (int idAlumno : alumnosSeleccionados) {
                    grupoAlumnoDAO.insertar(
                            new GrupoAlumno((int) idGrupo, idAlumno)
                    );
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "GRUPO REGISTRADO", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    validarEstadoGrupos();
                });

            }).start();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }



    private void mostrarDialogoAgregarAlumnosGrupo(int grupoId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.ventana_agregar_alumnos);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window != null) {

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int ancho = (int) (metrics.widthPixels * 0.85); // 85% de la pantalla

            window.setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        RecyclerView rv = dialog.findViewById(R.id.rvAgregarAlumnos);
        EditText buscador = dialog.findViewById(R.id.buscadorAlumnos);

        AppCompatButton btnAgregar = dialog.findViewById(R.id.agregarAlumnos);
        AppCompatButton btnCancelar = dialog.findViewById(R.id.cancelarAgregarAlumnos);

        rv.setLayoutManager(new LinearLayoutManager(this));

        AlumnoAgregarAdapter adapter = new AlumnoAgregarAdapter(this, new ArrayList<>());
        rv.setAdapter(adapter);

        new Thread(() -> {

            List<Alumno> lista = alumnoDao.obtenerAlumnosNoEnGrupo(grupoId);

            runOnUiThread(() -> adapter.actualizarLista(lista));

        }).start();

        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String texto = s.toString().trim();

                new Thread(() -> {

                    List<Alumno> filtrados;

                    if (texto.isEmpty()) {
                        filtrados = alumnoDao.obtenerAlumnosNoEnGrupo(grupoId);
                    } else {
                        filtrados = alumnoDao.buscarAlumnosNoEnGrupo(grupoId, texto);
                    }

                    runOnUiThread(() -> adapter.actualizarLista(filtrados));

                }).start();
            }

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAgregar.setOnClickListener(v -> {

            List<Integer> seleccionados = adapter.getAlumnosSeleccionados();

            if (seleccionados.isEmpty()) {
                Toast.makeText(this, "SELECCIONA AL MENOS UN ALUMNO", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {

                for (int idAlumno : seleccionados) {
                    grupoAlumnoDAO.insertar(
                            new GrupoAlumno(grupoId, idAlumno)
                    );
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "ALUMNOS AGREGADOS", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    mostrarGrupos();
                    mostrarAlumnos();
                });

            }).start();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarVentanaRegistrarAlumno() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.ventana_crear_alumno);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window != null) {

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int ancho = (int) (metrics.widthPixels * 0.85); // 85% de la pantalla

            window.setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etNombre = dialog.findViewById(R.id.etRegistrarAlumno);
        AppCompatButton guardar = dialog.findViewById(R.id.registrarAlumno);
        AppCompatButton cancelar = dialog.findViewById(R.id.cancelarRegistroAlumno);

        final String[] nombreFinal = {null};

        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

                String nombreOriginal = editable.toString().trim();

                if (nombreOriginal.isEmpty()) {
                    nombreFinal[0] = null;
                    etNombre.setError("INGRESE EL NOMBRE");
                    return;
                }

                String nombreBusqueda = normalizar(nombreOriginal);

                String nombreGuardar = quitarAcentos(nombreOriginal).toUpperCase();

                new Thread(() -> {

                    boolean existe = alumnoDao.existeAlumno(nombreBusqueda);

                    runOnUiThread(() -> {

                        if (existe) {
                            nombreFinal[0] = null;
                            etNombre.setError("YA EXISTE UN ALUMNO");
                        } else {
                            nombreFinal[0] = nombreGuardar;
                            etNombre.setError(null);
                        }

                    });

                }).start();
            }

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
        });

        guardar.setOnClickListener(v -> {

            if (nombreFinal[0] == null) {
                Toast.makeText(this, "DATOS INVÁLIDOS", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {

                alumnoDao.insertar(new Alumno(nombreFinal[0]));

                runOnUiThread(() -> {
                    mostrarAlumnos();
                    Toast.makeText(this, "ALUMNO REGISTRADO", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

            }).start();
        });

        cancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoRemoverAlumno(Alumno alumno, String accion) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.ventana_remover_alumno);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window != null) {

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int ancho = (int) (metrics.widthPixels * 0.85);

            window.setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView titulo = dialog.findViewById(R.id.tituloRemover);
        TextView nombre = dialog.findViewById(R.id.nombreAlumno);

        AppCompatButton cancelar = dialog.findViewById(R.id.cancelarRemover);
        AppCompatButton confirmar = dialog.findViewById(R.id.confirmarRemover);

        nombre.setText(alumno.getNombre());

        boolean esGrupo = grupoSeleccionadoId != null;

        if (esGrupo) {

            titulo.setText("¿QUITAR ESTE ALUMNO DEL GRUPO?");
            confirmar.setText("QUITAR");

        } else {

            titulo.setText("¿ELIMINAR ESTE ALUMNO DEFINITIVAMENTE?");
            confirmar.setText("ELIMINAR");
        }

        confirmar.setOnClickListener(v -> {

            new Thread(() -> {

                if (esGrupo) {
                    grupoAlumnoDAO.eliminarRelacion(
                            grupoSeleccionadoId,
                            alumno.getId()
                    );
                    mostrarGrupos();
                } else {
                    alumnoDao.eliminarAlumno(alumno.getId());
                }

                runOnUiThread(() -> {
                    mostrarAlumnos();
                    dialog.dismiss();
                    Toast.makeText(this, "OPERACIÓN REALIZADA", Toast.LENGTH_SHORT).show();
                });

            }).start();
        });

        cancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
