package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

import models.Alumno;
import models.AlumnoSinClase;

/**
 * Created by iopazog on 29-09-14.
 */
public class DBAlumnoSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;

    public DBAlumnoSource(Context context) {
        mContext = context;
        dbHelper = new DBHelper(mContext);
    }

    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public ArrayList<Alumno> getAlumnoByClass(int idClase, String orderByType) {
        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        String whereClause = dbHelper.COLUMN_ID_CLASE_SEDE_FK + " = ?";
        String orderby = String.format("%s %s",dbHelper.COLUMN_NOMBRE_ALUMNO, orderByType);

        Cursor cursor = mDatabase.query(
                dbHelper.TABLE_ALUMNO,
                new String[] {dbHelper.COLUMN_ID_ALUMNO_CLASE_SEDE, dbHelper.COLUMN_NOMBRE_ALUMNO,
                        dbHelper.COLUMN_ESTADO, dbHelper.COLUMN_FIRMA,
                        dbHelper.COLUMN_FK_ID_ALUMNO_SC
                },
                whereClause,
                new String[]{String.format("%d", idClase)},
                null,
                null,
                orderby
        );

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                int id = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID_ALUMNO_CLASE_SEDE));
                String nombre = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_NOMBRE_ALUMNO));
                int estado = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ESTADO));
                String firma = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_FIRMA));
                Alumno alumno = new Alumno(id, nombre, estado, idClase);
                alumno.setFirma(firma);

                /*
                    Aca traemos el alumno sin curso si es que el alumno tiene el FK != 0
                 */
                String whereClauseASC = dbHelper.COLUMN_ID_ALUMNO_SC + " = ? AND "
                                        + dbHelper.COLUMN_FK_ID_CLASE_SEDE + " = ?";
                Cursor cursorASC = mDatabase.query(
                        dbHelper.TABLE_ALUMNO_SIN_CLASE,
                        new String[] {dbHelper.COLUMN_ID_ALUMNO_SC, dbHelper.COLUMN_NOMBRE_SC, dbHelper.COLUMN_EMAIL_SC,
                                        dbHelper.COLUMN_NUMERO_DCTO, dbHelper.COLUMN_TIPO_DCTO},
                        whereClauseASC,
                        new String[] {String.format("%d", id), String.format("%d", idClase)},
                        null,
                        null,
                        null
                );

                if (cursorASC.moveToFirst()) {
                    while (!cursorASC.isAfterLast()) {
                        AlumnoSinClase alumnoSinClase = new AlumnoSinClase(idClase,
                                cursorASC.getString(cursorASC.getColumnIndex(dbHelper.COLUMN_NOMBRE_SC)),
                                cursorASC.getString(cursorASC.getColumnIndex(dbHelper.COLUMN_EMAIL_SC)),
                                cursorASC.getString(cursorASC.getColumnIndex(dbHelper.COLUMN_NUMERO_DCTO)),
                                cursorASC.getInt(cursorASC.getColumnIndex(dbHelper.COLUMN_TIPO_DCTO)));
                        alumnoSinClase.setId(cursorASC.getInt(cursorASC.getColumnIndex(dbHelper.COLUMN_ID_ALUMNO_SC)));
                        alumno.setAlumnoSinClase(alumnoSinClase);
                        cursorASC.moveToNext();
                    }
                }
                alumnos.add(alumno);
                cursor.moveToNext();
            }
        }

        return alumnos;
    }

    public void updateAlumno(int idAlumnoCursoClaseSede, String firma, int estado) {

        if(!mDatabase.inTransaction()) {
            mDatabase.beginTransaction();
        }
        try {
            String whereClause = dbHelper.COLUMN_ID_ALUMNO_CLASE_SEDE + " = ?";
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_FIRMA, firma);
            values.put(dbHelper.COLUMN_ESTADO, estado);
            mDatabase.update(
                    dbHelper.TABLE_ALUMNO,
                    values,
                    whereClause,
                    new String[] {String.format("%d", idAlumnoCursoClaseSede)});
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

    }
    /*
    Se cambia el estado a 2, el cual es ausente, previa confirmacion del profesor.
     */
    public void ausente(int idAlumnoCursoClaseSede) {

        if(!mDatabase.inTransaction()) {
            mDatabase.beginTransaction();
        }
        try {
            String whereClause = dbHelper.COLUMN_ID_ALUMNO_CLASE_SEDE + " = ?";
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_ESTADO, 2);
            mDatabase.update(
                    dbHelper.TABLE_ALUMNO,
                    values,
                    whereClause,
                    new String[] {String.format("%d", idAlumnoCursoClaseSede)});
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

    }

    /*
    Se restablece el estado a 0, previa confirmacion del profesor.
     */
    public void restablecer(int idAlumnoCursoClaseSede) {

        if(!mDatabase.inTransaction()) {
            mDatabase.beginTransaction();
        }
        try {
            String whereClause = dbHelper.COLUMN_ID_ALUMNO_CLASE_SEDE + " = ?";
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_ESTADO, 0);
            mDatabase.update(
                    dbHelper.TABLE_ALUMNO,
                    values,
                    whereClause,
                    new String[] {String.format("%d", idAlumnoCursoClaseSede)});
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

    }

    /*
    Se actualiza la clase a cerrada
     */
    public void cambiarEstadoClase(int idClaseSede, int estado) {
        if(!mDatabase.inTransaction()) {
            mDatabase.beginTransaction();
        }
        try {
            String whereClause = dbHelper.COLUMN_ID_CLASE_SEDE + " = ?";
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_ESTADO_CLASE, estado);
            mDatabase.update(
                    dbHelper.TABLE_CLASE,
                    values,
                    whereClause,
                    new String[] {String.format("%d", idClaseSede)});
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }
}
