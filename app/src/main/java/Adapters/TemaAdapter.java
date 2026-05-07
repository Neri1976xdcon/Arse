package Adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.List;
import java.util.Set;
import Entity.Tema;

public class TemaAdapter extends RecyclerView.Adapter<TemaAdapter.ViewHolder> {

    private List<Tema> temas;

    private int posicionSeleccionada = RecyclerView.NO_POSITION;

    private OnTemaSeleccionadoListener listener;

    public TemaAdapter(List<Tema> temas) {
        this.temas = temas;
    }

    public void setOnTemaSeleccionadoListener(OnTemaSeleccionadoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tema, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Tema tema = temas.get(position);

        boolean seleccionado = position == posicionSeleccionada;

        int color;

        try {
            color = Color.parseColor(tema.getColorPrincipal());
        } catch (Exception e) {
            color = Color.GRAY;
        }

        holder.color.setBackgroundTintList(
                ColorStateList.valueOf(color)
        );

        holder.itemView.setSelected(seleccionado);
        holder.itemView.setElevation(seleccionado ? 12f : 0f);

        holder.itemView.setOnClickListener(v -> {

            if (posicionSeleccionada == position) {

                int prev = posicionSeleccionada;
                posicionSeleccionada = RecyclerView.NO_POSITION;

                notifyItemChanged(prev);

                if (listener != null) {
                    listener.onTemaSeleccionado(null);
                }

                return;
            }

            int anterior = posicionSeleccionada;
            posicionSeleccionada = position;

            if (anterior != RecyclerView.NO_POSITION) {
                notifyItemChanged(anterior);
            }

            notifyItemChanged(position);

            if (listener != null) {
                listener.onTemaSeleccionado(tema);
            }
        });
    }

    @Override
    public int getItemCount() {
        return temas.size();
    }

    public Tema getTemaSeleccionado() {
        return posicionSeleccionada != RecyclerView.NO_POSITION
                ? temas.get(posicionSeleccionada)
                : null;
    }

    public void limpiarSeleccion() {
        if (posicionSeleccionada != RecyclerView.NO_POSITION) {
            int prev = posicionSeleccionada;
            posicionSeleccionada = RecyclerView.NO_POSITION;
            notifyItemChanged(prev);
        }
    }

    public void seleccionarTemaPorId(int idTema) {
        for (int i = 0; i < temas.size(); i++) {
            if (temas.get(i).getId() == idTema) {

                int prev = posicionSeleccionada;
                posicionSeleccionada = i;

                if (prev != RecyclerView.NO_POSITION) {
                    notifyItemChanged(prev);
                }

                notifyItemChanged(i);
                return;
            }
        }
    }

    public void actualizarLista(List<Tema> nuevaLista) {
        this.temas = nuevaLista;
        posicionSeleccionada = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public interface OnTemaSeleccionadoListener {
        void onTemaSeleccionado(Tema tema);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View color;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.vColor);
        }
    }
}