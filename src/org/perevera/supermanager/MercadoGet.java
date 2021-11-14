/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.perevera.supermanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.perevera.supermanager.Constants.*;

/**
 *
 * @author perevera
 */
public class PlayersGet extends GetInfo {

    private static final String TAG = "PlayersGet";
    public static final String KEY_POSITION = "org.perevera.supermanager.position";
    private final AssetManager assetManager;
    private String filename;
    private int position;
    
    // Define los patrones para encontrar la información relevante
    private Pattern inirow = Pattern.compile("<tr>");
    private Pattern second = Pattern.compile("class=\"grisizqda\"");
    private Pattern endrow = Pattern.compile("</tr>");
    private Pattern identificador = Pattern.compile("\"http://www\\.acb\\.com/stspartidojug\\.php\\?cod_jugador=([0-9A-Z][0-9A-Z][0-9A-Z])\" target=\"_blank\"><font color=\"black\">([\\w ]+, \\w+)<");
//    private Pattern name = Pattern.compile(">([\\w ]+, \\w+)<");
    private Pattern equipo = Pattern.compile(">([A-Z]+)<");
    private Pattern balance = Pattern.compile(">(\\d\\d*/\\d\\d*)<");
    private Pattern promedio = Pattern.compile(">([0-9]+,[0-9]+)<");
    private Pattern precio = Pattern.compile(">([0-9]+.[0-9]+)<");
    private Pattern minutos = Pattern.compile(">([0-9]+:[0-9][0-9])<");
    
    /**
        * Constructor
        * 
        * Solo para fase de pruebas ya que elige a piñón fijo un fichero en lugar de cargar la página de la web
        *
        * @param ctx -> Objeto de tipo Context
        * @param url -> URL de la página de donde cargar los datos (nula en la fase de tests)
        * @param assetManager -> Objeto de tipo AssetManager (solo para la fase de tests)
        * @param position -> Posición (bases, aleros o pivots)
        */
    public PlayersGet(Context ctx, String url, AssetManager assetManager, Integer position) {

        super(ctx, url);
        
        this.assetManager = assetManager;
       
        switch (position) {

            case 1:         // Bases

                filename = "Ver_Mercado_Bases.html";
                //                    filename = "Ver_Mercado_Bases_trucated.html";        
                break;

            case 2:         // Aleros

                filename = "Ver_Mercado_Aleros.html";
                break;

            case 3:         // Pivots

                filename = "Ver_Mercado_Pivots.html";
                break;

        }

    }
    
    /**
        * Carga la página HTML de un fichero guardado, solo para la fase de tests
        *
        *  @param url -> La URL de dónde cargar la página
        * 
        * @return El objeto InputStream con el contenido de la página leída
        */
    @Override
    protected InputStream downloadWebPage(String url) {
        
        // El parámetro url me lo paso por el forro

        InputStream content = null;
        
        try {
            
            content = assetManager.open("html/" + filename);

        } catch (IOException e) {
            
            System.err.println("SQLiteException: " + e.getMessage());
            
        }

        return content;
    }

    /**
        * Lee el fichero HTML para encontrar las secciones donde hay información de jugadores 
        *
        * @param in -> El objeto InputStream con el contenido de la página leída
        * 
        * @return Devuelve el nº de registros procesados
        */
    protected int processHtmlFile(InputStream in) {

        int numPlayers = 0;

        try {

            // La codificación del fichero es: HTML document, ISO-8859 text, with very long lines
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("ISO-8859-1")));

            StringBuilder out = new StringBuilder();
            String line;

            Matcher m1;
            Matcher m2;

            while ((line = reader.readLine()) != null) {

                //                out.append(line);
                // Now create matcher object.
                m1 = inirow.matcher(line);

                if (m1.matches()) {

                    if ((line = reader.readLine()) != null) {

                        m2 = second.matcher(line);

                        if (m2.find()) {

                            numPlayers++;
                            extractRecord(reader);

                        }

                    }

                }

            }

            //            Log.d(TAG, out.toString());   //Prints the string content read from input stream
            Log.d(TAG, "Number of players: " + numPlayers);   //Prints the number of players found

            reader.close();
            
            return numPlayers;

        } catch (IOException e) {

            System.err.println("IOException: " + e.getMessage());
            return -1;

        }

    }

    /**
        * Extrae información de un jugador leyendo línea a línea y la guarda en un registro de la DB
        *
        * @param reader -> Objeto de tipo BufferedReader con las líneas leídas del fichero HTML
         */    
    protected void extractRecord(BufferedReader reader) {

        try {

            int i = 1;
            String line;
            String playerId = "";
            String playerName = "";
            String[] splitRecord;
            Double pct;
            Matcher matcher;
            // Insert a new record into the Players data source.
            // You would do something similar for delete and update.
            
            ContentValues values = new ContentValues();

            player:
            while ((line = reader.readLine()) != null) {

                switch (i) {            // En función del número de línea relativo a los datos de este jugador

                    case 1:     // Las dos primeras líneas se saltan
                    case 2:

                        break;

                    case 3:     // Se extraen el ID y el nombre completo del jugador
                        
                        matcher = identificador.matcher(line);

                        if (matcher.find()) {
   
                            playerId = matcher.group(1);        // ID de jugador
                            playerName = matcher.group(2);      // Nombre completo 
                            // Se usarán luego estos valores o en la cláusula WHERE del UPDATE o en las columnas del INSERT
                            Log.d(TAG, "ID de jugador: " + playerId);
                            Log.d(TAG, "Nombre de jugador: " + playerName);

                        }

                        break;

                    case 5:     // Se extrae el nombre abreviado del equipo
                       
                        matcher = equipo.matcher(line);

                        if (matcher.find()) {
                            values.put(PLAYERS_TEAM, matcher.group(1));
                            Log.d(TAG, "Iniciales del equipo: " + matcher.group(1));
                        }

                        break;

                    case 6:     // Se extrae el balance de victorias/derrotas
                      
                        matcher = balance.matcher(line);

                        if (matcher.find()) {
                            String record, wins, losses;
                            record = matcher.group(1);
                            splitRecord = record.split("/");
                            wins = splitRecord[0];
                            losses = splitRecord[1];
                            pct = getPercentage(wins, losses);
                            values.put(PLAYERS_PERCENTAGE, pct);
                            Log.d(TAG, "Balance de victorias/derrotas: " + matcher.group(1));
                            Log.d(TAG, "Porcentaje de victorias: " + pct);
                        }

                        break;
                        
                    case 7:     // Se extrae el promedio de valoración

                        matcher = promedio.matcher(line);

                        if (matcher.find()) {
                            String average = matcher.group(1);
                            // Remplazar la coma por punto para separar decimales, si no la conversión posterior no funciona
                            average = average.replace(",", ".");
                            Double avg = Double.parseDouble(average);                          
                            values.put(PLAYERS_AVERAGE, avg);
                            Log.d(TAG, "Valoración promedio: " + avg);
                        }

                        break;
                        
                    case 8:     // Se extrae el precio
                        
                        matcher = precio.matcher(line);

                        if (matcher.find()) {
                            String price = matcher.group(1);
                            // Eliminar el punto que separa los miles, si no la conversión posterior no funciona
                            price = price.replace(".", "");
                            Integer prc = Integer.parseInt(price);                          
                            values.put(PLAYERS_PRICE, prc);
                            Log.d(TAG, "Precio: " + prc);
                        }

                        break;                        

                    case 9:     // Se extrae el promedio de minutos de juego
                        
                        matcher = minutos.matcher(line);

                        if (matcher.find()) {
//                            Integer mins = Integer.parseInt(matcher.group(1));      // se extraen los minutos y se convierten a entero                      
//                            Integer secs = Integer.parseInt(matcher.group(2));      // se extraen los segundos y se convierten a entero
//                            Integer totsecs = mins * 60 + secs;                     // se suma el total como segundos
                            values.put(PLAYERS_TIME, matcher.group(1));
                            Log.d(TAG, "Minutos: " + matcher.group(1));
                        }

                        break;
                                                
                    case 15:    // Comprobar que es la última línea y abandonar
                        
                        matcher = endrow.matcher(line);

                        if (matcher.matches()) {
                            break player;      // si se llegó al final de la información correspondiente a un jugador, salir del bucle para guardar los datos de la fila
                        }
                        break;

                    default:         // De momento, no hacer nada, son los que faltan implementar...
                   
                }

                i++;

            }
            
            // La columna posición se guarda aquí
            values.put(PLAYERS_POSITION, position);
            
            // Primero se intenta actualizar el registro de jugador, normalmente ya existirá
            String whereClause = "id = '" + playerId + "'";
            
            if (db.update(TABLE_PLAYERS, values, whereClause, null) < 1) {
                
                // Si no, entonces se inserta como nuevo registro de jugador
                values.put(PLAYERS_ID, playerId);       
                values.put(PLAYERS_NAME, playerName);  
                db.insertOrThrow(TABLE_PLAYERS, null, values); 
                
            }                      

        } catch (IOException e) {

            System.err.println("IOException: " + e.getMessage());

        } catch (NumberFormatException e) {

            System.err.println("NumberFormatException: " + e.getMessage());
            
        } catch (SQLiteException e) {

            System.err.println("SQLiteException: " + e.getMessage());
                        
        }

    }
    
    /**
        * Borra la tabla de jugadores
        *
          */
    protected void deleteTables() {
        
        // No borramos la tabla ahora!

//        try {
//
//            // Borra la tabla de jugadores
//            int num = db.delete(TABLE_PLAYERS, "1", null);
//
//            Log.d(TAG, "Number of players deleted from the table: " + num);
//
//        } catch (SQLiteException e) {
//
//            System.err.println("SQLiteException: " + e.getMessage());
//
//        }
    }

    /* getPercentage: Calcula el porcentaje de victorias a partir de los partidos ganados y perdidos */
    private Double getPercentage(String wins, String losses) {
        
        Double v = Double.parseDouble(wins);
        Double d = Double.parseDouble(losses);
        Double pct = v / (v+d);
        DecimalFormat twoDForm = new DecimalFormat("#.00");
        return Double.valueOf(twoDForm.format(pct));
        
    }

}
