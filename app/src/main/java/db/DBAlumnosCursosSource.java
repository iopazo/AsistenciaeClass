package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

import models.AlumnoCurso;

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
}
