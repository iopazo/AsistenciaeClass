package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.SQLException;

import db.DBAlumnoSCSource;
import models.AlumnoSinClase;


public class NuevoAlumno extends Activity {

    private EditText nombre = null;
    private EditText email = null;
    private EditText numeroDocumento = null;
    private Spinner tipoDocumento = null;
    private static int ID_CLASE;
    protected DBAlumnoSCSource dbAlumnoSCSource;

    //Dialogo cuando estemos guardando al alumno.
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_alumno);

        Intent intent = getIntent();
        if(intent.hasExtra("id_clase")) {
            ID_CLASE = intent.getIntExtra("id_clase", 0);
        }

        nombre = (EditText) findViewById(R.id.fullnameStudent);
        email = (EditText) findViewById(R.id.emailStudent);
        numeroDocumento = (EditText) findViewById(R.id.documentNumberStudent);
        tipoDocumento = (Spinner) findViewById(R.id.documentTypeStudent);

        //Le asignamos el evento on Focus a username
        numeroDocumento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && tipoDocumento.getSelectedItemPosition() == 0)
                    numeroDocumento.setText(Utils.formatear(numeroDocumento.getText().toString()));
            }
        });

        dbAlumnoSCSource = new DBAlumnoSCSource(NuevoAlumno.this);

    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nuevo_alumno, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public boolean crearAlumno(View view) throws SQLException {
       // pd = ProgressDialog.show(this, "", "Aqui va el texto", true);
        int seleccion = tipoDocumento.getSelectedItemPosition();

        switch (seleccion) {
            case 0:
                if(!Utils.validarRut(numeroDocumento.getText().toString())) {
                    numeroDocumento.setError(getResources().getString(R.string.bad_document_format));
                    //pd.cancel();
                }
                break;
            default:
                if(numeroDocumento.getText().toString().isEmpty()) {
                    numeroDocumento.setError(getResources().getString(R.string.enter_document));
                    //pd.cancel();
                }
                break;
        }

        if(nombre.getText().toString().isEmpty()) {
            nombre.setError("Debes ingresar el nombre del alumno");
        }

        if(email.getText().toString().isEmpty()) {
            email.setError("Debes ingresar el email del alumno");
        } else if(!Utils.isValidEmail(email.getText())) {
            email.setError("Debes ingresar un email válido");
        }

        AlumnoSinClase alumnoSinClase = new AlumnoSinClase(ID_CLASE, nombre.getText().toString(), email.getText().toString(), numeroDocumento.getText().toString(), tipoDocumento.getSelectedItemPosition());
        dbAlumnoSCSource.add(alumnoSinClase, ID_CLASE);

        return true;
    }
}
