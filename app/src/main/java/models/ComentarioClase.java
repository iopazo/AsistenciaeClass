package models;

import java.util.Date;

/**
 * Created by iopazog on 23-02-15.
 */
public class ComentarioClase {

    private String comentario;
    private int idClase;
    private Date fechaComentario;
    private int idUsuarioEclass;

    public ComentarioClase(int _idClase, String _comentario, Date _fechaComentario, int _idUsuarioEclass) {

        this.idClase = _idClase;
        this.comentario = _comentario;
        this.fechaComentario = _fechaComentario;
        this.idUsuarioEclass = _idUsuarioEclass;
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

    public Date getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(Date fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public int getIdUsuarioEclass() {
        return idUsuarioEclass;
    }

    public void setIdUsuarioEclass(int idUsuarioEclass) {
        this.idUsuarioEclass = idUsuarioEclass;
    }
}
