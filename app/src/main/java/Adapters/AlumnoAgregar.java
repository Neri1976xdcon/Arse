package Adapters;

import Clases.Alumno;
//agregar
public class AlumnoAgregar extends Alumno {
    boolean agregado;

    public AlumnoAgregar(int id, String nombre){
        super(id, nombre);
        agregado = false;
    }

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }
}
