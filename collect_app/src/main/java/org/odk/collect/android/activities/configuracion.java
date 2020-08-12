package org.odk.collect.android.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.BaseDatosEngine.BaseDatosEngine;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.Capania;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.ConfiguracionDB;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.ConfiguracionSession;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.CuentaSession;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.Producto;
import org.odk.collect.android.database.BaseDatosEngine.EstructuraBD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class configuracion extends AppCompatActivity {
    Spinner cmbcuentacamp;
    ProgressDialog progress;
    RadioButton radnombre;
    RadioButton radcodigo;
    JSONArray respJSON= new JSONArray();
    JSONArray respJSONPr= new JSONArray();
    JSONArray respJSONDs= new JSONArray();
    final CuentaSession objcuentaSession= new CuentaSession();
    final ConfiguracionSession objconfiguracionSession= new ConfiguracionSession();
    String Estado="";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        cmbcuentacamp=(Spinner) findViewById(R.id.cmbcampaniaCL);
        Button btnCargarCodigosCL=(Button)findViewById(R.id.btncargarlocalesCL);
        Button btncargarlocalcuenta=(Button)findViewById(R.id.btncargarlocalcuenta);
        Button btnjustificar=(Button)findViewById(R.id.btnjustificar);
        radnombre = (RadioButton) findViewById(R.id.radnombre);
        radcodigo = (RadioButton) findViewById(R.id.radcodigo);
        TextView idtext =(TextView) findViewById(R.id.textid);
        idtext.setText("Nutri-Pro 27072020");
        TextView TextversionImeid = (TextView) findViewById(R.id.txtIdVersionDivice);
        ImageButton copibutton = (ImageButton) findViewById(R.id.IdbtnCopi);
        TextversionImeid.setText(obterImeid());
        btnjustificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               alertDialogDemo();

            }
        });
        copibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager myClipboard = myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String text = TextversionImeid.getText().toString();
                ClipData clip = ClipData.newPlainText("text", text );
                myClipboard.setPrimaryClip(clip);
                Toast.makeText(getApplication(),
                        "ID fue copiado", Toast.LENGTH_SHORT).show();

            }
        });
        BaseDatosEngine usdbh = new BaseDatosEngine();
        usdbh = usdbh.open();
        Cursor c= usdbh.ConfiguracionLista();
        ConfiguracionDB actualconf= new ConfiguracionDB();
        CargarListacuenta();
        if(c!=null) {
            if (c.moveToFirst()) {
                do {
                    actualconf = new ConfiguracionDB();
                    actualconf.setId_cuenta(c.getString(1));
                    actualconf.setId_campania(c.getString(2));
                    actualconf.setFormaBusqueda(c.getString(3));
                    actualconf.setEstado(c.getString(4));
                    Cursor AccountCanpa = usdbh.ConfiguracionCanpania(actualconf.getId_cuenta(),actualconf.getId_campania());
                    if(AccountCanpa!=null) {
                        if (AccountCanpa.moveToFirst()) {
                            do {
                                Capania objcampania = new Capania();
                                objcampania.setID(AccountCanpa.getString(0));
                                objcampania.setIdAccount(AccountCanpa.getString(1));
                                objcampania.setAccountNombre(AccountCanpa.getString(2));
                                objcampania.setIdCampania(AccountCanpa.getString(3));
                                objcampania.setCampaniaNombre(AccountCanpa.getString(4));

                                int pos= getIndexSpinner(cmbcuentacamp,objcampania);
                                cmbcuentacamp.setSelection(pos);
                                if( actualconf.getFormaBusqueda().toString().equalsIgnoreCase("C")){
                                    radcodigo.setChecked(true);
                                    radnombre.setChecked(false);
                                }else{
                                    radcodigo.setChecked(false);
                                    radnombre.setChecked(true);
                                }
                            } while (AccountCanpa.moveToNext());
                        }
                    }
                } while (c.moveToNext());
            }
        }
        usdbh.close();



        btnCargarCodigosCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(getApplication().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // DatosConsulta();
                    CargarCampaniasCuentas cargarCampaniasCuentas = new CargarCampaniasCuentas(v.getContext());
                    cargarCampaniasCuentas.execute();

                } else {
                    Toast.makeText(getApplication(),
                            R.string.no_connection, Toast.LENGTH_SHORT).show();

                }

            }

        });
        cmbcuentacamp.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        Capania currentLead = (Capania) parent.getItemAtPosition(position);
                        objcuentaSession.setCu_ID("");
                        objcuentaSession.setCu_idAccount("");
                        objcuentaSession.setCu_AccountNombre("");
                        objcuentaSession.setCu_idcampania("");
                        objcuentaSession.setCu_CampaniaNombre("");

                        objcuentaSession.setCu_ID(currentLead.getID());
                        objcuentaSession.setCu_idAccount(currentLead.getIdAccount());
                        objcuentaSession.setCu_AccountNombre(currentLead.getAccountNombre());
                        objcuentaSession.setCu_idcampania(currentLead.getIdCampania());
                        objcuentaSession.setCu_CampaniaNombre(currentLead.getCampaniaNombre());

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        btncargarlocalcuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(objcuentaSession.getCu_ID()!=""){

                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(getApplication().CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // DatosConsulta();
                        BaseDatosEngine usdbh = new BaseDatosEngine();
                        usdbh = usdbh.open();
                        usdbh.EliminarRegistros();
                        usdbh.EliminarRegistrosCodigos();
                        usdbh.EliminarRegistrosProductos();
                        usdbh.EliminarRegistrosProductosPromo();
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(c.getTime());
                        if(!formattedDate.equals(objconfiguracionSession.getCnf_fechacargaruta())){
                            usdbh.EliminarRegistrosProductosOrde();
                        }

                        usdbh.close();
                        CargarLocales fetchJsonTask = new CargarLocales(v.getContext());
                        fetchJsonTask.execute();
                    } else {
                        Toast.makeText(getApplication(),
                                R.string.no_connection, Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(getApplication(),
                            "Seleccione una cuenta", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    void alertDialogDemo() {
        // get alert_dialog.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.alert_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        String[] myTaskParams = { userInput.getText().toString(),obterImeid()};
                        enviojustificacion fetchJsonTask = new enviojustificacion();
                        fetchJsonTask.execute(myTaskParams);
                        Toast.makeText(getApplicationContext(), "Entered: "+userInput.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    public void CargarListacuenta(){
        BaseDatosEngine usdbh = new BaseDatosEngine();
        usdbh = usdbh.open();
        Cursor cursor = usdbh.listarCanpanias();
        Capania objcampania= new Capania();
        List<Capania> listOBJ= new ArrayList<Capania>();
        if(cursor!=null) {
            if (cursor.moveToFirst()) {

                do {

                    objcampania = new Capania();
                    objcampania.setID(cursor.getString(0));
                    objcampania.setIdAccount(cursor.getString(1));
                    objcampania.setAccountNombre(cursor.getString(2));
                    objcampania.setIdCampania(cursor.getString(3));
                    objcampania.setCampaniaNombre(cursor.getString(4));

                    listOBJ.add(objcampania);

                } while (cursor.moveToNext());


            }

            ArrayAdapter<Capania> adaptador;
            adaptador = new ArrayAdapter<Capania>(getApplication(), R.layout.spinner_personalizado, listOBJ);
            cmbcuentacamp = (Spinner) findViewById(R.id.cmbcampaniaCL);
            cmbcuentacamp.setAdapter(adaptador);
        }
    }
    public static int getIndexSpinner(Spinner spinner, Capania myString)
    {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString.toString())) {
                index = i;
            }
        }
        return index;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("MissingPermission")
    public String obterImeid() {
        final String androidIdName = Settings.Secure.ANDROID_ID;

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNo="";
         String  myIMEI = mTelephony.getDeviceId();

        if (myIMEI == null) {
            SubscriptionManager subsManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

            List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

            if (subsList!=null) {
                for (SubscriptionInfo subsInfo : subsList) {
                    if (subsInfo != null) {
                        simSerialNo  = subsInfo.getIccId();
                    }
                }

            }
            if(simSerialNo.equals(""))
                simSerialNo = "358240051111110";
            myIMEI=simSerialNo;
        }

        if (myIMEI == null) {
            myIMEI = Settings.Secure.getString(Collect.getInstance().getApplicationContext().getContentResolver(), androidIdName);
        }
     /* String  deviceId = new PropertyManager(Collect.getInstance().getApplicationContext())
                .getSingularProperty(PropertyManager.withUri(PropertyManager.PROPMGR_DEVICE_ID));*/
        return myIMEI;

    }
    //clase para cargar campania
    public class CargarCampaniasCuentas extends AsyncTask<Void,Void,String> {
        public CargarCampaniasCuentas(Context context) {
            this.context = context;
        }
        private ProgressDialog progressDialog;
        private  Context context;
        @Override
        protected void onPreExecute() {
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
            }catch (Exception ex){
                //Log.e(BonusPackHelper.LOG_TAG, "Error ", ex);
            }

        }
        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Construct the URL somehow
                String url1 = "http://geomardis6728.cloudapp.net/msbancoGuayaquil/api/Canpania";
                URL url = new URL(url1);

                // Create the request to MuslimSalat.com, and open the connection

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                ///pasas paretros

                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();

                //JSONArray objetos = new JSONArray(result);
                respJSON = new JSONArray(jsonStr);
            } catch (IOException e) {
                //Log.e(BonusPackHelper.LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        //Log.e(BonusPackHelper.LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return jsonStr;
        }
        @Override
        protected void onPostExecute(String jsonString) {
            progressDialog.dismiss();
            progress=new ProgressDialog(context);
            progress.setMessage("Descargando Cuentas....");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);
            progress.setProgress(0);
            progress.setMax(respJSON.length());
            progress.show();
            final int totalProgressTime = respJSON.length();
            final Thread t = new Thread() {
                @Override
                public void run() {
                    int jumpTime = 0;
                    BaseDatosEngine usdbh = new BaseDatosEngine();
                    usdbh = usdbh.open();
                    usdbh.EliminarRegistrosCampania();
                    while(jumpTime < totalProgressTime) {
                        try {
                            JSONObject obj = respJSON.getJSONObject(jumpTime);
                            String id = obj.getString("id");
                            String name = obj.getString("name");
                            String idAccount = obj.getString("idAccount");
                            String accountName = obj.getString("accountName");
                            usdbh = usdbh.open();
                            try {
                                ContentValues Objdatos = new ContentValues();
                                Objdatos.put("ID", jumpTime);
                                Objdatos.put(EstructuraBD.CabecerasCampanias.IdCampania, id);
                                Objdatos.put(EstructuraBD.CabecerasCampanias.idAccount, idAccount);
                                Objdatos.put(EstructuraBD.CabecerasCampanias.AccountNombre, accountName.toUpperCase());
                                Objdatos.put(EstructuraBD.CabecerasCampanias.CampaniaNombre, name.toUpperCase());
                                usdbh.insertardatosCampania(Objdatos);
                                usdbh.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jumpTime += 1;
                            progress.setProgress(jumpTime);
                            sleep(25);
                        }
                        catch (InterruptedException e) {
                            Log.e("Error Carga",e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    progress.dismiss();
                }
            };
            t.start();
            try {
                t.join();
                CargarListacuenta();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    //clase cargar locales
    public   class CargarLocales extends AsyncTask<Void, Void, String> {
        private  ProgressDialog progressDialog;
        private  Context context;

        public CargarLocales(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // set up progress dialog
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");

                // show it
                progressDialog.show();
            }catch (Exception ex){
                //Log.e(BonusPackHelper.LOG_TAG, "Error ", ex);
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Construct the URL somehow
                String Idaccount =objcuentaSession.getCu_idAccount();
                String Idcampania="";
                String url1 = "http://geomardis6728.cloudapp.net/msBGE/api/Task?idAccount="+Idaccount+"&Imeid="+obterImeid();
                URL url = new URL(url1);

                // Create the request to MuslimSalat.com, and open the connection

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                jsonStr = buffer.toString();


                respJSON = new JSONArray(jsonStr);
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            progressDialog.dismiss();
            progress=new ProgressDialog(context);
            progress.setMessage("Descargando Locales....");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);

            progress.setProgress(0);
            progress.setMax(respJSON.length());
            progress.show();

            final int totalProgressTime = respJSON.length();

            final Thread t = new Thread() {
                @Override
                public void run() {
                    int jumpTime = 0;
                    while(jumpTime < totalProgressTime) {
                        String formattedDate="";
                        try {

                            JSONObject obj = respJSON.getJSONObject(jumpTime);
                            String id = obj.getString("id");
                            String idAccount = obj.getString("idAccount");
                            String externalCode = obj.getString("externalCode");
                            String code = obj.getString("code");
                            String name = obj.getString("name");
                            String mainStreet = obj.getString("mainStreet");
                            String neighborhood = obj.getString("neighborhood");
                            String reference = obj.getString("reference");
                            String propietario = obj.getString("propietario");
                            String uriformulario = "";
                            String idProvince = obj.getString("idProvince");
                            String idDistrict = obj.getString("idDistrict");
                            String idParish = obj.getString("idParish");
                            String rutaaggregate = obj.getString("rutaaggregate");
                            String imeI_ID = obj.getString("imeI_ID");
                            String LatitudeBranch = obj.getString("latitudeBranch");
                            String LenghtBranch = obj.getString("lenghtBranch");
                            String celular = obj.getString("celular");
                            String TypeBusiness  = obj.getString("typeBusiness");
                            String cedula=obj.getString("cedula");
                            //storeaudit
                            String ESTADOAGGREGATE=obj.getString("estadoaggregate");
                            String Link=obj.getString("link");
                            //censo
                            //String ESTADOAGGREGATE="S";
                            BaseDatosEngine usdbh = new BaseDatosEngine();

                            if(jumpTime==0){

                                if (radnombre.isChecked()){
                                    Estado="N";
                                }
                                else{
                                    Estado="C";

                                }
                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => "+c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                 formattedDate = df.format(c.getTime());
                                // formattedDate have current date/time


                                usdbh = usdbh.open();
                                usdbh.EliminarRegistrosConfiguracion();
                                ContentValues Objdatos = new ContentValues();
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.Id_cuenta, idAccount);
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.Id_campania, objcuentaSession.getCu_idcampania());
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.FormaBusqueda,Estado );
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.FechaCarga,formattedDate );
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.Estado, "A");
                                usdbh.insertardatosConfiguracion(Objdatos);
                                usdbh.close();
                            }
                            try {
                                usdbh = usdbh.open();
                                ContentValues Objdatos = new ContentValues();
                                Objdatos.put(EstructuraBD.CabecerasEngine.idbranch, id);
                                Objdatos.put(EstructuraBD.CabecerasEngine.idAccount, idAccount);
                                Objdatos.put(EstructuraBD.CabecerasEngine.externalCode, externalCode.toUpperCase());
                                Objdatos.put(EstructuraBD.CabecerasEngine.code, code.toUpperCase());
                                Objdatos.put(EstructuraBD.CabecerasEngine.name, name.toUpperCase());
                                Objdatos.put(EstructuraBD.CabecerasEngine.mainStreet, mainStreet);
                                Objdatos.put(EstructuraBD.CabecerasEngine.neighborhood, neighborhood);
                                Objdatos.put(EstructuraBD.CabecerasEngine.reference, reference);
                                Objdatos.put(EstructuraBD.CabecerasEngine.propietario, propietario);
                                Objdatos.put(EstructuraBD.CabecerasEngine.uriformulario, uriformulario);
                                Objdatos.put(EstructuraBD.CabecerasEngine.idprovince, idProvince);
                                Objdatos.put(EstructuraBD.CabecerasEngine.iddistrict, idDistrict);
                                Objdatos.put(EstructuraBD.CabecerasEngine.idParish, idParish);
                                Objdatos.put(EstructuraBD.CabecerasEngine.rutaaggregate, rutaaggregate=="null"?"0":rutaaggregate);
                                Objdatos.put(EstructuraBD.CabecerasEngine.imeI_ID, imeI_ID);
                                Objdatos.put(EstructuraBD.CabecerasEngine.LatitudeBranch, LatitudeBranch);
                                Objdatos.put(EstructuraBD.CabecerasEngine.LenghtBranch, LenghtBranch);
                                Objdatos.put(EstructuraBD.CabecerasEngine.Celular, celular);
                                Objdatos.put(EstructuraBD.CabecerasEngine.TypeBusiness, TypeBusiness);
                                Objdatos.put(EstructuraBD.CabecerasEngine.Cedula, cedula);
                                Objdatos.put(EstructuraBD.CabecerasEngine.ESTADOAGGREGATE,ESTADOAGGREGATE);
                                Objdatos.put(EstructuraBD.CabecerasEngine.Foto_Exterior,Link);
                                usdbh.insertardatos(Objdatos);
                                usdbh.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jumpTime += 1;
                            progress.setProgress(jumpTime);
                            sleep(25);
                        }
                        catch (InterruptedException e) {
                            Log.e("Error Carga",e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = df.format(c.getTime());
                    objconfiguracionSession.setCnf_idAccount(objcuentaSession.getCu_idAccount());
                    objconfiguracionSession.setCnf_idcampania(objcuentaSession.getCu_idcampania());
                    objconfiguracionSession.setCnf_AccountNombre(objcuentaSession.getCu_AccountNombre());
                    objconfiguracionSession.setCnf_CampaniaNombre(objcuentaSession.getCu_CampaniaNombre());
                    objconfiguracionSession.setCnf_fechacargaruta(formattedDate);
                    objconfiguracionSession.setCnf_factorbusqueda(Estado);


                    progress.dismiss();
                    if(totalProgressTime>0){
                        if(totalProgressTime==jumpTime){
                            CargarProductos cargarProductos = new CargarProductos(context);
                            cargarProductos.execute();
                        }
                    }

                }
            };
            t.start();




        }

    }


    ///CLASES CARGAR PRRODUCTOSD E INVENTARIO

    public   class CargarProductos extends AsyncTask<Void, Void, String> {
        private  ProgressDialog progressDialog;
        private  Context context;

        public CargarProductos(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // set up progress dialog
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");

                // show it
                progressDialog.show();
            }catch (Exception ex){
                //Log.e(BonusPackHelper.LOG_TAG, "Error ", ex);
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Construct the URL somehow
                String Idaccount =objcuentaSession.getCu_idAccount();
                String Idcampania="";
                String url1 = "http://geomardis6728.cloudapp.net/msNutri/api/Task/ProductNutri";
                URL url = new URL(url1);

                // Create the request to MuslimSalat.com, and open the connection

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                jsonStr = buffer.toString();


                respJSONPr = new JSONArray(jsonStr);
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            progress=new ProgressDialog(context);
            progress.setMessage("Descargando Productos....");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);

            progress.setProgress(0);
            progress.setMax(respJSONPr.length());
            progress.show();
            final int totalProgressTime = respJSONPr.length();
            final Thread t = new Thread() {
                @Override
                public void run() {
                    int jumpTime = 0;
                    int cod=1;
                    while(jumpTime < totalProgressTime) {
                        try {
                            JSONObject obj = respJSONPr.getJSONObject(jumpTime);
                            String id = obj.getString("id");
                            String id_mae = obj.getString("id_mae");
                            String name = obj.getString("name");
                            String bardcore = obj.getString("bardcore");
                            String id_cat_mae = obj.getString("id_cat_mae");
                            String des_categoria = obj.getString("des_categoria");
                            String price = obj.getString("price");
                            String priceIVA = obj.getString("priceIVA");
                            String iva = obj.getString("iva");
                            BaseDatosEngine usdbh = new BaseDatosEngine();

                            JSONArray promocion = new JSONArray();
                            promocion=obj.getJSONArray("promo");
                            int index=0;
                            while (index<promocion.length()){
                                JSONObject objpromo = promocion.getJSONObject(index);
                                String idmaestro=objpromo.getString("idMae");
                                String descripcion=objpromo.getString("descripcion");
                                String cantidad=objpromo.getString("cantidad");

                                ContentValues Objdatospromo = new ContentValues();
                                Objdatospromo.put(EstructuraBD.CabeceraPromo.idMae, idmaestro);
                                Objdatospromo.put(EstructuraBD.CabeceraPromo.descripcion, descripcion);
                                Objdatospromo.put(EstructuraBD.CabeceraPromo.cantidad, cantidad);
                                Objdatospromo.put(EstructuraBD.CabeceraPromo.idproductopromo, id_mae);
                                usdbh = usdbh.open();
                                usdbh.insertardatosPromo(Objdatospromo);
                                usdbh.close();
                                index++;
                            }


                            //censo
                            //String ESTADOAGGREGATE="S";


                            try {
                                usdbh = usdbh.open();
                                ContentValues Objdatos = new ContentValues();
                                Objdatos.put(EstructuraBD.CabeceraProductos.codigosecundario, id);
                                Objdatos.put(EstructuraBD.CabeceraProductos.codproducto, bardcore);
                                Objdatos.put(EstructuraBD.CabeceraProductos.descripcion, id_mae);
                                Objdatos.put(EstructuraBD.CabeceraProductos.id_cat_mae, id_cat_mae);
                                Objdatos.put(EstructuraBD.CabeceraProductos.des_categoria, des_categoria);
                                Objdatos.put(EstructuraBD.CabeceraProductos.estado, "A");
                                Objdatos.put(EstructuraBD.CabeceraProductos.pvp, "0");
                                Objdatos.put(EstructuraBD.CabeceraProductos.stock, "0");
                                Objdatos.put(EstructuraBD.CabeceraProductos.name, name);
                                Objdatos.put(EstructuraBD.CabeceraProductos.categoria, "cant_sku"+id_mae);
                                Objdatos.put(EstructuraBD.CabeceraProductos.price, price);
                                Objdatos.put(EstructuraBD.CabeceraProductos.priceIVA, priceIVA);
                                Objdatos.put(EstructuraBD.CabeceraProductos.iva, iva);
                                usdbh.insertardatosproductos(Objdatos);
                                usdbh.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jumpTime += 1;
                            cod++;
                            progress.setProgress(jumpTime);
                            sleep(25);
                        }
                        catch (InterruptedException e) {
                            Log.e("Error Carga",e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }



                    progress.dismiss();
                    if(totalProgressTime>0){
                        if(totalProgressTime==jumpTime){
                            CargarProductosStock cargarProductosStock = new CargarProductosStock(context);
                            cargarProductosStock.execute();
                        }
                    }

                }
            };
            t.start();


        }
    }

///Clase actualizar productos stock

    public   class CargarProductosStock extends AsyncTask<Void, Void, String> {
        private  ProgressDialog progressDialog;
        private  Context context;

        public CargarProductosStock(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // set up progress dialog
            try {

                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");

                // show it
                progressDialog.show();
            }catch (Exception ex){
                //Log.e(BonusPackHelper.LOG_TAG, "Error ", ex);
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Construct the URL somehow
                String Idaccount =objcuentaSession.getCu_idAccount();//"85024910-FC12-4DD8-AE58-761BF972DEB7";
                String Idcampania="";
                String url1 = "http://geomardis6728.cloudapp.net/msNutri/api/Task/ProductNutriDisponibilidad?idAccount="+Idaccount+"&Imei="+obterImeid();//"454";
                URL url = new URL(url1);

                // Create the request to MuslimSalat.com, and open the connection

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                jsonStr = buffer.toString();


                respJSONDs = new JSONArray(jsonStr);
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            progress=new ProgressDialog(context);
            progress.setMessage("Descargando Productos Stock....");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);

            progress.setProgress(0);
            progress.setMax(respJSONDs.length());
            progress.show();
            final int totalProgressTime = respJSONDs.length();
            final Thread t = new Thread() {
                @Override
                public void run() {
                    int jumpTime = 0;
                    while(jumpTime < totalProgressTime) {
                        try {
                            JSONObject obj = respJSONDs.getJSONObject(jumpTime);

                            String bardcore = obj.getString("bardCode");
                            String cantidad = obj.getString("cant");


                            //censo
                            //String ESTADOAGGREGATE="S";
                            BaseDatosEngine usdbh = new BaseDatosEngine();


                            try {
                                usdbh = usdbh.open();
                                ContentValues Objdatos = new ContentValues();
                                Objdatos.put(EstructuraBD.CabeceraProductos.stock,cantidad );
                                String where2 = "codproducto='" + bardcore
                                        + "'";
                                usdbh.ActualizarTablaStock(Objdatos, where2);
                                usdbh.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jumpTime += 1;
                            progress.setProgress(jumpTime);
                            sleep(25);
                        }
                        catch (InterruptedException e) {
                            Log.e("Error Carga",e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }



                    progress.dismiss();

                        if(totalProgressTime==jumpTime){
                            startActivity(new Intent(context, principal.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            finish();
                        }

                }
            };
            t.start();

               // t.join();


        }
    }

}


