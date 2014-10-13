package models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moveapps.asistenciaeclass.R;

import java.util.List;

import db.DBAlumnoSource;

/**
 * Created by Ignacio on 08/10/2014.
 */
public class AlumnoAdapter extends ArrayAdapter<Alumno> {

    List<Alumno> data;
    Context context;
    int layoutResId;
    static String passwordProfesor;
    protected DBAlumnoSource mAlumnoSource;
    AlumnoHolder holder;


    public AlumnoAdapter(Context context, int resource, List<Alumno> objects, String masterPassword, DBAlumnoSource mAlumnoSource) {
        super(context, resource, objects);

        this.data = objects;
        this.context = context;
        this.layoutResId = resource;
        this.passwordProfesor = masterPassword;
        this.mAlumnoSource = mAlumnoSource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AlumnoHolder holder = null;
        View row = convertView;

        holder = null;
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResId, parent, false);

            holder = new AlumnoHolder();

            holder.nombreAlumno = (TextView)row.findViewById(R.id.nombreAlumno);
            holder.botonAusente = (Button)row.findViewById(R.id.botonAusente);
            holder.botonRestablecer = (Button)row.findViewById(R.id.botonRestablecer);
            holder.presente = (ImageView)row.findViewById(R.id.presente);
            holder.ausente = (ImageView)row.findViewById(R.id.ausente);
            row.setTag(holder);
        } else {
            holder = (AlumnoHolder)row.getTag();
        }

        final Alumno alumnoData = data.get(position);
        holder.nombreAlumno.setText(alumnoData.getNombre());

        /*
        Estado
        0: Nada
        1: Presente
        2: Ausente
        */
        switch (alumnoData.getEstado()) {
            case 1:
                holder.presente.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.ausente.setVisibility(View.VISIBLE);
                break;
            default:
                holder.presente.setVisibility(View.INVISIBLE);
                holder.ausente.setVisibility(View.INVISIBLE);
                break;
        }

        holder.botonAusente.setOnClickListener(new View.OnClickListener() {

            /*
            0: Nada
            1: Presente
            2: Ausente
             */
            @Override
            public void onClick(View v) {
                // Si el alumno esta presente, entonces solo ahi hacemos la validacion.
                switch (alumnoData.getEstado()) {
                    case 0:
                        mAlumnoSource.ausente(alumnoData.getIdAlumnoCursoClaseSede());
                        Toast.makeText(context, "Change success!.", Toast.LENGTH_SHORT).show();
                        ((Activity) context).recreate();
                        break;
                    case 1:
                        AlertDialog.Builder cambiarEstadoAlert = new AlertDialog.Builder(context);
                        cambiarEstadoAlert.setTitle("Teacher confirms");
                        cambiarEstadoAlert.setMessage("Sure you want remove the signature?");
                        final EditText passwordConfirm = new EditText(context);
                        passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        cambiarEstadoAlert.setView(passwordConfirm);

                        AlertDialog.Builder builder = cambiarEstadoAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String value = passwordConfirm.getText().toString();
                                if(value.equals(passwordProfesor)) {
                                    mAlumnoSource.ausente(alumnoData.getIdAlumnoCursoClaseSede());
                                    Toast.makeText(context, "Change success!.", Toast.LENGTH_SHORT).show();
                                    ((Activity) context).recreate();
                                } else {
                                    Toast.makeText(context, "The password is incorrect, try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        cambiarEstadoAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        cambiarEstadoAlert.show();
                        break;
                    case 2:
                        Toast.makeText(context, "This student is already absent.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        holder.botonRestablecer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Si el alumno esta presente, entonces solo ahi hacemos la validacion.
                if(alumnoData.getEstado() != 0) {
                    AlertDialog.Builder restablerAlert = new AlertDialog.Builder(context);
                    restablerAlert.setTitle("Teacher confirms");
                    restablerAlert.setMessage("Sure you want reset the changes of this student?");
                    final EditText passwordConfirm = new EditText(context);
                    passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    restablerAlert.setView(passwordConfirm);

                    AlertDialog.Builder builder = restablerAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String value = passwordConfirm.getText().toString();
                            if(value.equals(passwordProfesor)) {
                                mAlumnoSource.restablecer(alumnoData.getIdAlumnoCursoClaseSede());
                                Toast.makeText(context, "Change success!.", Toast.LENGTH_SHORT).show();
                                ((Activity) context).recreate();
                            } else {
                                Toast.makeText(context, "The password is incorrect, try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    restablerAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    restablerAlert.show();
                } else if(alumnoData.getEstado() == 2){
                    Toast.makeText(context, "This student is already unmark.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return row;
    }

    static class AlumnoHolder {
        TextView nombreAlumno;
        Button botonAusente;
        Button botonRestablecer;
        ImageView presente;
        ImageView ausente;
    }
}
