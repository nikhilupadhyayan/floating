package android.health.gui;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.health.manager.R;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This activity allows the user to enter a meal into the diet logger, and stores this meal when the operation is done.
 * 
 * @author Joel Botner
 */
public class MealMakerActivity extends Activity {
	
	/*
	 * This class will create a AutoCompleteTextView, which allows the reader
	 * to search through the database of foods.
	 * 
	 * Based of AutoCompleteTextView tutorial from
	 * http://www.outofwhatbox.com/blog/2010/11/android-simpler-autocompletetextview-with-simplecursoradapter/
	 */
	
	OverallTabActivity otb;
	PedometerDatabaseAdapter theDB;
	Cursor recentCursor = null;

	private AutoCompleteTextView myFoodView;
	private TextView myFoodTextView;
	private EditText FoodLogView;
	private EditText GramsEaten;
	private int[] foodCalories;
	private int numFoods;
	private int currentCalories;
	
	final static int[] to = new int[] { android.R.id.text1 };
    final static String[] from = new String[] { PedometerDatabaseAdapter.KEY_DESCRIPTION };
	
	public void onCreate(Bundle savedInstanceState){
		theDB = new PedometerDatabaseAdapter(MealMakerActivity.this).open();
		foodCalories = new int[200];
		numFoods = 0;
		currentCalories = 0;
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_maker_tab);
        
 
        
       	myFoodTextView = (TextView) findViewById(R.id.title);
        myFoodView = (AutoCompleteTextView) findViewById(R.id.meal_name);
        GramsEaten = (EditText) findViewById(R.id.grams_eaten);
        FoodLogView = (EditText) findViewById(R.id.food_log);
        myFoodView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				if (myFoodView.getText().length() > 2){
					recentCursor.close();
					recentCursor = theDB.getFoodMatches(myFoodView.getText().toString());
					Log.i("ArrayWatching", "New Array Length = " + recentCursor.getCount());
					myFoodView.setAdapter(makeAdapter(recentCursor, 100));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
        	
        });
 
        
        myFoodView.setThreshold(3);
        recentCursor = theDB.fetchAllFoodEntrys();
        myFoodView.setAdapter(makeAdapter(recentCursor, 100));
        
        // Set an OnItemClickListener, to update dependent fields when
        // a choice is made in the AutoCompleteTextView.
        myFoodView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view,
                        int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
            	id = id > 0 ? id - 1 : id;
                recentCursor.moveToPosition((int) id);
 
                // Enable the grams eaten box
                GramsEaten.setText("0");
                GramsEaten.setEnabled(true);
               
            }
        });
        
        final Button button_save_meal = (Button) findViewById(R.id.button_save_meal);
        button_save_meal.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Save the meal to the database
            	theDB.createMeal(DietTabActivity.currentTime + 1 + "", currentCalories + "");
            	
            	//Return to the previous screen
            	Intent intent = new Intent().setClass(MealMakerActivity.this, MealLoggerActivity.class);
            	startActivity(intent);
            }
        });
        
        final Button saveFood = (Button) findViewById(R.id.button_save_food);
        saveFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(!(Double.valueOf(GramsEaten.getText().toString()) > 0)){
            		return;
            	}
            	String foodText = myFoodView.getText().toString();
            	if(foodText.length() > 33){
            		foodText = foodText.substring(0, 34) + "...";
            	}
                
                
                //Save the food information
                numFoods++;
                numFoods = numFoods > 200 ? 200 : numFoods;
                try{
                	foodCalories[numFoods - 1] = (int)(recentCursor.getDouble(recentCursor.getColumnIndex(PedometerDatabaseAdapter.KEY_CALORIES_STORED)) * Double.valueOf(GramsEaten.getText().toString()) / 100.0);
                } catch (CursorIndexOutOfBoundsException e){
                	recentCursor.move(-1);
                	foodCalories[numFoods - 1] = (int)(recentCursor.getDouble(recentCursor.getColumnIndex(PedometerDatabaseAdapter.KEY_CALORIES_STORED)) * Double.valueOf(GramsEaten.getText().toString()) / 100.0);
                }
                	currentCalories += foodCalories[numFoods - 1];
                
                FoodLogView.setText(FoodLogView.getText() + foodText + "\t" + GramsEaten.getText() + "g\n");
                myFoodView.setText("");
                GramsEaten.setText("0");
                GramsEaten.setEnabled(false);
            }
        });
        
        final Button removeFood = (Button) findViewById(R.id.button_remove_food);
        removeFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String newText = FoodLogView.getText().toString();
                if(numFoods > 0){
                	currentCalories -= foodCalories[numFoods - 1];
                	foodCalories[numFoods - 1] = 0;
                	numFoods--;
                }
                int index = newText.substring(0, newText.length() - 2).lastIndexOf("\n");
                if(index != -1){
                	newText = newText.substring(0, index) + "\n";
                }else{
                	newText = "";
                }
                FoodLogView.setText(newText);
            }
        });
 
	}
	
	/**
	 * This method converts the specified number of entries from the given cursor into 
	 * 
	 * @param target - The database Cursor to build the ArrayAdapter for.
	 * @param maxLength - The maximum number of entries to include in the adapter
	 * @return The {@link ArrayAdapter} as specified
	 */
	private ArrayAdapter<String> makeAdapter(Cursor target, int maxLength){
		String[] theList = new String[target.getCount() > maxLength ? maxLength : target.getCount()];
		target.moveToFirst();
		for (int a = 0; a < theList.length; a++){
			theList[a] = target.getString(target.getColumnIndex(PedometerDatabaseAdapter.KEY_DESCRIPTION));
			target.moveToNext();
		}
		return new ArrayAdapter<String>(this, R.layout.list_item, theList);
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
