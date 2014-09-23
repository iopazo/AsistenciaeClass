package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iopazog on 22-09-14.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_USUARIO = "USUARIO";


    private static final String DB_NAME = "eclass.db";
    private static final int DB_VERSION = 1;
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_LOGIN = "LOGIN";
    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_USUARIO + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_LOGIN + " TINYINT DEFAULT 0)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
