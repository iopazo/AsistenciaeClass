package com.moveapps.asistenciaeclass;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DBClaseSource;
import models.Alumno;
import models.AlumnoAdapter;
import models.Interface;


public class Alumnos extends ListActivity implements Interface {

    static String ID_CLASE;
    static String NOMBRE_CLASE;
    static final String TAG = Alumnos.class.getSimpleName();
    protected DBClaseSource mClasesource;
    static ArrayList<Alumno> alumnos = null;

    SwipeListView swipeListView;
    AlumnoAdapter adapter;
    List<Alumno> alumnoData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos);

        swipeListView = (SwipeListView)findViewById(R.id.example_swipe_lv_list);
        alumnoData = new ArrayList<Alumno>();
        adapter = new AlumnoAdapter(this, R.layout.custom_swipe_row, alumnoData);


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

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }
            @Override
            public void onClosed(int position, boolean fromRight) {
            }
            @Override
            public void onListChanged() {
            }
            @Override
            public void onMove(int position, float x) {
            }
            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }
            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }
            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
                Intent intent = new Intent(Alumnos.this, FirmaAlumno.class);
                intent.putExtra("nombre", alumnos.get(position).getNombre());
                intent.putExtra("id", alumnos.get(position).getIdAlumnoCursoClaseSede());
                intent.putExtra("nombre_clase", NOMBRE_CLASE);
                startActivityForResult(intent, 1);
                //swipeListView.openAnimate(position); //when you touch front view it will open
            }
            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
                swipeListView.closeAnimate(position);//when you touch back view it will close
            }
            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }
        });

        swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions
        swipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipeListView.setOffsetLeft(Utils.convertDpToPixel(0f, getResources())); // left side offset
        swipeListView.setOffsetRight(Utils.convertDpToPixel(80f, getResources())); // right side offset
        swipeListView.setAnimationTime(500); // Animation time
        swipeListView.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

        swipeListView.setAdapter(adapter);

        for(int i = 0; i < alumnos.size(); i++) {

            alumnoData.add(new Alumno(alumnos.get(i).getIdAlumnoCursoClaseSede(), alumnos.get(i).getNombre()));
        }
        adapter.notifyDataSetChanged();
        /*
        if(alumnos != null) {
            //Variable enviada para armar los item de la lista
            ArrayList<Map<String, String>> values = this.crearListado(alumnos);
            String[] from = {"nombre", "id"};
            int[] to = {R.id.label};

            SimpleAdapter adapter = new SimpleAdapter(this, values, R.layout.item_layout, from, to);
            setListAdapter(adapter);
        }*/
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
