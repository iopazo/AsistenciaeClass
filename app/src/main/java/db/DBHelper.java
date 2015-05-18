package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by iopazog on 22-09-14.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_USUARIO = "USUARIO";
    public static final String TABLE_CLASE = "CLASES";
    public static final String TABLE_ALUMNO = "ALUMNOS";
    public static final String TABLE_COMENTARIO = "COMENTARIOS";
    public static final String TABLE_ALUMNO_SIN_CLASE = "ALUMNO_SIN_CLASE";


    private static final String DB_NAME = "eclass.db";
    private static final int DB_VERSION = 2;

    //Taba usuario
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_LOGIN = "LOGIN";
    public static final String COLUMN_USUARIO = "ID_USUARIO";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_NOMBRE_USUARIO = "NOMBRE_USUARIO";
    public static final String COLUMN_ULTIMO_USUARIO = "ULTIMO_USUARIO";

    private static final String DB_CREATE_USUARIO =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USUARIO + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_LOGIN + " TINYINT DEFAULT 0, " +
                    "" + COLUMN_USERNAME + " INTEGER(10) DEFAULT 0, " +
                    "" + COLUMN_USUARIO + " INTEGER(9), " +
                    "" + COLUMN_PASSWORD + " VARCHAR(100), " +
                    "" + COLUMN_NOMBRE_USUARIO + " VARCHAR(100), " +
                    "" + COLUMN_ULTIMO_USUARIO + " TINYINT DEFAULT 1)";

    //Columnas tabla clases_sedes
    public static final String COLUMN_ID_CLASE = "ID";
    public static final String COLUMN_FK_USUARIO = "FK_ID_USUARIO";
    public static final String COLUMN_ID_CLASE_SEDE = "ID_CLASE_SEDE";
    public static final String COLUMN_NOMBRE_CLASE = "NOMBRE";
    public static final String COLUMN_FECHA = "FECHA";
    public static final String COLUMN_HORA = "HORA";
    public static final String COLUMN_ESTADO_CLASE = "ESTADO_CLASE";
    public static final String COLUMN_FECHA_SINCRONIZACION = "FECHA_SINCRONIZACION";

    private static final String DB_CREATE_CLASE  =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CLASE + " (" + COLUMN_ID_CLASE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_ID_CLASE_SEDE + " INTEGER NOT NULL, " +
                    "" + COLUMN_NOMBRE_CLASE + " VARCHAR(250), " +
                    "" + COLUMN_FECHA + " DATE, " +
                    "" + COLUMN_HORA + " VARCHAR(8), " +
                    "" + COLUMN_ESTADO_CLASE + " INTEGER(3) DEFAULT 0," +
                    "" + COLUMN_FECHA_SINCRONIZACION + " DATETIME, " +
                    "" + COLUMN_FK_USUARIO + " INTEGER(9))";

    //Columnas tabla alumnos_cursos_clases_sedes
    public static final String COLUMN_ID_ALUMNO = "ID";
    public static final String COLUMN_ID_ALUMNO_CLASE_SEDE = "ID_ALUMNO_CLASE_SEDE";
    public static final String COLUMN_NOMBRE_ALUMNO = "NOMBRE_ALUMNO";
    public static final String COLUMN_ID_CLASE_SEDE_FK = "ID_CLASE_SEDE_FK";
    public static final String COLUMN_ESTADO = "ESTADO";
    public static final String COLUMN_FIRMA = "FIRMA";
    public static final String COLUMN_FK_ID_ALUMNO_SC = "FK_ID_ALUMNO_SC";

    private static final String DB_CREATE_ALUMNO  =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ALUMNO + " (" + COLUMN_ID_ALUMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_ID_ALUMNO_CLASE_SEDE + " INTEGER NOT NULL, " +
                    "" + COLUMN_NOMBRE_ALUMNO + " VARCHAR(250), " +
                    "" + COLUMN_ID_CLASE_SEDE_FK + " INTEGER, " +
                    "" + COLUMN_FIRMA + " TEXT," +
                    "" + COLUMN_FK_ID_ALUMNO_SC + " INTEGER(9), " +
                    "" + COLUMN_ESTADO + " INTEGER(3) DEFAULT 0)";

    //Columnas tabla comentarios_clases
    public static final String COLUMN_ID_COMENTARIO = "ID";
    public static final String COLUMN_ID_CLASE_COMENTARIO = "ID_CLASE";
    public static final String COLUMN_NOMBRE_PROFESOR = "NOMBRE_PROFESOR";
    public static final String COLUMN_COMENTARIO = "COMENTARIO";
    public static final String COLUMN_FECHA_COMENTARIO = "FECHA_CREACION";

    private static final String DB_CREATE_COMENTARIO =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COMENTARIO + " (" + COLUMN_ID_COMENTARIO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_ID_CLASE_COMENTARIO + " INTEGER NOT NULL, " +
                    "" + COLUMN_COMENTARIO + " TEXT, " +
                    "" + COLUMN_NOMBRE_PROFESOR + " VARCHAR(250) NOT NULL, " +
                    "" + COLUMN_FECHA_COMENTARIO + " VARCHAR(20) NOT NULL)";


    //Columnas tabla alumno sin clase
    public static final String COLUMN_ID_ALUMNO_SC = "ID";
    public static final String COLUMN_FK_ID_CLASE_SEDE = "FK_ID_CLASE_SEDE";
    public static final String COLUMN_NOMBRE_SC = "NOMBRE";
    public static final String COLUMN_EMAIL_SC = "EMAIL";
    public static final String COLUMN_NUMERO_DCTO = "NUMERO_DCTO";
    public static final String COLUMN_TIPO_DCTO = "TIPO_DCTO";

    private static final String DB_CREATE_ALUMNO_SC  =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ALUMNO_SIN_CLASE + " (" + COLUMN_ID_ALUMNO_SC + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "" + COLUMN_FK_ID_CLASE_SEDE + " INTEGER NOT NULL, " +
                    "" + COLUMN_NOMBRE_SC + " VARCHAR(250), " +
                    "" + COLUMN_EMAIL_SC + " VARCHAR(250), " +
                    "" + COLUMN_NUMERO_DCTO + " VARCHAR(250), " +
                    "" + COLUMN_TIPO_DCTO + " VARCHAR(250))";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_USUARIO);
        db.execSQL(DB_CREATE_CLASE);
        db.execSQL(DB_CREATE_ALUMNO);
        db.execSQL(DB_CREATE_COMENTARIO);
        db.execSQL(DB_CREATE_ALUMNO_SC);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Para version 2
        db.execSQL(DB_CREATE_ALUMNO_SC);
        try {
            db.execSQL("ALTER TABLE " + TABLE_ALUMNO + " ADD COLUMN " + COLUMN_FK_ID_ALUMNO_SC + " INTEGER(9);");
        } catch (SQLiteException ex) {
            Log.i("Column already exists", "La columna de ya existe");
        }
    }
}