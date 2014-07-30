package org.perevera.supermanager;

import android.provider.BaseColumns;

/**
 *
 * @author perevera
 */

public interface Constants extends BaseColumns {

    // Nombres de tablas
    public static final String TABLE_PLAYERS = "Players";
    public static final String TABLE_TEAMS = "Teams";
    public static final String TABLE_PLAYERS2TEAMS = "Players2Teams";
    // Columnas de la tabla de jugadores
    public static final String PLAYERS_NAME = "name";
    public static final String PLAYERS_POSITION = "position";
    public static final String PLAYERS_TEAM = "team";
    public static final String PLAYERS_PERCENTAGE = "percentage";
    public static final String PLAYERS_AVERAGE = "average";
    public static final String PLAYERS_PRICE = "price";
    // Columnas de la tabla de equipos
    public static final String TEAMS_ID = "id";
    public static final String TEAMS_NAME = "name";
    public static final String TEAMS_POINTS = "points";
    public static final String TEAMS_MONEY = "money";
    public static final String TEAMS_GAMEDAY = "gameday";
}
