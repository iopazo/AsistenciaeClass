package models;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moveapps.asistenciaeclass.R;

import java.util.List;

import db.DBClaseSource;

/**
 * Created by Ignacio on 08/10/2014.
 */
public class ClaseAdapter extends ArrayAdapter<Clase> {

    List<Clase> data;
    Context context;
    int layoutResId;
    protected DBClaseSource mClaseSource;


    public ClaseAdapter(Context context, int resource, List<Clase> objects, DBClaseSource mClaseSource) {
        super(context, resource, objects);

        this.data = objects;
        this.context = context;
        this.layoutResId = resource;
        this.mClaseSource = mClaseSource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ClaseHolder holder = null;
        View row = convertView;

        holder = null;
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResId, parent, false);

            holder = new ClaseHolder();

            holder.nombreAlumno = (TextView)row.findViewById(R.id.nombreAlumno);
            holder.botonAusente = (Button)row.findViewById(R.id.botonAusente);
            holder.botonRestablecer = (Button)row.findViewById(R.id.botonRestablecer);
            holder.presente = (ImageView)row.findViewById(R.id.presente);
            holder.ausente = (ImageView)row.findViewById(R.id.ausente);
            row.setTag(holder);
        } else {
            holder = (ClaseHolder)row.getTag();
        }

        final Clase claseData = data.get(position);
        holder.nombreAlumno.setText(claseData.getNombre());

        /*
        Estado
        0: Nada
        1: Presente
        2: Ausente
        */
        switch (claseData.getEstado()) {
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
                
            }
        });

        holder.botonRestablecer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });

        return row;
    }

    static class ClaseHolder {
        TextView nombreAlumno;
        Button botonAusente;
        Button botonRestablecer;
        ImageView presente;
        ImageView ausente;
    }
}
