package org.odk.collect.android.activities;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostJSONWithHttpURLConnection extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            userlog _Userlog= new userlog();
            try {
                JsonObject postData = new JsonObject();
                postData.addProperty("GeoLength", Double.valueOf(params[1]));
                postData.addProperty("Geolatitude", Double.valueOf(params[0]));
                postData.addProperty("GeoAccuracy",Double.valueOf(params[2]));
                postData.addProperty("NameAccount", params[3]);
                postData.addProperty("Namecampaign", params[4]);
                postData.addProperty("IdDevice",params[5]);

                double a=Double.valueOf(params[6]);
                int bateria= (int) a;

                postData.addProperty("battery_level", bateria);


                URL url = new URL("https://dyvenpro.azurewebsites.net/api/Tracking/SaveStatusPerson");
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
                writer.write(postData.toString());
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    if(code==401){
                        _Userlog.setParams(null);
                        String[] login = {"sertecomcell@prospeccionclaro.com.ec","00"};
                        _Userlog.setParams(params);
                        _Userlog.setOpcion("t");
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
    }

