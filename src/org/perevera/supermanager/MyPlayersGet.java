/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.perevera.supermanager.Constants.*;

/**
 *
 * @author perevera
 */
public class MyPlayersGet extends GetInfo {
    
    private final AssetManager assetManager;
    private String filename;
    
    /**
        * Constructor
        * 
        * Solo para fase de pruebas ya que elige a piñón fijo un fichero en lugar de cargar la página de la web
        *
        * @param ctx -> Objeto de tipo Context
        * @param url -> URL de la página de donde cargar los datos (nula en la fase de tests)
        * @param assetManager -> Objeto de tipo AssetManager (solo para la fase de tests)
        */
    public MyPlayersGet(Context ctx, String url, AssetManager assetManager) {
        
        super(ctx, url);
        
        this.assetManager = assetManager;
        
        filename =  "Ver_Equipo_Melodramones";
        
    }
    
    /**
        * Carga la página HTML de un fichero guardado, solo para la fase de tests
        *
        *  @param url -> La URL de dónde cargar la página (Este parámetro me lo paso por el forro)
        * 
        * @return El objeto InputStream con el contenido de la página leída
        */
    @Override
    protected InputStream downloadWebPage(String url) {              

        InputStream content = null;
        
        try {
            
            content = assetManager.open("html/" + filename);

        } catch (IOException e) {
            
            System.err.println("SQLiteException: " + e.getMessage());
            
        }

        return content;
    }

    /**
        * Lee el fichero HTML para encontrar las secciones donde hay información de equipos 
        *
        * @param in -> El objeto InputStream con el contenido de la página leída
        * 
        * @return Devuelve el nº de registros procesados
        */
    protected int processHtmlFile(InputStream in) {

        int numTeams = 0;

        try {

            // La codificación del fichero es: HTML document, ISO-8859 text, with very long lines
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("ISO-8859-1")));

            StringBuilder out = new StringBuilder();
            String line;

            // Create a Pattern object
            
            Pattern inirow = Pattern.compile("<tr><td width=\"21\"");

            Matcher m1;

            while ((line = reader.readLine()) != null) {

                m1 = inirow.matcher(line);

                if (m1.find()) {

                    numTeams++;
                    extractRecord(reader);

                }

            }

            //            System.out.println(out.toString());   //Prints the string content read from input stream
            System.out.println("Number of players: " + numTeams);   //Prints the number of teams found

            reader.close();
            
            return numTeams;

        } catch (IOException e) {

            System.err.println("IOException: " + e.getMessage());
            return -1;

        }

    }

    /**
        * Extrae información de un equipo leyendo línea a línea y la guarda en un registro de la DB
        *
        * @param reader -> Objeto de tipo BufferedReader con las líneas leídas del fichero HTML
         */    
    protected void extractRecord(BufferedReader reader) {

        Pattern endrow = Pattern.compile("</tr>");

        try {

            int i = 1;
            String line = "";
            String[] splitRecord;
            Double pct;
            Matcher matcher;
            // Insert a new record into the Players data source.
            // You would do something similar for delete and update.
            
            ContentValues values = new ContentValues();

            players:
            do {        // Aquí no hay que leer una nueva línea ya que la línea que viene ya contiene información relevante

                switch (i) {            // En función del número de línea relativo a los datos de este equipo

                    case 1:
                        
                        // Se extrae el código del equipo
//                        Pattern id = Pattern.compile("verequipo.php?id_equ=(\\d\\d\\d\\d\\d\\d)");
                        Pattern id = Pattern.compile("id_equ=([0-9]+)\">([\\w ]+)<");
                        matcher = id.matcher(line);
                        
                        if (matcher.find()) {

                            String sId = matcher.group(1);                  // ID del equipo
                            Integer teamId = Integer.parseInt(sId);         // Se convierte a entero
                            values.put(TEAMS_ID, teamId);
                            System.out.println("ID del equipo: " + teamId);

//                        }
//
//                        // Se extrae el nombre del equipo
//                        Pattern name = Pattern.compile(">([\\w ]+)<");
//                        matcher = name.matcher(line);
//
//                        if (matcher.find()) {
                            
                            String teamName = matcher.group(2);     // nombre del equipo
                            values.put(TEAMS_NAME, teamName);
                            System.out.println("Nombre del equipo: " + teamName);
                            
                        }

                        break;

//                    case 5:
//
//                        // Se extrae el nombre abreviado del equipo
//                        Pattern equipo = Pattern.compile(">([A-Z]+)<");
//                        matcher = equipo.matcher(line);
//
//                        if (matcher.find()) {
//                            values.put(PLAYERS_TEAM, matcher.group(1));
//                            System.out.println("Iniciales del equipo: " + matcher.group(1));
//                        }
//
//                        break;
//
//                    case 6:
//
//                        // Se extrae el balance de victorias/derrotas
//                        Pattern balance = Pattern.compile(">(\\d\\d*/\\d\\d*)<");
//                        matcher = balance.matcher(line);
//
//                        if (matcher.find()) {
//                            String record, wins, losses;
//                            record = matcher.group(1);
//                            splitRecord = record.split("/");
//                            wins = splitRecord[0];
//                            losses = splitRecord[1];
//                            pct = getPercentage(wins, losses);
//                            values.put(PLAYERS_PERCENTAGE, pct);
//                            System.out.println("Balance de victorias/derrotas: " + matcher.group(1));
//                            System.out.println("Porcentaje de victorias: " + pct);
//                        }
//
//                        break;
//                        
//                    case 7:
//
//                        // Se extrae el promedio de valoración
//                        Pattern promedio = Pattern.compile(">([0-9]+,[0-9]+)<");
//                        matcher = promedio.matcher(line);
//
//                        if (matcher.find()) {
//                            String average;
//                            average = matcher.group(1);
//                            // Remplazar la coma por punto para separar decimales, si no la conversión posterior no funciona
//                            average = average.replace(",", ".");
//                            Double avg = Double.parseDouble(average);                          
//                            values.put(PLAYERS_AVERAGE, avg);
//                            System.out.println("Valoración promedio: " + avg);
//                        }
//
//                        break;
//                        
//                    case 8:
//
//                        // Se extrae el promedio de valoración
//                        Pattern precio = Pattern.compile(">([0-9]+.[0-9]+)<");
//                        matcher = precio.matcher(line);
//
//                        if (matcher.find()) {
//                            String price;
//                            price = matcher.group(1);
//                            // Eliminar el punto que separa los miles, si no la conversión posterior no funciona
//                            price = price.replace(".", "");
//                            Integer prc = Integer.parseInt(price);                          
//                            values.put(PLAYERS_PRICE, prc);
//                            System.out.println("Precio: " + prc);
//                        }
//
//                        break;                        
//
//                    case 15:
//
//                        // Tras la última línea se abandona
//                        matcher = endrow.matcher(line);
//
//                        if (matcher.matches()) {
//                            break player;      // si se llegó al final de la fila correspondiente a un jugador, salir del bucle para guardar los datos de la fila
//                        }
//                        break;
                        
                    case 7:

                        // Tras la última línea se abandona
                        matcher = endrow.matcher(line);

                        if (matcher.matches()) {
                            break players;      // si se llegó al final de la información correspondiente a un equipo, salir del bucle para guardar los datos de la fila
                        }
                        break;

                    default:

                    // De momento, no hacer nada, son los que faltan implementar...
                }

                i++;

            } while ((line = reader.readLine()) != null);
            
            // La columna posición se guarda aquí
//            values.put(PLAYERS_POSITION, position);

            // Aquí se inserta en la DB la fila con los datos correspondientes a este equipo
            db.insertOrThrow(TABLE_TEAMS, null, values);

        } catch (IOException e) {

            System.err.println("IOException: " + e.getMessage());

        } catch (NumberFormatException e) {

            System.err.println("NumberFormatException: " + e.getMessage());
            
        } catch (SQLiteException e) {

            System.err.println("SQLiteException: " + e.getMessage());
                        
        }

    }
    
    /**
        * No se hace nada puesto que la tabla de jugadores por equipos ya se borró en la clase MyTeamsGet
        *
          */
    protected void deleteTables() {

    }
    
}

