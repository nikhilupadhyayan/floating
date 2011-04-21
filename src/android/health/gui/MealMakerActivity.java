package android.health.gui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.health.dietlogging.DietDatabaseAdapter;
import android.health.manager.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

public class MealMakerActivity extends Activity {
	
	/*
	 * This class will create a AutoCompleteTextView, which allows the reader
	 * to search through the database of foods.
	 * 
	 * Based of AutoCompleteTextView tutorial from
	 * http://www.outofwhatbox.com/blog/2010/11/android-simpler-autocompletetextview-with-simplecursoradapter/
	 */
	
	OverallTabActivity otb;
	DietDatabaseAdapter dietDB;

	private AutoCompleteTextView myFoodView;
	private TextView myFoodTextView;
	
	final static int[] to = new int[] { android.R.id.text1 };
    final static String[] from = new String[] { "food" };
	
	public void onCreate(Bundle savedInstanceState){
		dietDB = otb.me.dietDB;

		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_maker_tab);
        final Button button_back_logger = (Button) findViewById(R.id.button_back_maker);
        button_back_logger.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent().setClass(MealMakerActivity.this, MealLoggerActivity.class);
            	startActivity(intent);
                
            }
        });
        /*final Button confirmButton = (Button) findViewById(R.id.button_add_meal);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });*/
 
        
       	myFoodTextView = (TextView) findViewById(R.id.title);
        myFoodView = (AutoCompleteTextView) findViewById(R.id.meal_name);
 
        /// Create a SimpleCursorAdapter for the Food Name field.
        SimpleCursorAdapter adapter =
            new SimpleCursorAdapter(this,
                    android.R.layout.simple_dropdown_item_1line, null,
                    from, to);
        myFoodView.setAdapter(adapter);
        
        // Set an OnItemClickListener, to update dependent fields when
        // a choice is made in the AutoCompleteTextView.
        myFoodView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view,
                        int position, long id) {
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
 
                // Get the food's name from this row in the database.
                String capital =
                    cursor.getString(cursor.getColumnIndexOrThrow(dietDB.KEY_ROWID));
 
                // Update the parent class's TextView
                myFoodView.setText(capital);
            }
        });
 
        // Set the CursorToStringConverter, to provide the labels for the
        // choices to be displayed in the AutoCompleteTextView.
        adapter.setCursorToStringConverter(new CursorToStringConverter() {
            public String convertToString(android.database.Cursor cursor) {
                // Get the label for this row out of the "" column
                final int columnIndex = cursor.getColumnIndexOrThrow(dietDB.KEY_DESCRIPTION);
                final String str = cursor.getString(columnIndex);
                return str;
            }
        });
 
        // Set the FilterQueryProvider, to run queries for choices
        // that match the specified input.
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                // Search for foods whose names begin with the specified letters.
                Cursor cursor = dietDB.getMatchingStates(
                        (constraint != null ? constraint.toString() : null));
                return cursor;
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
