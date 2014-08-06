package org.perevera.supermanager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SuperManager extends Activity implements OnClickListener
{
    private static final String TAG = "SuperManager";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Instancia el AssetManager para manejar recursos de tipo fichero
        AssetManager assetManager = getAssets();
        
        // La carga de datos se hará de otra manera, mediante threads asíncronos, pero de momento lo hacemos así:
        
        // Inicia el thread para carga de la tabla de bases en el mercado
        PlayersGet asyncLoadBases = new PlayersGet(this, null, assetManager, 1);
        asyncLoadBases.execute();
        
        // Inicia el thread para carga de la lista de equipos de usuario
        TeamsGet asyncLoadTeams = new TeamsGet(this, null, assetManager);
        asyncLoadTeams.execute();
        
        // Set up click listeners for all the buttons
        View basesButton = findViewById(R.id.btnBases);
        basesButton.setOnClickListener(this);
        View alerosButton = findViewById(R.id.btnAleros);
        alerosButton.setOnClickListener(this);
        View pivotsButton = findViewById(R.id.btnPivots);
        pivotsButton.setOnClickListener(this);
        
/*        // DEBUG: Me salto el login por ahora!
        // Start up login activity
        Log.d(TAG, "Starting login activity");
        Intent intent = new Intent(this, Login.class);
        //        intent.putExtra(Game.KEY_DIFFICULTY, i);
        startActivity(intent);  */

    }
    
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBases:
                Intent b = new Intent(this, PlayersList.class);
//                b.putExtra(PlayersList.KEY_POSITION, 1);
                startActivity(b);
                break;
            case R.id.btnAleros:
                Intent a = new Intent(this, PlayersList.class);
//                a.putExtra(PlayersList.KEY_POSITION, 2);
                startActivity(a);
                break;
            case R.id.btnPivots:
                Intent p = new Intent(this, PlayersList.class);
//                p.putExtra(PlayersList.KEY_POSITION, 2);
                startActivity(p);
                break;
        }
    }
}
