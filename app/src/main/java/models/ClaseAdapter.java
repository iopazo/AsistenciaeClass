package models;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moveapps.asistenciaeclass.R;

import org.json.JSONObject;

import java.util.List;

import api.eClassAPI;
import db.DBClaseSource;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ignacio on 08/10/2014.
 */
public class ClaseAdapter extends ArrayAdapter<Clase> {

    List<Clase> data;
    Context context;
    int layoutResId;
    protected DBClaseSource mClaseSource;
    JSONObject jsonObject;
    protected eClassAPI apiService;
    protected int id_clase;
    protected ProgressDialog pd;
    SwipeListView swipeListView;
    ClaseAdapter adapter;

    public ClaseAdapter(Context context, int resource, List<Clase> objects, DBClaseSource mClaseSource) {
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

            //Validamos si el tablet tiene una densidad de 120
            if(dpiValidate == 120) {
                holder.nombreCampus.setTextSize(25f);
                holder.nombrePrograma.setTextSize(18f);
                holder.nombreCurso.setTextSize(18f);
                holder.nombreClase.setTextSize(15f);
            }

            holder.botonSincronizar = (Button)row.findViewById(R.id.btnSincronizarClase);
            holder.botonEliminar = (Button)row.findViewById(R.id.btnEliminar);
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

        final ClaseHolder finalHolder = holder;
        final View finalRow = row;
        holder.botonSincronizar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(claseData.getEstado() == 1) {
                    pd = ProgressDialog.show(context, "", "Sync class, please wait...", true);
                    jsonObject = new JSONObject();
                    jsonObject = mClaseSource.getAlumnosByClass(claseData.getId());
                    byte[] jsonToByte = jsonObject.toString().getBytes();
                    String datos = Base64.encodeToString(jsonToByte, 0);

                    apiService = new eClassAPI(datos);
                    //Aca se llama a la Api y subimos la asistencia
                    apiService.subirAsistencia(mUsuarioService);
                    Log.d("ClaseAdapter", jsonObject.toString());

                    id_clase = claseData.getId();
                    finalHolder.cerrado.setVisibility(View.INVISIBLE);
                    finalHolder.sincronizado.setVisibility(View.VISIBLE);
                    finalRow.setDrawingCacheEnabled(true);
                    finalRow.refreshDrawableState();
                } else {
                    Toast.makeText(context, "Only closed classes can be synchronized!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder saveDialog = new AlertDialog.Builder(swipeListView.getContext());
                saveDialog.setTitle("Dismiss Class");
                saveDialog.setMessage("This action can't be undone, are you sure?");

                    AlertDialog.Builder builder = saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mClaseSource.cambiarEstadoClase(claseData.getId(), 3);
                            swipeListView.closeAnimate(position);
                            adapter.remove(claseData);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(context, "Class successfully removed.", Toast.LENGTH_LONG).show();
                        }
                    });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();
            }
        });

        return row;
    }

    protected Callback<JsonElement> mUsuarioService = new Callback<JsonElement>() {
        @Override
        public void success(JsonElement jsonElement, Response response) {
            JsonObject jsonObj = jsonElement.getAsJsonObject();
            String status = jsonObj.get("alumnos").getAsJsonObject().get("status").getAsString();
            String msg = jsonObj.get("alumnos").getAsJsonObject().get("msg").getAsString();
            //Si la respuesta es correcta.
            if(status.equals("success")) {
                //Marcamos la clase como sincronizada
                mClaseSource.cambiarEstadoClase(id_clase, 2);
                pd.cancel();
                Toast.makeText(context, "Class successfully uploaded.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                pd.cancel();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            pd.cancel();
        }
    };

    static class ClaseHolder {
        TextView nombreClase;
        TextView nombreCampus;
        TextView nombrePrograma;
        TextView nombreCurso;
        Button botonSincronizar;
        Button botonEliminar;
        ImageView sincronizado;
        ImageView cerrado;
    }
}
