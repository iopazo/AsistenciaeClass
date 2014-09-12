package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.Console;


public class Login extends Activity {

    final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Seteamos el background color al layout
        RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.relativeLayout);
        relativeLayout.setBackgroundColor(Color.parseColor("#f1f1f1"));

        //Seteamos las fuentes en bold
        Typeface latoBold = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bol.ttf");
        EditText username = (EditText) this.findViewById(R.id.username);
        EditText password = (EditText) this.findViewById(R.id.password);
        username.setTypeface(latoBold);
        password.setTypeface(latoBold);
        //Setamos las fuentes pero Regulares
        Typeface latoRegular = Typeface.createFromAsset(getAssets(), "fonts/Lato-Reg.ttf");
        Button botonIngresar = (Button) this.findViewById(R.id.buttonIngresar);
        botonIngresar.setTypeface(latoRegular);

    }

    public void ingresar(View view) {
        //Toast.makeText(this, "Boton login", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Cursos.class);
        startActivity(intent);
        finish();
    }
}
