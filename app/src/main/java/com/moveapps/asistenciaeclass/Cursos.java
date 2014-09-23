package com.moveapps.asistenciaeclass;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import db.DBDataSource;
import db.DBHelper;


public class Cursos extends ListActivity {

    protected DBDataSource mDataSource;
    protected Utils util;
    protected int ID_USUARIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        //Iniciamos el datasource
        mDataSource = new DBDataSource(Cursos.this);
        //Clase con metodos utiles
        util = new Utils();



        //Esta variable la debo llenar con el json, se debe cambiar cuando se conecte con la API
        JSONObject row = new JSONObject();
        JSONObject row2 = new JSONObject();
        JSONArray rowArray = new JSONArray();
        JSONObject data = new JSONObject();
        //Variable enviada para armar los item de la lista
        ArrayList<Map<String, String>> values = null;
        try {
            //Objeto mapeado
            row.put("id", "2");
            row.put("nombre", "Ingles 1 - Campus UAI");

            row2.put("id", "3");
            row2.put("nombre", "Ingles 2 - Campus UDD");

            //Objeto agregado a arreglo Json
            rowArray.put(row);
            rowArray.put(row2);

            //Cargamos el objeto json final con nombre "json" con el objeto array Json
            data.put("json", rowArray);

            //Pasamos el json object al metodo crearListado y nos retorna un arrayList
            values = util.crearListado(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] from = {"nombre", "id"};
        int[] to = {R.id.label};

        SimpleAdapter adapter = new SimpleAdapter(this, values, R.layout.item_layout, from, to);
        setListAdapter(adapter);
    }

    //Evento al clickear un item de la lista
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        HashMap<String, String> item = (HashMap<String,String>) getListAdapter().getItem(position);
        String nombre = item.get("nombre");
        String idCurso = item.get("id");
        Intent intent = new Intent(this, Clases.class);
        intent.putExtra("title", nombre);
        intent.putExtra("ID_CURSO", idCurso);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDataSource.open();

            Cursor cursor = mDataSource.selectUsuario();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                //Aca sacamos el valor
                ID_USUARIO = cursor.getColumnIndex(DBHelper.COLUMN_ID);
                Log.d("TAG", String.format("El id del usuario es: %d", ID_USUARIO));
                cursor.moveToNext();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cursos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
