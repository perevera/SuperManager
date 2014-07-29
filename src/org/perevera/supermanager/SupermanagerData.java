 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.perevera.supermanager;

//import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.InputStream;
//import static org.perevera.supermanager.Constants.*;
//import static org.perevera.supermanager.Constants.TABLE_NAME;
//import static org.perevera.supermanager.Constants.NAME;
//import static org.perevera.supermanager.Constants.TEAM;
//import static org.perevera.supermanager.Constants.PERCENTAGE;
//import static org.perevera.supermanager.Constants.AVERAGE;
//import static org.perevera.supermanager.Constants.PRICE;

/**
 *
 * @author perevera
 */
public class SupermanagerData extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "supermanager.db";
    private static final int DATABASE_VERSION = 1;
    private final Context myContext;

    /**
     * Create a helper object for the Events database
     */
    public SupermanagerData(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = ctx;
    }

    /*
        * Create the database by reading a sql file and executing the commands in it.
        */
    @Override
    public void onCreate(SQLiteDatabase db) {

//        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL," + TEAM + " TEXT NOT NULL,"
//                + PERCENTAGE + " REAL NOT NULL," + AVERAGE + " REAL NOT NULL," + PRICE + " INTEGER NOT NULL" + ");");
        
        try {

            InputStream is = myContext.getResources().getAssets().open("sql/create_database.sql");

            String[] statements = FileHelper.parseSqlFile(is);

            for (String statement : statements) {
                db.execSQL(statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        * Actualizar a una nueva versión la b.d.: primero se borran todas las tablas, luego se llama al método de creación
        */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        // Se procesa el script de borrado de todas las tablas
        try {
            InputStream is = myContext.getResources().getAssets().open("sql/drop_database.sql");

            String[] statements = FileHelper.parseSqlFile(is);

            for (String statement : statements) {
                db.execSQL(statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Se llama al método de creación de las tablas
        onCreate(db);
    }
    
   /*
        * Actualizar a una antigua versión la b.d.: primero se borran todas las tablas, luego se llama al método de creación
        * Este método se eliminará cuando el código esté estable y solo hace lo mismo que en caso de actualizar a una nueva versión
        */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        onUpgrade(db, oldVersion, newVersion);

    }    
}