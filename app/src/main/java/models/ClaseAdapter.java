package models;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

                if(claseData.getEstado() == 1) {
                    pd = ProgressDialog.show(context, "", "Sync class, please wait...", true);
                    jsonObject = new JSONObject();
                    jsonObject = mClaseSource.getAlumnosByClass(claseData.getId());
                    Log.d("ClaseAdapter", jsonObject.toString());
                    byte[] jsonToByte = jsonObject.toString().getBytes();
                    String datos = Base64.encodeToString(jsonToByte, 0);

                    apiService = new eClassAPI(datos);
                    apiService.subirAsistencia(mUsuarioService);

                    id_clase = claseData.getId();
                } else {
                    Toast.makeText(context, "Only closed classes can be synchronized!", Toast.LENGTH_SHORT).show();
                }
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
                ((Activity) context).recreate();
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                pd.cancel();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            //Log.d("ClaseAdapter", error.getMessage());
        }
    };

    static class ClaseHolder {
        TextView nombreClase;
        Button botonSincronizar;
        ImageView sincronizado;
        ImageView cerrado;
    }
}
