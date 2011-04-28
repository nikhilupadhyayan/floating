package android.health.gui;

/**
 * This class takes care of the display for the New Exercise Session button
 * from the default Exercise Tab
 * 
 * @author Dan Abrams
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.health.manager.HealthCoach;
import android.health.manager.R;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class NewExerciseSessionActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exercise_session_tab);
        final SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        
        //Handle the back button listener here
        /*final Button button_back_new_session = (Button) findViewById(R.id.button_back_new_session);
        button_back_new_session.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent().setClass(NewExerciseSessionActivity.this, TabSelector.class);
            	startActivity(intent);
            }
        });*/
        
        //Handle the "not now" button listener here
        final Button button_not_now = (Button) findViewById(R.id.button_cancel_session);
        button_not_now.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	Intent intent = new Intent().setClass(NewExerciseSessionActivity.this, TabSelector.class);
            	startActivity(intent);
            }
        });
        
        TextView recommended_calories = (TextView)findViewById(R.id.recommended_calories);
        TextView recommended_calories_label = (TextView)findViewById(R.id.recommended_calories_label);
        PedometerDatabaseAdapter theDB = new PedometerDatabaseAdapter(this).open();
        HealthCoach theCoach = new HealthCoach(PreferenceManager.getDefaultSharedPreferences(this), theDB, this);
        int recommendedCalories = theCoach.recommendCalories();
        if (recommendedCalories < 0){
        	recommended_calories.setTextColor(Color.GREEN);
        	recommended_calories_label.setText("Excess Calories Burned:");
        	recommended_calories.setText("" + (-1 * recommendedCalories));
        }else{
        	recommended_calories.setTextColor(Color.RED);
        	recommended_calories_label.setText("Calories Left to Burn:");
        	recommended_calories.setText("" + (recommendedCalories));
        }
        
        
        final EditText duration_limit = (EditText)findViewById(R.id.check_limit_box);
        final CheckBox limit_duration = (CheckBox)findViewById(R.id.check_limit);
        limit_duration.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				duration_limit.setEnabled(isChecked);
			}
		});
        
        
      //Watch the Session Title Field
        final EditText sessionTitle = (EditText) findViewById(R.id.title_edit);
        sessionTitle.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                  // Perform action on key press
                  Toast.makeText(NewExerciseSessionActivity.this, sessionTitle.getText(), Toast.LENGTH_SHORT).show();
                  return true;
                }
                return false;
            }
        });
        
        //Set up the RadioButton listeners
        final RadioButton runButton = (RadioButton)findViewById(R.id.radio_running);
        final RadioButton walkButton = (RadioButton)findViewById(R.id.radio_walking);
        final RadioButton bikeButton = (RadioButton)findViewById(R.id.radio_biking);
        final RadioButton gpsButton = (RadioButton)findViewById(R.id.radio_gps);
        final RadioButton accelButton = (RadioButton)findViewById(R.id.radio_accel);
        
        //Set the button listeners so Bike mode will only use GPS/Network
        bikeButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        @Override
		public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
			if (isChecked){
				gpsButton.setChecked(true);
				accelButton.setEnabled(false);
				
			}
		}});
        walkButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
    		public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
    			if (isChecked){
    				accelButton.setEnabled(true);
    			}
    		}});
        runButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
    		public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
    			if (isChecked){
    				accelButton.setEnabled(true);
    			}
    		}});
        
        
        //This radio button must ensure the Location-monitoring is enabled by the user before proceeding
        gpsButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
    		public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
            	if(!isChecked){
            		return;
            	}
            	
    			if (!bikeButton.isChecked()){
    				accelButton.setEnabled(true);
    			}
    			if(Integer.valueOf(mSettings.getString("trackingMechanism", "1")) == 1){
    				if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
    					accelButton.setEnabled(true);
    					accelButton.setChecked(true);
    					if(bikeButton.isChecked()){
    						runButton.setChecked(true);
    					}
    			        buildAlertMessageNoGps("GPS");
    			    }
    			}else{
    				if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER ) ) {
    					accelButton.setEnabled(true);
    					accelButton.setChecked(true);
    					if(bikeButton.isChecked()){
    						runButton.setChecked(true);
    					}
    			        buildAlertMessageNoGps("Wireless Networks Tracking");
    				}
    			}	
    		}
        });
        
      //Handle the "start session" button listener here
      final Button button_start_session = (Button) findViewById(R.id.button_new_session);
      button_start_session.setOnClickListener(new OnClickListener() {
    	  public void onClick(View v) {
    		  //Grab the user-entered data
    		  Bundle sessionParameters = new Bundle();
    		  int exerciseType = 0;
    		  exerciseType = walkButton.isChecked() ? 1 : 0;
    		  exerciseType = runButton.isChecked() ? 2 : exerciseType;
    		  exerciseType = bikeButton.isChecked() ? 3 : exerciseType;
    		  sessionParameters.putString("Session Title", sessionTitle.getText().toString());
    		  sessionParameters.putInt("Exercise Type", exerciseType);
    		  sessionParameters.putBoolean("Use GPS", gpsButton.isChecked());
    		  sessionParameters.putBoolean("Limit Checked", limit_duration.isChecked());
    		  try{
    			  if (((double)Double.valueOf(duration_limit.getText().toString()) <= 0) || duration_limit.getText().toString().equalsIgnoreCase("")){
    				  sessionParameters.putString("Limit Number", "-1");
    			  }else{
    				  sessionParameters.putString("Limit Number", duration_limit.getText().toString());
    			  }
    		  } catch (NumberFormatException e){
    			  sessionParameters.putString("Limit Number", "-1");
    		  }
    		  
              // Perform action on clicks
    		  Intent intent = new Intent().setClass(NewExerciseSessionActivity.this, SessionStatusActivity.class);
    		  intent.putExtras(sessionParameters);
    		  startActivity(intent);
          }
       });
        
        
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
	
	/**
	 * If the user needs to turn on their location management, this will
	 * open the necessary dialog to do so.
	 */
	private void launchGPSOptions() {
        final ComponentName toLaunch = new ComponentName("com.android.settings","com.android.settings.SecuritySettings");
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 0);
    }
	
	private void buildAlertMessageNoGps(String type) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Your " + type + " seems to be disabled, do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   launchGPSOptions(); 
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}


}