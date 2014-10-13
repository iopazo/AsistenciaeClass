package com.moveapps.asistenciaeclass;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBAlumnoSource;
import models.Alumno;
import models.AlumnoAdapter;


public class Alumnos extends ListActivity {

    static String PASSWORD;
    static String ID_CLASE;
    static String NOMBRE_CLASE;
    static final String TAG = Alumnos.class.getSimpleName();
    protected DBAlumnoSource mAlumnosource;
    static ArrayList<Alumno> alumnos = null;

    SwipeListView swipeListView;
    AlumnoAdapter adapter;
    List<Alumno> alumnoData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos);

        //Obtenemos los parametros enviados desde la vista Clases
        Intent intent = getIntent();
        if(intent.hasExtra("id")) {
            ID_CLASE = intent.getStringExtra("id");
        }
        if(intent.hasExtra("password")) {
            PASSWORD = intent.getStringExtra("password");
        }
        if(intent.hasExtra("title")) {
            NOMBRE_CLASE = String.format("Class %s", intent.getStringExtra("title"));
            TextView breadcrumb = (TextView)findViewById(R.id.bcrumbText);
            breadcrumb.setText(NOMBRE_CLASE);
        }

        //Iniciamos la instancia y abrimos la base de datos
        mAlumnosource = new DBAlumnoSource(Alumnos.this);
        try {
            mAlumnosource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        swipeListView = (SwipeListView)findViewById(R.id.example_swipe_lv_list);
        alumnoData = new ArrayList<Alumno>();
        adapter = new AlumnoAdapter(this, R.layout.custom_alumno_swipe_row, alumnoData, PASSWORD, mAlumnosource);

        //Traemos los alumnos
        alumnos = mAlumnosource.getAlumnoByClass(Integer.parseInt(ID_CLASE));
        onLoadSwipeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mAlumnosource.open();
            //Traemos los alumnos
            alumnos = mAlumnosource.getAlumnoByClass(Integer.parseInt(ID_CLASE));
            onLoadSwipeListener();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAlumnosource.close();
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

    private void onLoadSwipeListener() {
        alumnoData.removeAll(alumnoData);
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
                /*
                Al presionar sobre el nombre del alumno vamos a la vvista de formas solo si Firma esta seteada en 0
                0: Sin accion
                1: Firma
                2: Ausente
                 */
                if(alumnos.get(position).getEstado() != 1) {
                    Intent intent = new Intent(Alumnos.this, FirmaAlumno.class);
                    intent.putExtra("nombre", alumnos.get(position).getNombre());
                    intent.putExtra("id", alumnos.get(position).getIdAlumnoCursoClaseSede());
                    intent.putExtra("nombre_clase", NOMBRE_CLASE);
                    intent.putExtra("id_clase", ID_CLASE);
                    startActivityForResult(intent, 1);
                }
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

        swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT); // there are five swiping modes
        //swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions
        swipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipeListView.setOffsetLeft(Utils.convertDpToPixel(0f, getResources())); // left side offset
        swipeListView.setOffsetRight(Utils.convertDpToPixel(50f, getResources())); // right side offset
        swipeListView.setAnimationTime(400); // Animation time
        //swipeListView.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

        swipeListView.setAdapter(adapter);

        for(int i = 0; i < alumnos.size(); i++) {
            alumnoData.add(new Alumno(alumnos.get(i).getIdAlumnoCursoClaseSede(), alumnos.get(i).getNombre(), alumnos.get(i).getEstado()));
        }
        adapter.notifyDataSetChanged();
    }
}
