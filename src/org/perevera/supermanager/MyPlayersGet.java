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
    private int position;
    private String filename;
    
    // Define los patrones para encontrar la información relevante
    private Pattern status = Pattern.compile("alt=\"Jugador (\\w)");
    private Pattern identifier = Pattern.compile("\"http://www\\.acb\\.com/stspartidojug\\.php\\?cod_jugador=([0-9A-Z][0-9A-Z][0-9A-Z])\" target=\"_blank\"><font color=\"black\">([\\w ]+, \\w+)<");
    private Pattern team = Pattern.compile(">([A-Z]+)<");
    private Pattern purchase = Pattern.compile(">([0-9]*.?[0-9]+) \\(j&ordf;([0-9]+)\\)<");
 

    /**
     * Constructor
     *
     * Solo para fase de pruebas ya que elige a piñón fijo un fichero en lugar
     * de cargar la página de la web
     *
     * @param ctx -> Objeto de tipo Context
     * @param url -> URL de la página de donde cargar los datos (nula en la fase
     * de tests)
     * @param assetManager -> Objeto de tipo AssetManager (solo para la fase de
     * tests)
     */
    public MyPlayersGet(Context ctx, String url, AssetManager assetManager) {

        super(ctx, url);

        this.assetManager = assetManager;

        filename = "Ver_Equipo_Melodramones.html";

    }

    /**
     * Carga la página HTML de un fichero guardado, solo para la fase de tests
     *
     * @param url -> La URL de dónde cargar la página (Este parámetro me lo paso
     * por el forro)
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
     * Lee el fichero HTML para encontrar las secciones donde hay información de
     * equipos
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

            // El siguiente patrón encuentra la primera línea de una sección de jugadores (bases, aleros o pivots)
            Pattern inirow = Pattern.compile("<tr><td width=\"21\"");

            Matcher m1;

            while ((line = reader.readLine()) != null) {

                m1 = inirow.matcher(line);

                if (m1.find()) {

                    numPlayers++;
                    position++;
                    extractRecord(reader);

                }

            }

            //            System.out.println(out.toString());   //Prints the string content read from input stream
            System.out.println("Number of players: " + numPlayers);   //Prints the number of teams found

            reader.close();

            return numPlayers;

        } catch (IOException e) {

            System.err.println("IOException: " + e.getMessage());
            return -1;

        }

    }

    /**
     * Extrae información de un equipo leyendo línea a línea y la guarda en un
     * registro de la DB
     *
     * @param reader -> Objeto de tipo BufferedReader con las líneas leídas del
     * fichero HTML
     */
    protected void extractRecord(BufferedReader reader) {

        Pattern endrow = Pattern.compile("</tr>");

        try {

            int i = 1;
            String line;
            String[] splitRecord;
            Double pct;
            Matcher matcher;
            int numPlayers = 0;
           
            ContentValues values = new ContentValues();

            players:
            while ((line = reader.readLine()) != null) {

                // Filtramos las lineas en blanco que suelen venir en este fichero
                if (line.isEmpty()) {
                    continue;
                }

                switch (i) {            // En función del número de línea relativo

                    case 1:     // Categoría de jugador

//                        matcher = status.matcher(line);
//
//                        if (matcher.find()) {
//
//                            String tipoJugador = matcher.group(1);     // tipo de jugador
//                            values.put(PLAYERS_NAME, fullName);
//                            System.out.println("Tipo de jugador: " + tipoJugador);
//
//                        }

                        break;

                    case 2:     // ID y nombre de jugador (solo nos quedamos con el ID)

//                    matcher = name.matcher(line);
                        matcher = identifier.matcher(line);

                        if (matcher.find()) {

                            String playerId = matcher.group(1);     // ID de jugador
                            String fullName = matcher.group(2);     // nombre completo
                            values.put(PLAYERS_PLAYER_ID, playerId);
                            System.out.println("ID de jugador: " + playerId);
                            System.out.println("Nombre de jugador: " + fullName);
                            
                        }

                        break;

                    case 4:     // Abreviatura de equipo ACB

                        matcher = team.matcher(line);

                        if (matcher.find()) {

                            String teamShortName = matcher.group(1);     // Abreviatura de equipo ACB
                            values.put(PLAYERS_TEAM_ID, teamShortName);
                            System.out.println("Abreviatura de equipo: " + teamShortName);
                        }

                        break;
                        
                    case 5:     // Cotización

                        matcher = purchase.matcher(line);

                        if (matcher.find()) {

                            String cotiz = matcher.group(1);
                            String jda = matcher.group(2);
                            // Eliminar el punto
                            cotiz = cotiz.replace(".", "");
                            Integer precioCompra = Integer.parseInt(cotiz);   
                            Integer jornadaCompra = Integer.parseInt(jda);
                            values.put(PLAYERS_COST, precioCompra);
                            values.put(PLAYERS_DAY, jornadaCompra);
                            System.out.println("Precio pagado: " + precioCompra);
                            System.out.println("Jornada: " + jornadaCompra);
                            numPlayers++;

                        }

                        break;

                    default:        // Las líneas no significativas nos las vamos saltando
                        
                        break;

                }

                i++;

                switch (position) {

                    case 1:     // Bases, habrá 3

                        if (numPlayers == 3) {
                            break players;
                        }
                        break;

                    case 2:     // Aleros, habrá 4

                        if (numPlayers == 4) {
                            break players;
                        }
                        break;

                    case 3:     // Pivots, habrá 4

                        if (numPlayers == 4) {
                            break players;
                        }
                        break;

                    default:

                        break;

                }

            }

            // La columna posición se guarda aquí
//            values.put(PLAYERS_POSITION, position);
//             Aquí se inserta en la DB la fila con los datos correspondientes a este equipo
            db.insertOrThrow(TABLE_PLAYERS2TEAMS, null, values);
            
        } catch (IOException e) {

            System.err.println("IOException: " + e.getMessage());

        } catch (NumberFormatException e) {

            System.err.println("NumberFormatException: " + e.getMessage());

        } catch (SQLiteException e) {

            System.err.println("SQLiteException: " + e.getMessage());

        }

    }

    /**
     * No se hace nada puesto que la tabla de jugadores por equipos ya se borró
     * en la clase MyTeamsGet
     *
     */
    protected void deleteTables() {

    }

}
