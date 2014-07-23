 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.perevera.supermanager;

import static android.provider.BaseColumns._ID;
import static org.perevera.supermanager.Constants.TABLE_NAME;
import static org.perevera.supermanager.Constants.NAME;
import static org.perevera.supermanager.Constants.TEAM;
import static org.perevera.supermanager.Constants.PERCENTAGE;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author perevera
 */
public class SupermanagerData extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "supermanager.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Create a helper object for the Events database
     */
    public SupermanagerData(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL," + TEAM + " TEXT NOT NULL,"
                + PERCENTAGE + " REAL NOT NULL" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
