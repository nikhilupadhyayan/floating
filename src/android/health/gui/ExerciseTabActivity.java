package android.health.gui;

/**
 * This class takes care of the display for the default Exercise Tab
 * 
 * @author Dan Abrams
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.health.manager.R;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ExerciseTabActivity extends Activity {
	
	private Cursor allEntries;
	private SimpleCursorAdapter info_adapter;
	private ListView exercise_info_list;
	private PedometerDatabaseAdapter theDB;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_tab);
        TabSelector.currentTab = 2;
        theDB = new PedometerDatabaseAdapter(this).open();
        
        exercise_info_list = (ListView) findViewById(R.id.exercise_info_list);
        
        /*ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
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
                    new String[] {columnTitle1, columnTitle2, columnTitle3}, new int[] {R.id.session_name_col, R.id.distance_col, R.id.calories_burned_col});*/
        
        
        allEntries = theDB.fetchAllSessions();
        info_adapter = new SimpleCursorAdapter(this, R.layout.exercise_info_row, allEntries,
                    new String[] {PedometerDatabaseAdapter.KEY_TITLE, PedometerDatabaseAdapter.KEY_DISTANCE, PedometerDatabaseAdapter.KEY_CALORIES}, new int[] {R.id.session_name_col, R.id.distance_col, R.id.calories_burned_col});
        exercise_info_list.setAdapter(info_adapter);
        
        exercise_info_list.setOnItemClickListener(new OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent intent = new Intent(ExerciseTabActivity.this, ExerciseStatisticsActivity.class);
            	Bundle rowHolder = new Bundle();
            	rowHolder.putLong("Row ID", id);
            	intent.putExtras(rowHolder);
            	startActivity(intent);
            }
        });
    }
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.exercise_menu, menu);
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.preferences:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case R.id.new_session:
			startActivity(new Intent(this, NewExerciseSessionActivity.class));
			return true;
		}
		return false;
	}
	
	protected void onResume(){
		super.onResume();
		
		allEntries = theDB.fetchAllSessions();
		info_adapter.changeCursor(allEntries);
		info_adapter.notifyDataSetChanged();
	}
}