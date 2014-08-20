/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.perevera.supermanager;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import static android.provider.BaseColumns._ID;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import static org.perevera.supermanager.Constants.*;

/**
 *
 * @author perevera
 */
public class PlayersList extends ListActivity {

    private static final String TAG = "PlayersList";
    private static final String[] FROM = {_ID, PLAYERS_NAME, PLAYERS_TEAM, PLAYERS_PERCENTAGE, PLAYERS_AVERAGE, PLAYERS_PRICE};
    private static final int[] TO = {R.id.rowid, R.id.name, R.id.team, R.id.percent, R.id.average, R.id.price};
//    private static String ORDER_BY = TIME + " DESC";
    private DatabaseHelper players;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerslist);

        try {

            // Crea la instancia para acceder a la b.d.
            players = new DatabaseHelper(this);

            // Crea el cursor para obtener los datos leídos
            Cursor cursor = getPlayers();
            
            int count = cursor.getCount();

            // Visualiza la lista de jugadores
            showPlayers(cursor);

        } finally {
//            finish();
        }

    }

//    private Cursor getPlayers() {
//        // Run query
//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//        String[] projection = new String[]{ContactsContract.Contacts._ID,
//            ContactsContract.Contacts.DISPLAY_NAME};
//        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
//                + ("1") + "'";
//        String[] selectionArgs = null;
//        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
//                + " COLLATE LOCALIZED ASC";
//
//        return managedQuery(uri, projection, selection, selectionArgs,
//                sortOrder);
//    }
    
    private Cursor getPlayers() {
        // Perform a managed query. The Activity will handle closing
        // and re-querying the cursor when needed.
        SQLiteDatabase db = players.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_PLAYERS, FROM, null, null, null, null, null);
        String sCursor = DatabaseUtils.dumpCursorToString(cursor);
        Log.d(TAG, "Cursor: " + sCursor);
        startManagingCursor(cursor);
        return cursor;
    }
    
    private void showPlayers(Cursor cursor) {
        // Set up data binding
        // PENDIENTE: Usar otro tipo de adaptador que permita no tener que mostrar el rowid
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, FROM, TO);
        setListAdapter(adapter);
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        String item = (String) getListAdapter().getItem(position);
//        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
//        // Aquí terminamos esta actividad
//        finish();
//    }
    
}
