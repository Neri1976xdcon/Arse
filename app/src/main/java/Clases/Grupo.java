package Clases;

public class Grupo {
    private int id;
    private String grupo;
    private Aula aula;

    public Grupo(int id, String grupo, Aula aula) {
        this.id = id;
        this.grupo = grupo;
        this.aula = aula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }
}
