package org.odk.collect.android.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.BuildConfig;

import android.os.IBinder;
import android.preference.PreferenceManager;
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

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.viewmodels.FormDownloadListViewModel;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.database.BaseDatosEngine.BaseDatosEngine;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.Capania;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.ConfiguracionDB;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.ConfiguracionSession;
import org.odk.collect.android.database.BaseDatosEngine.Entidades.CuentaSession;
import org.odk.collect.android.database.BaseDatosEngine.EstructuraBD;
import org.odk.collect.android.listeners.FormListDownloaderListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.AuthDialogUtility;
import org.odk.collect.android.utilities.DialogUtils;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.android.utilities.WebCredentialsUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_AUTH_REQUIRED;

import timber.log.Timber;

public class configuracion extends  AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    Spinner cmbcuentacamp;
    @Inject
    WebCredentialsUtils webCredentialsUtils;
    ProgressDialog progress;
    AlertDialog alert = null;
    private ProgressDialog cancelDialog;
    LocationManager locationManager;
    RadioButton radnombre;
    RadioButton radcodigo;
    public static final String DL_ERROR_MSG = "dlerrormessage";
    private DownloadFormListTask downloadFormListTask;
    JSONArray respJSON = new JSONArray();
    JSONArray respJSONPr = new JSONArray();
    private ProgressDialog progressDialog;
    JSONArray respJSONDs = new JSONArray();
    final CuentaSession objcuentaSession = new CuentaSession();
    final ConfiguracionSession objconfiguracionSession = new ConfiguracionSession();
    String Estado = "";




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)





    private static final String TAG = configuracion.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL = 600000; // Every 60 seconds.

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    private static final long FASTEST_UPDATE_INTERVAL = 600000; // Every 30 seconds

    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuracion);

        if (!checkPermissions()) {
            requestPermissions();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        deleteCache(this);

        viewModel = ViewModelProviders.of(this).get(FormDownloadListViewModel.class);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        }
        cmbcuentacamp = (Spinner) findViewById(R.id.cmbcampaniaCL);
        Button btnCargarCodigosCL = (Button) findViewById(R.id.btncargarlocalesCL);
        Button btncargarlocalcuenta = (Button) findViewById(R.id.btncargarlocalcuenta);
        Button btnjustificar = (Button) findViewById(R.id.btnjustificar);
        radnombre = (RadioButton) findViewById(R.id.radnombre);
        radcodigo = (RadioButton) findViewById(R.id.radcodigo);
        TextView idtext = (TextView) findViewById(R.id.textid);
        idtext.setText("Nutri-Pro 27072020");
        TextView TextversionImeid = (TextView) findViewById(R.id.txtIdVersionDivice);
        ImageButton copibutton = (ImageButton) findViewById(R.id.IdbtnCopi);
        TextversionImeid.setText(obterImeid());
        objconfiguracionSession.setCnf_imei(obterImeid());
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float battery = (level / (float) scale) * 100;
        objconfiguracionSession.setCnf_batteryStatus(batteryStatus + "%");
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
                ClipData clip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(clip);
                Toast.makeText(getApplication(),
                        "ID fue copiado", Toast.LENGTH_SHORT).show();

            }
        });
        BaseDatosEngine usdbh = new BaseDatosEngine();
        usdbh = usdbh.open();
        Cursor c = usdbh.ConfiguracionLista();
        ConfiguracionDB actualconf = new ConfiguracionDB();
        CargarListacuenta();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    actualconf = new ConfiguracionDB();
                    actualconf.setId_cuenta(c.getString(1));
                    actualconf.setId_campania(c.getString(2));
                    actualconf.setFormaBusqueda(c.getString(3));
                    actualconf.setEstado(c.getString(4));
                    Cursor AccountCanpa = usdbh.ConfiguracionCanpania(actualconf.getId_cuenta(), actualconf.getId_campania());
                    if (AccountCanpa != null) {
                        if (AccountCanpa.moveToFirst()) {
                            do {
                                Capania objcampania = new Capania();
                                objcampania.setID(AccountCanpa.getString(0));
                                objcampania.setIdAccount(AccountCanpa.getString(1));
                                objcampania.setAccountNombre(AccountCanpa.getString(2));
                                objcampania.setIdCampania(AccountCanpa.getString(3));
                                objcampania.setCampaniaNombre(AccountCanpa.getString(4));

                                int pos = getIndexSpinner(cmbcuentacamp, objcampania);
                                cmbcuentacamp.setSelection(pos);
                                if (actualconf.getFormaBusqueda().toString().equalsIgnoreCase("C")) {
                                    radcodigo.setChecked(true);
                                    radnombre.setChecked(false);
                                } else {
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
                        objcuentaSession.setCu_Formularios("");

                        objcuentaSession.setCu_ID(currentLead.getID());
                        objcuentaSession.setCu_idAccount(currentLead.getIdAccount());
                        objcuentaSession.setCu_AccountNombre(currentLead.getAccountNombre());
                        objcuentaSession.setCu_idcampania(currentLead.getIdCampania());
                        objcuentaSession.setCu_CampaniaNombre(currentLead.getCampaniaNombre());
                        objcuentaSession.setCu_Formularios(currentLead.getFormularios());

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        btncargarlocalcuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (objcuentaSession.getCu_ID() != "") {

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
                        if (!formattedDate.equals(objconfiguracionSession.getCnf_fechacargaruta())) {
                            usdbh.EliminarRegistrosProductosOrde();
                        }

                        usdbh.close();


                        try {
                            mFusedLocationClient.removeLocationUpdates(getPendingIntent());
                            Log.i(TAG, "Starting location updates");
                            Utils.setRequestingLocationUpdates(v.getContext(), true);
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
                        } catch (SecurityException e) {
                            Utils.setRequestingLocationUpdates(v.getContext(), false);
                            e.printStackTrace();
                        }

                        if(!objcuentaSession.getCu_Formularios().equals("")){
                            downloadFormList();
                            CargarLocales fetchJsonTask = new CargarLocales(v.getContext());
                            fetchJsonTask.execute();

                        }else{
                            Toast.makeText(configuracion.this, "Configurar formularios para su proyecto...!!!", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(getApplication(),
                                R.string.no_connection, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getApplication(),
                            "Seleccione una cuenta", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }



    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
                        String[] myTaskParams = {userInput.getText().toString(), obterImeid()};
                        enviojustificacion fetchJsonTask = new enviojustificacion();
                        fetchJsonTask.execute(myTaskParams);
                        Toast.makeText(getApplicationContext(), "Entered: " + userInput.getText().toString(), Toast.LENGTH_LONG).show();
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

    public void CargarListacuenta() {
        BaseDatosEngine usdbh = new BaseDatosEngine();
        usdbh = usdbh.open();
        Cursor cursor = usdbh.listarCanpanias();
        Capania objcampania = new Capania();
        List<Capania> listOBJ = new ArrayList<Capania>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                do {

                    objcampania = new Capania();
                    objcampania.setID(cursor.getString(0));
                    objcampania.setIdAccount(cursor.getString(1));
                    objcampania.setAccountNombre(cursor.getString(2));
                    objcampania.setIdCampania(cursor.getString(3));
                    objcampania.setCampaniaNombre(cursor.getString(4));
                    objcampania.setFormularios(cursor.getString(5));

                    listOBJ.add(objcampania);

                } while (cursor.moveToNext());


            }

            ArrayAdapter<Capania> adaptador;
            adaptador = new ArrayAdapter<Capania>(getApplication(), R.layout.spinner_personalizado, listOBJ);
            cmbcuentacamp = (Spinner) findViewById(R.id.cmbcampaniaCL);
            cmbcuentacamp.setAdapter(adaptador);
        }
    }

    public static int getIndexSpinner(Spinner spinner, Capania myString) {
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
        String simSerialNo = "";
        String myIMEI = mTelephony.getDeviceId();

        if (myIMEI == null) {
            SubscriptionManager subsManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

            List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

            if (subsList != null) {
                for (SubscriptionInfo subsInfo : subsList) {
                    if (subsInfo != null) {
                        simSerialNo = subsInfo.getIccId();
                    }
                }

            }
            if (simSerialNo.equals(""))
                simSerialNo = "358240051111110";
            myIMEI = simSerialNo;
        }

        if (myIMEI == null) {
            myIMEI = Settings.Secure.getString(Collect.getInstance().getApplicationContext().getContentResolver(), androidIdName);
        }
     /* String  deviceId = new PropertyManager(Collect.getInstance().getApplicationContext())
                .getSingularProperty(PropertyManager.withUri(PropertyManager.PROPMGR_DEVICE_ID));*/
        return myIMEI;

    }

    //clase para cargar campania
    public class CargarCampaniasCuentas extends AsyncTask<Void, Void, String> {
        public CargarCampaniasCuentas(Context context) {
            this.context = context;
        }

        private ProgressDialog progressDialog;
        private Context context;

        @Override
        protected void onPreExecute() {
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
            } catch (Exception ex) {
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
            } finally {
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
            progress = new ProgressDialog(context);
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
                    while (jumpTime < totalProgressTime) {
                        try {
                            JSONObject obj = respJSON.getJSONObject(jumpTime);
                            String id = obj.getString("id");
                            String name = obj.getString("name");
                            String idAccount = obj.getString("idAccount");
                            String accountName = obj.getString("accountName");
                            String Idform = obj.getString("idform");
                            usdbh = usdbh.open();
                            try {
                                ContentValues Objdatos = new ContentValues();
                                Objdatos.put("ID", jumpTime);
                                Objdatos.put(EstructuraBD.CabecerasCampanias.IdCampania, id);
                                Objdatos.put(EstructuraBD.CabecerasCampanias.idAccount, idAccount);
                                Objdatos.put(EstructuraBD.CabecerasCampanias.AccountNombre, accountName.toUpperCase());
                                Objdatos.put(EstructuraBD.CabecerasCampanias.CampaniaNombre, name.toUpperCase());
                                Objdatos.put(EstructuraBD.CabecerasCampanias.Idform, Idform.toUpperCase());
                                usdbh.insertardatosCampania(Objdatos);
                                usdbh.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jumpTime += 1;
                            progress.setProgress(jumpTime);
                            sleep(25);
                        } catch (InterruptedException e) {
                            Log.e("Error Carga", e.getMessage());
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
    public class CargarLocales extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        private Context context;

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
            } catch (Exception ex) {
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
                String Idaccount = objcuentaSession.getCu_idAccount();
                String Idcampania = "";
                String url1 = "http://geomardis6728.cloudapp.net/msBGE/api/Task?idAccount=" + Idaccount + "&Imeid=" + obterImeid();
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
            } finally {
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
            progress = new ProgressDialog(context);
            progress.setMessage("Descargando Locales....");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);

            progress.setProgress(0);
            progress.setMax(respJSON.length());
            progress.show();

            final int totalProgressTime = respJSON.length();

            final Thread t = new Thread() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void run() {
                    List<rutasupervisor> objenviarrutassuper = new ArrayList<>();
                    int jumpTime = 0;
                    while (jumpTime < totalProgressTime) {
                        String formattedDate = "";
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
                            String TypeBusiness = obj.getString("typeBusiness");
                            String cedula = obj.getString("cedula");
                            //storeaudit
                            String ESTADOAGGREGATE = obj.getString("estadoaggregate");
                            String Link = obj.getString("link");
                            //censo
                            //String ESTADOAGGREGATE="S";
                            BaseDatosEngine usdbh = new BaseDatosEngine();

                            if (jumpTime == 0) {

                                if (radnombre.isChecked()) {
                                    Estado = "N";
                                } else {
                                    Estado = "C";

                                }
                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => " + c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                formattedDate = df.format(c.getTime());
                                // formattedDate have current date/time


                                usdbh = usdbh.open();
                                usdbh.EliminarRegistrosConfiguracion();
                                ContentValues Objdatos = new ContentValues();
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.Id_cuenta, idAccount);
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.Id_campania, objcuentaSession.getCu_idcampania());
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.FormaBusqueda, Estado);
                                Objdatos.put(EstructuraBD.CabeceraConfiguracion.FechaCarga, formattedDate);
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
                                Objdatos.put(EstructuraBD.CabecerasEngine.rutaaggregate, rutaaggregate == "null" ? "0" : rutaaggregate);
                                Objdatos.put(EstructuraBD.CabecerasEngine.imeI_ID, imeI_ID);
                                Objdatos.put(EstructuraBD.CabecerasEngine.LatitudeBranch, LatitudeBranch);
                                Objdatos.put(EstructuraBD.CabecerasEngine.LenghtBranch, LenghtBranch);
                                Objdatos.put(EstructuraBD.CabecerasEngine.Celular, celular);
                                Objdatos.put(EstructuraBD.CabecerasEngine.TypeBusiness, TypeBusiness);
                                Objdatos.put(EstructuraBD.CabecerasEngine.Cedula, cedula);
                                Objdatos.put(EstructuraBD.CabecerasEngine.ESTADOAGGREGATE, ESTADOAGGREGATE);
                                Objdatos.put(EstructuraBD.CabecerasEngine.Foto_Exterior, Link);


                                rutasupervisor objruta = new rutasupervisor();
                                objruta.setCodeBranch(code);
                                objruta.setNameBranch(name);
                                objruta.setStreetBranch(mainStreet);
                                objruta.setStatusBranch(ESTADOAGGREGATE);
                                objruta.setRouteBranch(rutaaggregate);
                                objruta.setIdDevice(obterImeid());
                                objruta.setCampaign(objcuentaSession.getCu_CampaniaNombre());
                                objruta.setGeoLength(Double.valueOf(LenghtBranch));
                                objruta.setGeolatitude(Double.valueOf(LatitudeBranch));
                                objenviarrutassuper.add(objruta);

                                usdbh.insertardatos(Objdatos);
                                usdbh.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jumpTime += 1;
                            progress.setProgress(jumpTime);
                            sleep(25);
                        } catch (InterruptedException e) {
                            Log.e("Error Carga", e.getMessage());
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
                    objconfiguracionSession.setCnf_formularios(objcuentaSession.getCu_Formularios());
                    objconfiguracionSession.setCnf_fechacargaruta(formattedDate);
                    objconfiguracionSession.setCnf_factorbusqueda(Estado);
                    objconfiguracionSession.setCnf_imei(obterImeid());


                    progress.dismiss();
                    if (totalProgressTime > 0) {
                        if (totalProgressTime == jumpTime) {
                            taskenvioruta tee = new taskenvioruta();


                            String[] myTaskParams = new String[0];
                            try {
                                myTaskParams = new String[]{tee.crearobjetojson(objenviarrutassuper)};
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            userlog _Userlog = new userlog();
                            if (_Userlog.getToken() == "") {
                                String[] login = {"sertecomcell@prospeccionclaro.com.ec", "00"};
                                _Userlog.setParams(myTaskParams);
                                _Userlog.setOpcion("r");
                                autentificacionapp fetchJsonTasklogin = new autentificacionapp();
                                fetchJsonTasklogin.execute(login);

                            } else {
                                taskenvioruta fetchJsonTask = new taskenvioruta();
                                fetchJsonTask.execute(myTaskParams);
                            }

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

    public class CargarProductos extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        private Context context;

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
            } catch (Exception ex) {
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
                String Idaccount = objcuentaSession.getCu_idAccount();
                String Idcampania = "";
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
            } finally {
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
            progress = new ProgressDialog(context);
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
                    int cod = 1;
                    while (jumpTime < totalProgressTime) {
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
                            promocion = obj.getJSONArray("promo");
                            int index = 0;
                            while (index < promocion.length()) {
                                JSONObject objpromo = promocion.getJSONObject(index);
                                String idmaestro = objpromo.getString("idMae");
                                String descripcion = objpromo.getString("descripcion");
                                String cantidad = objpromo.getString("cantidad");

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
                                Objdatos.put(EstructuraBD.CabeceraProductos.categoria, "cant_sku" + id_mae);
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
                        } catch (InterruptedException e) {
                            Log.e("Error Carga", e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }


                    progress.dismiss();
                    if (totalProgressTime > 0) {
                        if (totalProgressTime == jumpTime) {
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

    public class CargarProductosStock extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        private Context context;

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
            } catch (Exception ex) {
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
                String Idaccount = objcuentaSession.getCu_idAccount();//"85024910-FC12-4DD8-AE58-761BF972DEB7";
                String Idcampania = "";
                String url1 = "http://geomardis6728.cloudapp.net/msNutri/api/Task/ProductNutriDisponibilidad?idAccount=" + Idaccount + "&Imei=" + obterImeid();//"454";
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
            } finally {
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
            progress = new ProgressDialog(context);
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
                    while (jumpTime < totalProgressTime) {
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
                                Objdatos.put(EstructuraBD.CabeceraProductos.stock, cantidad);
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
                        } catch (InterruptedException e) {
                            Log.e("Error Carga", e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }


                    progress.dismiss();

                    if (totalProgressTime == jumpTime) {
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






    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */




    @Override
    protected void onStart() {


        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onStop() {

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //mLocationUpdatesResultView.setText(Utils.getLocationUpdatesResult(this));

    }

    @Override
    protected void onPause() {

        super.onPause();
    }



    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        // Note: apps running on "O" devices (regardless of targetSdkVersion) may receive updates
        // less frequently than this interval when the app is no longer in the foreground.
        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        // TODO(developer): uncomment to use PendingIntent.getService().
//        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
//        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Return the current state of the permissions needed.
     */



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void requestPermissions() {

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean backgroundLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean shouldProvideRationale =
                permissionAccessFineLocationApproved && backgroundLocationPermissionApproved;

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(configuracion.this,
                                    new String[] {
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION },
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(configuracion.this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");

            } else if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED)
            ) {
                // Permission was granted.
                requestLocationUpdates(null);

            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }



    /**
     * Handles the Request Updates button and requests start of location updates.
     */


    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */




    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int fineLocationPermissionState = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);


        return (fineLocationPermissionState == PackageManager.PERMISSION_GRANTED);
    }



    /**
     * Callback received when a permissions request has been completed.
     */


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Utils.KEY_LOCATION_UPDATES_RESULT)) {
           // mLocationUpdatesResultView.setText(Utils.getLocationUpdatesResult(this));
        } else if (s.equals(Utils.KEY_LOCATION_UPDATES_REQUESTED)) {

        }
    }

    /**
     * Handles the Request Updates button and requests start of location updates.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void requestLocationUpdates(View view) {
        try {
            Log.i(TAG, "Starting location updates");
            Utils.setRequestingLocationUpdates(this, true);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            Utils.setRequestingLocationUpdates(this, false);
            e.printStackTrace();
        }
    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */






    private DownloadFormsTask downloadFormsTask;


    private FormDownloadListViewModel viewModel;

    public void downloadFormList() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            ToastUtils.showShortToast(R.string.no_connection);

            if (viewModel.isDownloadOnlyMode()) {
                setReturnResult(false, getString(R.string.no_connection), viewModel.getFormResults());
                finish();
            }
        } else {
            viewModel.clearFormNamesAndURLs();
            if (progressDialog != null) {
                // This is needed because onPrepareDialog() is broken in 1.6.
                progressDialog.setMessage(viewModel.getProgressDialogMsg());
            }


            if (downloadFormListTask != null
                    && downloadFormListTask.getStatus() != AsyncTask.Status.FINISHED) {
                return; // we are already doing the download!!!
            } else if (downloadFormListTask != null) {
                downloadFormListTask.setDownloaderListener(null);
                downloadFormListTask.cancel(true);
                downloadFormListTask = null;
            }

            downloadFormListTask = new DownloadFormListTask();
            downloadFormListTask.setDownloaderListener(form);

            if (viewModel.isDownloadOnlyMode()) {
                // Pass over the nulls -> They have no effect if even one of them is a null
                downloadFormListTask.setAlternateCredentials(viewModel.getUrl(), viewModel.getUsername(), viewModel.getPassword());
            }

            downloadFormListTask.execute();

        }
    }

    private FormListDownloaderListener form = new FormListDownloaderListener() {
        @Override
        public void formListDownloadingComplete(HashMap<String, FormDetails> result) {


            ArrayList<FormDetails> filesToDownloadC = new ArrayList<FormDetails>();
            downloadFormListTask.setDownloaderListener(null);
            downloadFormListTask = null;

            if (result == null) {
                Timber.e("Formlist Downloading returned null.  That shouldn't happen");
                // Just displayes "error occured" to the user, but this should never happen.
                if (viewModel.isDownloadOnlyMode()) {
                    setReturnResult(false, "Formlist Downloading returned null.  That shouldn't happen", null);
                }


                return;
            }

            if (result.containsKey(DL_AUTH_REQUIRED)) {
                createAuthDialog();
            } else if (result.containsKey(DL_ERROR_MSG)) {
                // Download failed
                String dialogMessage =
                        getString(R.string.list_failed_with_error,
                                result.get(DL_ERROR_MSG).getErrorStr());
                String dialogTitle = getString(R.string.load_remote_form_error);

                if (viewModel.isDownloadOnlyMode()) {
                    setReturnResult(false, getString(R.string.load_remote_form_error), viewModel.getFormResults());
                }

            } else {
                // Everything worked. Clear the list and add the results.
                viewModel.setFormNamesAndURLs(result);

                //     viewModel.clearFormList();
                boolean successDo = false;
                ArrayList<String> ids = new ArrayList<String>(viewModel.getFormNamesAndURLs().keySet());
                String IdForm = objcuentaSession.getCu_Formularios();
                for (int i = 0; i < result.size(); i++) {

                    if (IdForm.toUpperCase().contains(ids.get(i).toUpperCase())) {
                        successDo = true;
                        String formDetailsKey = ids.get(i);
                        FormDetails details = viewModel.getFormNamesAndURLs().get(formDetailsKey);

                        if (isLocalFormSuperseded(ids.get(i))) {
                            filesToDownloadC.add(details);
                        }
                    }
                }
                if (successDo == true) {
                    if (filesToDownloadC.size() > 0) {
                        startFormsDownload(filesToDownloadC);
                    }
                    // Cuando va a cargar ruta
                    ///  GetRouteChariot();


                } else {

                    Toast.makeText(getApplication(),
                            "No se encuentra configurado ningún formulario para este levantamiento", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    private void createProgressDialog() {
        progressDialog = new ProgressDialog(this);
        DialogInterface.OnClickListener loadingButtonListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // we use the same progress dialog for both
                        // so whatever isn't null is running
                        dialog.dismiss();
                        if (downloadFormListTask != null) {
                            downloadFormListTask.setDownloaderListener(null);
                            downloadFormListTask.cancel(true);
                            downloadFormListTask = null;

                            // Only explicitly exit if DownloadFormListTask is running since
                            // DownloadFormTask has a callback when cancelled and has code to handle
                            // cancellation when in download mode only
                            if (viewModel.isDownloadOnlyMode()) {
                                setReturnResult(false, "User cancelled the operation", viewModel.getFormResults());
                                finish();
                            }
                        }

                        if (downloadFormsTask != null) {
                            createCancelDialog();
                            downloadFormsTask.cancel(true);
                        }
                        viewModel.setLoadingCanceled(true);
                        viewModel.setProgressDialogShowing(false);
                    }
                };
        progressDialog.setTitle(getString(R.string.downloading_data));
        progressDialog.setMessage(viewModel.getProgressDialogMsg());
        progressDialog.setIcon(android.R.drawable.ic_dialog_info);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setButton(getString(R.string.cancel), loadingButtonListener);
        viewModel.setProgressDialogShowing(true);
        DialogUtils.showDialog(progressDialog, this);
    }

    private void createCancelDialog() {
        cancelDialog = new ProgressDialog(this);
        cancelDialog.setTitle(getString(R.string.canceling));
        cancelDialog.setMessage(getString(R.string.please_wait));
        cancelDialog.setIcon(android.R.drawable.ic_dialog_info);
        cancelDialog.setIndeterminate(true);
        cancelDialog.setCancelable(false);
        viewModel.setCancelDialogShowing(true);
        DialogUtils.showDialog(cancelDialog, this);
    }

    private void createAuthDialog() {
        viewModel.setAlertShowing(false);

        AuthDialogUtility authDialogUtility = new AuthDialogUtility();
        if (viewModel.getUrl() != null && viewModel.getUsername() != null && viewModel.getPassword() != null) {
            authDialogUtility.setCustomUsername(viewModel.getUsername());
            authDialogUtility.setCustomPassword(viewModel.getPassword());
        }
        //  DialogUtils.showDialog(authDialogUtility.createDialog(this, this, viewModel.getUrl()), this);
    }

    @SuppressWarnings("unchecked")
    private void startFormsDownload(@NonNull ArrayList<FormDetails> filesToDownload) {
        int totalCount = filesToDownload.size();
        if (totalCount > 0) {
            // show dialog box


            downloadFormsTask = new DownloadFormsTask();
            downloadFormsTask.setDownloaderListener(null);

            if (viewModel.getUrl() != null) {
                if (viewModel.getUsername() != null && viewModel.getPassword() != null) {
                    webCredentialsUtils.saveCredentials(viewModel.getUrl(), viewModel.getUsername(), viewModel.getPassword());
                } else {
                    webCredentialsUtils.clearCredentials(viewModel.getUrl());
                }
            }

            downloadFormsTask.execute(filesToDownload);
        } else {
            ToastUtils.showShortToast(R.string.noselect_error);
        }
    }
    public boolean isLocalFormSuperseded(String formId) {
        if (formId == null) {
            Timber.e("isLocalFormSuperseded: server is not OpenRosa-compliant. <formID> is null!");
            return true;
        }

        try (
                Cursor formCursor = new FormsDao().getFormsCursorForFormId(formId)) {
            return formCursor != null && formCursor.getCount() == 0 ;// form does not already exist locally

        }
    }




    private void setReturnResult(boolean successful, @Nullable String message, @Nullable HashMap<String, Boolean> resultFormIds) {
            Intent intent = new Intent();
            intent.putExtra(ApplicationConstants.BundleKeys.SUCCESS_KEY, successful);
            if (message != null) {
                intent.putExtra(ApplicationConstants.BundleKeys.MESSAGE, message);
            }
            if (resultFormIds != null) {
                intent.putExtra(ApplicationConstants.BundleKeys.FORM_IDS, resultFormIds);
            }

            setResult(RESULT_OK, intent);
        }

}


