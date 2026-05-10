package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.List;

import Entity.Alumno;

public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder> {

    private List<Alumno> listaAlumnos;

    private OnAlumnoClickListener clickListener;

    private OnAlumnoAccionListener accionListener;

    private String modoAccion = null;

    public AlumnoAdapter(List<Alumno> listaAlumnos) {
        this.listaAlumnos = listaAlumnos;
    }

    // Ajuste de márgenes para pantallas pequeñas
    public void setModoAccion(String modo) {
        this.modoAccion = modo;
        notifyDataSetChanged();
    }

    public void setOnAlumnoClickListener(OnAlumnoClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnAlumnoAccionListener(OnAlumnoAccionListener listener) {
        this.accionListener = listener;
    }


    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {

        Alumno alumno = listaAlumnos.get(position);

        holder.nombreAlumno.setText(alumno.getNombre());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAlumnoClick(alumno);
            }
        });

        if (modoAccion != null) {
            holder.btnAccion.setVisibility(View.VISIBLE);
            holder.btnAccion.setText(modoAccion);
        } else {
            holder.btnAccion.setVisibility(View.GONE);
        }

        holder.btnAccion.setOnClickListener(v -> {
            if (accionListener != null && modoAccion != null) {
                accionListener.onAccionAlumno(alumno, modoAccion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAlumnos != null ? listaAlumnos.size() : 0;
    }


    public void actualizarLista(List<Alumno> nuevaLista) {
        this.listaAlumnos = nuevaLista;
        notifyDataSetChanged();
    }

    public Alumno getAlumno(int position) {
        return listaAlumnos.get(position);
    }


    static class AlumnoViewHolder extends RecyclerView.ViewHolder {

        TextView nombreAlumno;
        AppCompatButton btnAccion;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreAlumno = itemView.findViewById(R.id.nombreAlumno);
            btnAccion = itemView.findViewById(R.id.btnAccionALumno);
        }
    }


    public interface OnAlumnoClickListener {
        void onAlumnoClick(Alumno alumno);
    }

    public interface OnAlumnoAccionListener {
        void onAccionAlumno(Alumno alumno, String accion);
    }
}
