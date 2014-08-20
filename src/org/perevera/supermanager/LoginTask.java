/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.perevera.supermanager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.cookie.Cookie;

/**
 *
 * @author perevera
 */
public class LoginTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "MyAsyncTask";
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final DefaultHttpClient httpclient = new DefaultHttpClient();

    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;

    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    
    // Cookies
//    private String user;
//    private String passwd;
    private String phpsessid;
    private String sesionligafantastica;
    private int tries = 1;

    public LoginTask(Context context){

        this.mContext = context;

        //Get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }

//    protected void onPreExecute() {
//        createNotification("Data download is in progress","");
//    }

    protected Boolean doInBackground(String... pars) {

        String url = null;
        String user = "abc";
        String passwd = "xyz";

        try {

            // Recuperamos los parámetros recibidos
            url = pars[0];
            user = pars[1];
            passwd = pars[2];
                        
            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
            ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

            HttpPost httpPost = new HttpPost(url);

            //Any other parameters you would like to set
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("query", "Java"));
            nameValuePairs.add(new BasicNameValuePair("usuario", user));
            nameValuePairs.add(new BasicNameValuePair("clave", passwd));
            nameValuePairs.add(new BasicNameValuePair("control", "1"));
            
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Response from the Http Request
            response = httpclient.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();
            
            // Almacena las cookies recibidas
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            
            phpsessid = cookies.get(0).toString();
            sesionligafantastica = cookies.get(1).toString();

            Log.w(TAG, "PHPSESSID: " + phpsessid);
            Log.w(TAG, "sesionligafantastica: " + sesionligafantastica);
            
            //Check the Http Request for success
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                content = out.toString();
            }
            else{
                //Closes the connection.
                Log.w("HTTP1:",statusLine.getReasonPhrase());
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }


        } catch (ClientProtocolException e) {
            
            Log.w("HTTP2:",e );
            content = e.getMessage();
            error = true;
            cancel(true);
            
        } catch (IOException e) {
            
            Log.w("HTTP3:",e );
            content = e.getMessage();
            error = true;
            cancel(true);
            
        } catch (Exception e) {
            
            Log.w("HTTP4:",e );
            content = e.getMessage();
            error = true;
            cancel(true);
            
        }

        return !(error);
        
    }

//    protected void onCancelled() {
//        createNotification("Error occured during data download",content);
//    }

    protected void onPostExecute(Boolean error) {
        
        if (error) {
            
            createNotification("Could not log in on try # " + tries, "");
            tries++;         // Se incrementa el contador de intentos fallidos
            
        } else {
            
            createNotification("Log in completed", "");
            LoginActivity.phpsessid = phpsessid;
            LoginActivity.sesionligafantastica = sesionligafantastica;
            
            // Inicia la actividad Splash para cargar los datos generales y de usuario desde la página de Supermanager
//            Intent main = new Intent(this, Splash.class);
//            getApplication().startActivity(main);
            
//            tries = 0;      // Esto es un indicador de que el login ha tenido éxito
            
        }
        
    }

    private void createNotification(String contentTitle, String contentText) {

        //Build the notification using Notification.Builder
        Notification.Builder builder = new Notification.Builder(mContext)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setAutoCancel(true)
        .setContentTitle(contentTitle)
        .setContentText(contentText);

        //Get current notification
        mNotification = builder.getNotification();

        //Show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        
    }

}