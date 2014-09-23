package com.moveapps.asistenciaeclass;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Clases extends ListActivity {

    static String ID_CURSO;
    Utils util;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clases);

        //Obtengo el dato enviado desde la clase curso.
        Bundle mensajeCursos = getIntent().getExtras();
        if(mensajeCursos == null) {
            return;
        }
        ID_CURSO = mensajeCursos.getString("ID_CURSO");
        String title = mensajeCursos.getString("title");

        if(title != null) {
            //getActionBar().setTitle(String.format("Curso %s", title));
            TextView breadCrumb = (TextView) findViewById(R.id.bcrumbText);
            breadCrumb.setText(String.format("Curso %s", title));
        }

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
            row.put("nombre", "Seccion 1 - Clase Jueves 23 Sept, 23:00 Carmencita 25");

            row2.put("id", "3");
            row2.put("nombre", "Seccion 2 - Clase Jueves 23 Sept, 23:00 Carmencita 25");

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        HashMap<String, String> item = (HashMap<String,String>) getListAdapter().getItem(position);
        String nombre = item.get("nombre");
        String idClase = item.get("id");
        Intent intent = new Intent(this, Alumnos.class);
        intent.putExtra("title", nombre);
        intent.putExtra("ID_CLASE", idClase);
        startActivityForResult(intent, 1);
    }

    // En este metodo recojemos los datos devueltos desde la Actividad Clases
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getExtras().containsKey("ID_CLASE")){
            //Aca es cuando vuelve de la vista de alumnos...
        }
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
