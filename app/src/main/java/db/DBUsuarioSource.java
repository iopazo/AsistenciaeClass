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
        if(!mDatabase.inTransaction()) {
            mDatabase.beginTransaction();
        }
        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_USUARIO, usuario.getId());
            values.put(dbHelper.COLUMN_LOGIN, usuario.isLogin());
            values.put(dbHelper.COLUMN_PASSWORD, usuario.getPassword());
            mDatabase.insert(dbHelper.TABLE_USUARIO, null, values);
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    //select
    public Cursor selectUsuario() {
        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_USUARIO,//Tabla
                new String[] {DBHelper.COLUMN_USUARIO, DBHelper.COLUMN_LOGIN, DBHelper.COLUMN_PASSWORD},
                null, //where clause
                null, //where params
                null, //goup by
                null, //having
                null  //orderby
        );

        return cursor;
    }
    //update
    public int updateUsuario(int idUsuario, boolean login) {
        String whereClause = dbHelper.COLUMN_USUARIO + " = ?";
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_LOGIN, login);

        int filaActuazalizada = mDatabase.update(dbHelper.TABLE_USUARIO,
                values,
                whereClause,
                new String[]{String.format("%d", idUsuario)}
        );
        return filaActuazalizada;
    }


}
