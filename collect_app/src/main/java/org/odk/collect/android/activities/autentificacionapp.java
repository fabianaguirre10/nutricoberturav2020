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

public class autentificacionapp extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        userlog _Userlog= new userlog();
        try {
            JsonObject postData = new JsonObject();
            postData.addProperty("Email",params[0]);
            postData.addProperty("Password", params[1]);
            URL url = new URL("https://dyvenpro.azurewebsites.net/api/Login/Authenticate");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            //urlConnection.setRequestProperty("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJJZFVzZXIiOiIzOWMwOWE0MS0yNDg4LTQ5OWQtYTMyYS0wMWQ5ODYyOTdlMjEiLCJlbWFpbCI6InNlcnRlY29tY2VsbEBwcm9zcGVjY2lvbmNsYXJvLmNvbS5lYyIsIm5hbWUiOiI1MDA0NiIsImlkdHlwZSI6ImUzNzIzNmE0LTk3MWItNGZjYy04YWMwLTY5MjVlZGVjZDdlYiIsImlkQWNjb3VudCI6IjgiLCJuYmYiOjE1OTU0Mzk3MDYsImV4cCI6MTU5NjA0NDUwNiwiaWF0IjoxNTk1NDM5NzA2fQ.RREkkl-8q1SSoEPika6kzDUvOQXauuAVQQDXOvugCkM");
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
                throw new IOException("Invalid response from server: " + code);
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i("data", line);

                _Userlog.setToken(line);
                _Userlog.setUsername(params[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return _Userlog.getToken();
    }

    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);
        userlog _Userlog= new userlog();
        if(_Userlog.getOpcion().equals("r")) {
            taskenvioruta fetchJsonTask = new taskenvioruta();
            fetchJsonTask.execute(_Userlog.getParams());
        }
        if(_Userlog.getOpcion().equals("t")) {
            PostJSONWithHttpURLConnection fetchJsonTasktraking = new PostJSONWithHttpURLConnection();
            fetchJsonTasktraking.execute(_Userlog.getParams());
        }
        if(_Userlog.getOpcion().equals("u")) {
            updatestatus fetchJsonTasktraking = new updatestatus();
            fetchJsonTasktraking.execute(_Userlog.getParams());
        }
    }
}

