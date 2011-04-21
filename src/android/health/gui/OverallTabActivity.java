package android.health.gui;

/**
 * This class takes care of the display for the default Overall Tab
 * 
 * @author Dan Abrams
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.health.dietlogging.DietDatabaseAdapter;
import android.health.manager.HealthCoach;
import android.health.manager.R;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class OverallTabActivity extends Activity {
	boolean dbLoaded;
	DietDatabaseAdapter dietDB;
	
	public static OverallTabActivity me;

	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_tab);
        me = this;
        createDatabase task = new createDatabase();
       	dbLoaded = task.equals(dbLoaded);

        
        //General variables
        PedometerDatabaseAdapter theDB = new PedometerDatabaseAdapter(this).open();
        HealthCoach theCoach = new HealthCoach(PreferenceManager.getDefaultSharedPreferences(this), theDB);
        
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
        
        
        //John's work:
        
        
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
	
	private class createDatabase extends AsyncTask<Boolean, Void, Boolean> {
        protected Boolean doInBackground(Boolean...Boolean) {
        	dietDB = dietDB.open();
        	return true;
        }
    }

}
