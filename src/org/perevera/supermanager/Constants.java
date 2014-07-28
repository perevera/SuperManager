package org.perevera.supermanager;

import android.provider.BaseColumns;

/**
 *
 * @author perevera
 */

public interface Constants extends BaseColumns {

    public static final String TABLE_NAME = "players";
    // Columns in the players table
    public static final String NAME = "name";
    public static final String TEAM = "team";
    public static final String PERCENTAGE = "percentage";
    public static final String AVERAGE = "average";
    public static final String PRICE = "price";
}
