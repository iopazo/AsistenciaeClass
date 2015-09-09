package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import models.Alumno;
import models.AlumnoCurso;
import models.AlumnoSinClase;

/**
 * Created by iopazo on 9/8/15.
 */
public class DBAlumnosCursosSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;


    public DBAlumnosCursosSource(Context context) {
        mContext = context;
        dbHelper = new DBHelper(mContext);
    }

    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    /**
     * Metodo que trae el listado de alumnos cursos por clase sede.
     * @param idClaseSede
     * @return
     */
    public ArrayList<AlumnoCurso> getAlumnosByClaseSede(int idClaseSede) {
        ArrayList<AlumnoCurso> arrayAlumnos = new ArrayList<AlumnoCurso>();

        Cursor cursor = mDatabase.query(
                dbHelper.TABLE_ALUMNO_CURSO,
                new String[]{dbHelper.COLUMN_NOMBRE_AC, dbHelper.COLUMN_ID_ALUMNO_CURSO, dbHelper.COLUMN_AGREGADO},
                dbHelper.COLUMN_FK_ID_CLASE_SEDE_AC + " = " + String.format("%d", idClaseSede),
                null,
                null,
                null,
                dbHelper.COLUMN_NOMBRE_AC + " ASC"
                );

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String nombre = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_NOMBRE_AC));
                int id_alumno_curso = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID_ALUMNO_CURSO));
                int agregado = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_AGREGADO));
                AlumnoCurso alumnoCurso = new AlumnoCurso(id_alumno_curso, nombre, agregado, idClaseSede);
                arrayAlumnos.add(alumnoCurso);
                cursor.moveToNext();
            }
        }

        return arrayAlumnos;
    }

    /**
     * Metodo que graba una alumno sin clase sede y le asigna uno.
     * @param alumnoCurso
     * @param idClaseSede
     * @throws SQLException
     */
    public boolean add(AlumnoCurso alumnoCurso, int idClaseSede) throws SQLException{

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NUMERO_DCTO, alumnoCurso.get_id()); //En el numero documento guardamos el ID Alumno Curso
        values.put(DBHelper.COLUMN_NOMBRE_SC, alumnoCurso.get_nombre());
        values.put(DBHelper.COLUMN_EMAIL_SC, "");
        values.put(DBHelper.COLUMN_TIPO_DCTO, 99); //Usamos el 99 para diferencia dentro de la API
        values.put(DBHelper.COLUMN_FK_ID_CLASE_SEDE, idClaseSede);

        //Guardamos el id del alumno SC que se transforma en el id del alumno curso clase sede.
        //Para obtener el registro del Alumno SC se debe buscar despues por el id del alumno sc mas el idClaseSede
        int idAlumnoSC = (int) mDatabase.insert(DBHelper.TABLE_ALUMNO_SIN_CLASE, null, values);
        AlumnoSinClase alumnoSinClase = new AlumnoSinClase(idClaseSede, alumnoCurso.get_nombre(), "",
                String.format("%d", alumnoCurso.get_id()), 99);
        values = null;
        if(idAlumnoSC > 0) {
            //Actualizamos el alumno a agregado, para que no puedan volver agregarlo, shan!
            ContentValues valuesUpdate = new ContentValues();
            valuesUpdate.put(DBHelper.COLUMN_AGREGADO, 1);
            mDatabase.update(DBHelper.TABLE_ALUMNO_CURSO, valuesUpdate,
                    DBHelper.COLUMN_ID_ALUMNO_CURSO + " = ?", new String[]{String.format("%d", alumnoCurso.get_id())});

            Alumno alumno = new Alumno(idAlumnoSC, alumnoCurso.get_nombre(), 0, idClaseSede);
            alumno.setAlumnoSinClase(alumnoSinClase);
            this.insertAlumnoCursoClaseSede(alumno, idClaseSede);

            return true;
        }

        return false;
    }

    /**
     * Guardams un alumno curso clase sede en base a un alumno sin clase.
     * @param alumno
     * @param idClase
     */
    private void insertAlumnoCursoClaseSede(Alumno alumno, int idClase) {

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
}
