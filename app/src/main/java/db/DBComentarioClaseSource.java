package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import models.ComentarioClase;

/**
 * Created by iopazog on 23-02-15.
 */
public class DBComentarioClaseSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;

    public DBComentarioClaseSource(Context context) {
        mContext = context;
        dbHelper = new DBHelper(mContext);
    }

    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public boolean insert(ComentarioClase comentarioClase) {

        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_COMENTARIO, comentarioClase.getComentario());
            values.put(DBHelper.COLUMN_FECHA_COMENTARIO, comentarioClase.getFechaComentario());
            values.put(DBHelper.COLUMN_ID_CLASE_COMENTARIO, comentarioClase.getIdClase());
            values.put(DBHelper.COLUMN_NOMBRE_PROFESOR, comentarioClase.getNombreUsuario());
            if(mDatabase.insert(DBHelper.TABLE_COMENTARIO, null, values) > 0) {
                return true;
            }
            return false;
        } catch (NullPointerException ex) {
            Log.d("ComentarioSource", ex.getLocalizedMessage());
            return false;
        }
    }

    public ArrayList<String> list(int idClase) {
        ArrayList<String> listaComentarios = new ArrayList<String>();
        String whereClause = String.format("%s = ?", dbHelper.COLUMN_ID_CLASE_COMENTARIO);

        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_COMENTARIO,
                new String[] {DBHelper.COLUMN_ID_COMENTARIO, DBHelper.COLUMN_COMENTARIO, DBHelper.COLUMN_FECHA_COMENTARIO, DBHelper.COLUMN_NOMBRE_PROFESOR, DBHelper.COLUMN_ID_CLASE_COMENTARIO},
                whereClause,
                new String[] {String.format("%d", idClase)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                listaComentarios.add(String.format("%s %s %s %s %s", cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_COMENTARIO)),
                        "el", cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_FECHA_COMENTARIO)),
                        "por", cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_NOMBRE_PROFESOR))));
                cursor.moveToNext();
            }
        }
        return listaComentarios;
    }
}
