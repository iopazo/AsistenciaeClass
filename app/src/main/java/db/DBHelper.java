package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iopazog on 22-09-14.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_USUARIO = "USUARIO";
    public static final String TABLE_CLASE = "CLASES";
    public static final String TABLE_ALUMNO = "ALUMNOS";


    private static final String DB_NAME = "eclass.db";
    private static final int DB_VERSION = 1;

    //Taba usuario
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_LOGIN = "LOGIN";
    public static final String COLUMN_USUARIO = "ID_USUARIO";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";

    private static final String DB_CREATE_USUARIO =
            "CREATE TABLE " + TABLE_USUARIO + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_LOGIN + " TINYINT DEFAULT 0, " +
                    "" + COLUMN_USERNAME + " INTEGER(10) DEFAULT 0, " +
                    "" + COLUMN_USUARIO + " INTEGER(9), " +
                    "" + COLUMN_PASSWORD + " VARCHAR(100))";

    //Columnas tabla clases_sedes
    public static final String COLUMN_ID_CLASE = "ID";
    public static final String COLUMN_ID_CLASE_SEDE = "ID_CLASE_SEDE";
    public static final String COLUMN_NOMBRE_CLASE = "NOMBRE";
    public static final String COLUMN_FECHA = "FECHA";
    public static final String COLUMN_HORA = "HORA";
    public static final String COLUMN_ESTADO_CLASE = "ESTADO_CLASE";

    private static final String DB_CREATE_CLASE  =
            "CREATE TABLE " + TABLE_CLASE + " (" + COLUMN_ID_CLASE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_ID_CLASE_SEDE + " INTEGER NOT NULL, " +
                    "" + COLUMN_NOMBRE_CLASE + " VARCHAR(250), " +
                    "" + COLUMN_FECHA + " DATE, " +
                    "" + COLUMN_HORA + " VARCHAR(8), " +
                    "" + COLUMN_ESTADO_CLASE + " INTEGER(3) DEFAULT 0)";

    //Columnas tabla alumnos_cursos_clases_sedes
    public static final String COLUMN_ID_ALUMNO = "ID";
    public static final String COLUMN_ID_ALUMNO_CLASE_SEDE = "ID_ALUMNO_CLASE_SEDE";
    public static final String COLUMN_NOMBRE_ALUMNO = "NOMBRE_ALUMNO";
    public static final String COLUMN_ID_CLASE_SEDE_FK = "ID_CLASE_SEDE_FK";
    public static final String COLUMN_ESTADO = "ESTADO";
    public static final String COLUMN_FIRMA = "FIRMA";

    private static final String DB_CREATE_ALUMNO  =
            "CREATE TABLE " + TABLE_ALUMNO + " (" + COLUMN_ID_ALUMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_ID_ALUMNO_CLASE_SEDE + " INTEGER NOT NULL, " +
                    "" + COLUMN_NOMBRE_ALUMNO + " VARCHAR(250), " +
                    "" + COLUMN_ID_CLASE_SEDE_FK + " INTEGER, " +
                    "" + COLUMN_FIRMA + " TEXT," +
                    "" + COLUMN_ESTADO + " INTEGER(3) DEFAULT 0)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_USUARIO);
        db.execSQL(DB_CREATE_CLASE);
        db.execSQL(DB_CREATE_ALUMNO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
