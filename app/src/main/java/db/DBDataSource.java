package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moveapps.asistenciaeclass.Usuario;

import java.sql.SQLException;

/**
 * Created by iopazog on 22-09-14.
 */
public class DBDataSource {

    private SQLiteDatabase mDatabase;
    private DBHelper dbHelper;
    private Context mContext;

    public DBDataSource(Context context) {
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
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_ID, usuario.getId());
            values.put(dbHelper.COLUMN_LOGIN, usuario.isLogin());
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
                new String[] {DBHelper.COLUMN_ID},
                null, //where
                null, //params
                null, //goup by
                null, //having
                null  //orderby
        );

        return cursor;
    }

    //update

}
