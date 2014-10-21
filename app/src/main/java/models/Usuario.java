package models;

import android.database.Cursor;

import java.sql.SQLException;

import db.DBUsuarioSource;
import db.DBHelper;

/**
 * Created by iopazog on 22-09-14.
 */
public class Usuario {

    private int id;
    private boolean login;
    private String password;
    private Usuario _usuarioDB;
    private int username;

    public Usuario() {

    }

    public Usuario(int _id) {
        setId(_id);
    }

    public Usuario(int _id, String _password, boolean _login) {
        setId(_id);
        setPassword(_password);
        setLogin(_login);
    }

    public Usuario(int _id, String _password, boolean _login, int _username) {
        setId(_id);
        setPassword(_password);
        setLogin(_login);
        setUsername(_username);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Usuario getUser(DBUsuarioSource mDataSource) {

        set_usuarioDB(new Usuario());
        try {
            mDataSource.open();
            Cursor cursor = mDataSource.selectUsuario();
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()){
                    //Aca sacamos el valor
                    int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_USUARIO));
                    boolean isLogin = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_LOGIN)) > 0;
                    String password = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD));
                    int username = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME));
                    set_usuarioDB(new Usuario(id, password, isLogin, username));
                    cursor.moveToNext();
                }
            }
            mDataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getUsuarioDB();
    }

    public Usuario getUsuarioDB() {
        return _usuarioDB;
    }

    public void set_usuarioDB(Usuario _usuarioDB) {
        this._usuarioDB = _usuarioDB;
    }

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }
}
