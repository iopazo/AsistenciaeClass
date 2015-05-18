package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.SQLException;

import db.DBAlumnoSCSource;
import models.AlumnoSinClase;

import static com.moveapps.asistenciaeclass.Utils.formatear;
import static com.moveapps.asistenciaeclass.Utils.isValidEmail;
import static com.moveapps.asistenciaeclass.Utils.showToast;
import static com.moveapps.asistenciaeclass.Utils.validarRut;


public class NuevoAlumno extends Activity {

    private EditText nombre = null;
    private EditText email = null;
    private EditText numeroDocumento = null;
    private Spinner tipoDocumento = null;
    private static int ID_CLASE;
    protected DBAlumnoSCSource dbAlumnoSCSource;

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
                    numeroDocumento.setText(formatear(numeroDocumento.getText().toString()));
            }
        });

        dbAlumnoSCSource = new DBAlumnoSCSource(this);

        try {
            dbAlumnoSCSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean crearAlumno(View view) throws SQLException {
        int seleccion = tipoDocumento.getSelectedItemPosition();
        boolean error = false;
        switch (seleccion) {
            case 0:
                if(!validarRut(numeroDocumento.getText().toString())) {
                    numeroDocumento.setError(getResources().getString(R.string.bad_document_format));
                    error = true;
                }
                break;
            default:
                if(numeroDocumento.getText().toString().isEmpty()) {
                    numeroDocumento.setError(getResources().getString(R.string.enter_document));
                    error = true;
                }
                break;
        }

        if(nombre.getText().toString().isEmpty()) {
            nombre.setError(getResources().getString(R.string.enter_name));
            error = true;
        }

        if(email.getText().toString().isEmpty()) {
            email.setError(getResources().getString(R.string.enter_email));
            error = true;
        } else if(!isValidEmail(email.getText())) {
            email.setError(getResources().getString(R.string.valid_email));
            error = true;
        }

        if(!error) {
            dbAlumnoSCSource.open();
            AlumnoSinClase alumnoSinClase = new AlumnoSinClase(ID_CLASE, nombre.getText().toString(),
                    email.getText().toString(),
                    numeroDocumento.getText().toString(),
                    tipoDocumento.getSelectedItemPosition());
            if(dbAlumnoSCSource.add(alumnoSinClase, ID_CLASE)) {
                showToast(this, getResources().getString(R.string.student_created));
            } else {
                showToast(this, getResources().getString(R.string.student_already_exist));
            }
            dbAlumnoSCSource.close();
            tipoDocumento.setSelection(0, true);
            numeroDocumento.setText("");
            nombre.setText("");
            email.setText("");
        }

        return true;
    }
}
