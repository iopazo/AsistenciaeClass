package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
    protected DBUsuarioSource usuarioDataSource;
    protected DBClaseSource claseDataSource;
    protected EditText username;
    protected EditText password;
    protected Spinner documentoIdentificacion;
    protected TextView nombreProfesor;
    protected TextView otraCuenta;
    protected eClassAPI apiService;
    private Usuario userDB;
    protected ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        //Abrimos la conexion a Usuarios y Clases
        usuarioDataSource = new DBUsuarioSource(Login.this);
        claseDataSource = new DBClaseSource(Login.this);

        //Instanciamos Usuario como global
        userDB = new Usuario();
        //Traemos el usuario que tenga la marca de ultimo ingreso
        userDB.getUser(usuarioDataSource, 0);

        //Iniciamos select y campo username para aplicar reglas.
        documentoIdentificacion = (Spinner) this.findViewById(R.id.spinner);
        username = (EditText) this.findViewById(R.id.username);
        nombreProfesor = (TextView) this.findViewById(R.id.nombreProfesor);
        otraCuenta = (TextView) this.findViewById(R.id.otraCuenta);

        //Si encontramos un usuario, validamos que este tenga la marca de login, de lo contrario mostramos su nombre y que ingrese su password.
        if(userDB.getUsuarioDB().getId() > 0 && userDB.getUsuarioDB().isLogin()) {
            Intent intent = new Intent(Login.this, Clases.class);
            intent.putExtra("password", userDB.getUsuarioDB().getPassword());
            intent.putExtra("username", userDB.getUsuarioDB().getUsername());
            userDB = null;
            startActivity(intent);
            finish();
        }
        else if(userDB.getUsuarioDB().getId() > 0 && !userDB.getUsuarioDB().isLogin()) {
            documentoIdentificacion.setVisibility(View.INVISIBLE);
            username.setVisibility(View.INVISIBLE);
            nombreProfesor.setText(String.format("%s, %s", getResources().getString(R.string.welcome), userDB.getUsuarioDB().getNombreProfesor()));
            nombreProfesor.setVisibility(View.VISIBLE);
            otraCuenta.setVisibility(View.VISIBLE);
            username.setText(String.format("%d", userDB.getUsuarioDB().getUsername()));
            //Dejamos por defecto DNI
            documentoIdentificacion.setSelection(1);
        }
        //Cae aca cuando no existe ninguna opcion
        else {
            userDB.set_usuarioDB(null);
        }

        //Seteamos el background color al layout
        RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.relativeLayout);
        relativeLayout.setBackgroundColor(Color.parseColor("#f1f1f1"));

        //Seteamos las fuentes en bold
        Typeface latoBold = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bol.ttf");
        password = (EditText) this.findViewById(R.id.password);
        username.setTypeface(latoBold);
        password.setTypeface(latoBold);

        //Le asignamos el evento on Focus a username
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && documentoIdentificacion.getSelectedItemPosition() == 0)
                    username.setText(Utils.formatear(username.getText().toString()));
            }
        });

        //Dejamos todos los usuarios en login 0 y ultimo usuario en 0
        otraCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.makeInAnimation(Login.this, true);
                animation.setDuration(500);
                username.setVisibility(View.VISIBLE);
                username.startAnimation(animation);
                username.setText("");
                documentoIdentificacion.setVisibility(View.VISIBLE);
                documentoIdentificacion.startAnimation(animation);
                documentoIdentificacion.setSelection(0);
                otraCuenta.setVisibility(View.INVISIBLE);
                nombreProfesor.setVisibility(View.INVISIBLE);
                usuarioDataSource.updateUsuario(userDB.getUsuarioDB().getId(), false, false);
                userDB.set_usuarioDB(null);
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

    @Override
    protected void onStart() {
        super.onStart();
        try {
            usuarioDataSource.open();
            claseDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

                claseDataSource.insertClaseAlumnos(clases, 0, data.get("id").getAsInt());
                Intent intent = new Intent(Login.this, Clases.class);
                Usuario usuario = new Usuario(data.get("id").getAsInt(), password.getText().toString(), true, data.get("username").getAsInt(), data.get("nombre").getAsString());
                //Guardamos los datos del usuario.
                usuarioDataSource.insertUsuario(usuario);
                intent.putExtra("password", password.getText().toString());
                intent.putExtra("username", usuario.getUsername());
                startActivity(intent);
                usuarioDataSource.close();
                userDB = null;
                pd.cancel();
                finish();
            } else {
                String msgStatus = jsonObj.get("usuario").getAsJsonObject().get("msg").getAsString();
                if(msg.equals("error")) {
                    pd.cancel();
                    Utils.showToast(Login.this, msgStatus);
                }
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Utils.showToast(Login.this, error.getMessage());
            pd.cancel();
        }
    };

    //Metodo que nos conecta con eClassAPI y le enviamos los parametros por post para conectar
    protected void loadUsuarioData() {

        //userDB.getUser(usuarioDataSource);
        Intent intent = new Intent(Login.this, Clases.class);
        if(userDB.getUsuarioDB().getId() == 0) {
            //Aca debemos armar los datos para llamar a la API
            JSONObject datosJson = new JSONObject();
            try {
                String numero_documento;
                int seleccion = documentoIdentificacion.getSelectedItemPosition();
                numero_documento = getNumeroDocumento(seleccion);
                datosJson.put("numero_documento", numero_documento);
                datosJson.put("password", Utils.MD5(password.getText().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, datosJson.toString());
            byte[] jsonToByte = datosJson.toString().getBytes();
            String datos = Base64.encodeToString(jsonToByte,0);

            apiService = new eClassAPI(datos);
            apiService.getUsuarioData(mUsuarioSerice);

        } else {
            if (password.getText().toString().equals(userDB.getUsuarioDB().getPassword())) {
                if (usuarioDataSource.updateUsuario(userDB.getUsuarioDB().getId(), true, true) > 0) {
                    intent.putExtra("password", userDB.getUsuarioDB().getPassword());
                    intent.putExtra("username", userDB.getUsuarioDB().getUsername());
                    startActivity(intent);
                    usuarioDataSource.close();
                    userDB = null;
                    pd.cancel();
                    finish();
                }
            } else {
                pd.cancel();
                Utils.showToast(Login.this, getResources().getString(R.string.password_incorrect));
            }
        }

    }

    private String getNumeroDocumento(int seleccion) {
        String numero_documento;
        if(seleccion == 0) {
           numero_documento = (username.getText().toString().length() == 10) ? username.getText().toString().substring(0, 8) : username.getText().toString().substring(0, 7);
        } else {
           numero_documento = username.getText().toString();
        }
        return numero_documento;
    }

    public boolean ingresar(View view) {
        pd = ProgressDialog.show(this, "", getResources().getString(R.string.loading_data), true);
        int seleccion = documentoIdentificacion.getSelectedItemPosition();

        switch (seleccion) {
            case 0:
                if(!Utils.validarRut(username.getText().toString())) {
                    username.setError(getResources().getString(R.string.bad_document_format));
                    pd.cancel();
                    return false;
                }
            break;
            default:
                if(username.getText().toString().isEmpty()) {
                    username.setError(getResources().getString(R.string.enter_document));
                    pd.cancel();
                    return false;
                }
            break;
        }

        if(password.getText().toString().isEmpty()) {
            password.setError(getResources().getString(R.string.enter_password));
            pd.cancel();
            return false;
        }

        if(userDB.getUsuarioDB() == null) {
            userDB.getUser(usuarioDataSource, Integer.parseInt(getNumeroDocumento(seleccion)));
        }
        //Si va bien llamamos a los datos de la Api
        loadUsuarioData();
        return true;
    }
}
