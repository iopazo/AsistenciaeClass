package models;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filterable;
import android.widget.TextView;

import com.moveapps.asistenciaeclass.R;

import java.util.ArrayList;

import db.DBAlumnosCursosSource;

/**
 * Created by iopazo on 9/8/15.
 */
public class AlumnoCursoAdapter extends ArrayAdapter<AlumnoCurso> implements Filterable {

    //private AlumnoFilter alumnoFilter;
    private ArrayList<AlumnoCurso> data;
    public ArrayList<AlumnoCurso> alumnosSeleccionados = null;
    Context context;
    int layoutResId;
    protected DBAlumnosCursosSource mAlumnoCursoSource;
    View row;


    public AlumnoCursoAdapter(Context context, int resource, ArrayList<AlumnoCurso> objects, DBAlumnosCursosSource mAlumnoSource) {
        super(context, resource, objects);

        this.data = objects;
        //this.dataFilter = objects;
        this.context = context;
        this.layoutResId = resource;
        this.mAlumnoCursoSource = mAlumnoSource;
        this.alumnosSeleccionados = new ArrayList<AlumnoCurso>();
        getFilter();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AlumnoCurso getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        row = convertView;
        AlumnoCursoHolder holder = null;
        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResId, parent, false);
            holder = new AlumnoCursoHolder();
            holder.nombreAlumno = (TextView)row.findViewById(R.id.nombreAlumnoCurso);
            holder.checkBox = (CheckBox)row.findViewById(R.id.checkBoxAgregado);
            row.setTag(holder);
        }
        else {
            holder = (AlumnoCursoHolder)row.getTag();
        }

        final AlumnoCurso alumnoCurso = (AlumnoCurso) getItem(position);

        holder.nombreAlumno.setText(alumnoCurso.get_nombre());
        holder.checkBox.setChecked(alumnoCurso.get_agregado() != 0);
        holder.checkBox.setEnabled(alumnoCurso.get_agregado() == 0);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (isChecked) {
                    alumnosSeleccionados.add(alumnoCurso);
                } else {
                    alumnosSeleccionados.remove(alumnoCurso);
                }
            }
        });

        return row;
    }

    static class AlumnoCursoHolder {
        TextView nombreAlumno;
        CheckBox checkBox;
    }
}
