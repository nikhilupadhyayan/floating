package android.health.gui;

/**
 * This class takes care of the display for the default Overall Tab
 * 
 * @author Dan Abrams
 */

import android.app.Activity;
import android.content.Intent;
import android.health.manager.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class OverallTabActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_tab);
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
