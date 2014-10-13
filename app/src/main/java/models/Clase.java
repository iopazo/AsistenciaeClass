package models;

/**
 * Created by iopazog on 29-09-14.
 */
public class Clase {

    private int id;
    private String nombre;
    private int estado;

    public Clase() {

    }
    public Clase(int _id, String _nombre) {
        setId(_id);
        setNombre(_nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
