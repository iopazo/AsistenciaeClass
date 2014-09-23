package com.moveapps.asistenciaeclass;

/**
 * Created by iopazog on 22-09-14.
 */
public class Usuario {

    private int id;
    private boolean login;

    public Usuario() {

    }

    public Usuario(int _id) {
        setId(_id);
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
}
