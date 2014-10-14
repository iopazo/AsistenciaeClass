package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBClaseSource;
import db.DBUsuarioSource;
import models.Clase;
import models.ClaseAdapter;
import models.Usuario;


public class Clases extends Activity {

    static String PASSWORD;
    static final String TAG = Clases.class.getSimpleName();
    protected DBUsuarioSource mUsuarioDatasource;
    protected DBClaseSource mClaseDatasource;
    protected Usuario dbUsuario;
    static ArrayList<Clase> clases = null;

    SwipeListView swipeListView;
    ClaseAdapter adapter;
    List<Clase> claseData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clases);

        Intent intent = getIntent();

        if (intent.hasExtra("password")) {
            PASSWORD = intent.getStringExtra("password");
        }

        mUsuarioDatasource = new DBUsuarioSource(Clases.this);
        mClaseDatasource = new DBClaseSource(Clases.this);

        try {
            mUsuarioDatasource.open();
            mClaseDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        swipeListView = (SwipeListView) findViewById(R.id.clases_swipe_list);
        claseData = new ArrayList<Clase>();
        adapter = new ClaseAdapter(this, R.layout.custom_clases_swipe_row, claseData, mClaseDatasource);

        //Traemos los alumnos
        clases = mClaseDatasource.list();
        onLoadSwipeListener();
    }

    public void onStart() {
        super.onStart();
        //Log.d(TAG, "In the onStart() event");
    }

    public void onRestart() {
        super.onRestart();
        try {
            mClaseDatasource.open();
            clases = mClaseDatasource.list();
            onLoadSwipeListener();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "In the onRestart() event");
    }

    public void onStop() {
        super.onStop();
        mClaseDatasource.close();
        mUsuarioDatasource.close();
        //Log.d(TAG, "In the onStop() event");
    }

    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "In the onDestroy() event");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mClaseDatasource.open();
            mUsuarioDatasource.open();
            clases = mClaseDatasource.list();
            onLoadSwipeListener();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        //Log.d(TAG, "In the onPause() event");
        super.onPause();
        mUsuarioDatasource.close();
        mClaseDatasource.close();
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

            if (mUsuarioDatasource.updateUsuario(dbUsuario.getId(), false) > 0) {
                Intent intent = new Intent(Clases.this, Login.class);
                startActivity(intent);
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void onLoadSwipeListener() {

        claseData.removeAll(claseData);

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            int lastKnownPosition = -1;
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                //Log.d("swipe", String.format("onClosedddd %d", position));
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                //Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                //Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                /*
                Al presionar sobre el nombre del alumno vamos a la vvista de formas solo si Firma esta seteada en 0
                0: Sin accion
                1: Firma
                2: Ausente
                 */
                if (clases.get(position).getEstado() == 0) {
                    Intent intent = new Intent(Clases.this, Alumnos.class);
                    intent.putExtra("title", clases.get(position).getNombre());
                    intent.putExtra("id", clases.get(position).getId());
                    intent.putExtra("password", PASSWORD);
                    startActivityForResult(intent, 1);
                }
                //swipeListView.openAnimate(position); //when you touch front view it will open
            }

            @Override
            public void onClickBackView(int position) {
                //Log.d("swipe", String.format("onClickBackView %d", position));
                swipeListView.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            /*
            Al deslizara de derecha a izquierda borramos la clase, dejandola en estado 3.
             */
            public void onDismiss(int[] reverseSortedPositions) {

                AlertDialog.Builder saveDialog = new AlertDialog.Builder(swipeListView.getContext());
                saveDialog.setTitle("Dismiss Class");
                saveDialog.setMessage("This action can't be undone, are you sure?");

                for (final int position : reverseSortedPositions) {
                    AlertDialog.Builder builder = saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mClaseDatasource.cambiarEstadoClase(claseData.get(position).getId(), 3);
                            claseData.remove(position);
                            adapter.notifyDataSetChanged();

                        }
                    });
                }
                //mClaseDatasource.close();
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();
            }
        });

        swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions
        swipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipeListView.setOffsetLeft(Utils.convertDpToPixel(0f, getResources())); // left side offset
        swipeListView.setOffsetRight(Utils.convertDpToPixel(50f, getResources())); // right side offset
        swipeListView.setAnimationTime(400); // Animation time
        //swipeListView.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

        swipeListView.setAdapter(adapter);

        for (int i = 0; i < clases.size(); i++) {
            claseData.add(new Clase(clases.get(i).getId(), clases.get(i).getNombre(), clases.get(i).getEstado()));
        }
        adapter.notifyDataSetChanged();

    }
}