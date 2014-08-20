/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.perevera.supermanager;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author perevera
 */
public class Login extends AsyncTask<Void, Void, Integer>{

    private static final String TAG = "Login";
    public static final String host = "supermanager.acb.com";
    public static final String page = "/mundial/index.php";
    private String phpsessid;
    private String sesionligafantastica;

//    /**
//        * Constructor
//        *
//        * @param 
//        */   
//    public Login(Context ctx, String url) {
//        
//    }
    
    /**
        * Al inicio, pone a nulas las cookies
        */  
    @Override
    protected void onPreExecute() {

        LoginActivity.phpsessid = null;
        LoginActivity.sesionligafantastica = null;

    }

    /**
        * Realiza el envío de credenciales para la conexión en un hilo aparte
        *
        * @param Ninguno
        * 
        * @return True si el login tuvo éxito, False en caso contrario
        */
    @Override
    protected Integer doInBackground(Void... params) {

        try {

            /* Versión Apache */
            DefaultHttpClient client = new DefaultHttpClient();
//			HttpClient client = new DefaultHttpClient();

//		    CookieStore store = new BasicCookieStore();
//		    store.addCookie(MyCookieStorageClass.getCookie());
//		    client.setCookieStore(store);			
            HttpPost post = new HttpPost("http://" + host + page);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("query", "Java"));
            nameValuePairs.add(new BasicNameValuePair("usuario", "Melodrame"));
            nameValuePairs.add(new BasicNameValuePair("clave", "s1ns0nte"));
            nameValuePairs.add(new BasicNameValuePair("control", "1"));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);

            List<Cookie> cookies = client.getCookieStore().getCookies();

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                Log.d(TAG, line);
                if (line.startsWith("Auth=")) {
                    String key = line.substring(5);
                    // Do something with the key
                }

            }

            phpsessid = cookies.get(0).toString();
            sesionligafantastica = cookies.get(1).toString();

            Log.d(TAG, "PHPSESSID: " + phpsessid);
            Log.d(TAG, "sesionligafantastica: " + sesionligafantastica);
            
            return 1;

        } catch (Exception e) {
            
            e.printStackTrace();
            return 0;
            
        }

    }

     /**
        * Al final, guarda las cookies en variables del UI
        */      
    protected void onPostExecute(Integer result) {

        if (result == 1) {
            LoginActivity.phpsessid = phpsessid;
            LoginActivity.sesionligafantastica = sesionligafantastica;
//            Splash.tries = 0;      // Esto es un indicador de que el login ha tenido éxito
        } else {
//            Splash.tries++;
        }
    }
    
}
