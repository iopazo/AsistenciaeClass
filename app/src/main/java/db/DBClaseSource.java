package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.ArrayList;

import models.Clase;

/**
 * Created by iopazog on 29-09-14.
 */
public class DBClaseSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;

    public DBClaseSource(Context context) {
        mContext = context;
        dbHelper = new DBHelper(mContext);
    }

    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public void insertClaseAlumnos(JsonArray clases) {

        mDatabase.beginTransaction();

        try {
            for (int i = 0; i < clases.size(); i++) {
                try {
                    JsonObject clase = clases.get(i).getAsJsonObject();
                    ContentValues values = new ContentValues();
                    values.put(dbHelper.COLUMN_ID_CLASE_SEDE, clase.get("id_clase_sede").getAsInt());
                    values.put(dbHelper.COLUMN_NOMBRE_CLASE, clase.get("nombre_completo").getAsString());
                    values.put(dbHelper.COLUMN_FECHA, clase.get("fecha").getAsString());
                    values.put(dbHelper.COLUMN_HORA, clase.get("hora").getAsString());

                    if(mDatabase.insert(dbHelper.TABLE_CLASE, null, values) > 0) {
                        this.insertAlumnoCurso(clase.getAsJsonArray("alumnos"), clase.get("id_clase_sede").getAsInt());
                    }
                } catch (NullPointerException ex) {
                    Log.d("ClaseSource", ex.getLocalizedMessage());
                }
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

    }

    private void insertAlumnoCurso(JsonArray alumnos, int idClase) {

        for (int i = 0; i < alumnos.size(); i++) {
            try {
                JsonObject alumno = alumnos.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(dbHelper.COLUMN_NOMBRE_ALUMNO, alumno.get("nombre").getAsString());
                values.put(dbHelper.COLUMN_ID_ALUMNO_CLASE_SEDE, alumno.get("id_alumno_clase_sede").getAsString());
                values.put(dbHelper.COLUMN_ID_CLASE_SEDE_FK, idClase);
                mDatabase.insert(dbHelper.TABLE_ALUMNO, null, values);
            } catch (NullPointerException ex) {
                Log.d("ClaseSource", ex.getLocalizedMessage());
            }
        }
    }
    /*
    Estados de clase:
    0: Activa
    1: Cerrada
    2: Sincronizada
    Solo mostramos las clases que esten activas.
     */
    public ArrayList<Clase> list() throws NullPointerException {
        ArrayList<Clase> clases = new ArrayList<Clase>();
        String whereClause = dbHelper.COLUMN_ESTADO_CLASE + " = ?";

        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_CLASE,
                new String[] {dbHelper.COLUMN_ID_CLASE_SEDE, dbHelper.COLUMN_NOMBRE_CLASE},
                whereClause,
                new String[] {String.format("%d", 0)},
                null,
                null,
                null
        );

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                //Aca sacamos el valor
                int id = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID_CLASE_SEDE));
                String nombre = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_NOMBRE_CLASE));
                Clase clase = new Clase(id, nombre);
                clases.add(clase);
                cursor.moveToNext();
            }
        }
        return clases;
    }
}
