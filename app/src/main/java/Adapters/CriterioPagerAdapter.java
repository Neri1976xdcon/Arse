package Adapters;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.text.Normalizer;
import java.util.List;

import Entity.CriterioEvaluacion;

public class CriterioPagerAdapter
        extends FragmentStateAdapter {

    private List<CriterioEvaluacion> lista;
    private int idGrupo;
    private int idClase;

    public CriterioPagerAdapter(
            FragmentActivity activity,
            List<CriterioEvaluacion> lista,
            int idGrupo,
            int idClase
    ) {
        super(activity);
        this.lista = lista;
        this.idGrupo = idGrupo;
        this.idClase = idClase;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        CriterioEvaluacion criterio =
                lista.get(position);

        return crearFragmentDinamico(
                criterio.getNombre(),
                idGrupo
        );
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private String normalizarNombre(String nombre) {

        String sinAcentos =
                Normalizer.normalize(
                                nombre,
                                Normalizer.Form.NFD
                        )
                        .replaceAll(
                                "\\p{InCombiningDiacriticalMarks}+",
                                ""
                        );

        return sinAcentos
                .toLowerCase()
                .replace(" ", "");
    }

    private Fragment crearFragmentDinamico(
            String nombreCriterio,
            int idGrupo
    ) {

        String nombreClase =
                capitalizar(
                                normalizarNombre(
                                        nombreCriterio
                                )
                        );

        Log.d(
                "FRAGMENT_DEBUG",
                "Intentando cargar: " +
                        "com.example.arse." +
                        nombreClase
        );

        try {

            Class<?> clazz =
                    Class.forName(
                            "com.example.arse." +
                                    nombreClase
                    );

            Fragment fragment =
                    (Fragment)
                            clazz
                                    .getDeclaredConstructor()
                                    .newInstance();

            Bundle args = new Bundle();
            args.putInt("idGrupo", idGrupo);
            args.putInt("idClase", idClase);
            fragment.setArguments(args);

            return fragment;

        } catch (Exception e) {

            Log.e(
                    "FRAGMENT_ERROR",
                    "No se pudo crear: " +
                            nombreClase,
                    e
            );

            return new Fragment();
        }
    }
    private String capitalizar(String texto) {

        if (texto == null || texto.isEmpty())
            return texto;

        return texto.substring(0, 1)
                .toUpperCase()
                + texto.substring(1);
    }
}
