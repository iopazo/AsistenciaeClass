package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

import db.DBAlumnosCursosSource;
import models.AlumnoCurso;
import models.AlumnoCursoAdapter;

public class AlumnosCursos extends Activity implements View.OnClickListener {

    private ListView listViewAlumnos;
    static ArrayList<AlumnoCurso> alumnos = null;
    private ArrayList<AlumnoCurso> alumnoCursoData = null;
    protected DBAlumnosCursosSource mAlumnoCursoSource;
    protected int ID_CLASE;
    AlumnoCursoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos_cursos);

        Intent intent = getIntent();
        if(intent.hasExtra("id_clase_sede")) {
            ID_CLASE = intent.getIntExtra("id_clase_sede", 0);
        }
        mAlumnoCursoSource = new DBAlumnosCursosSource(AlumnosCursos.this);
        try {
            mAlumnoCursoSource.open();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        alumnoCursoData = mAlumnoCursoSource.getAlumnosByClaseSede(ID_CLASE);

        listViewAlumnos = (ListView)findViewById(R.id.alumnosCursosListView);
        adapter = new AlumnoCursoAdapter(this, R.layout.custom_alumno_curso_row, alumnoCursoData, mAlumnoCursoSource);
        listViewAlumnos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alumnos_cursos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_alumnos) {
            boolean error = false;
            for (int i = 0; i < adapter.alumnosSeleccionados.size(); i++) {
                try {
                    if(!mAlumnoCursoSource.add(adapter.alumnosSeleccionados.get(i), ID_CLASE)) {
                        error = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(!error) {
                Utils.showToast(this, getResources().getString(R.string.student_created));
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
}
