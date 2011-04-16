package android.health.gui;

/**
 * This class takes care of the display for the default Exercise Tab
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

public class ExerciseTabActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_tab);
        TabSelector.currentTab = 2;
        
        final Button button_stat = (Button) findViewById(R.id.button_stat);
        button_stat.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent(ExerciseTabActivity.this, ExerciseStatisticsActivity.class);
            	startActivity(intent);
            }
        });
        final Button button_new_session = (Button) findViewById(R.id.button_new_session);
        button_new_session.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent(ExerciseTabActivity.this, NewExerciseSessionActivity.class);
            	startActivity(intent);
            }
        });
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