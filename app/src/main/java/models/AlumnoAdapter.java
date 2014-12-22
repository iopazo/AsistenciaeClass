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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.moveapps.asistenciaeclass.R;
import com.moveapps.asistenciaeclass.Utils;

import java.util.ArrayList;

import db.DBAlumnoSource;

/**
 * Created by Ignacio on 08/10/2014.
 */
public class AlumnoAdapter extends ArrayAdapter<Alumno> implements Filterable {

    private AlumnoFilter alumnoFilter;
    private ArrayList<Alumno> data;
    private ArrayList<Alumno> dataFilter;
    Context context;
    int layoutResId;
    static String passwordProfesor;
    protected DBAlumnoSource mAlumnoSource;
    View row;


    public AlumnoAdapter(Context context, int resource, ArrayList<Alumno> objects, String masterPassword, DBAlumnoSource mAlumnoSource) {
        super(context, resource, objects);

        this.data = objects;
        this.dataFilter = objects;
        this.context = context;
        this.layoutResId = resource;
        this.passwordProfesor = masterPassword;
        this.mAlumnoSource = mAlumnoSource;

        getFilter();
    }

    @Override
    public int getCount() {
        return dataFilter.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        AlumnoHolder holder = null;
        row = convertView;

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

        final Alumno alumnoData = (Alumno) getItem(position);
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
                holder.ausente.setVisibility(View.INVISIBLE);
                break;
            case 2:
                holder.presente.setVisibility(View.INVISIBLE);
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
                        Utils.showToast(context, context.getResources().getString(R.string.change_sucess));
                        ((Activity) context).recreate();
                        break;
                    case 1:
                        AlertDialog.Builder cambiarEstadoAlert = new AlertDialog.Builder(context);
                        cambiarEstadoAlert.setTitle(context.getResources().getString(R.string.teacher_confirm));
                        cambiarEstadoAlert.setMessage(context.getResources().getString(R.string.remove_signature));
                        final EditText passwordConfirm = new EditText(context);
                        passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        cambiarEstadoAlert.setView(passwordConfirm);

                        AlertDialog.Builder builder = cambiarEstadoAlert.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String value = passwordConfirm.getText().toString();
                                if(value.equals(passwordProfesor)) {
                                    mAlumnoSource.ausente(alumnoData.getIdAlumnoCursoClaseSede());
                                    Utils.showToast(context, context.getResources().getString(R.string.change_sucess));
                                    ((Activity) context).recreate();
                                } else {
                                    Utils.showToast(context, context.getResources().getString(R.string.password_incorrect));
                                }
                            }
                        });

                        cambiarEstadoAlert.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        cambiarEstadoAlert.show();
                        break;
                    case 2:
                        Utils.showToast(context, context.getResources().getString(R.string.already_absent));
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
                    restablerAlert.setTitle(context.getResources().getString(R.string.teacher_confirm));
                    restablerAlert.setMessage(context.getResources().getString(R.string.reset_changes));
                    final EditText passwordConfirm = new EditText(context);
                    passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    restablerAlert.setView(passwordConfirm);

                    AlertDialog.Builder builder = restablerAlert.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String value = passwordConfirm.getText().toString();
                            if(value.equals(passwordProfesor)) {
                                mAlumnoSource.restablecer(alumnoData.getIdAlumnoCursoClaseSede());
                                Utils.showToast(context, context.getResources().getString(R.string.change_sucess));
                                ((Activity) context).recreate();
                            } else {
                                Utils.showToast(context, context.getResources().getString(R.string.password_incorrect));
                            }
                        }
                    });

                    restablerAlert.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    restablerAlert.show();
                } else if(alumnoData.getEstado() == 2){
                    Utils.showToast(context, context.getResources().getString(R.string.studen_unmark));
                }
            }
        });

        return row;
    }

    @Override
    public Alumno getItem(int position) {
        return dataFilter.get(position);
    }

    @Override
    public Filter getFilter() {
        if(alumnoFilter == null) {
            alumnoFilter = new AlumnoFilter();
        }
        return alumnoFilter;
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class AlumnoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Alumno> tempList = new ArrayList<Alumno>();

                // search content in friend list
                for (Alumno alumno : data) {
                    if (alumno.getNombre().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(alumno);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = data.size();
                filterResults.values = data;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataFilter = (ArrayList<Alumno>) results.values;
            notifyDataSetChanged();
        }
    }

    static class AlumnoHolder {
        TextView nombreAlumno;
        Button botonAusente;
        Button botonRestablecer;
        ImageView presente;
        ImageView ausente;
    }
}
