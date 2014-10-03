package com.moveapps.asistenciaeclass;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import db.DBClaseSource;
import db.DBUsuarioSource;
import models.Clase;
import models.Interface;
import models.Usuario;


public class Clases extends ListActivity implements Interface {

    static String ID_CURSO;
    static final String TAG = Clases.class.getSimpleName();
    protected DBUsuarioSource mUsuarioDatasource;
    protected DBClaseSource mClaseDatasource;
    protected Usuario dbUsuario;
    static ArrayList<Clase> clases = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clases);
        Log.d(TAG, "In the onCreate() event");

        mUsuarioDatasource = new DBUsuarioSource(Clases.this);
        mClaseDatasource = new DBClaseSource(Clases.this);

        try {
            mUsuarioDatasource.open();
            mClaseDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        clases = mClaseDatasource.list();
        if(clases != null) {
            //Variable enviada para armar los item de la lista
            ArrayList<Map<String, String>> values = this.crearListado(clases);
            String[] from = {"nombre", "id"};
            int[] to = {R.id.label};

            SimpleAdapter adapter = new SimpleAdapter(this, values, R.layout.item_layout, from, to);
            setListAdapter(adapter);
        }

    }

    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "In the onStart() event");
    }
    public void onRestart()
    {
        super.onRestart();
        Log.d(TAG, "In the onRestart() event");
    }
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "In the onStop() event");
    }
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "In the onDestroy() event");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mClaseDatasource.open();
            mUsuarioDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "In the onPause() event");
        super.onPause();
        mUsuarioDatasource.close();
        mClaseDatasource.close();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        HashMap<String, String> item = (HashMap<String,String>) getListAdapter().getItem(position);
        String nombre = item.get("nombre");
        String idClase = item.get("id");
        Intent intent = new Intent(this, Alumnos.class);
        intent.putExtra("title", nombre);
        intent.putExtra("id", idClase);
        startActivityForResult(intent, 1);
    }

    // En este metodo recojemos los datos devueltos desde la Actividad Clases
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        //if(data.getExtras().containsKey("ID_CLASE")){
            //Aca es cuando vuelve de la vista de alumnos...
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.clases, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_salir) {
            Usuario usuario = new Usuario();
            usuario.getUser(mUsuarioDatasource);
            dbUsuario = usuario.getUsuarioDB();

            if(mUsuarioDatasource.updateUsuario(dbUsuario.getId(), false) > 0) {
                Intent intent = new Intent(Clases.this, Login.class);
                startActivity(intent);
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ArrayList<Map<String, String>> crearListado(ArrayList arrayList) {
        ArrayList<Map<String, String>> listadoCursos = new ArrayList<Map<String, String>>();

        for (int i = 0; i < arrayList.size(); i++) {
            Clase clase = (Clase)arrayList.get(i);
            listadoCursos.add(Utils.putData(Integer.toString(clase.getId()), clase.getNombre()));
        }
        return listadoCursos;
    }
}
