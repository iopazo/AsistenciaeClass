package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import db.DBAlumnoSource;
import db.DBComentarioClaseSource;
import models.Alumno;
import models.AlumnoAdapter;
import models.ComentarioClase;


public class Alumnos extends Activity implements SearchView.OnQueryTextListener, View.OnClickListener {

    static String PASSWORD;
    static int ID_CLASE;
    static String NOMBRE_CLASE;
    static String NOMBRE_USUARIO;
    protected DBAlumnoSource mAlumnosource;
    static ArrayList<Alumno> alumnos = null;
    SwipeListView swipeListView;
    AlumnoAdapter adapter;
    ArrayList<Alumno> alumnoData;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private String orderByType = "ASC";
    private PopupWindow popWindow;
    private ListView listViewComentarios;
    private ArrayList<String> commentsList;
    private ArrayAdapter<String> commentAdapter;


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
        if(intent.hasExtra("username")) {
            NOMBRE_USUARIO = intent.getStringExtra("username");
        }
        if(intent.hasExtra("title")) {
            NOMBRE_CLASE = String.format("Class %s", intent.getStringExtra("title"));
            TextView breadcrumb = (TextView)findViewById(R.id.bcrumbText);
            breadcrumb.setText(NOMBRE_CLASE);
        }

        Button btnNewStudent = (Button) findViewById(R.id.newStudent);
        btnNewStudent.setOnClickListener(this);

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
        alumnos = mAlumnosource.getAlumnoByClass(ID_CLASE, orderByType);
        onLoadSwipeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mAlumnosource.open();
            //Traemos los alumnos
            alumnos = mAlumnosource.getAlumnoByClass(ID_CLASE, orderByType);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alumnos, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

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
            saveDialog.setTitle(getResources().getString(R.string.close_class));
            saveDialog.setMessage(getResources().getString(R.string.action_undone));
            saveDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(mAlumnosource.cambiarEstadoClase(ID_CLASE, 1)) {
                        finish();
                    } else {
                        dialog.cancel();
                    }
                }
            });

            saveDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }

        if(id == R.id.sort) {
            if(orderByType.equals("ASC")) {
                orderByType = "DESC";
            } else {
                orderByType = "ASC";
            }
            alumnos = mAlumnosource.getAlumnoByClass(ID_CLASE, orderByType);
            onLoadSwipeListener();
            Utils.showToast(this, String.format("%s %s", getResources().getString(R.string.order_text), orderByType));
        }

        if (id == R.id.comentarios) {
            onShowPopup(getWindow().getDecorView().findViewById(android.R.id.content));
        }

        if(id == R.id.listStudents){
            Intent intent = new Intent(Alumnos.this, AlumnosCursos.class);
            intent.putExtra("id_clase_sede", ID_CLASE);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onShowPopup(View v) {

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);

        listViewComentarios = (ListView)inflatedView.findViewById(R.id.commentsListView);

        Button btnComentario = (Button)inflatedView.findViewById(R.id.btnComentario);
        final EditText textComentario = (EditText)inflatedView.findViewById(R.id.writeComment);

        btnComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String now = format.format(Calendar.getInstance().getTime());

                if(!textComentario.getText().toString().equals("")) {
                    ComentarioClase comentario = new ComentarioClase(ID_CLASE, textComentario.getText().toString(), now, NOMBRE_USUARIO);
                    DBComentarioClaseSource comentarioDb = new DBComentarioClaseSource(Alumnos.this);
                    try {
                        comentarioDb.open();
                        if(comentarioDb.insert(comentario)) {
                            addItems(v, comentario);
                            textComentario.setText("");
                        } else {
                            Utils.showToast(Alumnos.this, getResources().getString(R.string.error_save_comment));
                        }
                        comentarioDb.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.showToast(Alumnos.this, getResources().getString(R.string.empty_comment));
                }

            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        setSimpleList(listViewComentarios);

        popWindow = new PopupWindow(inflatedView, size.x - 50, size.y - 400, true);
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setAnimationStyle(R.style.AnimationPopup);
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 100);

    }
    //Cargamos los comentarios a la lista
    public void setSimpleList(ListView listView){

        commentsList = new ArrayList<String>(); //Aca debemos traer los comentarios de la clase.
        DBComentarioClaseSource comentarioDb = new DBComentarioClaseSource(Alumnos.this);
        try {
            comentarioDb.open();
            commentsList = comentarioDb.list(ID_CLASE);
            comentarioDb.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        commentAdapter = new ArrayAdapter<String>(Alumnos.this, R.layout.comments_list_item, android.R.id.text1,commentsList);
        listView.setAdapter(commentAdapter);
    }
    //Agregamos un comentario a la lista
    private void addItems(View v, ComentarioClase comentario) {
        commentsList.add(String.format("'%s' %s %s %s %s", comentario.getComentario(), "el", comentario.getFechaComentario(), "por", comentario.getNombreUsuario()));
        commentAdapter.notifyDataSetChanged();
    }

    /**
     * Funcion que carga el listado de alumnos
     */
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
                    cambiarEstadoAlert.setTitle(getResources().getString(R.string.teacher_confirm));
                    cambiarEstadoAlert.setMessage(getResources().getString(R.string.remove_signature));
                    final EditText passwordConfirm = new EditText(Alumnos.this);
                    passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    cambiarEstadoAlert.setView(passwordConfirm);

                    cambiarEstadoAlert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String value = passwordConfirm.getText().toString();
                            if(value.equals(PASSWORD)) {
                                //Escondemos la barra de busqueda y unseteamos el texto
                                if (searchView.isShown()) {
                                    searchMenuItem.collapseActionView();
                                    searchView.setQuery("", false);
                                }

                                Intent intent = new Intent(Alumnos.this, FirmaAlumno.class);
                                intent.putExtra("nombre", alumnos.get(position).getNombre());
                                intent.putExtra("id", alumnos.get(position).getIdAlumnoCursoClaseSede());
                                intent.putExtra("nombre_clase", NOMBRE_CLASE);
                                intent.putExtra("id_clase", ID_CLASE);
                                startActivityForResult(intent, 1);
                            } else {
                                Utils.showToast(Alumnos.this, getResources().getString(R.string.password_incorrect));
                            }
                        }
                    });

                    cambiarEstadoAlert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    cambiarEstadoAlert.show();
                }
                if(alumnos.get(position).getEstado() == 0) {

                    //Escondemos la barra de busqueda y unseteamos el texto
                    if (searchView.isShown()) {
                        searchMenuItem.collapseActionView();
                        searchView.setQuery("", false);
                    }

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

    /**
     * Funcion que busca alumnos en el listado al apretar el boton
     * @param query
     * @return boolean
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Funcion que busca alumnos en el listado al escribir
     * @param newText
     * @return boolean
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }

    @Override
    /**
     * Funcion para capturar cualquier evento click en la actividad.
     */
    public void onClick(View view) {
        if(view.getId() == R.id.newStudent) {
            Intent intent = new Intent(Alumnos.this, NuevoAlumno.class);
            intent.putExtra("id_clase", ID_CLASE);
            startActivityForResult(intent, 1);
        }
    }
}
