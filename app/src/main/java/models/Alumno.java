package models;

/**
 * Created by iopazog on 29-09-14.
 */
public class Alumno {

    private int idAlumnoCursoClaseSede;
    private String nombre;
    private int asistencia;
    private int idClaseSede;
    private boolean sincronizado;

    public Alumno(int _idAlumnoCursoClaseSede, String _nombre) {
        this.setIdAlumnoCursoClaseSede(_idAlumnoCursoClaseSede);
        this.setNombre(_nombre);
    }

    public int getIdAlumnoCursoClaseSede() {
        return idAlumnoCursoClaseSede;
    }

    public void setIdAlumnoCursoClaseSede(int idAlumnoCursoClaseSede) {
        this.idAlumnoCursoClaseSede = idAlumnoCursoClaseSede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(int asistencia) {
        this.asistencia = asistencia;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public int getIdClaseSede() {
        return idClaseSede;
    }

    public void setIdClaseSede(int idClaseSede) {
        this.idClaseSede = idClaseSede;
    }
}
