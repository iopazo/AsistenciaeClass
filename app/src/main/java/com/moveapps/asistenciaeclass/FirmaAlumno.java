package com.moveapps.asistenciaeclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.UUID;

import db.DBAlumnoSource;


public class FirmaAlumno extends Activity implements OnClickListener {

    static String TAG = FirmaAlumno.class.getSimpleName();
    static int ID_ALUMNO;
    static String NOMBRE_ALUMNO;
    static String ID_CLASE;

    protected DBAlumnoSource mAlumnoSource;
    /*
    Draw view
     */
    //custom drawing view
    private DrawingView drawView;
    //buttons
    private Button newBtn, saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firma_alumno);

        //Iniciamos la instancia y abrimos la base de datos
        mAlumnoSource = new DBAlumnoSource(FirmaAlumno.this);
        try {
            mAlumnoSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Obtenemos los parametros enviados desde la vista Clases
        Intent intent = getIntent();
        if(intent.hasExtra("id")) {
            ID_ALUMNO = intent.getIntExtra("id", 0);
        }
        if(intent.hasExtra("id_clase")) {
            ID_CLASE = intent.getStringExtra("id_clase");
        }
        if(intent.hasExtra("nombre")) {
            NOMBRE_ALUMNO = String.format("%s", intent.getStringExtra("nombre"));
            TextView breadcrumb = (TextView)findViewById(R.id.bcrumbText);
            breadcrumb.setText(NOMBRE_ALUMNO);
        }

        //get drawing view
        drawView = (DrawingView)findViewById(R.id.view);
        //new button
        /*
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        */

        //save button
        saveBtn = (Button)findViewById(R.id.buttonAceptar);
        saveBtn.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mAlumnoSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAlumnoSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.firma_alumno, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Alumnos alumnos = new Alumnos();
        /*if(view.getId()==R.id.new_btn){
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        else */if(view.getId()==R.id.buttonAceptar){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Guardar Firma");
            saveDialog.setMessage("Esta seguro que desea guardar su firma?");
            AlertDialog.Builder builder = saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //save drawing
                    drawView.setDrawingCacheEnabled(true);
                    //attempt to save
                    String nameImage = UUID.randomUUID().toString() + ".png";
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            nameImage, "drawing");

                    /*
                    Validamos que la imagen se haya guardado
                    Aca transformamos la imagen guardada en un String Base64 para guardarlo en la base de datos
                     */
                    if (imgSaved != null) {
                        Bitmap bm = Bitmap.createBitmap(drawView.getDrawingCache());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] byteArrayImage = baos.toByteArray();
                        String firmaEncoded = Base64.encodeToString(byteArrayImage, 0);
                        /*Si la firma viene bien encodeada la apsamos para guardarla*/
                        if (firmaEncoded != null) {
                            mAlumnoSource.updateAlumno(ID_ALUMNO, firmaEncoded, 1);
                            finish();
                        }
                    } else {
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "La imagen no pudo ser guardada, intente nuevamente.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });

            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }
}
