package db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moveapps.asistenciaeclass.R;
import com.moveapps.asistenciaeclass.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import models.Alumno;
import models.Clase;

/**
 * Created by iopazog on 29-09-14.
 */
public class DBClaseSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;
    private DBAlumnoSource dbAlumnoSource;

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

    /*
    variable sync: 0 guarda todas las clases
    sync: 1 solo guarda las clases nuevas y
     */

    public void insertClaseAlumnos(JsonArray clases, int sync, int id_usuario) {

        if(!mDatabase.inTransaction()) {
            mDatabase.beginTransaction();
        }
        ArrayList<Clase> clasesDb = null;
        Integer[] ids = null;
        int contadorSincronizados = 0;
        try {
            if(sync == 1) {
                clasesDb = this.list(4, id_usuario, true);
                ids = new Integer[clasesDb.size()];
                for (int j = 0; j < clasesDb.size(); j++) {
                    ids[j] = clasesDb.get(j).getId();
                }
            }
            for (int i = 0; i < clases.size(); i++) {
                try {
                    JsonObject clase = clases.get(i).getAsJsonObject();
                    if(sync == 0) {
                        ContentValues values = new ContentValues();
                        values.put(dbHelper.COLUMN_ID_CLASE_SEDE, clase.get("id_clase_sede").getAsInt());
                        values.put(dbHelper.COLUMN_NOMBRE_CLASE, clase.get("nombre_completo").getAsString());
                        values.put(dbHelper.COLUMN_FECHA, clase.get("fecha").getAsString());
                        values.put(dbHelper.COLUMN_HORA, clase.get("hora").getAsString());
                        values.put(dbHelper.COLUMN_FK_USUARIO, id_usuario);

                        if (mDatabase.insert(dbHelper.TABLE_CLASE, null, values) > 0) {
                            this.insertAlumnoCurso(clase.getAsJsonArray("alumnos"), clase.get("id_clase_sede").getAsInt());
                        }
                    } else if(sync == 1) {
                        if (!Arrays.asList(ids).contains(clase.get("id_clase_sede").getAsInt())) {
                            contadorSincronizados++;

                            ContentValues values = new ContentValues();
                            values.put(dbHelper.COLUMN_ID_CLASE_SEDE, clase.get("id_clase_sede").getAsInt());
                            values.put(dbHelper.COLUMN_NOMBRE_CLASE, clase.get("nombre_completo").getAsString());
                            values.put(dbHelper.COLUMN_FECHA, clase.get("fecha").getAsString());
                            values.put(dbHelper.COLUMN_HORA, clase.get("hora").getAsString());
                            values.put(dbHelper.COLUMN_FK_USUARIO, id_usuario);

                            if (mDatabase.insert(dbHelper.TABLE_CLASE, null, values) > 0) {
                                this.insertAlumnoCurso(clase.getAsJsonArray("alumnos"), clase.get("id_clase_sede").getAsInt());
                            }
                        }
                    }
                } catch (NullPointerException ex) {
                    Log.d("ClaseSource", ex.getLocalizedMessage());
                }
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
            if(sync == 1) {
                Activity claseActivity = (Activity) mContext;
                if(contadorSincronizados > 0) {
                    Utils.showToast(mContext, String.format("%s %d %s", mContext.getResources().getString(R.string.has_sync), contadorSincronizados, mContext.getResources().getString(R.string.string_class)));
                    claseActivity.recreate();
                } else {
                    Utils.showToast(mContext, mContext.getResources().getString(R.string.dont_class_sync));
                }
            }
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
    3: Eliminada
    4: Todas
    Solo mostramos las clases que esten activas.
     */

    /**
     *
     * @param notIncludeState
     * @param idUsuario
     * @param excludeState true: trae todos los estados diferente al notIncludeState
     *                     false: trae solo las clases iguales al notIncludeState
     * @return
     * @throws NullPointerException
     */
    public ArrayList<Clase> list(int notIncludeState, int idUsuario, boolean excludeState) throws NullPointerException {
        ArrayList<Clase> clases = new ArrayList<Clase>();

        String operatorSqlLite = "!=";
        if(!excludeState) {
            operatorSqlLite = "=";
        }
        String whereClause = String.format("%s %s ? AND %s = ?", dbHelper.COLUMN_ESTADO_CLASE, operatorSqlLite, dbHelper.COLUMN_FK_USUARIO);
        String orderBy = dbHelper.COLUMN_ESTADO_CLASE + " DESC, " + dbHelper.COLUMN_FECHA + " ASC, " + dbHelper.COLUMN_HORA + " ASC";

        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_CLASE,
                new String[] {dbHelper.COLUMN_ID_CLASE_SEDE, dbHelper.COLUMN_NOMBRE_CLASE, dbHelper.COLUMN_ESTADO_CLASE},
                whereClause,
                new String[] {String.format("%d", notIncludeState), String.format("%d", idUsuario)},
                null,
                null,
                orderBy
        );

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                //Aca sacamos el valor
                int id = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID_CLASE_SEDE));
                String nombre = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_NOMBRE_CLASE));
                int estado = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ESTADO_CLASE));
                Clase clase = new Clase(id, nombre, estado);
                clases.add(clase);
                cursor.moveToNext();
            }
        }
        return clases;
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

    /*
    Armamos un json con los datos de los alumnos de una clase
     */
    public JSONObject getAlumnosByClass(int idClase) {

        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        ArrayList<Alumno> alumnos;

        try {
            dbAlumnoSource = new DBAlumnoSource(mContext);
            dbAlumnoSource.open();
            alumnos = dbAlumnoSource.getAlumnoByClass(idClase, "ASC");
            dbAlumnoSource.close();
            if(alumnos.size() > 0) {
                for (int i = 0; i < alumnos.size(); i++) {
                    JSONObject alumnoJObject = new JSONObject();
                    try {
                        alumnoJObject.put("id", alumnos.get(i).getIdAlumnoCursoClaseSede());
                        alumnoJObject.put("estado", String.format("%d", alumnos.get(i).getEstado()));
                        alumnoJObject.put("firma", (alumnos.get(i).getFirma() != null ? alumnos.get(i).getFirma() : ""));
                        jsonArray.put(alumnoJObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            try {
                jsonObject.put("alumnos", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
