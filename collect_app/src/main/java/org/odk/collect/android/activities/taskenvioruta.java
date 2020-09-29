package org.odk.collect.android.activities;



import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class taskenvioruta extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;

        try {


            userlog _Userlog= new userlog();

            //postData.addProperty("batteryStatus ",params[6]);


            URL url = new URL("https://dyvenpro.azurewebsites.net/api/Tracking/SaveBranchTracking");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + _Userlog.getToken());
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    out, "UTF-8"));
            writer.write(params[0]);
            writer.flush();

            int code = urlConnection.getResponseCode();
            if (code !=  200) {
                if(code==401){
                    String[] login = {"sertecomcell@prospeccionclaro.com.ec","00"};
                    _Userlog.setParams(params);
                    _Userlog.setOpcion("r");
                    autentificacionapp fetchJsonTasklogin = new autentificacionapp();
                    fetchJsonTasklogin.execute(login);
                }
                throw new IOException("Invalid response from server: " + code);

            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i("data", line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }


    public String crearobjetojson(@NotNull List<rutasupervisor> obj) throws JSONException {

        String jSonobjetos = "";
        JSONArray jsonArrayruta = null;
        JSONObject jsonitemruta;
        jsonArrayruta = new JSONArray();
        for(rutasupervisor x:obj){
            jsonitemruta = new JSONObject();
            jsonitemruta.put("CodeBranch", x.getCodeBranch());
            jsonitemruta.put("NameBranch", x.getNameBranch());
            jsonitemruta.put("StreetBranch", x.getStreetBranch());
            jsonitemruta.put("StatusBranch", x.getStatusBranch());

            jsonitemruta.put("RouteBranch", x.getRouteBranch());
            //jsonitemruta.put("IdDevice", x.getIdDevice());
            jsonitemruta.put("campaign", x.getCampaign());

            jsonitemruta.put("IdDevice",x.getIdDevice());
            //jsonitemruta.put("campaign", "Medición Septiembre-P14".trim());



            jsonitemruta.put("GeoLength", x.getGeoLength());
            jsonitemruta.put("Geolatitude", x.getGeolatitude());

            jsonitemruta.put("timeLastTask", x.get_timetaks());
            jsonitemruta.put("dateexec", x.get_dateexec()=="null"?null:x.get_dateexec());
            jsonitemruta.put("dateexecini", x.getStartDate()=="null"?null:x.getStartDate());

            jsonArrayruta.put(jsonitemruta);
        }
        jSonobjetos = jsonArrayruta.toString();
        return jSonobjetos;
    }
}


