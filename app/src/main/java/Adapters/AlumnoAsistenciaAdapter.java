package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entity.Alumno;

public class AlumnoAsistenciaAdapter
        extends RecyclerView.Adapter<AlumnoAsistenciaAdapter.ViewHolder> {

    private List<Alumno> lista;

    private List<HorizontalScrollView> scrolls = new ArrayList<>();
    private int scrollX = 0;
    private HorizontalScrollView scrollHeader;
    private boolean isSyncing = false;

    private boolean modoRegistro = false;
    private boolean modoEdicion = false;
    private int columnaActiva = -1;
    private int idSesionActual = -1;

    private Map<Integer, Map<Integer, Boolean>> asistencias = new HashMap<>();
    private List<Integer> listaSesionesIds = new ArrayList<>();

    public AlumnoAsistenciaAdapter(
            List<Alumno> lista,
            HorizontalScrollView scrollHeader
    ) {
        this.lista = lista;
        this.scrollHeader = scrollHeader;
    }

    public List<HorizontalScrollView> getScrolls() {
        return scrolls;
    }

    public void setModoRegistro(boolean activo, int columna, int idSesion, boolean modoEdicion) {
        this.modoRegistro = activo;
        this.columnaActiva = columna;
        this.idSesionActual = idSesion;
        this.modoEdicion = modoEdicion;
        notifyDataSetChanged();
    }

    public void setDatos(
            Map<Integer, Map<Integer, Boolean>> asistencias,
            List<Integer> sesionesIds
    ) {
        this.asistencias = asistencias;
        this.listaSesionesIds = sesionesIds;
        notifyDataSetChanged();
    }

    public void seleccionarColumna(int posicion) {
        this.columnaActiva = posicion;

        if (posicion >= 0 && posicion < listaSesionesIds.size()) {
            this.idSesionActual = listaSesionesIds.get(posicion);
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtPorcentaje;
        LinearLayout layoutAsistencias;
        HorizontalScrollView scrollFila;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPorcentaje = itemView.findViewById(R.id.txtPorcentaje);
            layoutAsistencias = itemView.findViewById(R.id.layoutAsistencias);
            scrollFila = itemView.findViewById(R.id.scrollFila);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fila_asistencia, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Alumno alumno = lista.get(position);
        holder.txtNombre.setText(alumno.getNombre());

        int totalColumnas = listaSesionesIds.size();

        if (holder.layoutAsistencias.getChildCount() != totalColumnas) {

            holder.layoutAsistencias.removeAllViews();

            for (int i = 0; i < totalColumnas; i++) {
                holder.layoutAsistencias.addView(crearCelda(holder));
            }
        }

        int asistenciasCount = 0;

        for (int i = 0; i < totalColumnas; i++) {

            TextView tv = (TextView) holder.layoutAsistencias.getChildAt(i);
            int idSesion = listaSesionesIds.get(i);

            Map<Integer, Boolean> mapaAlumno = asistencias.get(alumno.getId());
            Boolean asistio = null;

            if (mapaAlumno != null && mapaAlumno.containsKey(idSesion)) {
                asistio = mapaAlumno.get(idSesion);
            }

            if(asistio == null){

                tv.setText("/");
                tv.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grisPalidoOscuro));

            }else if(asistio){

                tv.setText("✔");

            }else{

                tv.setText("✘");
            }

            if (Boolean.TRUE.equals(asistio)) {
                asistenciasCount++;
            }

            tv.setOnClickListener(null);
            tv.setBackgroundColor(Color.TRANSPARENT);

            if ((modoRegistro || modoEdicion)
                    && i == columnaActiva
                    && asistio != null) {

                tv.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.amarilloClaro));

                tv.setOnClickListener(v -> {

                    Map<Integer, Boolean> mapa =
                            asistencias.computeIfAbsent(
                                    alumno.getId(),
                                    k -> new HashMap<>()
                            );

                    boolean actual = mapa.getOrDefault(idSesion, false);
                    mapa.put(idSesion, !actual);

                    int adapterPosition = holder.getBindingAdapterPosition();

                    if(adapterPosition != RecyclerView.NO_POSITION){
                        notifyItemChanged(adapterPosition);
                    }
                });
            }
        }

        int totalValidas = 0;

        for(int i = 0; i < totalColumnas; i++){

            int idSesion = listaSesionesIds.get(i);

            Map<Integer, Boolean> mapaAlumno =
                    asistencias.get(alumno.getId());

            if(mapaAlumno != null
                    && mapaAlumno.containsKey(idSesion)){

                totalValidas++;
            }
        }

        int porcentaje =
                totalValidas == 0
                        ? 0
                        : (int)((asistenciasCount * 100f)
                        / totalValidas);

        holder.txtPorcentaje.setText(porcentaje + "%");

        holder.scrollFila.setOnTouchListener(
                modoRegistro ? (v, e) -> true : null
        );

        scrolls.remove(holder.scrollFila);
        scrolls.add(holder.scrollFila);

        holder.scrollFila.setOnScrollChangeListener((v, x, y, oldx, oldy) -> {

            if (isSyncing) return;

            isSyncing = true;
            scrollX = x;

            for (HorizontalScrollView s : scrolls) {
                if (s != holder.scrollFila) {
                    s.scrollTo(scrollX, 0);
                }
            }

            if (scrollHeader != null) {
                scrollHeader.scrollTo(scrollX, 0);
            }

            isSyncing = false;
        });

        holder.scrollFila.scrollTo(scrollX, 0);
    }

    private TextView crearCelda(ViewHolder holder) {

        TextView tv = new TextView(holder.itemView.getContext());

        tv.setLayoutParams(
                new LinearLayout.LayoutParams(80, ViewGroup.LayoutParams.MATCH_PARENT)
        );

        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(12);

        return tv;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}