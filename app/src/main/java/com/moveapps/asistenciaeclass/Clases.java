package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import api.eClassAPI;
import db.DBClaseSource;
import db.DBUsuarioSource;
import models.Clase;
import models.ClaseAdapter;
import models.Usuario;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Clases extends Activity {

    static String PASSWORD;
    static int USERNAME;
    protected DBUsuarioSource mUsuarioDatasource;
    protected DBClaseSource mClaseDatasource;
    protected Usuario dbUsuario;
    static ArrayList<Clase> clases = null;
    protected eClassAPI apiService;
    final String[] classState = new String[]{"0, 1"};
    static SharedPreferences prefs;
    private String days;
    SwipeListView swipeListView;
    ClaseAdapter adapter;
    List<Clase> claseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clases);

        long percentAvailableMemorySize = Storage.getTotalMemorySize();
        //Si el porcentaje de espacio en el disco es menor o igual al 10% enviamos un aviso
        if(percentAvailableMemorySize <= Storage.MinimunPercent) {
            Utils.showToast(this, getResources().getString(R.string.memory_size));
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        days = prefs.getString("sync_frequency", "-1");

        Intent intent = getIntent();

        if(intent.hasExtra("username")) {
            USERNAME = intent.getIntExtra("username", 0);
        }
        if (intent.hasExtra("password")) {
            PASSWORD = intent.getStringExtra("password");
        }

        mUsuarioDatasource = new DBUsuarioSource(Clases.this);
        mClaseDatasource = new DBClaseSource(Clases.this);

        try {
            mUsuarioDatasource.open();
            mClaseDatasource.open();

            Usuario usuario = new Usuario();
            usuario.getUser(mUsuarioDatasource, 0);
            dbUsuario = usuario.getUsuarioDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        swipeListView = (SwipeListView) findViewById(R.id.clases_swipe_list);
        claseData = new ArrayList<Clase>();
        adapter = new ClaseAdapter(this, R.layout.custom_clases_swipe_row, claseData, mClaseDatasource, dbUsuario);

        //Traemos las clases
        clases = mClaseDatasource.list(classState, dbUsuario.getId(), days);
        onLoadSwipeListener();
    }

    public void onStart() {
        super.onStart();
        //Log.d(TAG, "In the onStart() event");
    }

    public void onRestart() {
        super.onRestart();
        try {
            days = prefs.getString("sync_frequency", "-1");
            mClaseDatasource.open();
            clases = mClaseDatasource.list(classState, dbUsuario.getId(), days);
            onLoadSwipeListener();
            //Log.d("CLASES", "In the onRestart() event");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            days = prefs.getString("sync_frequency", "-1");
            mClaseDatasource.open();
            mUsuarioDatasource.open();
            clases = mClaseDatasource.list(classState, dbUsuario.getId(), days);
            onLoadSwipeListener();
            //Log.d("CLASES", "In the onResume() event");

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

            if (mUsuarioDatasource.updateUsuario(dbUsuario.getId(), false, true) > 0) {
                Intent intent = new Intent(Clases.this, Login.class);
                startActivity(intent);
                finish();
                return true;
            }

        }

        if (id == R.id.configuracion) {
            Intent intent = new Intent(Clases.this, ConfiguracionActivity.class);
            startActivityForResult(intent, 1);
        }

        //Accion boton sincronizar
        if (id == R.id.sincronizar) {
            JSONObject datosJson = new JSONObject();
            try {
                datosJson.put("numero_documento", USERNAME);
                datosJson.put("password", Utils.MD5(PASSWORD));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] jsonToByte = datosJson.toString().getBytes();
            String datos = Base64.encodeToString(jsonToByte, 0);

            apiService = new eClassAPI(datos);
            apiService.getUsuarioData(mUsuarioSerice);
        }

        if(id == R.id.clases_cerradas) {
            Intent intent = new Intent(Clases.this, ClasesCerradas.class);
            intent.putExtra("password", dbUsuario.getPassword());
            intent.putExtra("username", dbUsuario.getUsername());
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    //Metodo llamado para sincronizar clases.
    protected Callback<JsonElement> mUsuarioSerice = new Callback<JsonElement>() {
        @Override
        public void success(JsonElement element, Response response) {
            JsonObject jsonObj = element.getAsJsonObject();
            String msg = jsonObj.get("usuario").getAsJsonObject().get("status").getAsString();
            //Si la respuesta es correcta.
            if(msg.equals("success")) {
                JsonObject data = jsonObj.get("usuario").getAsJsonObject().getAsJsonObject("data");
                JsonArray clasesJson = data.getAsJsonArray("clases");
                JsonArray clasesCanceladas = data.getAsJsonArray("clases_canceladas");
                mClaseDatasource.insertClaseAlumnos(clasesJson, clasesCanceladas, 1, data.get("id").getAsInt());
                clases = mClaseDatasource.list(classState, dbUsuario.getId(), days);
                onLoadSwipeListener();
                //adapter.notifyDataSetChanged();
            } else if(msg.equals("error")) {
                Utils.showToast(Clases.this, getResources().getString(R.string.action_undone));
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Utils.showToast(Clases.this, getResources().getString(R.string.no_connection));
        }
    };

    private void onLoadSwipeListener() {

        claseData.removeAll(claseData);

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
                if(right) {
                    final Button btnSincronizar = (Button)findViewById(R.id.btnSincronizarClase);
                    float rightOffset = metrics.widthPixels - btnSincronizar.getWidth();
                    swipeListView.setOffsetRight(rightOffset); // right side offset
                }
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
                    intent.putExtra("username", dbUsuario.getNombreProfesor());
                    startActivityForResult(intent, 1);
                    swipeListView.closeOpenedItems();
                }
            }

            @Override
            public void onClickBackView(int position) {
                swipeListView.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            /*
            Al deslizara de derecha a izquierda borramos la clase, dejandola en estado 3.
             */
            public void onDismiss(int[] reverseSortedPositions) {
            }

        });

        swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT); // there are five swiping modes
        //swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        swipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);

        swipeListView.setAnimationTime(200); // Animation time
        swipeListView.setSwipeOpenOnLongPress(false);

        swipeListView.setAdapter(adapter);

        for (int i = 0; i < clases.size(); i++) {
            claseData.add(new Clase(clases.get(i).getId(), clases.get(i).getNombre(), clases.get(i).getEstado(), clases.get(i).getFechaSincronizacion()));
        }
        adapter.notifyDataSetChanged();

    }
}