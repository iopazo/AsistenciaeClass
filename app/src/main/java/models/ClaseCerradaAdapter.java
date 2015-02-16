package models;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.moveapps.asistenciaeclass.R;

import org.json.JSONObject;

import java.util.List;

import api.eClassAPI;
import db.DBClaseSource;

/**
 * Created by Ignacio on 08/10/2014.
 */
public class ClaseCerradaAdapter extends ArrayAdapter<Clase> {

    List<Clase> data;
    Context context;
    int layoutResId;
    protected DBClaseSource mClaseSource;
    JSONObject jsonObject;
    protected eClassAPI apiService;
    protected int id_clase;
    protected ProgressDialog pd;
    SwipeListView swipeListView;
    ClaseCerradaAdapter adapter;

    public ClaseCerradaAdapter(Context context, int resource, List<Clase> objects, DBClaseSource mClaseSource) {
        super(context, resource, objects);

        this.data = objects;
        this.context = context;
        this.layoutResId = resource;
        this.mClaseSource = mClaseSource;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int dpiValidate = metrics.densityDpi;

        ClaseHolder holder = null;
        View row = convertView;
        adapter = this;
        swipeListView = (SwipeListView) parent;

        holder = null;
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResId, parent, false);

            holder = new ClaseHolder();

            holder.nombreClase = (TextView)row.findViewById(R.id.nombreClase);
            holder.nombreCampus = (TextView)row.findViewById(R.id.nombreCampus);
            holder.nombrePrograma = (TextView)row.findViewById(R.id.nombrePrograma);
            holder.nombreCurso = (TextView)row.findViewById(R.id.nombreCurso);
            holder.fechaSincronizacion = (TextView)row.findViewById(R.id.fecha_sincronizacion);

            //Validamos si el tablet tiene una densidad de 120
            if(dpiValidate == 120) {
                holder.nombreCampus.setTextSize(25f);
                holder.nombrePrograma.setTextSize(18f);
                holder.nombreCurso.setTextSize(18f);
                holder.nombreClase.setTextSize(15f);
                holder.fechaSincronizacion.setTextSize(15f);
            }


            holder.botonSincronizar = (Button)row.findViewById(R.id.btnSincronizarClase);
            //holder.botonEliminar = (Button)row.findViewById(R.id.btnEliminar);
            holder.cerrado = (ImageView)row.findViewById(R.id.cerrada);
            holder.sincronizado = (ImageView)row.findViewById(R.id.sincronizada);
            row.setTag(holder);

        } else {
            holder = (ClaseHolder)row.getTag();
        }

        final Clase claseData = data.get(position);

        String[] separado = claseData.getNombre().split(" - ");

        for (int i = 0; i < separado.length; i++) {
            switch (i) {
                case 0:
                    holder.nombreCampus.setText(separado[0].toString());
                    break;
                case 1:
                    holder.nombrePrograma.setText(separado[1].toString());
                    break;
                case 2:
                    holder.nombreCurso.setText(separado[2].toString());
                    break;
                case 3:
                    holder.nombreClase.setText(separado[3].toString());
                    break;
            }
        }
        holder.fechaSincronizacion.setText(claseData.getFechaSincronizacion());
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

        return row;
    }

    static class ClaseHolder {
        TextView nombreClase;
        TextView nombreCampus;
        TextView nombrePrograma;
        TextView nombreCurso;
        TextView fechaSincronizacion;
        Button botonSincronizar;
        Button botonEliminar;
        ImageView sincronizado;
        ImageView cerrado;
    }
}
