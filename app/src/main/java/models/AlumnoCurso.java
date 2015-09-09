package models;

/**
 * Created by iopazo on 9/6/15.
 */
public class AlumnoCurso {

    private int _id;
    private String _nombre;
    private int _agregado;
    private int _clase;

    public AlumnoCurso(int id, String nombre, int agregado, int clase) {
        this.set_id(id);
        this.set_nombre(nombre);
        this.set_agregado(agregado);
        this.setClase(clase);
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_nombre() {
        return _nombre;
    }

    public void set_nombre(String _nombre) {
        this._nombre = _nombre;
    }

    public int get_agregado() {
        return _agregado;
    }

    public void set_agregado(int _agregado) {
        this._agregado = _agregado;
    }

    public int getClase() {
        return _clase;
    }

    public void setClase(int clase) {
        this._clase = clase;
    }
}
