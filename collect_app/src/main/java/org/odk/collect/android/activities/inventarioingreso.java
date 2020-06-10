package org.odk.collect.android.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.database.BaseDatosEngine.BaseDatosEngine;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.Producto;
import org.odk.collect.android.database.BaseDatosEngine.EstructuraBD;
import org.odk.collect.android.logic.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class inventarioingreso extends AppCompatActivity {
    Engine_util objutil;
    int idman=0;
    String codactualizar="";
    Spinner cmbproductos;
    Button signin;
    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private TextView tvCodigoLeido;
    JSONArray respJSON= new JSONArray();
    ProgressDialog progress;
    private boolean reintentarObtenerIntentZxing;
    private Intent intentScan ;
    TextView stock;
    EditText cantida;
    private static final String BS_PACKAGE = "com.google.zxing.client.android";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventarioingreso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        verificarYPedirPermisosDeCamara();

        cmbproductos=(Spinner)findViewById(R.id.cmbproductoslista);
        cantida=(EditText)findViewById(R.id.txtcantidad) ;
        stock=(TextView) findViewById(R.id.txtstock) ;
        Button signup = (Button) findViewById(R.id.crear_boton);
        signin = (Button) findViewById(R.id.entrar_boton);
        ImageButton scam = (ImageButton) findViewById(R.id.imgBtnScan);
        scam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!permisoCamaraConcedido) {
                    Toast.makeText(v.getContext(), "Por favor permite que la app acceda a la cámara", Toast.LENGTH_SHORT).show();
                    permisoSolicitadoDesdeBoton = true;
                    verificarYPedirPermisosDeCamara();
                    return;
                }
                escanear();
            }
        });
        cargarProductoslista(1);
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Crear Cuenta...
                        startActivity(new Intent(getApplication(), ListProductDes.class));
                        finish();

                    }
                }
        );
        cmbproductos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        Producto currentLead = (Producto) parent.getItemAtPosition(position);
                        stock.setText(String.valueOf(currentLead.getStock()));
                        signin.setEnabled(true);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        signin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!cantida.getText().equals("") || !cantida.getText().equals("0")) {
                            int pos=cmbproductos.getSelectedItemPosition();
                            Producto prod=(Producto) cmbproductos.getSelectedItem();
                            cargarinformacionllenar(prod,pos);
                        }else {

                            Toast.makeText(inventarioingreso.this, "La cantidad no puede ser vacia o 0..!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );

    }
    public void cargarinformacionllenar(Producto producto,int pos){
       if(producto.getCodproducto()!="") {
           BaseDatosEngine usdbh = new BaseDatosEngine();
           usdbh = usdbh.open();
           ContentValues Objdatos = new ContentValues();


           stock.setText(String.valueOf(producto.getStock()));
           Double s = 0.0, c = 0.0;
           s = Double.valueOf(stock.getText().toString());
           c = Double.valueOf(cantida.getText().toString());
           Objdatos.put(EstructuraBD.CabeceraProductos.stock, s + c);

           String where2 = "_id='" + producto.get_id()
                   + "'";
           usdbh.ActualizarTablaStock(Objdatos, where2);
           usdbh.close();

           cantida.setText("0");
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
           stock.setText("0");
           TareaWSInsertar enviarmovimientos = new TareaWSInsertar();
           enviarmovimientos.execute(c.toString(), producto.getDescripcion(), obterImeid(), producto.getCodproducto());
           cargarProductoslista(pos);
       }
    }
    public static int obtenerPosicionItem(Spinner spinner, Producto producto) {
        //Creamos la variable posicion y lo inicializamos en 0
        int posicion = 0;
        //Recorre el spinner en busca del ítem que coincida con el parametro `String fruta`
        //que lo pasaremos posteriormente
        for (int i = 0; i < spinner.getCount(); i++) {
            //Almacena la posición del ítem que coincida con la búsqueda
            if (spinner.getItemAtPosition(i).equals(producto)) {
                posicion = i;
            }
        }
        //Devuelve un valor entero (si encontro una coincidencia devuelve la
        // posición 0 o N, de lo contrario devuelve 0 = posición inicial)
        return posicion;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    String opcion = "";
                    String[] args = new String[]{};
                    String where = "where 1=1 and codproducto ='"+codigo+"'";
                    objutil = new Engine_util();
                    Cursor cursor = objutil.Listarproductoscombo(args, opcion, where);
                    Producto prod=new Producto();
                    if (cursor.moveToFirst()) {

                        do {
                            Drawable imagenpne = null;
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
                            prod=item;

                        } while (cursor.moveToNext());
                    }
                    if(prod.getCodproducto()!=null) {
                        stock.setText(String.valueOf(prod.getStock()));
                        obtenerPosicionItem(cmbproductos, prod);
                        cmbproductos.setSelection(obtenerPosicionItem(cmbproductos, prod));

                    }else {
                        Toast.makeText(this, "No Existe el Producto", Toast.LENGTH_SHORT).show();
                        signin.setEnabled(false);

                    }

                }else {
                    Toast.makeText(this, "No Existe el Producto", Toast.LENGTH_SHORT).show();
                    signin.setEnabled(false);

                }
            }else {
                Toast.makeText(this, "No Existe el Producto", Toast.LENGTH_SHORT).show();
                signin.setEnabled(false);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Escanear directamten solo si fue pedido desde el botón
                    if (permisoSolicitadoDesdeBoton) {
                        escanear();
                    }
                    permisoCamaraConcedido = true;
                } else {
                    permisoDeCamaraDenegado();
                }
                break;
        }
    }
    @SuppressLint("MissingPermission")
    public String  obterImeid(){
        String myIMEI = "0";
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null){
            myIMEI = mTelephony.getDeviceId();
        }
        return myIMEI;
    }

    private void escanear() {
        Intent i = new Intent(this, ActivityEscanear.class);
        startActivityForResult(i, CODIGO_INTENT);
    }
    private void verificarYPedirPermisosDeCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoCamaraConcedido = true;
        } else {
            // Si no, pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CODIGO_PERMISOS_CAMARA);
        }
    }
    private void permisoDeCamaraDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(this, "No puedes escanear si no das permiso", Toast.LENGTH_SHORT).show();
    }
    public void cargarProductoslista(int pos){

            ArrayList<Category> category = new ArrayList<Category>();
            String where = "";
            String opcion = "";
            String[] args = new String[]{};
            where = "where 1=1 and Estado ='A'";
            objutil = new Engine_util();
            Cursor cursor = objutil.Listarproductoscombo(args, opcion, where);
            ArrayList<Producto> listaproducto = new ArrayList<>();
            if (cursor.moveToFirst()) {

                do {
                    Drawable imagenpne = null;
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
            adaptador = new ArrayAdapter<Producto>(this, R.layout.spinner_personalizado, listaproducto);

            cmbproductos.setAdapter(adaptador);
            cmbproductos.setSelection(pos);


    }
    private class TareaWSInsertar extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new
                    HttpPost("http://geomardis6728.cloudapp.net/msNutri/api/Task/UpdateProductNutri");

            post.setHeader("content-type", "application/json");

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();

                dato.put("cantidad", params[0]);
                dato.put("idmae", params[1]);
                idman=Integer.valueOf(params[1]);
                codactualizar=params[3];
                dato.put("imei", params[2]);

                StringEntity entity = new StringEntity(dato.toString());
                post.setEntity(entity);

                HttpResponse resp = httpClient.execute(post);
                String respStr = EntityUtils.toString(resp.getEntity());

                if(!respStr.equals("1"))
                    resul = false;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
               // lblResultado.setText("Insertado OK.");
                BaseDatosEngine usdbh = new BaseDatosEngine();
                usdbh = usdbh.open();
                ContentValues Objdatos = new ContentValues();

                Objdatos.put(EstructuraBD.CabeceraOperaciones.estado, "F");


                String where2 = "codlocal='BODEGA' and codproducto='"+codactualizar+"'";
                usdbh.ActualizarTablaStockOperaciones(Objdatos, where2);
                usdbh.close();
                Toast.makeText(inventarioingreso.this, "ok insert", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
