package models;

/**
 * Created by iopazog on 10-05-15.
 */
public enum EstadoAsistencia {
    Ausente(-1),
    Present(1),
    Nada(0);

    private final int estado;

    EstadoAsistencia(int estado) {
        this.estado = estado;
    }

    public int getValue() {

        return estado;
    }

}
