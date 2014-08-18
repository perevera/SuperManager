/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.perevera.supermanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

/**
 *
 * @author perevera
 */
public class Splash extends Activity {
    
    private static final String TAG = "SuperManager";
    static int tries;
    static String phpsessid;
    static String sesionligafantastica;
    static int ongoingTasks;
    
    /**
        * Constructor
        *
        * @param 
        */   
    public Splash() {
        
        tries = 1;
        
    }

   /**
     * Aquí se muestra la pantalla de splash mientras se lanzan los procesos de carga de datos en paralelo
     */
    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(R.layout.splash);
        
        logIn();
        
        Multithread multithread = new Multithread(this);
        multithread.start();
                
    }
    
    @Override
    public void onPause() {
        super.onPause();
        finish();
            
    }
    
    private boolean logIn() {
        try {

            // Inicia el thread para login
            Login login = new Login();
            login.execute();

            // No dejamos la pantalla de splash mientras haya tareas en ejecución
            do {
                Log.d(TAG, "Log in try #: " + tries);
                Thread.sleep(200);
                if (tries == 3) {
                    return false;
                }
            } while (tries > 0);

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }
    
    private class Multithread extends Thread {

        Context ctx;
        AssetManager assetManager;

        Multithread(Context ctx) {
            
            this.ctx = ctx;
                        // Instancia el AssetManager para manejar recursos de tipo fichero
            assetManager = getAssets();
            
        }

        public void run() {
            try {
                
                // Inicia el thread para carga de la tabla de bases en el mercado
                PlayersGet asyncLoadBases = new PlayersGet(ctx, null, assetManager, 1);
                asyncLoadBases.execute();
//
                // Inicia el thread para carga de la lista de equipos de usuario
                MyTeamsGet asyncLoadTeams = new MyTeamsGet(ctx, null, assetManager);
                asyncLoadTeams.execute();
                
                // Pausa de 5 segundos, aunque en realidad quiero que dure el tiempo que se tarda en cargar los datos
//                sleep(5000);
                
                // No dejamos la pantalla de splash mientras haya tareas en ejecución
                do {
                    System.out.println("Ongoing tasks: " + ongoingTasks);
                    Thread.sleep(200);
                } while (ongoingTasks > 0);

            } catch (Exception e) {
                e.printStackTrace();
                
            } finally {
                Intent openSuperManager = new Intent("org.perevera.supermanager.STARTINGPOING");
                startActivity(openSuperManager);
            }
        }
    }
}
