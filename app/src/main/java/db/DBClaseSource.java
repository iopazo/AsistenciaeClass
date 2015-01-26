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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
        ArrayList<Clase> clasesDb;
        Integer[] ids = null;
        int contadorSincronizados = 0;
        try {
            if(sync == 1) {
                String[] state = new String[]{"0","1","2","3"};
                clasesDb = this.list(state, id_usuario, "-1");
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
                        values.put(DBHelper.COLUMN_ID_CLASE_SEDE, clase.get("id_clase_sede").getAsInt());
                        values.put(DBHelper.COLUMN_NOMBRE_CLASE, clase.get("nombre_completo").getAsString());
                        values.put(DBHelper.COLUMN_FECHA, clase.get("fecha").getAsString());
                        values.put(DBHelper.COLUMN_HORA, clase.get("hora").getAsString());
                        values.put(DBHelper.COLUMN_FK_USUARIO, id_usuario);

                        if (mDatabase.insert(DBHelper.TABLE_CLASE, null, values) > 0) {
                            this.insertAlumnoCurso(clase.getAsJsonArray("alumnos"), clase.get("id_clase_sede").getAsInt());
                        }
                    } else if(sync == 1) {
                        if (ids != null && !Arrays.asList(ids).contains(clase.get("id_clase_sede").getAsInt())) {
                            contadorSincronizados++;

                            ContentValues values = new ContentValues();
                            values.put(DBHelper.COLUMN_ID_CLASE_SEDE, clase.get("id_clase_sede").getAsInt());
                            values.put(DBHelper.COLUMN_NOMBRE_CLASE, clase.get("nombre_completo").getAsString());
                            values.put(DBHelper.COLUMN_FECHA, clase.get("fecha").getAsString());
                            values.put(DBHelper.COLUMN_HORA, clase.get("hora").getAsString());
                            values.put(DBHelper.COLUMN_FK_USUARIO, id_usuario);

                            if (mDatabase.insert(DBHelper.TABLE_CLASE, null, values) > 0) {
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
                values.put(DBHelper.COLUMN_NOMBRE_ALUMNO, alumno.get("nombre").getAsString());
                values.put(DBHelper.COLUMN_ID_ALUMNO_CLASE_SEDE, alumno.get("id_alumno_clase_sede").getAsString());
                values.put(DBHelper.COLUMN_ID_CLASE_SEDE_FK, idClase);
                mDatabase.insert(DBHelper.TABLE_ALUMNO, null, values);
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
     * @param days Rango de dias que debe traer clases para el profesor.
     *                     false: trae solo las clases iguales al notIncludeState
     * @return
     * @throws NullPointerException
     */
    public ArrayList<Clase> list(String[] notIncludeState, int idUsuario, String days) throws NullPointerException {

        ArrayList<Clase> clases = new ArrayList<Clase>();
        String conditions = "";

        if(notIncludeState.length > 0) {
            conditions += " IN(" + Utils.implode(",", notIncludeState) + ")";
        }

        if(!days.equals("-1")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());
            cl.add(Calendar.DATE, Integer.parseInt(days));
            String dayPlusDays = dateFormat.format(cl.getTime());
            conditions += " AND " + DBHelper.COLUMN_FECHA + " <= '" + dayPlusDays + "'";
        }

        String whereClause = String.format("%s %s AND %s = ?", DBHelper.COLUMN_ESTADO_CLASE, conditions, DBHelper.COLUMN_FK_USUARIO);
        String orderBy = DBHelper.COLUMN_ESTADO_CLASE + " DESC, " + DBHelper.COLUMN_FECHA + " ASC, " + DBHelper.COLUMN_HORA + " ASC";

        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_CLASE,
                new String[] {DBHelper.COLUMN_ID_CLASE_SEDE, DBHelper.COLUMN_NOMBRE_CLASE, DBHelper.COLUMN_ESTADO_CLASE, DBHelper.COLUMN_FECHA_SINCRONIZACION, DBHelper.COLUMN_FECHA},
                whereClause,
                new String[] {String.format("%d", idUsuario)},
                null,
                null,
                orderBy
        );

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                //Aca sacamos el valor
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID_CLASE_SEDE));
                String nombre = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOMBRE_CLASE));
                int estado = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ESTADO_CLASE));
                String fecha = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_FECHA_SINCRONIZACION));
                Clase clase = new Clase(id, nombre, estado, fecha);
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
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String today = dateFormat.format(date);

            String whereClause = DBHelper.COLUMN_ID_CLASE_SEDE + " = ?";
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_ESTADO_CLASE, estado);
            values.put(DBHelper.COLUMN_FECHA_SINCRONIZACION, today);
            mDatabase.update(
                    DBHelper.TABLE_CLASE,
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
    public JSONObject getAlumnosByClass(int idClase, int idUsuarioEclass) {

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
                        alumnoJObject.put("id_usuario", idUsuarioEclass);
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
