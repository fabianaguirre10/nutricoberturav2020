package org.odk.collect.android.activities;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.database.BaseDatosEngine.BaseDatosEngine;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.BranchProducto;

public class ListProductDes extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<itemlist> listDataHeader;
    Engine_util objutil;
    HashMap<itemlist, List<itemlist>> listDataChild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_des);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // new SimpleDialog().show(getSupportFragmentManager(), "SimpleDialog");
                Intent intent = new Intent(getApplication(), inventarioingreso.class);
                startActivityForResult(intent, 0);
                finish();



            }
        });
        Button bcardex = findViewById(R.id.reportecardex);
        bcardex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // new SimpleDialog().show(getSupportFragmentManager(), "SimpleDialog");
                Intent intent = new Intent(getApplication(), Item_Cardex.class);
                startActivityForResult(intent, 0);
                finish();



            }
        });
/*cambio ejemplo*/
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<itemlist>();

        listDataChild = new HashMap<itemlist, List<itemlist>>();
        BaseDatosEngine usdbh = new BaseDatosEngine();
        usdbh = usdbh.open();
        Cursor cursorhead=usdbh.listarProductoCategoria();
        List<itemlist>  itemhead= new ArrayList<itemlist>();
        if(cursorhead!=null) {
            if (cursorhead.moveToFirst()) {
                do {
                    itemlist op = new itemlist();
                    op.setCodigo(cursorhead.getInt(0));
                    op.setDescripcion(cursorhead.getString(1));
                    op.setCantidad(cursorhead.getInt(2));
                    listDataHeader.add(op);

                } while (cursorhead.moveToNext());
            }


        }
        usdbh.close();

        //listDataChild.put(listDataHeader.get(0), top250);

        String where="";
        String opcion = "";
        String[] args = new String[]{};

        for (itemlist x :listDataHeader) {
            where = "where 1=1 and id_cat_mae ='"+x.getCodigo()+"'";
            objutil= new Engine_util();
            itemhead= new ArrayList<itemlist>();
            Cursor cursor = objutil.Listarproductos(args, opcion, where);
            if (cursor.moveToFirst()) {

                do {
                    itemlist op = new itemlist();
                    op.setCodigo(cursor.getInt(6));
                    op.setDescripcion(cursor.getString(2));
                    op.setCodigo2(cursor.getString(1));
                    op.setCantidad(cursor.getInt(5));
                    op.setTitulo(cursor.getString(4));
                    itemhead.add(op);


                } while (cursor.moveToNext());

            }
            listDataChild.put(x, itemhead);
        }






        // Adding child data


        /*listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");*/

        // Adding child data


        //listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        //listDataChild.put(listDataHeader.get(1), nowShowing);
        //listDataChild.put(listDataHeader.get(2), comingSoon);
    }
}