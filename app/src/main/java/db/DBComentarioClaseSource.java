package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    //Retorna un Listado de String simple para armar un listview.
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

    //Retorna un objeto Json en base a una lsita de String.
    public JSONObject jsonList(int idClase, int idUsuario) {

        ArrayList<String> listado = this.list(idClase);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        if(listado.size() > 0) {
            for (int i = 0; i < listado.size(); i++) {
                String textoComentario = listado.get(i);
                JSONObject jsonComentarioClase = new JSONObject();
                try {
                    jsonComentarioClase.put("comentario", textoComentario);
                    jsonComentarioClase.put("id_clase_sede", idClase);
                    jsonComentarioClase.put("id_usuario", idUsuario);
                    jsonArray.put(jsonComentarioClase);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                jsonObject.put("comentarios", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }
}
