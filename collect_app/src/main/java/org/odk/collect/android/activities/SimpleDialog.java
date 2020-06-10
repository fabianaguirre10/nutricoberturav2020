package org.odk.collect.android.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.odk.collect.android.R;
import org.odk.collect.android.database.BaseDatosEngine.BaseDatosEngine;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.Capania;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.Producto;
import org.odk.collect.android.database.BaseDatosEngine.EstructuraBD;
import org.odk.collect.android.logic.AdapterItem;
import org.odk.collect.android.logic.Category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SimpleDialog extends DialogFragment {

    public SimpleDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createSimpleDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     *
     * @return Nuevo diálogo
     */
    Engine_util objutil;
    Spinner cmbproductos;
    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();


        View v = inflater.inflate(R.layout.inventariomovimiento, null);

        builder.setView(v);

        cmbproductos=(Spinner)v.findViewById(R.id.cmbproductoslista);
        EditText cantida=(EditText)v.findViewById(R.id.txtcantidad) ;
        TextView stock=(TextView) v.findViewById(R.id.txtstock) ;

        cargarProductoslista();

        Button signup = (Button) v.findViewById(R.id.crear_boton);
        Button signin = (Button) v.findViewById(R.id.entrar_boton);

        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Crear Cuenta...
                        dismiss();
                    }
                }
        );
        cmbproductos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        Producto currentLead = (Producto) parent.getItemAtPosition(position);
                        stock.setText(String.valueOf(currentLead.getStock()));

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        signin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Loguear...
                        BaseDatosEngine usdbh = new BaseDatosEngine();
                            usdbh = usdbh.open();
                            ContentValues Objdatos = new ContentValues();
                            Producto producto =(Producto) cmbproductos.getSelectedItem();
                            stock.setText(String.valueOf(producto.getStock()));
                            Double s=0.0,c=0.0;
                            s=Double.valueOf( stock.getText().toString());
                            c=Double.valueOf( cantida.getText().toString());
                            Objdatos.put(EstructuraBD.CabeceraProductos.stock, s+c);

                            String where2 = "_id='" + producto.get_id()
                                    + "'";
                                usdbh.ActualizarTablaStock(Objdatos, where2);
                                usdbh.close();

                        stock.setText("0");
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues Objdatosop = new ContentValues();
                        Objdatosop.put(EstructuraBD.CabeceraOperaciones.cantidad, c);
                        Objdatosop.put(EstructuraBD.CabeceraOperaciones.codproducto, producto.getCodproducto());
                        Objdatosop.put(EstructuraBD.CabeceraOperaciones.tipooperacion, "E");
                        Objdatosop.put(EstructuraBD.CabeceraOperaciones.fecha, dateFormat.format(date));
                        Objdatosop.put(EstructuraBD.CabeceraOperaciones.codlocal, "BODEGA");
                        Objdatosop.put(EstructuraBD.CabeceraOperaciones.stock, s + c);
                        Objdatosop.put(EstructuraBD.CabeceraOperaciones.estado, "E");
                        usdbh = usdbh.open();
                        usdbh.insertardatosproductosOperaciones(Objdatosop);
                        usdbh.close();
                        MainInventario ma = new MainInventario();
                        ma.cargarlistaproductos();
                        cargarProductoslista();
                    }
                }

        );

        return builder.create();
    }
    public void cargarProductoslista(){
    ArrayList<Category> category = new ArrayList<Category>();
    String where="";
    String opcion = "";
    String[] args = new String[]{};
    where = "where 1=1 and Estado ='A'";
    objutil= new Engine_util();
    Cursor cursor = objutil.Listarproductos(args, opcion, where);
    ArrayList<Producto> listaproducto = new ArrayList<>();
    if (cursor.moveToFirst()) {

        do {
            Drawable imagenpne=null;
            Producto item = new Producto();
            item.set_id(cursor.getInt(0));
            item.setCodproducto(cursor.getString(1));
            item.setName(cursor.getString(2));
            item.setPvp(cursor.getDouble(3));
            item.setCategoria(cursor.getString(4));
            item.setStock(cursor.getDouble(5));
            item.setDescripcion(cursor.getString(6));
            item.setCodigosecundario(cursor.getString(7));
            item.setEstado(cursor.getString(8));
            listaproducto.add(item);

        } while (cursor.moveToNext());
    }
    ArrayAdapter<Producto> adaptador;
    adaptador = new ArrayAdapter<Producto>(getActivity(), R.layout.spinner_personalizado, listaproducto);

    cmbproductos.setAdapter(adaptador);
}
}
