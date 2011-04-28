package android.health.dietlogging;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This service is designed to import the FDA food data into a SQLite database on the app's first run
 * 
 * @author Joel Botner
 */
public class FDAimportService extends Service{
	
    @Override
    public void onCreate() {
    	super.onCreate();
    	Log.i("FDAimportService", "FDA import has begun");
    	PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("foodDatabaseImported", false).commit();
    	
    	//Grab the FDA Database and extract the contents (all this is done in a background thread)
    	(new Thread(new Runnable(){
    		@Override
    		public void run(){
    	AssetManager theAssets = getResources().getAssets();
    	Scanner theData = null;
    	PedometerDatabaseAdapter theFoodDB = new PedometerDatabaseAdapter(FDAimportService.this).open();
    	try {
			theData = new Scanner(theAssets.open("fda_food_consolidated.txt"));
		} catch (IOException e) {
			Log.i("FDAimportService", "FDA import blew up");
			FDAimportService.this.stopSelf();
		}
		
		//Extract and record the data
		while (theData.hasNextLine()){
			StringTokenizer attributeExtractor = new StringTokenizer(theData.nextLine());
			theFoodDB.createFoodEntry(attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"),
					attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"),
					attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"),
					attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"),
					attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"), attributeExtractor.nextToken("\t"));
		}
		
		//Finish things up
		//theFoodDB.close();
		theData.close();
		theFoodDB.close();
		PreferenceManager.getDefaultSharedPreferences(FDAimportService.this).edit().putBoolean("foodDatabaseImported", true).commit();
		
		Log.i("FDAimportService", "FDA import has finished");
    		}
    	})).start();
		this.stopSelf();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
