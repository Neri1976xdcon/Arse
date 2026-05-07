package Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Entity.Alumno;

public class AlumnoAgregarAdapter
        extends RecyclerView.Adapter<AlumnoAgregarAdapter.ViewHolder> {

    private List<Alumno> lista;
    private Set<Integer> alumnosSeleccionados = new HashSet<>();

    private Context context;

    public AlumnoAgregarAdapter(Context context, List<Alumno> lista) {
        this.context = context;
        this.lista = lista;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        AppCompatButton boton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreAlumno);
            boton = itemView.findViewById(R.id.btnAccionALumno);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Alumno alumno = lista.get(position);

        holder.nombre.setText(alumno.getNombre());

        boolean seleccionado = alumnosSeleccionados.contains(alumno.getId());

        if (seleccionado) {
            holder.boton.setText("AGREGADO");
            holder.boton.setTextColor(
                    ContextCompat.getColor(context, R.color.verdeDrawer)
            );
        } else {
            holder.boton.setText("AGREGAR");
            holder.boton.setTextColor(
                    ContextCompat.getColor(context, R.color.grisPalidoOscuro)
            );
            holder.boton.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.grisPalidoClaro)
            );
        }

        holder.boton.setOnClickListener(v -> {

            if (seleccionado) {
                alumnosSeleccionados.remove(alumno.getId());
            } else {
                alumnosSeleccionados.add(alumno.getId());
            }

            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizarLista(List<Alumno> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public List<Integer> getAlumnosSeleccionados() {
        return new ArrayList<>(alumnosSeleccionados);
    }
}


