package org.odk.collect.android.Tracking;

import android.content.ContentValues;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.BaseDatosEngine.BaseDatosEngine;
import org.odk.collect.android.database.BaseDatosEngine.EstructuraBD;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JavaRestClient {
/*
    public void getToken(JSONObject _user){
 try {

     Date date = new Date();
     SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
     String Token="";
     Retrofit _retrofit =new  Retrofit.Builder()
             .baseUrl("https://dyvenpro.azurewebsites.net/")
             .addConverterFactory(GsonConverterFactory.create())
             .build();
     PostService service = _retrofit.create(PostService.class);
     Call<String> call=service.GetTokem(_user);
     call.enqueue(new Callback<String>() {
         @Override
         public void onResponse(Call<String> call, Response<String> response) {
             BaseDatosEngine usdbh = new BaseDatosEngine();
             usdbh = usdbh.open();
             if(!response.isSuccessful()){

                 int responseCOde=response.code();
             }else {
                 ContentValues Objdatos = new ContentValues();
                 Objdatos.put("ID", 20);
                 Objdatos.put(EstructuraBD.CabecerasToken.token, response.body());
                 Objdatos.put(EstructuraBD.CabecerasToken.fecha, formatter.format(date));
                 usdbh.InsertToken(Objdatos);
                 usdbh.close();
             }
         }

         @Override
         public void onFailure(Call<String> call, Throwable t) {
             String d=t.getMessage();
         }
     });

     }catch (Exception e){
       String eS=e.getMessage();

     }
    }
*/

    public void getToken2(User _user){
        try {



            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            String Token="";
            Retrofit _retrofit =new  Retrofit.Builder()
                    .baseUrl(Collect.getInstance().getString(R.string.api_server))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostService service = _retrofit.create(PostService.class);
            Call<org.odk.collect.android.Tracking.Token> call=service.GetTokem2(_user);
            call.enqueue(new Callback<org.odk.collect.android.Tracking.Token>() {
                @Override
                public void onResponse(Call<org.odk.collect.android.Tracking.Token> call, Response<org.odk.collect.android.Tracking.Token> response) {
                    BaseDatosEngine usdbh = new BaseDatosEngine();
                    usdbh = usdbh.open();
                    if(!response.isSuccessful()){

                        int responseCOde=response.code();
                    }else {
                        ContentValues Objdatos = new ContentValues();
                        Objdatos.put("ID", 20);
                        Objdatos.put(EstructuraBD.CabecerasToken.token, response.body().getToken());
                        Objdatos.put(EstructuraBD.CabecerasToken.fecha, formatter.format(date));
                        usdbh.InsertToken(Objdatos);
                        usdbh.close();
                    }
                }

                @Override
                public void onFailure(Call<org.odk.collect.android.Tracking.Token> call, Throwable t) {
                    String d=t.getMessage();
                }
            });

        }catch (Exception e){
            String eS=e.getMessage();

        }
    }
    /*Envio de datos de Tracking
     *
     * */
    public void SetRouteBranches(List<RouteBranches> _routeBranches){
        try {

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String Token="";
            Retrofit _retrofit =new  Retrofit.Builder()
                    .client(clientHeader())
                    .baseUrl(Collect.getInstance().getString(R.string.api_server))
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
            PostService service = _retrofit.create(PostService.class);
            Call<Post> call=service.SetRouteBranches(_routeBranches);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(!response.isSuccessful()){

                        int responseCOde=response.code();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    String d=t.getMessage();
                }
            });


        }catch (Exception e){
            String eS=e.getMessage();

        }
    }
    public void SetNewBranch(SaveNewBranchTracking _routeBranches){
        try {

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String Token="";
            Retrofit _retrofit =new  Retrofit.Builder()
                    .client(clientHeader())
                    .baseUrl(Collect.getInstance().getString(R.string.api_server))
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
            PostService service = _retrofit.create(PostService.class);
            Call<Post> call=service.SaveNewBranchTracking(_routeBranches);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(!response.isSuccessful()){

                        int responseCOde=response.code();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    String d=t.getMessage();
                }
            });


        }catch (Exception e){
            String eS=e.getMessage();

        }
    }
    /*Envio de datos de Tracking
     *
     * */
    public void SetTracking(Tracking _tracking ){
        try {




            Retrofit _retrofit =new  Retrofit.Builder()
                    .client(clientHeader())
                    .baseUrl(Collect.getInstance().getString(R.string.api_server))
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
            PostService service = _retrofit.create(PostService.class);
            Call<Post> call=service.SetTracking(_tracking);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(!response.isSuccessful()){

                        int responseCOde=response.code();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {

                }
            });


        }catch (Exception e){
            String eS=e.getMessage();

        }
    }


    /*Envio de datos de Tracking
     *
     * */
    public void SaveStatusBranchTracking(SaveStatusBranchTracking _tracking ){
        try {




            Retrofit _retrofit =new  Retrofit.Builder()
                    .client(clientHeader())
                    .baseUrl(Collect.getInstance().getString(R.string.api_server))
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
            PostService service = _retrofit.create(PostService.class);
            Call<Post> call=service.SaveStatusBranchTracking(_tracking);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(!response.isSuccessful()){

                        int responseCOde=response.code();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {

                }
            });


        }catch (Exception e){
            String eS=e.getMessage();

        }
    }
    public OkHttpClient clientHeader(){

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String Token="";
                try {
                    BaseDatosEngine _context = new BaseDatosEngine();
                    _context = _context.open();
                    Token= _context.GetTokenSelect();
                    _context.close();
                }catch (Exception e){

                    Token="";
                }
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + Token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        return client;

    }
}
