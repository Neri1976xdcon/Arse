package Clases;

public class HorarioDia {

    private int diaId;

    private Integer horaInicio;
    private Integer minutosInicio;
    private Integer horaFin;
    private Integer minutosFin;

    private String error;

    public HorarioDia(int diaId) {
        this.diaId = diaId;
    }

    public int getDiaId() {
        return diaId;
    }

    public Integer getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Integer horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getMinutosInicio() {
        return minutosInicio;
    }

    public void setMinutosInicio(Integer minutosInicio) {
        this.minutosInicio = minutosInicio;
    }

    public Integer getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Integer horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getMinutosFin() {
        return minutosFin;
    }

    public void setMinutosFin(Integer minutosFin) {
        this.minutosFin = minutosFin;
    }

    public boolean estaVacio() {
        return horaInicio == null
                && minutosInicio == null
                && horaFin == null
                && minutosFin == null;
    }

    public boolean estaCompleto() {
        return horaInicio != null
                && minutosInicio != null
                && horaFin != null
                && minutosFin != null;
    }

    public boolean esValido() {

        if (!estaCompleto())
            return false;

        int inicio =
                horaInicio * 60
                        + minutosInicio;

        int fin =
                horaFin * 60
                        + minutosFin;

        return fin > inicio;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void limpiarError() {
        this.error = null;
    }

    public boolean tieneError() {
        return error != null;
    }

    public String obtenerInicioCompleto() {

        if (horaInicio == null || minutosInicio == null) {
            return null;
        }

        return formatearHora(
                horaInicio,
                minutosInicio
        );
    }

    public String obtenerFinCompleto() {

        if (horaFin == null || minutosFin == null) {
            return null;
        }

        return formatearHora(
                horaFin,
                minutosFin
        );
    }

// =========================
// MÉTODO CENTRALIZADO
// =========================

    private String formatearHora(
            int hora,
            int minutos
    ) {

        return String.format(
                java.util.Locale.US,
                "%02d:%02d",
                hora,
                minutos
        );
    }
}