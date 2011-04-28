package android.health.gui;

/**
 * This class takes care of the display for the Meal Logger button
 * from the default Diet Tab
 * 
 * @author Dan Abrams
 */

import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.health.manager.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MealLoggerActivity extends Activity {
	private TextView myDisplayDate;
	private TextView caloriesEaten;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_logger_tab);
        final Button button_back_logger = (Button) findViewById(R.id.button_back_logger);
        button_back_logger.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent().setClass(MealLoggerActivity.this, TabSelector.class);
            	startActivity(intent);
                
            }
        });
        
        caloriesEaten = (TextView) findViewById(R.id.total_calories_logger_number);
        caloriesEaten.setText((DietTabActivity.theDB.getCalsEatenSince(DietTabActivity.currentTime) - (DietTabActivity.theDB.getCalsEatenSince(DietTabActivity.currentTime + 1000 * 3600 * 24))) + "");
        
        final Button button_add_meal = (Button) findViewById(R.id.button_add_meal);
        button_add_meal.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(PreferenceManager.getDefaultSharedPreferences(MealLoggerActivity.this).getBoolean("foodDatabaseImported", false)){
            		// Perform action on clicks
            		Intent intent = new Intent().setClass(MealLoggerActivity.this, MealMakerActivity.class);
            		startActivity(intent);
            	}else{
            		AlertDialog alertDialog = new AlertDialog.Builder(MealLoggerActivity.this).create();
            		alertDialog.setMessage("Please wait, still importing the FDA food database.");
            		final AlertDialog theDialog = alertDialog;
            		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            		   public void onClick(DialogInterface dialog, int which) {
            		      theDialog.dismiss();
            		   }
            		});
            		alertDialog.show();
            	}
                
            }
        });
        myDisplayDate = (TextView) findViewById(R.id.date_display_logger);
        
        myDisplayDate.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(DietTabActivity.myMonth + 1).append("-")
                        .append(DietTabActivity.myDay).append("-")
                        .append(DietTabActivity.myYear).append(" "));
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	public void onResume(){
		super.onResume();
		caloriesEaten.setText((DietTabActivity.theDB.getCalsEatenSince(DietTabActivity.currentTime) - (DietTabActivity.theDB.getCalsEatenSince(DietTabActivity.currentTime + 1000 * 3600 * 24))) + "");
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
