package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    public boolean add(AlumnoSinClase alumnoSinClase, int idClaseSede) throws SQLException{

        //Si ya existe el alumno sin clase, devolvemos false
        if(this.exist(alumnoSinClase, idClaseSede)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NUMERO_DCTO, alumnoSinClase.getNumeroDocumento());
        values.put(DBHelper.COLUMN_NOMBRE_SC, alumnoSinClase.getNombreCompleto());
        values.put(DBHelper.COLUMN_EMAIL_SC, alumnoSinClase.getEmail());
        values.put(DBHelper.COLUMN_TIPO_DCTO, alumnoSinClase.getTipoDocumento());
        values.put(DBHelper.COLUMN_FK_ID_CLASE_SEDE, idClaseSede);

        //Guardamos el id del alumno SC que se transforma en el id del alumno curso clase sede.
        //Para obtener el registro del Alumno SC se debe buscar despues por el id del alumno sc mas el idClaseSede
        int idAlumnoSC = (int) mDatabase.insert(DBHelper.TABLE_ALUMNO_SIN_CLASE, null, values);

        if(idAlumnoSC > 0) {
            alumnoSinClase.setId(idAlumnoSC);
            Alumno alumno = new Alumno(idAlumnoSC, alumnoSinClase.getNombreCompleto(), 0, idClaseSede);
            alumno.setAlumnoSinClase(alumnoSinClase);
            this.insertAlumnoCurso(alumno, idClaseSede);
        }

        return true;
    }

    /**
     * Guardams un alumno curso en base a un alumno sin clase.
     * @param alumno
     * @param idClase
     */
    private void insertAlumnoCurso(Alumno alumno, int idClase) {

        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_NOMBRE_ALUMNO, alumno.getNombre());
            values.put(DBHelper.COLUMN_ID_ALUMNO_CLASE_SEDE, alumno.getIdAlumnoCursoClaseSede());
            values.put(DBHelper.COLUMN_ID_CLASE_SEDE_FK, idClase);
            values.put(DBHelper.COLUMN_FK_ID_ALUMNO_SC, alumno.getAlumnoSinClase().getId());
            mDatabase.insert(DBHelper.TABLE_ALUMNO, null, values);
        } catch (NullPointerException ex) {
            Log.d("AlumnoCurso", ex.getMessage());
        }
    }

    private boolean exist(AlumnoSinClase alumnoSC, int idClase) {
        boolean exists = false;

        String whereClause = DBHelper.COLUMN_NUMERO_DCTO + " = ? AND " + DBHelper.COLUMN_TIPO_DCTO +
                " = ? AND " + DBHelper.COLUMN_FK_ID_CLASE_SEDE + " = ? OR " + DBHelper.COLUMN_EMAIL_SC + " = ?";
        String[] whereParams = {alumnoSC.getNumeroDocumento(),String.format("%d", alumnoSC.getTipoDocumento()),
                                String.format("%d", idClase), alumnoSC.getEmail()};

        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_ALUMNO_SIN_CLASE,
                new String[] {DBHelper.COLUMN_ID_ALUMNO_SC},
                whereClause,
                whereParams,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()) {
            exists = true;
        }

        return exists;
    }
}
