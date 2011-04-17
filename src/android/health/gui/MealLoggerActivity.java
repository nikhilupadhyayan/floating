package android.health.gui;

/**
 * This class takes care of the display for the Meal Logger button
 * from the default Diet Tab
 * 
 * @author Dan Abrams
 */

import android.app.Activity;
import android.content.Intent;
import android.health.manager.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MealLoggerActivity extends Activity {
	private TextView myDisplayDate;
	
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
        final Button button_add_meal = (Button) findViewById(R.id.button_add_meal);
        button_add_meal.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent().setClass(MealLoggerActivity.this, MealMakerActivity.class);
            	startActivity(intent);
                
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
		menu.add(Menu.NONE, 0, 0, "Preferences");
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 0:
			startActivity(new Intent(this, Preferences.class));
			return true;
		}
		return false;
	}

}
