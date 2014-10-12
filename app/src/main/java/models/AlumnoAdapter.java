package models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
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

/**
 * Created by Ignacio on 08/10/2014.
 */
public class AlumnoAdapter extends ArrayAdapter<Alumno> {

    List<Alumno> data;
    Context context;
    int layoutResId;
    static String passwordProfesor;


    public AlumnoAdapter(Context context, int resource, List<Alumno> objects, String masterPassword) {
        super(context, resource, objects);

        this.data = objects;
        this.context = context;
        this.layoutResId = resource;
        this.passwordProfesor = masterPassword;
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

            @Override
            public void onClick(View v) {
                // Levantamos siempre la pregunta de que el profesor tenga que validar con su password para dar de baja a este alumno.
                if(alumnoData.getEstado() != 2) {
                    AlertDialog.Builder cambiarEstadoAlert = new AlertDialog.Builder(context);
                    cambiarEstadoAlert.setTitle("Marcar como ausente");
                    cambiarEstadoAlert.setMessage("Esta seguro que desea eliminar la firma?");
                    final EditText passwordConfirm = new EditText(context);
                    passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    cambiarEstadoAlert.setView(passwordConfirm);
                    AlertDialog.Builder builder = cambiarEstadoAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String value = passwordConfirm.getText().toString();
                            Log.d("Adapter", value);
                            Log.d("Password", passwordProfesor);
                        }
                    });

                    cambiarEstadoAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    cambiarEstadoAlert.show();
                } else {
                    Toast.makeText(context, "Este alumno se encuentra actualmente ausente", Toast.LENGTH_SHORT);
                }
            }
        });

        holder.botonRestablecer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 2 Clicked",Toast.LENGTH_SHORT).show();
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
