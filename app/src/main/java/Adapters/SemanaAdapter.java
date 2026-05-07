package Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.List;

import Clases.SemanaCalendario;
import Entity.SesionClase;

public class SemanaAdapter
        extends RecyclerView.Adapter<SemanaAdapter.ViewHolder> {

    private Context context;

    private List<SemanaCalendario> listaSemanas;

    private boolean modoCalendario = false;

    private boolean modoBajoCumplimiento = false;

    private int semanaSeleccionada = -1;

    private int sesionSeleccionada = -1;
    private int idAlumnoSeleccionado = -1;

    private OnDiaClickListener listener;

    public interface OnDiaClickListener{

        void onDiaClick(SesionClase sesion);

        void onSemanaClick(List<SesionClase> sesiones);
    }
    public SemanaAdapter(Context context,
                         List<SemanaCalendario> listaSemanas,
                         OnDiaClickListener listener) {

        this.context = context;

        this.listaSemanas = listaSemanas;

        this.listener = listener;
    }

    public SemanaAdapter(Context context,
                         List<SemanaCalendario> listaSemanas,
                         boolean modoBajoCumplimiento) {

        this.context = context;

        this.listaSemanas = listaSemanas;

        this.modoBajoCumplimiento = modoBajoCumplimiento;

        this.listener = null;
    }

    public void actualizarLista(
            List<SemanaCalendario> nuevaLista){

        listaSemanas = nuevaLista;

        notifyDataSetChanged();
    }

    public void seleccionarUltimaSesion(){

        if(listaSemanas.isEmpty())
            return;

        int ultimaSemana =
                listaSemanas.size() - 1;

        SemanaCalendario semana =
                listaSemanas.get(ultimaSemana);

        if(semana.getSesiones().isEmpty())
            return;

        SesionClase ultimaSesion =
                semana.getSesiones()
                        .get(
                                semana.getSesiones().size() - 1
                        );

        semanaSeleccionada = -1;

        sesionSeleccionada =
                ultimaSesion.getId();

        notifyDataSetChanged();

        if(listener != null){

            listener.onDiaClick(
                    ultimaSesion
            );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_semana,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        SemanaCalendario semana =
                listaSemanas.get(position);

        holder.textoSemana.setText(
                semana.getNombreSemana()
        );

        if(modoBajoCumplimiento){

            holder.textoSemana.setVisibility(
                    View.GONE
            );

        }else{

            holder.textoSemana.setVisibility(
                    View.VISIBLE
            );
        }

        if(position == semanaSeleccionada){

            aplicarColorFondo(
                    holder.textoSemana,
                    R.color.verdeClaro
            );

        }else{

            aplicarColorFondo(
                    holder.textoSemana,
                    R.color.grisPalidoClaro
            );
        }

        holder.textoSemana.setOnClickListener(v -> {

            if(modoBajoCumplimiento)
                return;

            semanaSeleccionada = position;

            sesionSeleccionada = -1;

            notifyDataSetChanged();

            if(listener != null){

                listener.onSemanaClick(
                        semana.getSesiones()
                );
            }
        });

        holder.lunes.removeAllViews();

        holder.martes.removeAllViews();

        holder.miercoles.removeAllViews();

        holder.jueves.removeAllViews();

        holder.viernes.removeAllViews();

        holder.sabado.removeAllViews();

        for(SesionClase sesion
                : semana.getSesiones()){

            TextView dia =
                    (TextView)
                            LayoutInflater.from(context)
                                    .inflate(
                                            R.layout.item_dia_calendario,
                                            null,
                                            false
                                    );

            String numeroDia =
                    sesion.getFecha()
                            .substring(8,10);

            dia.setText(numeroDia);

            if(modoBajoCumplimiento){

                int colorFondo;

                int colorTexto =
                        ContextCompat.getColor(
                                context,
                                R.color.grisPalidoOscuro
                        );

                Boolean asistencia =
                        sesion.getAsistenciaAlumno();

                if(asistencia == null){

                    colorFondo =
                            ContextCompat.getColor(
                                    context,
                                    R.color.grisPalidoClaro
                            );

                }else if(asistencia){

                    colorFondo =
                            ContextCompat.getColor(
                                    context,
                                    R.color.amarilloClaro
                            );

                }else{

                    colorFondo =
                            ContextCompat.getColor(
                                    context,
                                    R.color.rojoCalido
                            );
                }

                GradientDrawable fondo =
                        (GradientDrawable)
                                ContextCompat.getDrawable(
                                                context,
                                                R.drawable.fondo_redondeado
                                        )
                                        .getConstantState()
                                        .newDrawable()
                                        .mutate();

                fondo.setColor(colorFondo);

                dia.setBackground(fondo);

                dia.setTextColor(colorTexto);

                dia.setOnClickListener(null);

            }else{

                if(sesion.getId()
                        == sesionSeleccionada){

                    aplicarColorFondo(
                            dia,
                            R.color.amarilloClaro
                    );

                    dia.setTextColor(
                            ContextCompat.getColor(
                                    context,
                                    R.color.negro
                            )
                    );

                }else{

                    aplicarColorFondo(
                            dia,
                            R.color.grisPalidoClaro
                    );

                    dia.setTextColor(
                            ContextCompat.getColor(
                                    context,
                                    R.color.grisPalidoOscuro
                            )
                    );
                }

                dia.setOnClickListener(v -> {

                    sesionSeleccionada =
                            sesion.getId();

                    semanaSeleccionada = -1;

                    notifyDataSetChanged();

                    if(listener != null){

                        listener.onDiaClick(
                                sesion
                        );
                    }
                });
            }

            switch (sesion.getIdDia()){

                case 1:

                    holder.lunes.addView(dia);

                    break;

                case 2:

                    holder.martes.addView(dia);

                    break;

                case 3:

                    holder.miercoles.addView(dia);

                    break;

                case 4:

                    holder.jueves.addView(dia);

                    break;

                case 5:

                    holder.viernes.addView(dia);

                    break;

                case 6:

                    holder.sabado.addView(dia);

                    break;
            }
        }
    }

    @Override
    public int getItemCount() {

        return listaSemanas.size();
    }

    private void aplicarColorFondo(
            View view,
            int colorRes){

        GradientDrawable fondo =
                (GradientDrawable)
                        ContextCompat.getDrawable(
                                        context,
                                        R.drawable.fondo_redondeado
                                )
                                .getConstantState()
                                .newDrawable()
                                .mutate();

        fondo.setColor(
                ContextCompat.getColor(
                        context,
                        colorRes
                )
        );

        view.setBackground(fondo);
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder{

        TextView textoSemana;

        FrameLayout lunes;

        FrameLayout martes;

        FrameLayout miercoles;

        FrameLayout jueves;

        FrameLayout viernes;

        FrameLayout sabado;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            textoSemana =
                    itemView.findViewById(
                            R.id.textoSemana
                    );

            lunes =
                    itemView.findViewById(
                            R.id.lunes
                    );

            martes =
                    itemView.findViewById(
                            R.id.martes
                    );

            miercoles =
                    itemView.findViewById(
                            R.id.miercoles
                    );

            jueves =
                    itemView.findViewById(
                            R.id.jueves
                    );

            viernes =
                    itemView.findViewById(
                            R.id.viernes
                    );

            sabado =
                    itemView.findViewById(
                            R.id.sabado
                    );
        }
    }
}