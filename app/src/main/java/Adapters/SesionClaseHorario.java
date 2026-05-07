package Adapters;

import Clases.Dia;

public class SesionClaseHorario {

    private Dia dia;

    private Integer horaInicio;
    private Integer minutosInicio;
    private Integer horaFin;
    private Integer minutosFin;

    private boolean expandido = false;
    private boolean tieneError = false;

    public SesionClaseHorario(Dia dia) {
        this.dia = dia;
    }

    public boolean isExpandido() {
        return expandido;
    }

    public void setExpandido(boolean expandido) {
        this.expandido = expandido;
    }

    public boolean tieneError() {
        return tieneError;
    }

    public Dia getDia() {
        return dia;
    }

    public void setTieneError(boolean tieneError) {
        this.tieneError = tieneError;
    }

    public boolean isInicioCompleto() {
        return horaInicio != null && minutosInicio != null;
    }

    public boolean isFinalCompleto() {
        return horaFin != null && minutosFin != null;
    }

    public boolean isHorarioCompleto() {
        return isInicioCompleto() && isFinalCompleto();
    }

    public boolean isInicioValido() {
        return horaInicio != null && horaInicio >= 0 && horaInicio <= 23
                && minutosInicio != null && minutosInicio >= 0 && minutosInicio <= 59;
    }

    public boolean isFinalValido() {
        return horaFin != null && horaFin >= 0 && horaFin <= 23
                && minutosFin != null && minutosFin >= 0 && minutosFin <= 59;
    }

    public boolean isRangoValido() {
        if (!isHorarioCompleto()) return false;

        int inicio = horaInicio * 60 + minutosInicio;
        int fin = horaFin * 60 + minutosFin;

        return inicio < fin;
    }

    public boolean isHorarioValido() {
        return isInicioValido() && isFinalValido() && isRangoValido();
    }
}

