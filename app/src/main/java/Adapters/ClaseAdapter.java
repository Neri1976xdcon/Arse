package Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.InterfazClase;
import com.example.arse.R;

import java.util.ArrayList;
import java.util.List;

import Clases.ClaseItem;
import Entity.Clase;

public class ClaseAdapter
        extends RecyclerView.Adapter<ClaseAdapter.ViewHolder> {

    private List<ClaseItem> lista;
    private Context context;

    public ClaseAdapter(
            List<ClaseItem> lista
    ) {
        this.lista = lista != null
                ? lista
                : new ArrayList<>();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView grupoClase;
        TextView aulaClase;
        TextView asignaturaClase;

        ConstraintLayout fondoImagenClase;

        ImageView iconoClase;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            grupoClase =
                    itemView.findViewById(
                            R.id.grupoClase
                    );

            aulaClase =
                    itemView.findViewById(
                            R.id.aulaClase
                    );

            asignaturaClase =
                    itemView.findViewById(
                            R.id.asignaturaClase
                    );

            fondoImagenClase =
                    itemView.findViewById(
                            R.id.fondoImagenClase
                    );

            iconoClase =
                    itemView.findViewById(
                            R.id.iconoClase
                    );
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        context = parent.getContext();

        View view = LayoutInflater
                .from(context)
                .inflate(
                        R.layout.item_clase,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        ClaseItem item =
                lista.get(position);

        Clase clase =
                item.getClase();

        if (clase == null)
            return;


        holder.asignaturaClase.setText(
                clase.getNombre()
        );

        holder.grupoClase.setText(
                item.getNombreGrupo()
        );

        String nombreAula =
                item.getNombreAula();

        holder.aulaClase.setText(
                nombreAula != null
                        ? nombreAula
                        : ""
        );


        Context ctx =
                holder.itemView.getContext();

        int colorPrincipal;
        int colorFondo;

        try {

            colorPrincipal =
                    Color.parseColor(
                            item.getColorPrincipal()
                    );

        } catch (Exception e) {

            colorPrincipal =
                    ContextCompat.getColor(
                            ctx,
                            R.color.azulArse
                    );
        }

        try {

            colorFondo =
                    Color.parseColor(
                            item.getColorFondo()
                    );

        } catch (Exception e) {

            colorFondo =
                    ContextCompat.getColor(
                            ctx,
                            R.color.blanco
                    );
        }


        if (holder.fondoImagenClase
                .getBackground()
                instanceof GradientDrawable) {

            GradientDrawable fondoImgClase =
                    (GradientDrawable)
                            holder.fondoImagenClase
                                    .getBackground()
                                    .getConstantState()
                                    .newDrawable()
                                    .mutate();

            fondoImgClase.setColor(
                    colorPrincipal
            );
            holder.fondoImagenClase.setBackground(fondoImgClase);
        }


        if (holder.grupoClase
                .getBackground()
                instanceof GradientDrawable) {

            GradientDrawable fondoGrupo =
                    (GradientDrawable)
                            holder.grupoClase
                                    .getBackground()
                                    .mutate();

            fondoGrupo.setColor(
                    colorPrincipal
            );
        }


        holder.aulaClase.setTextColor(
                colorPrincipal
        );


        holder.iconoClase.setImageTintList(
                ColorStateList.valueOf(
                        colorFondo
                )
        );


        holder.itemView.setOnClickListener(v -> {

            int pos =
                    holder.getAdapterPosition();

            if (pos == RecyclerView.NO_POSITION)
                return;

            ClaseItem itemClick =
                    lista.get(pos);

            Intent intent =
                    new Intent(
                            v.getContext(),
                            InterfazClase.class
                    );

            intent.putExtra(
                    "idclase",
                    itemClick.getClase().getId()
            );

            v.getContext()
                    .startActivity(intent);

        });

    }


    @Override
    public int getItemCount() {
        return lista.size();
    }


    public void actualizar(
            List<ClaseItem> nuevaLista
    ) {

        if (nuevaLista == null)
            return;

        lista.clear();

        lista.addAll(
                nuevaLista
        );

        notifyDataSetChanged();

    }


    public ClaseItem obtenerItem(
            int posicion
    ) {
        return lista.get(posicion);
    }

}

