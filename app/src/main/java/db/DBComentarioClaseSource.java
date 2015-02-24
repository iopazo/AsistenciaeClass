package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
}
