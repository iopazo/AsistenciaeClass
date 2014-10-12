package models;

/**
 * Created by iopazog on 29-09-14.
 */
public class Alumno {

    private int idAlumnoCursoClaseSede;
    private String nombre;
    private int asistencia;
    private int idClaseSede;
    private int estado;
    private String firma;

    public Alumno(int _idAlumnoCursoClaseSede, String _nombre) {
        this.setIdAlumnoCursoClaseSede(_idAlumnoCursoClaseSede);
        this.setNombre(_nombre);
    }

    public Alumno(int _idAlumnoCursoClaseSede, String _nombre, int _estado) {
        this.setIdAlumnoCursoClaseSede(_idAlumnoCursoClaseSede);
        this.setNombre(_nombre);
        this.setEstado(_estado);
    }

    public Alumno(int _idAlumnoCursoClaseSede, String _nombre, int _estado, int _idClaseSede) {
        this.setIdAlumnoCursoClaseSede(_idAlumnoCursoClaseSede);
        this.setNombre(_nombre);
        this.setEstado(_estado);
        this.setIdClaseSede(_idClaseSede);
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

    public int getIdClaseSede() {
        return idClaseSede;
    }

    public void setIdClaseSede(int idClaseSede) {
        this.idClaseSede = idClaseSede;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }
}
