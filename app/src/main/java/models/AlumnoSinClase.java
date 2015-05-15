package models;

/**
 * Created by iopazog on 10-05-15.
 */
public class AlumnoSinClase {

    private int id;
    private int idClaseSede;
    private String nombreCompleto;
    private String email;
    private String numeroDocumento;
    private int tipoDocumento;

    /*
    Constructor por defecto
     */
    public AlumnoSinClase() {

    }

    public AlumnoSinClase(int _idClaseSede, String _nombre, String _email, String _numeroDocumento,
                          int _tipoDocumento)
    {
        this.idClaseSede = _idClaseSede;
        this.nombreCompleto = _nombre;
        this.email = _email;
        this.numeroDocumento = _numeroDocumento;
        this.tipoDocumento = _tipoDocumento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdClaseSede() {
        return idClaseSede;
    }

    public void setIdClaseSede(int idClaseSede) {
        this.idClaseSede = idClaseSede;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

}
