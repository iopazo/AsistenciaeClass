package models;

/**
 * Created by iopazog on 23-02-15.
 */
public class ComentarioClase {

    private String comentario;
    private int idClase;
    private String fechaComentario;
    private String nombreUsuario;

    public ComentarioClase(int _idClase, String _comentario, String _fechaComentario, String nombreUsuario) {

        this.idClase = _idClase;
        this.comentario = _comentario;
        this.fechaComentario = _fechaComentario;
        this.nombreUsuario = nombreUsuario;
    }


    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
