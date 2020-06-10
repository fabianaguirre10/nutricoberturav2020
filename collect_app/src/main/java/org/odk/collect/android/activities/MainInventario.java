package org.odk.collect.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.util.DateTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;

import org.odk.collect.android.R;
import org.odk.collect.android.adapters.InventarioListAdapter;
import org.odk.collect.android.adapters.model.Iteminventario;
import org.odk.collect.android.logic.AdapterItem;
import org.odk.collect.android.logic.Category;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainInventario extends AppCompatActivity {
    Engine_util objutil;
    ListView listainventa;
    Drawable imagenpne=null;
    AdapterItem adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_inventario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Date d = new Date();
        Iteminventario movimiento = new Iteminventario();
        ArrayList<Iteminventario> listmoviemtos= new ArrayList<>();
         listainventa = findViewById(R.id.listainventario);
        imagenpne=getResources().getDrawable(R.drawable.btn_moreinfo);
        cargarlistaproductos();




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
    }
    public void cargarlistaproductos(){

        ArrayList<Category> category = new ArrayList<Category>();

        String where="";
        String opcion = "";
        String[] args = new String[]{};
        where = "where 1=1 and Estado ='A'";
        objutil= new Engine_util();
        Cursor cursor = objutil.Listarproductos(args, opcion, where);
        if (cursor.moveToFirst()) {

            do {


                category.add(new Category(cursor.getString(1),cursor.getString(1),cursor.getString(2)+'\n'+"Stock: "+cursor.getString(5),cursor.getString(2),imagenpne));
            } while (cursor.moveToNext());
        }
        adapter = new AdapterItem(this, category);
        listainventa.setAdapter(adapter);
    }

}
