package models;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moveapps.asistenciaeclass.R;
import com.moveapps.asistenciaeclass.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import api.eClassAPI;
import db.DBClaseSource;
import db.DBComentarioClaseSource;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ignacio on 08/10/2014.
 */
public class ClaseCerradaAdapter extends ArrayAdapter<Clase> {

    List<Clase> data;
    Context context;
    int layoutResId;
    protected DBClaseSource mClaseSource;
    protected Usuario usuario;
    protected eClassAPI apiService;
    protected Clase claseProtected;
    protected ProgressDialog pd;
    SwipeListView swipeListView;
    int dpiValidate;
    ClaseCerradaAdapter adapter;

    public ClaseCerradaAdapter(Context context, int resource, List<Clase> objects, DBClaseSource mClaseSource, Usuario _usuario) {
        super(context, resource, objects);

        this.data = objects;
        this.context = context;
        this.layoutResId = resource;
        this.mClaseSource = mClaseSource;
        this.usuario = _usuario;
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

        holder.botonSincronizar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (claseData.getEstado() == 2) {
                    claseProtected = claseData;
                    pd = ProgressDialog.show(context, "", context.getResources().getString(R.string.sync_class), true);

                    JSONObject jsonAlumnos;
                    JSONObject jsonComentarios = new JSONObject();
                    jsonAlumnos = mClaseSource.getAlumnosByClass(claseData.getId(), usuario.getId());
                    DBComentarioClaseSource dbComentarioClaseSource = new DBComentarioClaseSource(context);
                    try {
                        dbComentarioClaseSource.open();
                        jsonComentarios = dbComentarioClaseSource.jsonList(claseData.getId(), usuario.getId());
                        dbComentarioClaseSource.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonAlumnos.put("comentarios", jsonComentarios);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    byte[] jsonToByte = jsonAlumnos.toString().getBytes();
                    String datos = Base64.encodeToString(jsonToByte, 0);

                    apiService = new eClassAPI(datos);
                    //Aca se llama a la Api y subimos la asistencia
                    apiService.subirAsistencia(mUsuarioService);
                    swipeListView.closeAnimate(position);
                } else {
                    Utils.showToast(context, context.getResources().getString(R.string.only_class_sync));
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
                //Marcamos la clase como sincronizada y la quitamos de la lista
                adapter.notifyDataSetChanged();
                pd.cancel();
                Utils.showToast(context, context.getResources().getString(R.string.class_uploaded));
            } else {
                Utils.showToast(context, msg);
                pd.cancel();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Utils.showToast(context,  error.getMessage());
            pd.cancel();
        }
    };

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
