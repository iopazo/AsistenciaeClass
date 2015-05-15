package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

import models.Alumno;
import models.AlumnoSinClase;

/**
 * Created by iopazog on 14-05-15.
 */
public class DBAlumnoSCSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;

    public DBAlumnoSCSource(Context context) {
        mContext = context;
        dbHelper = new DBHelper(mContext);
    }

    public void open() throws SQLException{
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() { dbHelper.close(); }

    /**
     * Metodo que graba una alumno sin clase sede y le asigna uno.
     * @param alumnoSinClase
     * @param idClaseSede
     * @throws SQLException
     */
    public void add(AlumnoSinClase alumnoSinClase, int idClaseSede) throws SQLException{

        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_NUMERO_DCTO, alumnoSinClase.getNumeroDocumento());
        values.put(dbHelper.COLUMN_NOMBRE_SC, alumnoSinClase.getNombreCompleto());
        values.put(dbHelper.COLUMN_EMAIL_SC, alumnoSinClase.getEmail());
        values.put(dbHelper.COLUMN_TIPO_DCTO, alumnoSinClase.getTipoDocumento());
        values.put(dbHelper.COLUMN_FK_ID_CLASE_SEDE, idClaseSede);

        if(!mDatabase.isOpen()) {
            this.open();
        }
        //Guardamos el id del alumno SC que se transforma en el id del alumno curso clase sede.
        //Para obtener el registro del Alumno SC se debe buscar despues por el id del alumno sc mas el idClaseSede
        int idAlumnoSC = (int) mDatabase.insert(dbHelper.TABLE_ALUMNO_SIN_CLASE, null, values);

        if(idAlumnoSC > 0) {
            Alumno alumno = new Alumno(idAlumnoSC, alumnoSinClase.getNombreCompleto(), 0, idClaseSede);
            this.insertAlumnoCurso(alumno, idClaseSede);
        }

        if(mDatabase.isOpen()) {
            this.close();
        }
    }

    private void insertAlumnoCurso(Alumno alumno, int idClase) {

        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_NOMBRE_ALUMNO, alumno.getNombre());
            values.put(DBHelper.COLUMN_ID_ALUMNO_CLASE_SEDE, alumno.getIdAlumnoCursoClaseSede());
            values.put(DBHelper.COLUMN_ID_CLASE_SEDE_FK, idClase);
        } catch (NullPointerException ex) {
            Log.d("ClaseSource", ex.getMessage());
        }

    }
}
