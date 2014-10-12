package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import api.eClassAPI;
import db.DBClaseSource;
import db.DBUsuarioSource;
import models.Usuario;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends Activity {

    final static String TAG = Login.class.getSimpleName();
    Utils util;

    protected DBUsuarioSource usuarioDataSource;
    protected DBClaseSource claseDataSource;
    protected EditText username;
    protected EditText password;
    protected eClassAPI apiService;
    private Usuario userDB;
    protected ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Clase con metodos utiles
        util = new Utils();
        //manipulador de base de datos
        usuarioDataSource = new DBUsuarioSource(Login.this);
        claseDataSource = new DBClaseSource(Login.this);
        userDB = new Usuario();
        //Hacemos la consulta a
        userDB.getUser(usuarioDataSource);
        if(userDB.getUsuarioDB().getId() > 0 && userDB.getUsuarioDB().isLogin()) {
            Intent intent = new Intent(Login.this, Clases.class);
            intent.putExtra("password", userDB.getUsuarioDB().getPassword());
            startActivity(intent);
            finish();
        }

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
            usuarioDataSource.open();
            claseDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        usuarioDataSource.close();
        claseDataSource.close();
    }

    //Callback API donde leemos el JSON.
    protected Callback<JsonElement> mUsuarioSerice = new Callback<JsonElement>() {
        @Override
        public void success(JsonElement element, Response response) {
            JsonObject jsonObj = element.getAsJsonObject();
            String msg = jsonObj.get("usuario").getAsJsonObject().get("status").getAsString();
            //Si la respuesta es correcta.
            if(msg.equals("success")) {
                JsonObject data = jsonObj.get("usuario").getAsJsonObject().getAsJsonObject("data");
                JsonArray clases = data.getAsJsonArray("clases");

                claseDataSource.insertClaseAlumnos(clases);
                Intent intent = new Intent(Login.this, Clases.class);
                Usuario usuario = new Usuario(data.get("id").getAsInt(), password.getText().toString(), true);
                usuarioDataSource.insertUsuario(usuario);
                intent.putExtra("password", password.getText().toString());
                startActivity(intent);
                pd.cancel();
                finish();
            } else {
                if(msg.equals("error")) {
                    pd.cancel();
                    Toast.makeText(Login.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            pd.cancel();
        }
    };

    //Metodo que nos conecta con eClassAPI y le enviamos los parametros por post para conectar
    protected void loadUsuarioData() {

        userDB.getUser(usuarioDataSource);
        Intent intent = new Intent(Login.this, Clases.class);
        if(userDB.getUsuarioDB().getId() == 0) {
            //Aca debemos armar los datos para llamar a la API
            JSONObject datosJson = new JSONObject();
            try {
                datosJson.put("numero_documento", username.getText().toString().substring(0, 8));
                datosJson.put("password", Utils.MD5(password.getText().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] jsonToByte = datosJson.toString().getBytes();
            String datos = Base64.encodeToString(jsonToByte,0);

            apiService = new eClassAPI(datos);
            apiService.getUsuarioData(mUsuarioSerice);

        } else if(password.getText().toString().equals(userDB.getUsuarioDB().getPassword().toString())){
            if(usuarioDataSource.updateUsuario(userDB.getUsuarioDB().getId(), true) > 0) {
                intent.putExtra("password", userDB.getUsuarioDB().getPassword());
                startActivity(intent);
                pd.cancel();
                finish();
            }
        } else {
            pd.cancel();
            Toast.makeText(Login.this, "Usuario no encontrado en DB", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean ingresar(View view) {
        pd = ProgressDialog.show(this, "", "Cargando datos, porfavor espere...", true);
        if(!util.validarRut(username.getText().toString())) {
            username.setError("El rut ingresado no es válido");
            pd.cancel();
            return false;
        }
        if(password.getText().toString().isEmpty()) {
            password.setError("Debe ingresar el password");
            pd.cancel();
            return false;
        }
        //Si va bien llamamos a los datos de la Api
        loadUsuarioData();
        return true;
    }
}
