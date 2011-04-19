package android.health.gui;

/**
 * This class takes care of the display for the default Exercise Tab
 * 
 * @author Dan Abrams
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.health.manager.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ExerciseTabActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_tab);
        TabSelector.currentTab = 2;
        
        ListView exercise_info_list = (ListView) findViewById(R.id.exercise_info_list);
        
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        String columnTitle1 = "name";
        String columnTitle2 = "dist";
        String columnTitle3 = "burn";
        //enter data in this format, with the test# strings replaced with
        //the corresponding data for each column
        map.put(columnTitle1, "test1");
        map.put(columnTitle2, "test2");
        map.put(columnTitle3, "test3");
        mylist.add(map);
        
        map = new HashMap<String, String>();
        map.put(columnTitle1, "test1row2");
        map.put(columnTitle2, "test2row2");
        map.put(columnTitle3, "test3row2");
        mylist.add(map);
        
        // etc.
        
        SimpleAdapter info_adapter = new SimpleAdapter(this, mylist, R.layout.exercise_info_row,
                    new String[] {columnTitle1, columnTitle2, columnTitle3}, new int[] {R.id.session_name_col, R.id.distance_col, R.id.calories_burned_col});
        exercise_info_list.setAdapter(info_adapter);
        
        exercise_info_list.setOnItemClickListener(new OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent intent = new Intent(ExerciseTabActivity.this, ExerciseStatisticsActivity.class);
            	startActivity(intent);
            }
        });
    }
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "Preferences");
		menu.add(Menu.NONE, 1, 1, "New Session");
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 0:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case 1:
			startActivity(new Intent(this, NewExerciseSessionActivity.class));
			return true;
		}
		return false;
	}
	
}