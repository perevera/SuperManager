/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.perevera.supermanager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import static org.perevera.supermanager.Constants.host;
import static org.perevera.supermanager.Constants.page;

/**
 *
 * @author perevera
 */
public class LoginActivity extends Activity implements OnClickListener {
    
    // Se ponen las credenciales aquí a piñón solo para la fase de test
    private String user = "Melodrame";
    private String passwd = "s1ns0nte";
    
    public static String phpsessid;
    public static String sesionligafantastica;
    protected static int tries = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        // Se asigna el layout de login
        setContentView(R.layout.login);
        
        // Se inicializan los valores de los cuadros de texto
        EditText userText = (EditText) findViewById(R.id.txtUsuario);
        userText.setText(user);
        
        EditText pwdText = (EditText)findViewById(R.id.txtPass);
        pwdText.setText(passwd);
        
        // Se asocia un listener para el botón de login
        View loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(this);

    }
    
    public void onClick(View v) {
        
        // Chequeamos que el click provenga del botón de login (¡no puede ser de otra manera!)
        if (v.getId() == R.id.btnLogin) {

            // Se leen los valores introducidos en los campos de texto
            EditText userText = (EditText) findViewById(R.id.txtUsuario);
            user = userText.getText().toString();

            EditText pwdText = (EditText) findViewById(R.id.txtPass);
            passwd = pwdText.getText().toString();

            // Lanzamos el proceso de login en hilo aparte
            String url = "http://" + host + page;
            new LoginTask(this).execute(url, user, passwd);
            
        }
        
    }
    
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "MyAsyncTask";
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final DefaultHttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
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

        public LoginTask(Context context) {

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

                //Check the Http Request for success
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();

                } else {

                    //Closes the connection.
                    Log.w("HTTP1:", statusLine.getReasonPhrase());
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());

                }

            } catch (ClientProtocolException e) {

                Log.w("HTTP2:", e);
                content = e.getMessage();
                error = true;
                cancel(true);

            } catch (IOException e) {

                Log.w("HTTP3:", e);
                content = e.getMessage();
                error = true;
                cancel(true);

            } catch (Exception e) {

                Log.w("HTTP4:", e);
                content = e.getMessage();
                error = true;
                cancel(true);

            }
            
            // Almacena las cookies recibidas
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();

            // Para determinar si el login es correcto simplemente comprobamos que se hayan recibido las dos cookies necesarias, en cuyo caso las almacenamos
            if (cookies.size() == 2) {

                phpsessid = cookies.get(0).toString();
                sesionligafantastica = cookies.get(1).toString();

                Log.w(TAG, "PHPSESSID: " + phpsessid);
                Log.w(TAG, "sesionligafantastica: " + sesionligafantastica);
                
                return true;
               
            } else {

                return false;
                
            }

        }

//    protected void onCancelled() {
//        createNotification("Error occured during data download",content);
//    }
        protected void onPostExecute(Boolean okey) {

            if (okey) {

                createNotification("Log in completed", "");
                LoginActivity.phpsessid = phpsessid;
                LoginActivity.sesionligafantastica = sesionligafantastica;

                // Inicia la actividad Splash para cargar los datos generales y de usuario desde la página de Supermanager
                Intent main = new Intent(getApplicationContext(), Splash.class);
                startActivity(main);

            } else {

                createNotification("Could not log in on try # " + tries, "");
                tries++;         // Se incrementa el contador de intentos fallidos

                // Se controla el límte máximo de intentos fallidos
                if (tries > 3) {
                    // PENDIENTE: Aquí falta mostrar una ventana de error
                    finish();       // Se abandona la ejecución
                }

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
   
}
