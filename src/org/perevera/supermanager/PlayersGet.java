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
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.perevera.supermanager.Constants.*;

/**
 *
 * @author perevera
 */
public class PlayersGet extends GetInfo {

    public static final String KEY_POSITION = "org.perevera.supermanager.position";
    private final AssetManager assetManager;
    private String filename;
    private int position;
    
    /**
        * Constructor
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

            // Create a Pattern object
            Pattern inirow = Pattern.compile("<tr>");
            Pattern second = Pattern.compile("class=\"grisizqda\"");

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

            //            System.out.println(out.toString());   //Prints the string content read from input stream
            System.out.println("Number of players: " + numPlayers);   //Prints the number of players found

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

        // Ojo con los acentos, que no los entendemos con esta codificación, y también con los nombres compuestos (dos mayúsculas) y demás casos raros
//        Pattern name = Pattern.compile(">([A-Z][a-zA-Z ]+, [A-Z][a-zA-Z]+)<");   
//        Pattern minutos = Pattern.compile(">([\\w ]+, \\w+)<");        
//        Pattern ultimaj = Pattern.compile(">([\\w ]+, \\w+)<");        
//        Pattern ultimas3 = Pattern.compile(">([\\w ]+, \\w+)<");        
//        Pattern sube = Pattern.compile(">([\\w ]+, \\w+)<");        
//        Pattern mantiene = Pattern.compile(">([\\w ]+, \\w+)<");        
//        Pattern baja = Pattern.compile(">([\\w ]+, \\w+)<");

        Pattern endrow = Pattern.compile("</tr>");

        try {

            int i = 1;
            String line;
            String[] splitRecord;
            Double pct;
            Matcher matcher;
            // Insert a new record into the Players data source.
            // You would do something similar for delete and update.
            
            ContentValues values = new ContentValues();

            player:
            while ((line = reader.readLine()) != null) {

                switch (i) {            // En función del número de línea relativo a los datos de este jugador

                    case 1:
                    case 2:

                        // Las dos primeras líneas se saltan
                        break;

                    case 3:

                        // Se extrae el nombre completo del jugador
                        Pattern name = Pattern.compile(">([\\w ]+, \\w+)<");
                        matcher = name.matcher(line);

                        if (matcher.find()) {
                            
                            String fullName = matcher.group(1);     // nombre completo
                            values.put(PLAYERS_NAME, fullName);
                            System.out.println("Nombre del jugador: " + fullName);
                            
                        }

                        break;

                    case 5:

                        // Se extrae el nombre abreviado del equipo
                        Pattern equipo = Pattern.compile(">([A-Z]+)<");
                        matcher = equipo.matcher(line);

                        if (matcher.find()) {
                            values.put(PLAYERS_TEAM, matcher.group(1));
                            System.out.println("Iniciales del equipo: " + matcher.group(1));
                        }

                        break;

                    case 6:

                        // Se extrae el balance de victorias/derrotas
                        Pattern balance = Pattern.compile(">(\\d\\d*/\\d\\d*)<");
                        matcher = balance.matcher(line);

                        if (matcher.find()) {
                            String record, wins, losses;
                            record = matcher.group(1);
                            splitRecord = record.split("/");
                            wins = splitRecord[0];
                            losses = splitRecord[1];
                            pct = getPercentage(wins, losses);
                            values.put(PLAYERS_PERCENTAGE, pct);
                            System.out.println("Balance de victorias/derrotas: " + matcher.group(1));
                            System.out.println("Porcentaje de victorias: " + pct);
                        }

                        break;
                        
                    case 7:

                        // Se extrae el promedio de valoración
                        Pattern promedio = Pattern.compile(">([0-9]+,[0-9]+)<");
                        matcher = promedio.matcher(line);

                        if (matcher.find()) {
                            String average;
                            average = matcher.group(1);
                            // Remplazar la coma por punto para separar decimales, si no la conversión posterior no funciona
                            average = average.replace(",", ".");
                            Double avg = Double.parseDouble(average);                          
                            values.put(PLAYERS_AVERAGE, avg);
                            System.out.println("Valoración promedio: " + avg);
                        }

                        break;
                        
                    case 8:

                        // Se extrae el precio
                        Pattern precio = Pattern.compile(">([0-9]+.[0-9]+)<");
                        matcher = precio.matcher(line);

                        if (matcher.find()) {
                            String price;
                            price = matcher.group(1);
                            // Eliminar el punto que separa los miles, si no la conversión posterior no funciona
                            price = price.replace(".", "");
                            Integer prc = Integer.parseInt(price);                          
                            values.put(PLAYERS_PRICE, prc);
                            System.out.println("Precio: " + prc);
                        }

                        break;                        

                    case 15:

                        // Tras la última línea se abandona
                        matcher = endrow.matcher(line);

                        if (matcher.matches()) {
                            break player;      // si se llegó al final de la información correspondiente a un jugador, salir del bucle para guardar los datos de la fila
                        }
                        break;

                    default:

                    // De momento, no hacer nada, son los que faltan implementar...
                }

                i++;

            }
            
            // La columna posición se guarda aquí
            values.put(PLAYERS_POSITION, position);

            // Aquí se inserta en la DB la fila con los datos correspondientes a este jugador
            db.insertOrThrow(TABLE_PLAYERS, null, values);

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

        try {

            // Borra la tabla de jugadores
            int num = db.delete(TABLE_PLAYERS, "1", null);

            System.out.println("Number of players deleted from the table: " + num);

        } catch (SQLiteException e) {

            System.err.println("SQLiteException: " + e.getMessage());

        }
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
