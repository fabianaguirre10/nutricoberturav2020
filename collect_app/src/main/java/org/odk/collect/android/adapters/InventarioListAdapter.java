package org.odk.collect.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.adapters.model.Iteminventario;
import org.odk.collect.android.logic.Category;

import java.util.ArrayList;

public class InventarioListAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<Iteminventario> items;

    public InventarioListAdapter(Activity activity, ArrayList<Iteminventario> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Iteminventario> iteminventario) {
        for (int i = 0; i < iteminventario.size(); i++) {
            items.add(iteminventario.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.iteminventario, null);
        }

        Iteminventario dir = items.get(position);

        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        CharSequence s = DateFormat.format("MMMM d, yyyy ", dir.getFecha().getTime());
        fecha.setText(s);
        TextView codproducto = (TextView) v.findViewById(R.id.codproducto);
        codproducto.setText(dir.getCodproducto());
        TextView codlocal = (TextView) v.findViewById(R.id.codlocal);
        codlocal.setText(dir.getCodlocal());
        TextView tipooperacion = (TextView) v.findViewById(R.id.tipooperacion);
        tipooperacion.setText(dir.getTipooperacion());
        TextView cantidad = (TextView) v.findViewById(R.id.cantidad);
        cantidad.setText(String.valueOf(dir.getCantidad()));
        TextView saldo = (TextView) v.findViewById(R.id.saldo);
        saldo.setText(String.valueOf(dir.getSaldo()));


        return v;
    }
}
