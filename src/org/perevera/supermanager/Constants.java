package org.perevera.supermanager;

import android.provider.BaseColumns;

/**
 *
 * @author perevera
 */

public interface Constants extends BaseColumns {

    // Tabla de jugadores de mercado
    public static final String TABLE_PLAYERS = "Players";
    public static final String PLAYERS_ID = "id";
    public static final String PLAYERS_NAME = "name";
    public static final String PLAYERS_POSITION = "position";
    public static final String PLAYERS_TEAM = "team";
    public static final String PLAYERS_PERCENTAGE = "percentage";
    public static final String PLAYERS_AVERAGE = "average";
    public static final String PLAYERS_PRICE = "price";
    public static final String PLAYERS_TIME = "time";
    
    // Tabla de equipos de usuario
    public static final String TABLE_TEAMS = "Teams";
    public static final String TEAMS_ID = "id";
    public static final String TEAMS_NAME = "name";
    public static final String TEAMS_POINTS = "points";
    public static final String TEAMS_MONEY = "money";
    public static final String TEAMS_GAMEDAY = "gameday";
    // Tabla de jugadores de equipos de usuario (las que no existen ya)
    public static final String TABLE_PLAYERS2TEAMS = "Players2Teams";
    public static final String PLAYERS_PLAYER_ID = "player_id";
    public static final String PLAYERS_TEAM_ID = "team_id";
    public static final String PLAYERS_COST = "costpurchase";
    public static final String PLAYERS_DAY = "gameday";
    
}
