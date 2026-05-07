package Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.ArrayList;
import java.util.List;

import Clases.AlumnoAsistencia;
import Clases.AlumnoCumplimiento;
import Entity.Alumno;

public class CumplimientoAlumnoAdapter
        extends RecyclerView.Adapter<
        CumplimientoAlumnoAdapter.ViewHolder> {


    private Context context;


    private OnAlumnoClickListener listener;


    private List<AlumnoAsistencia> listaOriginal =
            new ArrayList<>();

    private List<AlumnoAsistencia> listaFiltrada =
            new ArrayList<>();


    private List<AlumnoCumplimiento> listaCumplimiento =
            new ArrayList<>();


    private Boolean filtroAsistencia = null;

    private boolean modoPorcentaje = false;

    private boolean modoBajoCumplimiento = false;


    private int idAlumnoSeleccionado = -1;


    public CumplimientoAlumnoAdapter(
            Context context,
            List<AlumnoAsistencia> lista){

        this.context = context;

        this.listaOriginal =
                new ArrayList<>(lista);

        this.listaFiltrada =
                new ArrayList<>(lista);

        this.modoPorcentaje = false;
    }

    public CumplimientoAlumnoAdapter(
            Context context,
            List<AlumnoCumplimiento> lista,
            boolean modoBajoCumplimiento,
            boolean modoPorcentaje){

        this.context = context;

        this.listaCumplimiento =
                new ArrayList<>(lista);

        this.modoBajoCumplimiento =
                modoBajoCumplimiento;

        this.modoPorcentaje =
                modoPorcentaje;
    }

    public CumplimientoAlumnoAdapter(
            Context context,
            List<AlumnoCumplimiento> lista,
            boolean modoBajoCumplimiento,
            boolean modoPorcentaje,
            OnAlumnoClickListener listener){

        this.context = context;

        this.listaCumplimiento =
                new ArrayList<>(lista);

        this.modoBajoCumplimiento =
                modoBajoCumplimiento;

        this.modoPorcentaje =
                modoPorcentaje;

        this.listener = listener;
    }

    public void actualizarLista(
            List<AlumnoAsistencia> nuevaLista){

        listaOriginal.clear();

        listaOriginal.addAll(nuevaLista);

        listaFiltrada.clear();

        listaFiltrada.addAll(nuevaLista);

        notifyDataSetChanged();
    }

    public void actualizarListaCumplimiento(
            List<AlumnoCumplimiento> nuevaLista){

        listaCumplimiento.clear();

        listaCumplimiento.addAll(nuevaLista);

        notifyDataSetChanged();
    }

    public void filtrar(String texto){

        if(modoPorcentaje)
            return;

        listaFiltrada.clear();

        for(AlumnoAsistencia alumno
                : listaOriginal){

            boolean coincideTexto =
                    alumno.getNombre()
                            .toLowerCase()
                            .contains(
                                    texto.toLowerCase()
                            );

            boolean coincideAsistencia =
                    filtroAsistencia == null
                            ||
                            alumno.isAsistencia()
                                    == filtroAsistencia;

            if(coincideTexto
                    && coincideAsistencia){

                listaFiltrada.add(alumno);
            }
        }

        notifyDataSetChanged();
    }

    public void filtrarAsistencia(
            Boolean asistencia){

        if(modoPorcentaje)
            return;

        filtroAsistencia = asistencia;

        listaFiltrada.clear();

        for(AlumnoAsistencia alumno
                : listaOriginal){

            if(asistencia == null){

                listaFiltrada.add(alumno);

            }else if(
                    alumno.isAsistencia()
                            == asistencia){

                listaFiltrada.add(alumno);
            }
        }

        notifyDataSetChanged();
    }

    public void seleccionarAlumno(
            int idAlumno){

        idAlumnoSeleccionado =
                idAlumno;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_cumplimiento_alumno,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        aplicarColorFondo(
                holder.fondo,
                R.color.grisPalidoClaro
        );

        if(modoPorcentaje){

            AlumnoCumplimiento alumno =
                    listaCumplimiento.get(position);

            holder.nombreAlumno.setText(
                    alumno.getAlumno()
                            .getNombre()
            );

            holder.infoAlumno.setText(
                    alumno.getPorcentaje()
                            + "%"
            );

            holder.infoAlumno.setTextColor(
                    ContextCompat.getColor(
                            context,
                            R.color.grisPalidoOscuro
                    )
            );

            if(alumno.getAlumno().getId()
                    == idAlumnoSeleccionado){

                aplicarColorFondo(
                        holder.fondo,
                        R.color.amarilloClaro
                );

            }else{

                aplicarColorFondo(
                        holder.fondo,
                        R.color.grisPalidoClaro
                );
            }

            holder.itemView.setOnClickListener(v -> {

                idAlumnoSeleccionado =
                        alumno.getAlumno().getId();

                notifyDataSetChanged();

                if(listener != null){

                    listener.onAlumnoClick(
                            alumno.getAlumno()
                    );
                }
            });

            return;
        }

        AlumnoAsistencia alumno =
                listaFiltrada.get(position);

        holder.nombreAlumno.setText(
                alumno.getNombre()
        );

        if(alumno.isAsistencia()){

            holder.infoAlumno.setText("✔");

            holder.infoAlumno.setTextColor(
                    ContextCompat.getColor(
                            context,
                            R.color.verdeDrawer
                    )
            );

        }else{

            holder.infoAlumno.setText("✘");

            holder.infoAlumno.setTextColor(
                    ContextCompat.getColor(
                            context,
                            R.color.rojoError
                    )
            );
        }

        aplicarColorFondo(
                holder.fondo,
                R.color.grisPalidoClaro
        );
    }

    private void aplicarColorFondo(
            View view,
            int colorRes){

        GradientDrawable fondo =
                new GradientDrawable();

        fondo.setShape(
                GradientDrawable.RECTANGLE
        );

        fondo.setCornerRadius(30f);

        fondo.setColor(
                ContextCompat.getColor(
                        context,
                        colorRes
                )
        );

        view.setBackground(fondo);
    }

    @Override
    public int getItemCount() {

        if(modoPorcentaje){

            return listaCumplimiento.size();
        }

        return listaFiltrada.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder{

        LinearLayout fondo;

        TextView nombreAlumno;

        TextView infoAlumno;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            fondo =
                    itemView.findViewById(
                            R.id.itemAlumnoCumplimiento
                    );

            nombreAlumno =
                    itemView.findViewById(
                            R.id.nombreAlumno
                    );

            infoAlumno =
                    itemView.findViewById(
                            R.id.infoAlumno
                    );
        }
    }

    public interface OnAlumnoClickListener{

        void onAlumnoClick(
                Alumno alumno
        );
    }

    public void setOnAlumnoClickListener(
            OnAlumnoClickListener listener){

        this.listener = listener;
    }
}
