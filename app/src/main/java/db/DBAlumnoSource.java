package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

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

    public void updateAlumno(int idAlumnoCursoClaseSede, String firma, int estado) {

        mDatabase.beginTransaction();
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
}
