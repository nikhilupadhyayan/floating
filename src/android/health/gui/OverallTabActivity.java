package android.health.gui;

/**
 * This class takes care of the display for the default Overall Tab
 * 
 * @author Dan Abrams and Joel Botner
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.health.dietlogging.FDAimportService;
import android.health.manager.HealthCoach;
import android.health.manager.R;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class OverallTabActivity extends Activity {
	boolean dbLoaded;
	//DietDatabaseAdapter dietDB;
	PedometerDatabaseAdapter theDB;
	HealthCoach theCoach;
	private static boolean runningImporter = false;
	
	public static OverallTabActivity me;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_tab);
        
        //General variables
        theDB = new PedometerDatabaseAdapter(this).open();
        theCoach = new HealthCoach(PreferenceManager.getDefaultSharedPreferences(this), theDB, this);
        
        //Here we collect the references
        final TextView burned_one_day = (TextView)findViewById(R.id.burned_one_day);
        final TextView burned_one_week = (TextView)findViewById(R.id.burned_one_week);
        final TextView burned_thirty_days = (TextView)findViewById(R.id.burned_thirty_days);
        
        final TextView consumed_one_day = (TextView)findViewById(R.id.consumed_one_day);
        final TextView consumed_one_week = (TextView)findViewById(R.id.consumed_one_week);
        final TextView consumed_thirty_days = (TextView)findViewById(R.id.consumed_thirty_days);
        
        final TextView total_one_day = (TextView)findViewById(R.id.total_one_day);
        final TextView total_one_week = (TextView)findViewById(R.id.total_one_week);
        final TextView total_thirty_days = (TextView)findViewById(R.id.total_thirty_days);
        
        //Now we set the fields
        burned_one_day.setText("" + theCoach.getTotalCaloriesBurned(1));
        burned_one_week.setText("" + theCoach.getTotalCaloriesBurned(2));
        burned_thirty_days.setText("" + theCoach.getTotalCaloriesBurned(3));
        
        consumed_one_day.setText("" + theCoach.getTotalCaloriesEaten(1));
        consumed_one_week.setText("" + theCoach.getTotalCaloriesEaten(2));
        consumed_thirty_days.setText("" + theCoach.getTotalCaloriesEaten(3));
        
        int recommendation = theCoach.recommendCalories(1);
        if(recommendation < 0){
        	total_one_day.setTextColor(Color.GREEN);
        	total_one_day.setText("+" + (-1 * recommendation));
        }else{
        	total_one_day.setTextColor(Color.RED);
        	total_one_day.setText("-" + (recommendation));
        }
        
        recommendation = theCoach.recommendCalories(2);
        if(recommendation < 0){
        	total_one_week.setTextColor(Color.GREEN);
        	total_one_week.setText("+" + (-1 * recommendation));
        }else{
        	total_one_week.setTextColor(Color.RED);
        	total_one_week.setText("-" + (recommendation));
        }
        
        recommendation = theCoach.recommendCalories(3);
        if(recommendation < 0){
        	total_thirty_days.setTextColor(Color.GREEN);
        	total_thirty_days.setText("+" + (-1 * recommendation));
        }else{
        	total_thirty_days.setTextColor(Color.RED);
        	total_thirty_days.setText("-" + (recommendation));
        }
        
        //PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("foodDatabaseImported", false).commit(); //UNCOMMENT THIS TO FORCE REBUILD OF FDA DATABASE ON START
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("foodDatabaseImported", false) && !runningImporter){
        	runningImporter = true;
        	AlertDialog alertDialog = new AlertDialog.Builder(OverallTabActivity.this).create();
    		alertDialog.setMessage("Since this is your first run of the Health Manager, we must build the Food Nutrients database. All logging functionality will be disabled until this operation is complete. Depending on your phone, it should take about 10-15 minutes to complete.");
    		final AlertDialog theDialog = alertDialog;
    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    		   public void onClick(DialogInterface dialog, int which) {
    		      theDialog.dismiss();
    		   }
    		});
    		alertDialog.show();
        	(new Runnable(){
				@Override
				public void run() {
					OverallTabActivity.this.startService(new Intent(OverallTabActivity.this, FDAimportService.class));
				}
        	}).run();
        }
        
	}
	
	public void onResume(){
		super.onResume();
		
		//Here we collect the references
        final TextView burned_one_day = (TextView)findViewById(R.id.burned_one_day);
        final TextView burned_one_week = (TextView)findViewById(R.id.burned_one_week);
        final TextView burned_thirty_days = (TextView)findViewById(R.id.burned_thirty_days);
        
        final TextView consumed_one_day = (TextView)findViewById(R.id.consumed_one_day);
        final TextView consumed_one_week = (TextView)findViewById(R.id.consumed_one_week);
        final TextView consumed_thirty_days = (TextView)findViewById(R.id.consumed_thirty_days);
        
        final TextView total_one_day = (TextView)findViewById(R.id.total_one_day);
        final TextView total_one_week = (TextView)findViewById(R.id.total_one_week);
        final TextView total_thirty_days = (TextView)findViewById(R.id.total_thirty_days);
        
        //Now we set the fields
        burned_one_day.setText("" + theCoach.getTotalCaloriesBurned(1));
        burned_one_week.setText("" + theCoach.getTotalCaloriesBurned(2));
        burned_thirty_days.setText("" + theCoach.getTotalCaloriesBurned(3));
        
        consumed_one_day.setText("" + theCoach.getTotalCaloriesEaten(1));
        consumed_one_week.setText("" + theCoach.getTotalCaloriesEaten(2));
        consumed_thirty_days.setText("" + theCoach.getTotalCaloriesEaten(3));
        
        int recommendation = theCoach.recommendCalories(1);
        if(recommendation < 0){
        	total_one_day.setTextColor(Color.GREEN);
        	total_one_day.setText("+" + (-1 * recommendation));
        }else{
        	total_one_day.setTextColor(Color.RED);
        	total_one_day.setText("-" + (recommendation));
        }
        
        recommendation = theCoach.recommendCalories(2);
        if(recommendation < 0){
        	total_one_week.setTextColor(Color.GREEN);
        	total_one_week.setText("+" + (-1 * recommendation));
        }else{
        	total_one_week.setTextColor(Color.RED);
        	total_one_week.setText("-" + (recommendation));
        }
        
        recommendation = theCoach.recommendCalories(3);
        if(recommendation < 0){
        	total_thirty_days.setTextColor(Color.GREEN);
        	total_thirty_days.setText("+" + (-1 * recommendation));
        }else{
        	total_thirty_days.setTextColor(Color.RED);
        	total_thirty_days.setText("-" + (recommendation));
        }
		
	}
	
	public void onDestroy(){
		super.onDestroy();
		theDB.close();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.preferences:
			startActivity(new Intent(this, Preferences.class));
			return true;
		}
		return false;
	}
}
