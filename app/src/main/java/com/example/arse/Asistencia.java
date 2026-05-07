package com.example.arse;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

import Adapters.AlumnoAdapter;
import Adapters.AlumnoAsistenciaAdapter;
import BD.BaseDat;
import DAO.AlumnoDAO;
import DAO.GrupoDAO;
import Entity.Alumno;
import Entity.AsistenciaAlumno;
import Entity.SesionClase;

public class Asistencia extends Fragment {

    private BaseDat baseDatos;
    private AlumnoDAO alumnoDao;

    private RecyclerView recyclerAlumnos;
    private HorizontalScrollView scrollHeader;
    private LinearLayout headerFechas;

    private int idGrupo;
    private int idClase;

    private AlumnoAsistenciaAdapter adapter;

    private List<Alumno> listaAlumnos = new ArrayList<>();

    private boolean isSyncing = false;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private LinearLayout overlay;

    private SesionClase sesionActual;
    private boolean modoRegistro = false;
    private boolean modoEdicion = false;
    private int columnaActiva = -1;

    private Map<Integer, Map<Integer, Boolean>> asistenciasTemp = new HashMap<>();
    private List<Integer> listaSesionesIds = new ArrayList<>();
    private AppCompatButton btnGuardar;
    private AppCompatButton btnCancelarCambios;
    private LinearLayout layoutBotones;

    public Asistencia() {}

    public static Asistencia newInstance(int idGrupo, int idClase) {
        Asistencia fragment = new Asistencia();
        Bundle args = new Bundle();
        args.putInt("idGrupo", idGrupo);
        args.putInt("idClase", idClase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idGrupo = getArguments().getInt("idGrupo");
            idClase = getArguments().getInt("idClase");
        }

        baseDatos = BaseDat.getInstance(requireContext());
        alumnoDao = baseDatos.alumnoDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_asistencia, container, false);

        recyclerAlumnos = view.findViewById(R.id.recyclerAlumnos);
        scrollHeader = view.findViewById(R.id.scrollHeader);
        headerFechas = view.findViewById(R.id.headerFechas);
        overlay = view.findViewById(R.id.layoutOverlay);

        btnGuardar = view.findViewById(R.id.guardarAsistencias);
        btnCancelarCambios = view.findViewById(R.id.cancelarCambiosAsistencia);
        layoutBotones = (LinearLayout) btnGuardar.getParent();

        btnGuardar.setOnClickListener(v -> {

            if (modoRegistro || modoEdicion) {

                if (columnaActiva == -1) {
                    Toast.makeText(getContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show();
                    return;
                }

                guardarAsistencia();

            } else {
                modoEdicion = true;
                modoRegistro = false;

                columnaActiva = -1;

                adapter.setModoRegistro(true, -1, -1, true);

                actualizarUI();
            }
        });

        btnCancelarCambios.setOnClickListener(v -> {

            modoRegistro = false;
            modoEdicion = false;

            columnaActiva = -1;

            adapter.setModoRegistro(false, -1, -1, false);

            cargarSesionesYAsistencias(false);

            actualizarUI();
        });

        recyclerAlumnos.setLayoutManager(new LinearLayoutManager(getContext()));

        configurarOverlay(view);
        cargarAlumnos();
        verificarAsistenciaActiva();
        actualizarUI();

        return view;
    }


    private void cargarHeaderSesiones(List<SesionClase> sesiones) {

        headerFechas.removeAllViews();

        Context context = getContext();

        if (context == null)
            return;

        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < sesiones.size(); i++) {

            SesionClase s = sesiones.get(i);

            int index = i;

            TextView tv = new TextView(context);

            tv.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            80,
                            100
                    )
            );

            String fecha = s.getFecha();

            if (fecha != null && fecha.length() >= 10) {
                tv.setText(fecha.substring(8));
            } else {
                tv.setText("--");
            }

            tv.setGravity(Gravity.CENTER);

            boolean cancelada = asistenciasTemp.values().stream().noneMatch(mapa -> mapa.containsKey(s.getId()));

            if (cancelada) {

                tv.setBackgroundColor(
                        ContextCompat.getColor(
                                context,
                                R.color.grisPalidoClaro
                        )
                );

            } else if (index == columnaActiva) {

                tv.setBackgroundColor(
                        ContextCompat.getColor(
                                context,
                                R.color.amarilloClaro
                        )
                );

            } else {

                tv.setBackgroundColor(Color.TRANSPARENT);
            }

            tv.setOnClickListener(v -> {

                if (cancelada) {
                    return;
                }

                if (!modoEdicion && !modoRegistro)
                    return;

                columnaActiva = index;

                adapter.seleccionarColumna(index);

                cargarHeaderSesiones(sesiones);
            });

            layout.addView(tv);
        }

        headerFechas.addView(layout);
    }


    private void cargarAlumnos() {

        Executors.newSingleThreadExecutor().execute(() -> {

            List<Alumno> lista = alumnoDao.obtenerAlumnosGrupo(idGrupo);
            if (lista == null) return;

            requireActivity().runOnUiThread(() -> {

                listaAlumnos.clear();
                listaAlumnos.addAll(lista);

                adapter = new AlumnoAsistenciaAdapter(
                        listaAlumnos,
                        scrollHeader
                );

                recyclerAlumnos.setAdapter(adapter);

                sincronizarScroll();
                cargarSesionesYAsistencias(false);
            });

        });
    }

    private void actualizarUI() {

        if (modoRegistro || modoEdicion) {

            btnGuardar.setText("GUARDAR");
            btnCancelarCambios.setVisibility(View.VISIBLE);

        } else {

            btnGuardar.setText("EDITAR");
            btnCancelarCambios.setVisibility(View.GONE);
        }


    }

    private void cargarSesionesYAsistencias(boolean activarRegistro) {

        Executors.newSingleThreadExecutor().execute(() -> {

            List<SesionClase> sesiones =
                    baseDatos.sesionClaseDao()
                            .obtenerSesionesPorClase(idClase);

            listaSesionesIds.clear();

            for (SesionClase s : sesiones) {
                listaSesionesIds.add(s.getId());
            }

            asistenciasTemp.clear();

            for (Alumno a : listaAlumnos) {

                Map<Integer, Boolean> mapa = new HashMap<>();

                List<AsistenciaAlumno> asistencias =
                        baseDatos.asistenciaAlumnoDao()
                                .obtenerAsistenciasAlumnoClase(a.getId(), idClase);

                for (AsistenciaAlumno as : asistencias) {
                    mapa.put(as.idSesion, as.asistencia);
                }

                asistenciasTemp.put(a.getId(), mapa);
            }

            requireActivity().runOnUiThread(() -> {

                cargarHeaderSesiones(sesiones);
                adapter.setDatos(asistenciasTemp, listaSesionesIds);

                if (activarRegistro && sesionActual != null) {
                    activarModoRegistroUI();
                }
            });

        });
    }

    private void iniciarSesionSiNoExiste() {

        Executors.newSingleThreadExecutor().execute(() -> {

            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            SesionClase sesion =
                    baseDatos.sesionClaseDao()
                            .obtenerSesionPorFecha(idClase, fechaHoy);

            if (sesion == null) {

                SesionClase nueva = new SesionClase(
                        fechaHoy,
                        false,
                        obtenerDiaActual()
                );

                nueva.setIdClase(idClase);

                long id = baseDatos.sesionClaseDao().insertar(nueva);
                nueva.setId((int) id);

                sesion = nueva;
            }

            SesionClase finalSesion = sesion;

            requireActivity().runOnUiThread(() -> {
                sesionActual = finalSesion;
                prepararAsistenciasIniciales();
            });

        });
    }

    private void prepararAsistenciasIniciales() {

        if(sesionActual == null)
            return;

        Executors.newSingleThreadExecutor().execute(() -> {

            for (Alumno a : listaAlumnos) {

                AsistenciaAlumno existente =
                        baseDatos.asistenciaAlumnoDao()
                                .buscarAsistenciaAlumnoClase(
                                        sesionActual.getId(),
                                        a.getId()
                                );

                if (existente == null) {

                    AsistenciaAlumno nueva =
                            new AsistenciaAlumno();

                    nueva.idAlumno = a.getId();
                    nueva.idSesion = sesionActual.getId();

                    nueva.asistencia = false;

                    baseDatos
                            .asistenciaAlumnoDao()
                            .insertar(nueva);
                }
            }

            requireActivity().runOnUiThread(() -> {
                cargarSesionesYAsistencias(true);
            });

        });
    }


    private void activarModoRegistroUI() {

        modoRegistro = true;
        modoEdicion = false;

        columnaActiva = listaSesionesIds.indexOf(sesionActual.getId());

        scrollHeader.post(() -> scrollHeader.fullScroll(View.FOCUS_RIGHT));

        adapter.setModoRegistro(true, columnaActiva, sesionActual.getId(), false);

        actualizarUI();
    }


    private void sincronizarScroll() {

        scrollHeader.setOnScrollChangeListener((v, x, y, oldx, oldy) -> {

            if (isSyncing) return;

            isSyncing = true;

            if (adapter != null) {
                for (HorizontalScrollView s : adapter.getScrolls()) {
                    s.scrollTo(x, 0);
                }
            }

            isSyncing = false;
        });
    }

    private String obtenerHoraActual() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private int obtenerDiaActual() {

        int dia = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        switch (dia) {
            case Calendar.MONDAY: return 1;
            case Calendar.TUESDAY: return 2;
            case Calendar.WEDNESDAY: return 3;
            case Calendar.THURSDAY: return 4;
            case Calendar.FRIDAY: return 5;
            case Calendar.SATURDAY: return 6;
            default: return -1;
        }
    }

    private void verificarAsistenciaActiva() {

        Executors.newSingleThreadExecutor().execute(() -> {

            boolean horarioActivo =
                    baseDatos.claseDao()
                            .registroActivoClase(
                                    idClase,
                                    obtenerDiaActual(),
                                    obtenerHoraActual()
                            );

            String fechaHoy =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(new Date());

            SesionClase sesion =
                    baseDatos.sesionClaseDao()
                            .obtenerSesionPorFecha(
                                    idClase,
                                    fechaHoy
                            );

            boolean mostrarOverlay;

            if (horarioActivo) {

                mostrarOverlay =
                        (sesion == null || !sesion.isTomada());

            } else {

                mostrarOverlay = false;
            }

            if (!isAdded())
                return;

            requireActivity().runOnUiThread(() -> {

                overlay.setVisibility(
                        mostrarOverlay
                                ? View.VISIBLE
                                : View.GONE
                );
            });
        });
    }

    private void configurarOverlay(View view) {

        AppCompatButton btnComenzar =
                view.findViewById(R.id.btnComenzar);

        AppCompatButton btnCancelar =
                view.findViewById(R.id.btnCancelarRegistro);

        btnComenzar.setOnClickListener(v -> {

            overlay.setVisibility(View.GONE);

            modoRegistro = true;
            modoEdicion = false;

            iniciarSesionSiNoExiste();
        });

        btnCancelar.setOnClickListener(v -> {

            Executors.newSingleThreadExecutor().execute(() -> {

                String fechaHoy =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        ).format(new Date());

                SesionClase sesion =
                        baseDatos.sesionClaseDao()
                                .obtenerSesionPorFecha(
                                        idClase,
                                        fechaHoy
                                );

                if (sesion == null) {

                    SesionClase nueva =
                            new SesionClase(
                                    fechaHoy,
                                    true,
                                    obtenerDiaActual()
                            );

                    nueva.setIdClase(idClase);

                    baseDatos.sesionClaseDao()
                            .insertar(nueva);

                } else {
                    sesion.setTomada(true);

                    baseDatos.sesionClaseDao()
                            .actualizarSesion(sesion);
                }

                if (!isAdded())
                    return;

                requireActivity().runOnUiThread(() -> {

                    overlay.setVisibility(View.GONE);

                    cargarSesionesYAsistencias(false);

                    if(getActivity() instanceof InterfazClase){

                        ((InterfazClase)getActivity())
                                .actualizarIndicadores();
                    }
                });

            });
        });
    }

    private void guardarAsistencia() {

        Executors.newSingleThreadExecutor().execute(() -> {

            if (columnaActiva == -1) return;

            int idSesion = listaSesionesIds.get(columnaActiva);

            for (Alumno a : listaAlumnos) {

                Map<Integer, Boolean> mapa =
                        asistenciasTemp.get(a.getId());

                if (mapa != null) {

                    Boolean asistencia = mapa.get(idSesion);

                    if (asistencia != null) {

                        AsistenciaAlumno registro =
                                baseDatos.asistenciaAlumnoDao()
                                        .buscarAsistenciaAlumnoClase(
                                                idSesion,
                                                a.getId()
                                        );

                        if (registro != null) {

                            registro.asistencia = asistencia;

                            baseDatos.asistenciaAlumnoDao()
                                    .actualizarAsistencia(registro);
                        }
                    }
                }
            }

            if (modoRegistro
                    && sesionActual != null
                    && idSesion == sesionActual.getId()) {

                sesionActual.setTomada(true);

                baseDatos.sesionClaseDao()
                        .actualizarSesion(sesionActual);
            }

            if (!isAdded())
                return;

            requireActivity().runOnUiThread(() -> {

                modoRegistro = false;
                modoEdicion = false;
                columnaActiva = -1;

                adapter.setModoRegistro(false, -1, -1, false);

                Toast.makeText(
                        getContext(),
                        "Asistencia guardada",
                        Toast.LENGTH_SHORT
                ).show();

                if(isAdded() && getActivity() instanceof InterfazClase){

                    InterfazClase activity =
                            (InterfazClase) getActivity();

                    activity.actualizarIndicadores();
                }

                actualizarUI();

                cargarSesionesYAsistencias(false);
            });

        });
    }
}