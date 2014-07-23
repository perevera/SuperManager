/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.perevera.supermanager;

import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
//import org.jsoup.Connection.Method;
//import org.jsoup.Connection.Response;
//import org.jsoup.Jsoup;

/**
 *
 * @author perevera
 */
public class Login extends Activity implements OnClickListener {

    private static final String TAG = "SuperManager";
    private EditText usrEditText;
    private EditText pwdEditText;
    private String phpsessid;
    private String sesionligafantastica;
    public static final String host = "supermanager.acb.com";

    /**
        * Called when the activity is first created.
        */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set View to login.xml
        setContentView(R.layout.login);

        // Set up click listener for login button
        Button loginButton = (Button) findViewById(R.id.btnLogin);        
        loginButton.setOnClickListener(this);
        
        // Get EditText objects
        usrEditText = (EditText) findViewById(R.id.edit_user);
        pwdEditText = (EditText) findViewById(R.id.edit_password);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                logIn();
                break;
            // More buttons go here (if any) ...
        }
        
        // Aquí terminamos esta actividad
        finish();
    }

    /**
        * Carga la página inicial enviando las credenciales
        */
    private void logIn() {

        try {

            /* Versión JSoup */
//            Response response = Jsoup.connect("http://" + host)
//                    .userAgent("Mozilla")
//                    .cookie("auth", "token")
////                    .data("query", "Java", "usuario", "Melodrame", "clave", "s1ns0nte", "control", "1")
//                    .data("query", "Java", "usuario", usrEditText.getText().toString(), "clave", pwdEditText.getText().toString(), "control", "1")
//                    .method(Method.POST)
//                    .execute();

//            phpsessid = response.cookie("PHPSESSID");
//            sesionligafantastica = response.cookie("sesionligafantastica");

            Log.d(TAG, "PHPSESSID: " + phpsessid);
            Log.d(TAG, "sesionligafantastica: " + sesionligafantastica);
//
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
