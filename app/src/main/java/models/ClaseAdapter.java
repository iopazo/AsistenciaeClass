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

            holder.nombreClase = (TextView)row.findViewById(R.id.nombreAlumno);
            holder.botonSincronizar = (Button)row.findViewById(R.id.btnSincronizarClase);
            holder.cerrado = (ImageView)row.findViewById(R.id.cerrada);
            holder.sincronizado = (ImageView)row.findViewById(R.id.sincronizada);
            row.setTag(holder);
        } else {
            holder = (ClaseHolder)row.getTag();
        }

        final Clase claseData = data.get(position);
        holder.nombreClase.setText(claseData.getNombre());

        /*
        Estado
        0: Nada
        1: Cerrado
        2: Sincronizado
        3: Eliminada
        */
        switch (claseData.getEstado()) {
            case 1:
                holder.cerrado.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.sincronizado.setVisibility(View.VISIBLE);
                break;
            default:
                holder.cerrado.setVisibility(View.INVISIBLE);
                holder.sincronizado.setVisibility(View.INVISIBLE);
                break;
        }

        holder.botonSincronizar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                
            }
        });


        return row;
    }

    static class ClaseHolder {
        TextView nombreClase;
        Button botonSincronizar;
        ImageView sincronizado;
        ImageView cerrado;
    }
}
