package org.odk.collect.android.activities;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.odk.collect.android.R;
import org.odk.collect.android.logic.AdapterItemCardex;
import org.odk.collect.android.logic.Cardex;

import java.util.ArrayList;

public class Item_Cardex extends AppCompatActivity {
    Engine_util objutil = new Engine_util();
    double sumv=0.0;
    double sumdevo=0.0;
    double sumdevoventa=0.0;
    double sumusd=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_cardex);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView lw = (ListView) findViewById(R.id.cardex);

        TextView sumventa=(TextView) findViewById(R.id.sumventa);
        TextView sumd=(TextView) findViewById(R.id.sumdevolucion);
        TextView sumdv=(TextView) findViewById(R.id.sumdevoventa);
        TextView sumdolares=(TextView) findViewById(R.id.sumuusd);

        setSupportActionBar(toolbar);

        Cursor cursor = objutil.ListaCardex();
        ArrayList<Cardex> lista = new ArrayList<>();

        if (cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    Cardex item = new Cardex();
                    item.setProducto(cursor.getString(3));
                    item.setCantVenta(cursor.getInt(6));
                    item.setCantDevo(cursor.getInt(7));
                    item.setCantDevoVenta(cursor.getInt(8));
                    item.setSaldo(cursor.getInt(5));
                    item.setTipoOperacion(cursor.getString(4));
                    item.setCodLocal(cursor.getString(9));
                    item.setUsd(cursor.getDouble(11));
                    sumv=sumv+item.getCantVenta();
                    sumdevo=sumdevo+item.getCantDevo();
                    sumdevoventa=sumdevoventa+item.getCantDevoVenta();
                    sumusd=sumusd+item.getUsd();
                    lista.add(item);
                } while (cursor.moveToNext());
            }
        }
        AdapterItemCardex adapter = new AdapterItemCardex(this, lista);
        lw.setAdapter(adapter);
        sumventa.setText(String.valueOf(sumv));
        sumd.setText(String.valueOf(sumdevo));
        sumdv.setText(String.valueOf(sumdevoventa));
        sumdolares.setText(String.valueOf(sumusd));
        sumventa.setTypeface(null, Typeface.BOLD);
        sumd.setTypeface(null, Typeface.BOLD);
        sumdv.setTypeface(null, Typeface.BOLD);
        sumdolares.setTypeface(null, Typeface.BOLD);

    }


}
