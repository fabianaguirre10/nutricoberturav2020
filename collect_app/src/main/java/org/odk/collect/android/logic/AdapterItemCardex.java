package org.odk.collect.android.logic;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static org.odk.collect.android.R.id;
import static org.odk.collect.android.R.layout;

public class AdapterItemCardex extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<Cardex> items;


    public AdapterItemCardex(Activity activity, ArrayList<Cardex> items) {
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

    public void addAll(ArrayList<Cardex> lista) {
        for (int i = 0; i < lista.size(); i++) {
            items.add(lista.get(i));
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
            v = inf.inflate(layout.activity_item_cardex, null);
        }

        Cardex dir = items.get(position);
        DecimalFormat df = new DecimalFormat("0.00");
        String resultadoUsd = df.format(dir.getUsd());

        TextView producto = (TextView) v.findViewById(id.producto2);
        producto.setText(dir.getProducto());

        TextView operacion = (TextView) v.findViewById(id.operacion2);
        operacion.setText(dir.getTipoOperacion());

        TextView saldo = (TextView) v.findViewById(id.saldo2);
        saldo.setText(String.valueOf(dir.getSaldo()));

        TextView venta = (TextView) v.findViewById(id.venta2);
        venta.setText(String.valueOf(dir.getCantVenta()));

        TextView devolucion = (TextView) v.findViewById(id.devolucion2);
        devolucion.setText(String.valueOf(dir.getCantDevo()));

        TextView devolucionV = (TextView) v.findViewById(id.devoventa2);
        devolucionV.setText(String.valueOf(dir.getCantDevoVenta()));

        TextView usd = (TextView) v.findViewById(id.usd2);
        usd.setText(String.valueOf(dir.getUsd()));

        TextView codLocal = (TextView) v.findViewById(id.codlocal2);
        codLocal.setText(dir.getCodLocal());

        return v;
    }

}
