package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import api.eClassAPI;
import db.DBDataSource;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Login extends Activity {

    final static String TAG = Login.class.getSimpleName();
    Utils util;

    protected DBDataSource mDataSource;
    protected EditText username;
    protected EditText password;
    protected eClassAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Clase con metodos utiles
        util = new Utils();
        //manipulador de base de datos
        mDataSource = new DBDataSource(Login.this);


        //Seteamos el background color al layout
        RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.relativeLayout);
        relativeLayout.setBackgroundColor(Color.parseColor("#f1f1f1"));

        //Seteamos las fuentes en bold
        Typeface latoBold = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bol.ttf");
        username = (EditText) this.findViewById(R.id.username);
        password = (EditText) this.findViewById(R.id.password);
        username.setTypeface(latoBold);
        password.setTypeface(latoBold);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    username.setText(util.formatear(username.getText().toString()));
            }
        });


        //Setamos las fuentes pero Regulares
        Typeface latoRegular = Typeface.createFromAsset(getAssets(), "fonts/Lato-Reg.ttf");
        Button botonIngresar = (Button) this.findViewById(R.id.buttonIngresar);
        botonIngresar.setTypeface(latoRegular);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    //Callback API donde leemos el JSON.
    protected Callback<JsonElement> mUsuarioSerice = new Callback<JsonElement>() {
        @Override
        public void success(JsonElement element, Response response) {
            JsonObject jsonObj = element.getAsJsonObject();


            //Usuario usuario = new Usuario(31);
            //usuario.setLogin(true);
            //mDataSource.insertUsuario(usuario);
            //Log.d(TAG, String.format("Json Object %d", jsonObj));
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    //llamamos esto para tener los datos que nos retornara el metodo de api para usuarios
    protected void loadUsuarioData() {
        //Aca debemos armar los datos
        JSONObject datosJson = new JSONObject();
        try {
            datosJson.put("numero_documento", username.getText().toString().substring(0, 8));
            datosJson.put("password", Utils.MD5(password.getText().toString()));//"2807430c9f0d818fe4d8a018f4f56ff5");//);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] jsonToByte = datosJson.toString().getBytes();
        String datos = Base64.encodeToString(jsonToByte,0);

        apiService = new eClassAPI(datos);
        apiService.getUsuarioData(mUsuarioSerice);
    }

    public void ingresar(View view) {

        if(!util.validarRut(username.getText().toString())) {
            username.setError("El rut ingresado no es válido");
        } else {
            loadUsuarioData();
            //Traemos la info de la API, la cargamos al objeto usuario y lo guardamos en la base de datos.
            //Usuario usuario = new Usuario(31);
            //usuario.setLogin(true);
            //mDataSource.insertUsuario(usuario);
            //Intent intent = new Intent(this, Cursos.class);
            //startActivity(intent);
            //finish();
        }

    }
}
