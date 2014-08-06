/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.perevera.supermanager;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import static org.perevera.supermanager.Constants.TABLE_PLAYERS;

/**
 *
 * @author perevera
 */
public abstract class GetInfo extends AsyncTask<Void, Void, Integer> {
    
//    public static final String KEY_POSITION = "org.perevera.supermanager.position";
    
    private final Context ctx;
    private final String url;
    private DatabaseHelper dbhelper;
    protected SQLiteDatabase db;
    
    /**
        * Constructor
        *
        * @param ctx -> Objeto de tipo Context
        * @param url -> URL de la página de donde cargar los datos
        * @param assetManager -> Objeto de tipo AssetManager (solo para la fase de tests!)
        * @param filename -> Nombre del fichero HTML con los datos de prueba (solo para la fase de tests!)
        */   
    public GetInfo(Context ctx, String url) {
        
        this.ctx = ctx;
        this.url = url;
        
    }
    
    /**
        * Realiza las acciones en un hilo aparte: cargar página, leer línea a línea, extraer datos de registros y guardarlos
        * en las tablas que toque de la DB
        *
        * @param Ninguno
        * 
        * @return Número de registros procesados, -1 en caso de error
        */    
    @Override
    protected Integer doInBackground(Void... params) {

        try {
            
            // Instancia el objeto que extiende SQLiteOpenHelper
            dbhelper = new DatabaseHelper(ctx);

            // Obtiene acceso a la b.d.
            db = dbhelper.getWritableDatabase();
                        
            // Borra las tablas implicadas antes de cargar los datos actuales
            deleteTables();
            
            // Carga la página del servidor
            InputStream in = downloadWebPage(url);
            // En pruebas, en vez de eso cargamos el fichero que se indica como cuarto parámetro
//            InputStream in = loadHtmlFile(filename);

            // Carga el fichero HTML con la lista de jugadores de la posición indicada
            return processHtmlFile(in);

        } catch (SQLiteException e) {

            System.err.println("SQLiteException: " + e.getMessage());
            return -1;
            
        } catch (Exception e) {

            System.err.println("Exception: " + e.getMessage());
            return -1;

        } finally {

            dbhelper.close();

        }

    }
    
    /**
        * Borra las tablas implicadas antes de cargar los datos actuales 
        *
        * @param reader -> Objeto de tipo BufferedReader con las líneas leídas del fichero HTML
         */
    protected abstract void deleteTables();
    
    /**
        * Descarga la pagina de la URL indicada para obtener la informacion particular
        *
        * @param url -> La URL de dónde cargar la página
        * 
        * @return El objeto InputStream con el contenido de la página leída
        */
    protected InputStream downloadWebPage(String url) {

        InputStream content = null;

        DefaultHttpClient client = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);

        try {

            HttpResponse execute = client.execute(httpGet);
            content = execute.getEntity().getContent();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }
    
//    /**
//        * Carga la página HTML de un fichero guardado, solo para la fase de tests
//        *
//        * @param url -> El nombre del fichero
//        * 
//        * @return El objeto InputStream con el contenido de la página leída
//        */
//    protected InputStream loadHtmlFile(String filename) {
//
//        InputStream content = null;
//        
//        try {
//            
//            content = assetManager.open("html/" + filename);
//
//        } catch (IOException e) {
//            
//            System.err.println("SQLiteException: " + e.getMessage());
//            
//        }
//
//        return content;
//    }

    /**
        * Lee el fichero HTML para encontrar las secciones donde hay información relevante 
        *
        * @param in -> El objeto InputStream con el contenido de la página leída
        * 
        * @return Devuelve el nº de registros procesados
        */
    protected abstract int processHtmlFile(InputStream in);

    /**
        * Extrae información de un elemento leyendo línea a línea y la guarda en un registro de la DB
        *
        * @param reader -> Objeto de tipo BufferedReader con las líneas leídas del fichero HTML
         */
    protected abstract void extractRecord(BufferedReader reader);

}
