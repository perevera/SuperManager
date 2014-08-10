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

/**
 *
 * @author perevera
 */
public class Splash extends Activity {
    
    static int ongoingTasks;

   /**
     * Aquí se muestra la pantalla de splash mientras se lanzan los procesos de carga de datos en paralelo
     */
    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(R.layout.splash);
        
//        ongoingTasks = 0;
        
        Multithread multithread = new Multithread(this);
        multithread.start();
        

//        // Instancia el AssetManager para manejar recursos de tipo fichero
//        AssetManager assetManager = getAssets();

//        // Inicia el thread para carga de la tabla de bases en el mercado
//        PlayersGet asyncLoadBases = new PlayersGet(this, null, assetManager, 1);
//        asyncLoadBases.execute();
//
//        // Inicia el thread para carga de la lista de equipos de usuario
//        MyTeamsGet asyncLoadTeams = new MyTeamsGet(this, null, assetManager);
//        asyncLoadTeams.execute();
//                    
//        try {
//            // Pausa de 5 segundos, aunque en realidad quiero que dure el tiempo que se tarda en cargar los datos
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            Intent openSuperManager = new Intent("org.perevera.supermanager.STARTINGPOING");
//            startActivity(openSuperManager);
//        }
//        
//        Thread timer = new Thread() {
//            public void run() {
//                try {
//                    // Instancia el AssetManager para manejar recursos de tipo fichero
//                    AssetManager assetManager = getAssets();
//
//                    // Inicia el thread para carga de la tabla de bases en el mercado
//                    PlayersGet asyncLoadBases = new PlayersGet(this, null, assetManager, 1);
//                    asyncLoadBases.execute();
////
//                    // Inicia el thread para carga de la lista de equipos de usuario
//                    MyTeamsGet asyncLoadTeams = new MyTeamsGet(this, null, assetManager);
//                    asyncLoadTeams.execute();
//                    // Pausa de 5 segundos, aunque en realidad quiero que dure el tiempo que se tarda en cargar los datos
//                    sleep(5000);
//                    
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    Intent openSuperManager = new Intent("org.perevera.supermanager.STARTINGPOING");
//                    startActivity(openSuperManager);
//                }
//            }
//        };
        
//        timer.start();
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        finish();
            
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
