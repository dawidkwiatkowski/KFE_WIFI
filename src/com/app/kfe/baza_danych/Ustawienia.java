package com.app.kfe.baza_danych;


import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.app.kfe.R;
import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import sqlite.model.Haslo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Ustawienia extends Activity {
	
	private static final String LOG_TAG = "SettingsActivity";
	private Button btn_save;
	private EditText etName;
	  private CheckBox radioDzwiek;
	 public static final String PREFS_NAME = "MyPrefsFile";
	 MediaPlayer mpButtonClick;
	 boolean silent;
	 
	DatabaseHelper db;

	private class UpdateKeyStringDatabaseTask extends AsyncTask<URL, Integer, InputStream> {

		@Override
		protected InputStream doInBackground(URL... params) {
			InputStream result = null;
			try {
				for(URL url : params) {
					HttpURLConnection urlConnection = (HttpURLConnection)(url.openConnection());
					result = urlConnection.getInputStream();
				}
			}
			catch (IOException ioe) {}
			return result;
		}

		@Override
		protected void onPostExecute(InputStream inputStream) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while((line = reader.readLine()) != null) {
					db.addHaslo(line);
                }
			} catch (IOException e) {}
		}
	}
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ustawienia);
       
        initUiElements();
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        silent = settings.getBoolean("silentMode", true);
       radioDzwiek.setChecked(silent);


     
        
        db = new DatabaseHelper(getApplicationContext());
        List<Gracz> allGracze = db.getAllGracze();
        int ostatniGracz=0;
        if (allGracze != null && !allGracze.isEmpty()) {
			 ostatniGracz=allGracze.size()-1;
			}
        if(!allGracze.isEmpty()){
       allGracze.get(ostatniGracz).getName();
        etName.setText(allGracze.get(ostatniGracz).getName());
        }
        addListenerOnButton();
        
    }
    private void initUiElements() {
    	etName = (EditText) findViewById(R.id.etName);
    	btn_save = (Button) findViewById(R.id.btnSave);
    	radioDzwiek = (CheckBox) findViewById(R.id.radioDzwiek);
    	 mpButtonClick = MediaPlayer.create(this, R.raw.button);
    	
	}
   
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ustawienia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean result = super.onOptionsItemSelected(item);
    	switch(item.getItemId()) {
    	case R.id.action_update_keystring_database:
			try {
				URL url = new URL("https://raw.githubusercontent.com/dawidkwiatkowski/KFE_WIFI/master/123.txt");
				new UpdateKeyStringDatabaseTask().execute(url);
			}
			catch (MalformedURLException mue){}
    		break;
    		
    	case R.id.action_show_keystring_database:
    		ArrayList<Haslo> keyStrings = db.getAllHasla();
    		for(Haslo keyString : keyStrings) {
    			Log.d(LOG_TAG, keyString.getHaslo());
    		}
    		break;
    	}
    	return result;
    }
    
    
    
    
    
    
    public void addListenerOnButton() {
    	 
    	
    
    	
    	btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mpButtonClick != null && silent){
					mpButtonClick.start();
					}
				String nazwa_gracza = etName.getText().toString();
				
				 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			      SharedPreferences.Editor editor = settings.edit();
			      
			      editor.putBoolean("silentMode",radioDzwiek.isChecked());
			      // Commit the edits!
			      editor.commit();

				
				if(nazwa_gracza.equals("")){
					etName.setError("Your task description couldn't be empty string.");
				} 
				
				
				else {
					int i=0;
					for (Gracz gracz : db.getAllGracze()) {
						if(nazwa_gracza.equals(gracz.getName())){
							
							 Toast.makeText(getBaseContext(),"Gracz "+ nazwa_gracza +" " +"istnieje ju� w bazie ", Toast.LENGTH_LONG).show();
							 i++;
							 break;
						}
					}
					if(i==0){
					Gracz player = new Gracz(nazwa_gracza);
					db.createGracz(player);
					 Toast.makeText(getBaseContext(),"Gracz zosta� zapisany", Toast.LENGTH_LONG).show();}

				}
				db.closeDB();
			}

    	});



	}
    
}