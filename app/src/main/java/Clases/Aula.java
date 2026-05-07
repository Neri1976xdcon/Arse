package Clases;

public class Aula {
    private int id;
    private String aula;

    public Aula(int id, String aula) {
        this.id = id;
        this.aula = aula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }
}
