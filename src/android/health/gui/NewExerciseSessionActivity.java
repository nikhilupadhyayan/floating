package android.health.gui;

/**
 * This class takes care of the display for the New Exercise Session button
 * from the default Exercise Tab
 * 
 * @author Dan Abrams
 */

import android.app.Activity;
import android.content.Intent;
import android.health.manager.R;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class NewExerciseSessionActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exercise_session_tab);
        
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
    		  
              // Perform action on clicks
    		  Intent intent = new Intent().setClass(NewExerciseSessionActivity.this, SessionStatusActivity.class);
    		  intent.putExtras(sessionParameters);
    		  startActivity(intent);
          }
       });
        
        
	}

}