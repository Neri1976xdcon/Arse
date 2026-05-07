package Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import Clases.HorarioDia;
import Entity.Dia;

public class HorarioAdapter
        extends RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder> {

    private List<Dia> dias;

    private Set<Integer> expandidos = new HashSet<>();
    private Map<Integer, HorarioDia> horarios = new HashMap<>();
    private Map<Integer, String> erroresExternos = new HashMap<>();

    public HorarioAdapter(List<Dia> dias) {
        this.dias = dias;
    }

    static class HorarioViewHolder extends RecyclerView.ViewHolder {

        LinearLayout contenedorDia, layoutHorario;
        TextView nombreDia, mensaje;
        ImageView flecha;

        EditText etHoraInicio, etMinutosInicio;
        EditText etHoraFin, etMinutosFin;

        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);

            contenedorDia = itemView.findViewById(R.id.contenedorDia);
            layoutHorario = itemView.findViewById(R.id.layoutHorario);

            nombreDia = itemView.findViewById(R.id.nombreDia);
            mensaje = itemView.findViewById(R.id.mensajeHorario);
            flecha = itemView.findViewById(R.id.flecha);

            etHoraInicio = itemView.findViewById(R.id.etHoraInicio);
            etMinutosInicio = itemView.findViewById(R.id.etMinutosInicio);
            etHoraFin = itemView.findViewById(R.id.etHoraFin);
            etMinutosFin = itemView.findViewById(R.id.etMinutosFin);
        }
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario_dia, parent, false);

        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {

        Dia dia = dias.get(position);
        int id = dia.getId();

        holder.nombreDia.setText(
                dia.getNombre()
                        .toUpperCase()
        );

        holder.itemView.setTag(id);

        HorarioDia horario = horarios.get(id);

        if (horario == null) {
            horario = new HorarioDia(id);
            horarios.put(id, horario);
        }

        boolean expandido = expandidos.contains(id);

        holder.layoutHorario.setVisibility(expandido ? View.VISIBLE : View.GONE);
        holder.flecha.setRotation(expandido ? 180f : 0f);

        setTexto(holder.etHoraInicio, horario.getHoraInicio());
        setTexto(holder.etMinutosInicio, horario.getMinutosInicio());
        setTexto(holder.etHoraFin, horario.getHoraFin());
        setTexto(holder.etMinutosFin, horario.getMinutosFin());

        HorarioDia finalHorario = horario;
        holder.contenedorDia.setOnClickListener(v -> {

            if (expandidos.contains(id)) {

                validarHorario(finalHorario);
                expandidos.remove(id);

            } else {
                expandidos.add(id);
            }

            notifyItemChanged(holder.getAdapterPosition());
        });

        agregarWatcher(holder.etHoraInicio, 7, 20, horario::setHoraInicio);
        agregarWatcher(holder.etMinutosInicio, 0, 59, horario::setMinutosInicio);
        agregarWatcher(holder.etHoraFin, 7, 21, horario::setHoraFin);
        agregarWatcher(holder.etMinutosFin, 0, 59, horario::setMinutosFin);

        pintarEstado(holder, horario);
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    public List<HorarioDia> obtenerHorariosRegistrados() {
        List<HorarioDia> lista = new ArrayList<>();

        for (HorarioDia h : horarios.values()) {
            if (!h.estaVacio()) {
                lista.add(h);
            }
        }
        return lista;
    }

    public List<HorarioDia> obtenerHorariosValidos() {
        List<HorarioDia> lista = new ArrayList<>();

        for (HorarioDia h : horarios.values()) {
            if (!h.estaVacio()
                    && h.estaCompleto()
                    && h.esValido()
                    && !h.tieneError()) {

                lista.add(h);
            }
        }
        return lista;
    }

    public void actualizarLista(List<Dia> nuevaLista) {

        this.dias = nuevaLista != null ? nuevaLista : new ArrayList<>();

        expandidos.clear();
        horarios.clear();
        erroresExternos.clear();

        notifyDataSetChanged();
    }

    public void expandirDia(int diaId) {

        expandidos.add(diaId);

        HorarioDia h = horarios.get(diaId);

        if (h != null && !h.tieneError()) {
            h.setError("⚠️ Verifica este horario");
        }

        notifyDataSetChanged();
    }

    public void setErroresExternos(
            Map<Integer, String> errores
    ) {

        erroresExternos.clear();

        if (errores != null) {
            erroresExternos.putAll(
                    errores
            );
        }

        for (HorarioDia h : horarios.values()) {

            if (erroresExternos.containsKey(
                    h.getDiaId()
            )) {

                h.setError(
                        erroresExternos.get(
                                h.getDiaId()
                        )
                );

                expandidos.add(
                        h.getDiaId()
                );
            }
        }

        notifyDataSetChanged();
    }


    private void validarHorario(HorarioDia h) {

        h.limpiarError();

        if (h.estaVacio()) return;

        if (!h.estaCompleto()) {

            h.setError("⚠️ HORARIO INCOMPLETO");

        }
        else if (!h.esValido()) {

            h.setError("⚠️ HORA FIN MENOR A INICIO");

        }
        else if (erroresExternos.containsKey(
                h.getDiaId()
        )) {

            h.setError(
                    erroresExternos.get(
                            h.getDiaId()
                    )
            );
        }
    }

    public void expandirDiasConError() {

        for (HorarioDia h : horarios.values()) {

            if (h.tieneError()) {

                expandidos.add(
                        h.getDiaId()
                );
            }
        }

        notifyDataSetChanged();
    }

    private void agregarWatcher(
            EditText et,
            int min,
            int max,
            OnValorChange callback
    ) {

        if (et.getTag() instanceof TextWatcher) {
            et.removeTextChangedListener(
                    (TextWatcher) et.getTag()
            );
        }

        TextWatcher watcher = new TextWatcher() {

            boolean editando = false;

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int a,
                    int b,
                    int c
            ) {}

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int a,
                    int b,
                    int c
            ) {}

            @Override
            public void afterTextChanged(
                    Editable s
            ) {

                if (editando) return;

                if (!et.isFocused()) return;

                editando = true;

                String txt =
                        s.toString();

                HorarioDia h =
                        obtenerHorarioPorEditText(et);

                if (!txt.isEmpty()) {


                    if (!txt.matches("\\d+")) {

                        txt =
                                txt.replaceAll(
                                        "[^\\d]",
                                        ""
                                );

                        et.setText(txt);
                        et.setSelection(
                                txt.length()
                        );
                    }

                    if (!txt.isEmpty()) {

                        int val =
                                Integer.parseInt(
                                        txt
                                );

                        if (val < min
                                || val > max) {

                            et.setError(
                                    "Rango "
                                            + min
                                            + "-"
                                            + max
                            );

                            callback.onChange(
                                    null
                            );

                        }

                        else {

                            et.setError(
                                    null
                            );

                            callback.onChange(
                                    val
                            );

                            if (h != null) {

                                erroresExternos.remove(
                                        h.getDiaId()
                                );

                                h.limpiarError();

                                validarHorario(h);

                            }
                        }
                    }

                }

                else {

                    callback.onChange(
                            null
                    );

                    if (h != null) {

                        erroresExternos.remove(
                                h.getDiaId()
                        );

                        h.limpiarError();

                        validarHorario(h);

                        notifyDataSetChanged();
                    }
                }

                editando = false;
            }
        };

        et.addTextChangedListener(
                watcher
        );

        et.setTag(
                watcher
        );
    }


    private HorarioDia obtenerHorarioPorEditText(
            EditText et
    ) {

        View item =
                (View) et.getParent();

        while (item != null
                && !(item.getTag() instanceof Integer)) {

            ViewParent parent =
                    item.getParent();

            if (parent instanceof View) {
                item = (View) parent;
            } else {
                return null;
            }
        }

        if (item == null)
            return null;

        Integer diaId =
                (Integer) item.getTag();

        return horarios.get(diaId);
    }


    public boolean validarTodosLosHorarios() {

        boolean hayErrores = false;

        for (HorarioDia h : horarios.values()) {

            validarHorario(h);

            if (h.tieneError()) {

                expandidos.add(
                        h.getDiaId()
                );

                hayErrores = true;
            }
        }

        notifyDataSetChanged();

        return hayErrores;
    }


    public boolean hayErrores() {

        for (HorarioDia h : horarios.values()) {

            if (h.tieneError()) {
                return true;
            }
        }

        return false;
    }

    private void pintarEstado(HorarioViewHolder holder, HorarioDia h) {

        Context ctx = holder.itemView.getContext();

        if (h.estaVacio()) {

            holder.mensaje.setVisibility(View.GONE);
            pintar(holder, ctx, R.color.grisPalidoClaro, R.color.grisPalidoOscuro);
            return;
        }

        if (h.tieneError()) {

            holder.mensaje.setText(h.getError());
            holder.mensaje.setVisibility(View.VISIBLE);

            pintar(holder, ctx, R.color.rojoError, R.color.blanco);

        } else {

            holder.mensaje.setVisibility(View.GONE);
            pintar(holder, ctx, R.color.azulArse, R.color.blanco);
        }
    }

    private void pintar(HorarioViewHolder holder, Context ctx, int fondo, int texto) {

        GradientDrawable drawable = (GradientDrawable)
                holder.contenedorDia.getBackground().mutate();

        drawable.setColor(ContextCompat.getColor(ctx, fondo));

        holder.nombreDia.setTextColor(ContextCompat.getColor(ctx, texto));
        holder.flecha.setColorFilter(ContextCompat.getColor(ctx, texto));
    }

    private void setTexto(EditText et, Integer val) {

        String nuevo = val != null ? String.valueOf(val) : "";

        if (!et.getText().toString().equals(nuevo)) {
            et.setText(nuevo);
        }
    }

    interface OnValorChange {
        void onChange(Integer val);
    }
}