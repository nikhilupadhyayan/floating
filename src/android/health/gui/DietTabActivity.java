package android.health.gui;

/**
 * This class takes care of the display for the default Diet Tab
 * 
 * @author Dan Abrams
 */

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.health.manager.HealthCoach;
import android.health.manager.R;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class DietTabActivity extends Activity {
	private TextView myDisplayDate;
    private Button myPickDate;
    static int myYear;
    static int myMonth;
    static int myDay;
    public static PedometerDatabaseAdapter theDB;
    private TextView caloriesBurned;
	private TextView caloriesEaten;
	private TextView calorieBalance;
	public static HealthCoach theCoach;
	public static long currentTime = ((long)(new GregorianCalendar().getTimeInMillis() / 8640000)) * 8640000;

    static final int DATE_DIALOG_ID = 0;
	public void onCreate(Bundle savedInstanceState) {
		theDB = new PedometerDatabaseAdapter(this).open();
		theCoach = new HealthCoach(PreferenceManager.getDefaultSharedPreferences(this), theDB, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diet_tab);
        TabSelector.currentTab = 1;
        
     // capture our View elements
        myDisplayDate = (TextView) findViewById(R.id.display_date);
        calorieBalance = (TextView) findViewById(R.id.total_number);
        caloriesBurned = (TextView) findViewById(R.id.burned_number);
        caloriesEaten = (TextView) findViewById(R.id.consumed_number);
        myPickDate = (Button) findViewById(R.id.pick_date);

        // add a click listener to the button
        myPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        // get the current date
        final Calendar c = Calendar.getInstance();
        myYear = c.get(Calendar.YEAR);
        myMonth = c.get(Calendar.MONTH);
        myDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date (this method is below)
        updateDisplay();
        
        final Button button_meal_logger = (Button) findViewById(R.id.button_meal_logger);
        button_meal_logger.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent(DietTabActivity.this, MealLoggerActivity.class);
            	startActivity(intent);
            }
        });
	}
	// updates the date in the TextView
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
	
	private void updateDisplay() {
        myDisplayDate.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(myMonth + 1).append("-")
                    .append(myDay).append("-")
                    .append(myYear).append(" "));
        
        caloriesEaten.setText((theDB.getCalsEatenSince(currentTime) - (theDB.getCalsEatenSince(currentTime + 1000 * 3600 * 24))) + "");
        caloriesBurned.setText((theDB.getCalsBurnedSince(currentTime)- (theDB.getCalsBurnedSince(currentTime + 1000 * 3600 * 24))) + "");

        int recommendedCalories = theCoach.recommendCalories();
        if (recommendedCalories < 0){
        	calorieBalance.setTextColor(Color.GREEN);
        	calorieBalance.setText("" + (-1 * recommendedCalories));
        }else{
        	calorieBalance.setTextColor(Color.RED);
        	calorieBalance.setText("" + (recommendedCalories));
        }
    }
    private DatePickerDialog.OnDateSetListener myDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                myYear = year;
                myMonth = monthOfYear;
                myDay = dayOfMonth;
                updateDisplay();
                currentTime = (new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTimeInMillis();
            }
        };
    protected Dialog onCreateDialog(int id) {
            switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            myDateSetListener,
                            myYear, myMonth, myDay);
            }
            return null;
    }
}