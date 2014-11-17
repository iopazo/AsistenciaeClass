package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBAlumnoSource;
import models.Alumno;
import models.AlumnoAdapter;


public class Alumnos extends Activity {

    static String PASSWORD;
    static int ID_CLASE;
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
            ID_CLASE = intent.getIntExtra("id", 0);
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
        alumnos = mAlumnosource.getAlumnoByClass(ID_CLASE);
        onLoadSwipeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mAlumnosource.open();
            //Traemos los alumnos
            alumnos = mAlumnosource.getAlumnoByClass(ID_CLASE);
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
        if (id == R.id.save_class) {

            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Close Class?");
            saveDialog.setMessage("This action can't be undone");
            AlertDialog.Builder builder = saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mAlumnosource.cambiarEstadoClase(ID_CLASE, 1);
                    finish();
                }
            });

            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLoadSwipeListener() {
        alumnoData.removeAll(alumnoData);

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            DisplayMetrics metrics = getResources().getDisplayMetrics();

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
                final Button btnAusente = (Button)findViewById(R.id.botonAusente);
                final Button btnRestablecer = (Button)findViewById(R.id.botonRestablecer);
                float leftOffset = metrics.widthPixels - btnAusente.getWidth() - btnRestablecer.getWidth();
                swipeListView.setOffsetRight(leftOffset); // left side offset
            }
            @Override
            public void onStartClose(int position, boolean right) {
                //Log.d("swipe", String.format("onStartClose %d", position));
            }
            @Override
            public void onClickFrontView(final int position) {
                /*
                Al presionar sobre el nombre del alumno vamos a la vvista de formas solo si Firma esta seteada en 0
                0: Sin accion
                1: Firma
                2: Ausente
                 */
                if(alumnos.get(position).getEstado() == 2) {

                    AlertDialog.Builder cambiarEstadoAlert = new AlertDialog.Builder(Alumnos.this);
                    cambiarEstadoAlert.setTitle("Teacher confirms");
                    cambiarEstadoAlert.setMessage("Sure you want remove the signature?");
                    final EditText passwordConfirm = new EditText(Alumnos.this);
                    passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    cambiarEstadoAlert.setView(passwordConfirm);

                    AlertDialog.Builder builder = cambiarEstadoAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String value = passwordConfirm.getText().toString();
                            if(value.equals(PASSWORD)) {
                                Intent intent = new Intent(Alumnos.this, FirmaAlumno.class);
                                intent.putExtra("nombre", alumnos.get(position).getNombre());
                                intent.putExtra("id", alumnos.get(position).getIdAlumnoCursoClaseSede());
                                intent.putExtra("nombre_clase", NOMBRE_CLASE);
                                intent.putExtra("id_clase", ID_CLASE);
                                startActivityForResult(intent, 1);
                            } else {
                                Toast.makeText(Alumnos.this, "The password is incorrect, try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    cambiarEstadoAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    cambiarEstadoAlert.show();
                }
                if(alumnos.get(position).getEstado() == 0) {
                    Intent intent = new Intent(Alumnos.this, FirmaAlumno.class);
                    intent.putExtra("nombre", alumnos.get(position).getNombre());
                    intent.putExtra("id", alumnos.get(position).getIdAlumnoCursoClaseSede());
                    intent.putExtra("nombre_clase", NOMBRE_CLASE);
                    intent.putExtra("id_clase", ID_CLASE);
                    startActivityForResult(intent, 1);
                }
            }
            @Override
            public void onClickBackView(int position) {
                swipeListView.closeAnimate(position);//when you touch back view it will close
            }
            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }
        });

        swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT); // there are five swiping modes
        swipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipeListView.setAnimationTime(200); // Animation time

        swipeListView.setAdapter(adapter);

        for(int i = 0; i < alumnos.size(); i++) {
            alumnoData.add(new Alumno(alumnos.get(i).getIdAlumnoCursoClaseSede(), alumnos.get(i).getNombre(), alumnos.get(i).getEstado()));
        }
        adapter.notifyDataSetChanged();
    }
}
