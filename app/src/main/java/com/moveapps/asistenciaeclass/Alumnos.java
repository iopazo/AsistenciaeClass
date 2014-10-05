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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import db.DBClaseSource;
import models.Alumno;
import models.Interface;


public class Alumnos extends ListActivity implements Interface {

    static String ID_CLASE;
    static String NOMBRE_CLASE;
    static final String TAG = Alumnos.class.getSimpleName();
    protected DBClaseSource mClasesource;
    static ArrayList<Alumno> alumnos = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos);

        //Iniciamos la instancia y abrimos la base de datos
        mClasesource = new DBClaseSource(Alumnos.this);
        try {
            mClasesource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Obtenemos los parametros enviados desde la vista Clases
        Intent intent = getIntent();
        if(intent.hasExtra("id")) {
            ID_CLASE = intent.getStringExtra("id");
        }
        if(intent.hasExtra("title")) {
            NOMBRE_CLASE = String.format("Class %s", intent.getStringExtra("title"));
            TextView breadcrumb = (TextView)findViewById(R.id.bcrumbText);
            breadcrumb.setText(NOMBRE_CLASE);
        }

        //Traemos los alumnos
        alumnos = mClasesource.getAlumnoByClass(Integer.parseInt(ID_CLASE));
        if(alumnos != null) {
            //Variable enviada para armar los item de la lista
            ArrayList<Map<String, String>> values = this.crearListado(alumnos);
            String[] from = {"nombre", "id"};
            int[] to = {R.id.label};

            SimpleAdapter adapter = new SimpleAdapter(this, values, R.layout.item_layout, from, to);
            setListAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mClasesource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mClasesource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alumnos, menu);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        HashMap<String, String> item = (HashMap<String,String>) getListAdapter().getItem(position);
        String nombre = item.get("nombre");
        String idAlumoClaseSede = item.get("id");
        Intent intent = new Intent(this, FirmaAlumno.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("id", idAlumoClaseSede);
        intent.putExtra("nombre_clase", NOMBRE_CLASE);
        startActivityForResult(intent, 1);
    }

    @Override
    public ArrayList<Map<String, String>> crearListado(ArrayList arrayList) {
        ArrayList<Map<String, String>> listadoCursos = new ArrayList<Map<String, String>>();

        for (int i = 0; i < arrayList.size(); i++) {
            Alumno alumno = (Alumno)arrayList.get(i);
            listadoCursos.add(Utils.putData(Integer.toString(alumno.getIdAlumnoCursoClaseSede()), alumno.getNombre()));
        }
        return listadoCursos;
    }
}
