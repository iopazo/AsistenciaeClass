package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import models.Usuario;

/**
 * Created by iopazog on 22-09-14.
 */
public class DBUsuarioSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;

    public DBUsuarioSource(Context context) {
        mContext = context;
        dbHelper = new DBHelper(mContext);
    }

    // abrir la base de datos
    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    // cerrarla la base de datos
    public void close() {
        mDatabase.close();
    }

    //insert
    public void insertUsuario(Usuario usuario) {
        if(mDatabase.isOpen() &&  !mDatabase.inTransaction()) {
            mDatabase.beginTransaction();
        }
        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_USUARIO, usuario.getId());
            values.put(dbHelper.COLUMN_LOGIN, usuario.isLogin());
            values.put(dbHelper.COLUMN_PASSWORD, usuario.getPassword());
            values.put(dbHelper.COLUMN_USERNAME, usuario.getUsername());
            values.put(dbHelper.COLUMN_NOMBRE_USUARIO, usuario.getNombreProfesor());
            mDatabase.insert(dbHelper.TABLE_USUARIO, null, values);
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    //select
    public Cursor selectUsuario(int username) {

        String whereClause;
        String[] whereParams;

        //Si nos envian el parametro username solo buscamos ese usuario
        if(username > 0) {
           whereClause = String.format("%s", dbHelper.COLUMN_USERNAME + " = ?");
           whereParams = new String[]{String.format("%d", username)};
        }
        //Si no nos envian el parametro username, buscamos el primero que este logueado
        else {
            whereClause = dbHelper.COLUMN_ULTIMO_USUARIO + " = ?";
            whereParams = new String[]{String.format("%d", 1)};
        }

        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_USUARIO,//Tabla
                new String[] {DBHelper.COLUMN_USUARIO, DBHelper.COLUMN_LOGIN, DBHelper.COLUMN_PASSWORD, DBHelper.COLUMN_USERNAME, DBHelper.COLUMN_NOMBRE_USUARIO},
                whereClause, //where clause
                whereParams, //where params
                null, //goup by
                null, //having
                null  //orderby
        );

        return cursor;
    }
    //update
    public int updateUsuario(int idUsuario, boolean login, boolean ultimoUsuario) {
        String whereClause = dbHelper.COLUMN_USUARIO + " = ?";
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_LOGIN, login);
        values.put(dbHelper.COLUMN_ULTIMO_USUARIO, ultimoUsuario);

        int filaActuazalizada = mDatabase.update(dbHelper.TABLE_USUARIO,
                values,
                whereClause,
                new String[]{String.format("%d", idUsuario)}
        );
        return filaActuazalizada;
    }
}
