package models;

/**
 * Created by iopazog on 10-05-15.
 */
public class AlumnoSinClase {

    private String id;
    private int idClaseSede;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String numeroDocumento;
    private int tipoDocumento;
    private Enum estadoAsistencia;

    /*
    Constructor por defecto
     */
    public AlumnoSinClase() {

    }

    public AlumnoSinClase(String _id, int _idClaseSede, String _nombre, String _apellidoPaterno,
                          String _apellidoMaterno, String _email, String _numeroDocumento,
                          String _tipoDocumento, Enum _estadoAsistencia)
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdClaseSede() {
        return idClaseSede;
    }

    public void setIdClaseSede(int idClaseSede) {
        this.idClaseSede = idClaseSede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
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

    public Enum getEstadoAsistencia() {
        return estadoAsistencia;
    }

    public void setEstadoAsistencia(Enum estadoAsistencia) {
        estadoAsistencia = estadoAsistencia;
    }
}
