package Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.List;

import Clases.GrupoItem;

// Adaptador para mostrar y gestionar la selección de grupos en un RecyclerView

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder> {

    private List<GrupoItem> lista;
    private int posicionSeleccionada = RecyclerView.NO_POSITION;

    public GrupoAdapter(List<GrupoItem> lista) {
        this.lista = lista;
    }

    private OnGrupoSeleccionadoListener listener;

    public void setOnGrupoSeleccionadoListener(OnGrupoSeleccionadoListener listener) {
        this.listener = listener;
    }

    private int grupoSeleccionadoId = -1;

    public void setGrupoSeleccionadoId(int id) {
        this.grupoSeleccionadoId = id;
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo, parent, false);
        return new GrupoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {

        GrupoItem grupo = lista.get(position);
        // Asigna los datos del objeto GrupoItem a los componentes visuales
        holder.nombre.setText(grupo.getNombre());
        holder.aula.setText(grupo.getAula());
        holder.total.setText(String.valueOf(grupo.getTotalAlumnos()));

        boolean seleccionado = grupo.getId() == grupoSeleccionadoId;

        holder.itemView.setSelected(seleccionado);
        holder.itemView.setElevation(seleccionado ? 10f : 0f);

        holder.itemView.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            GrupoItem grupoActual = lista.get(pos);
            // Si el usuario toca el grupo que ya estaba seleccionado, se deselecciona (Toggle)

            if (grupoSeleccionadoId == grupoActual.getId()) {

                int anterior = posicionSeleccionada;

                grupoSeleccionadoId = -1;
                posicionSeleccionada = RecyclerView.NO_POSITION;

                if (anterior != RecyclerView.NO_POSITION && anterior < lista.size()) {
                    notifyItemChanged(anterior);
                }

                if (listener != null) listener.onGrupoSeleccionado(null);
                return;
            }

            int anterior = posicionSeleccionada;

            grupoSeleccionadoId = grupoActual.getId();
            posicionSeleccionada = pos;

            if (anterior != RecyclerView.NO_POSITION && anterior < lista.size()) {
                notifyItemChanged(anterior);
            }

            notifyItemChanged(pos);

            if (listener != null) {
                listener.onGrupoSeleccionado(grupoActual);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public GrupoItem getGrupoSeleccionado() {
        return posicionSeleccionada != RecyclerView.NO_POSITION
                ? lista.get(posicionSeleccionada)
                : null;
    }
      
      // Permite actualizar la lista de grupos desde fuera (ej. después de una búsqueda o carga de BD)
    public void actualizarLista(List<GrupoItem> nuevaLista) {
        this.lista = nuevaLista;

        posicionSeleccionada = RecyclerView.NO_POSITION;

        if (grupoSeleccionadoId != -1) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == grupoSeleccionadoId) {
                    posicionSeleccionada = i;
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    public void limpiarSeleccion() {

        int anterior = posicionSeleccionada;

        posicionSeleccionada = RecyclerView.NO_POSITION;
        grupoSeleccionadoId = -1;

        if (anterior != RecyclerView.NO_POSITION && anterior < lista.size()) {
            notifyItemChanged(anterior);
        }
    }

    public void seleccionarGrupoPorId(int grupoId) {

        grupoSeleccionadoId = grupoId;

        int anterior = posicionSeleccionada;
        posicionSeleccionada = RecyclerView.NO_POSITION;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == grupoId) {
                posicionSeleccionada = i;
                break;
            }
        }

        if (anterior != RecyclerView.NO_POSITION && anterior < lista.size()) {
            notifyItemChanged(anterior);
        }

        if (posicionSeleccionada != RecyclerView.NO_POSITION) {
            notifyItemChanged(posicionSeleccionada);
        }
    }
    static class GrupoViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, aula, total;

        public GrupoViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.grupoNombre);
            aula = itemView.findViewById(R.id.aulaGrupo);
            total = itemView.findViewById(R.id.totalAlumnos);
        }
    }
    public interface OnGrupoSeleccionadoListener {
        void onGrupoSeleccionado(GrupoItem grupo);
    }
}