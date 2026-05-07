package Adapters;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
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

import java.util.List;

import Entity.CriterioEvaluacion;

public class CriterioClaseAdapter extends RecyclerView.Adapter<CriterioClaseAdapter.CriterioViewHolder> {

    private Context context;
    private List<CriterioEvaluacion> lista;

    private int colorFondoSeleccionado;
    private boolean modoCompacto;

    private int posicionSeleccionada = 0;

    private OnCriterioSeleccionadoListener listener;

    public interface OnCriterioSeleccionadoListener {
        void onCriterioSeleccionado(CriterioEvaluacion criterio);
    }

    public CriterioClaseAdapter(
            Context context,
            List<CriterioEvaluacion> lista,
            int colorFondoSeleccionado,
            boolean modoCompacto,
            OnCriterioSeleccionadoListener listener
    ) {
        this.context = context;
        this.lista = lista;

        // 🔥 FIX: convertir a color real
        this.colorFondoSeleccionado = colorFondoSeleccionado;

        this.modoCompacto = modoCompacto;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CriterioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_criterio, parent, false);

        return new CriterioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CriterioViewHolder holder, int position) {

        CriterioEvaluacion criterio = lista.get(position);

        holder.txtNombre.setText(criterio.getNombre());


        if (modoCompacto) {
            ajustarTamanoCompacto(holder);
            holder.txtNombre.setVisibility(View.GONE);
        } else {
            holder.txtNombre.setVisibility(View.VISIBLE);
        }


        int resId = context.getResources().getIdentifier(
                criterio.getIcono(),
                "drawable",
                context.getPackageName()
        );

        if (resId != 0) {
            holder.imgIcono.setImageResource(resId);
        }


        boolean seleccionado = position == posicionSeleccionada;
        holder.contenedor.setSelected(seleccionado);


        holder.itemView.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();

            if (pos == RecyclerView.NO_POSITION) return;

            int anterior = posicionSeleccionada;
            posicionSeleccionada = pos;

            notifyItemChanged(anterior);
            notifyItemChanged(posicionSeleccionada);

            if (listener != null) {
                listener.onCriterioSeleccionado(lista.get(posicionSeleccionada));
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }


    private void ajustarTamanoCompacto(CriterioViewHolder holder) {

        int size = dp(45);
        int padding = dp(10);

        RecyclerView.LayoutParams params =
                new RecyclerView.LayoutParams(size, size);

        params.setMargins(dp(4), dp(4), dp(4), dp(4));

        holder.contenedor.setLayoutParams(params);
        holder.contenedor.setPadding(padding, padding, padding, padding);

        holder.contenedor.setOrientation(LinearLayout.VERTICAL);
        holder.contenedor.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0
                );

        iconParams.weight = 1;
        iconParams.gravity = Gravity.CENTER;

        holder.imgIcono.setLayoutParams(iconParams);
        holder.imgIcono.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.imgIcono.setAdjustViewBounds(true);
    }


    private int dp(int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

    private void desaturarIcono(ImageView imageView) {

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);

        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void saturarIcono(ImageView imageView) {

        imageView.clearColorFilter();
    }


    public static class CriterioViewHolder extends RecyclerView.ViewHolder {

        LinearLayout contenedor;
        ImageView imgIcono;
        TextView txtNombre;

        public CriterioViewHolder(@NonNull View itemView) {
            super(itemView);

            contenedor = itemView.findViewById(R.id.itemCriterio);
            imgIcono = itemView.findViewById(R.id.imgItemCriterio);
            txtNombre = itemView.findViewById(R.id.txtItemCriterio);
        }
    }


    public CriterioEvaluacion obtenerSeleccionado() {

        if (lista.isEmpty()) return null;

        return lista.get(posicionSeleccionada);
    }

    public void seleccionarPosicion(int nuevaPosicion) {

        if (nuevaPosicion < 0 || nuevaPosicion >= lista.size())
            return;

        if (nuevaPosicion == posicionSeleccionada)
            return;

        int anterior = posicionSeleccionada;

        posicionSeleccionada = nuevaPosicion;

        notifyItemChanged(anterior);
        notifyItemChanged(posicionSeleccionada);
    }

    public int obtenerPosicionSeleccionada() {
        return posicionSeleccionada;
    }
}
