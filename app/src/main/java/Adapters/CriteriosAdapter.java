package Adapters;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Entity.CriterioEvaluacion;


public class CriteriosAdapter
        extends RecyclerView.Adapter<CriteriosAdapter.CriterioViewHolder> {

    private List<CriterioEvaluacion> criterios;

    private Set<Integer> seleccionados = new HashSet<>();

    public CriteriosAdapter(List<CriterioEvaluacion> criterios) {
        this.criterios = criterios;
    }

    @NonNull
    @Override
    public CriterioViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_criterio, parent, false);

        return new CriterioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CriterioViewHolder holder,
            int position
    ) {

        CriterioEvaluacion criterio = criterios.get(position);

        holder.nombre.setText(criterio.getNombre());

        int resId = holder.itemView.getContext()
                .getResources()
                .getIdentifier(
                        criterio.getIcono(),
                        "drawable",
                        holder.itemView.getContext().getPackageName()
                );

        holder.imagen.setImageResource(resId);

        boolean seleccionado = seleccionados.contains(criterio.getId());

        holder.item.setSelected(seleccionado);

        if (seleccionado) {
            setSaturacion(holder.imagen, 1f);
        } else {
            setSaturacion(holder.imagen, 0f);
        }

        holder.nombre.setTextColor(
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        seleccionado
                                ? R.color.azulArse
                                : R.color.grisPalidoOscuro
                )
        );

        holder.item.setOnClickListener(v -> {

            if (seleccionados.contains(criterio.getId())) {
                seleccionados.remove(criterio.getId());
            } else {
                seleccionados.add(criterio.getId());
            }

            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return criterios.size();
    }

    public List<CriterioEvaluacion> getSeleccionados() {
        List<CriterioEvaluacion> lista = new ArrayList<>();

        for (CriterioEvaluacion c : criterios) {
            if (seleccionados.contains(c.getId())) {
                lista.add(c);
            }
        }

        return lista;
    }

    public void actualizarLista(List<CriterioEvaluacion> nuevaLista) {
        this.criterios = nuevaLista;
        notifyDataSetChanged();
    }

    static class CriterioViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item;
        ImageView imagen;
        TextView nombre;

        public CriterioViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.itemCriterio);
            imagen = itemView.findViewById(R.id.imgItemCriterio);
            nombre = itemView.findViewById(R.id.txtItemCriterio);
        }
    }

    private void setSaturacion(ImageView imageView, float saturacion) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(saturacion);
        imageView.setColorFilter(
                new ColorMatrixColorFilter(matrix)
        );
    }
}