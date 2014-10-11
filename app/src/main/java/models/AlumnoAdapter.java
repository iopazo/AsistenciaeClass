package models;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


    public AlumnoAdapter(Context context, int resource, List<Alumno> objects) {
        super(context, resource, objects);

        this.data = objects;
        this.context = context;
        this.layoutResId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsHolder holder = null;
        View row = convertView;

        holder = null;
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResId, parent, false);

            holder = new NewsHolder();

            holder.nombreAlumno = (TextView)row.findViewById(R.id.nombreAlumno);
            holder.botonFirma = (Button)row.findViewById(R.id.botonFirma);
            holder.botonAusente = (Button)row.findViewById(R.id.botonAusente);
            row.setTag(holder);
        } else {
            holder = (NewsHolder)row.getTag();
        }

        Alumno alumnoData = data.get(position);
        holder.nombreAlumno.setText(alumnoData.getNombre());

        holder.botonFirma.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 1 Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.botonAusente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 2 Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }

    static class NewsHolder {
        TextView nombreAlumno;
        Button botonFirma;
        Button botonAusente;
    }
}
