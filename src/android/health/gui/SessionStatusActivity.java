package android.health.gui;

/**
 * This class takes care of the display for the New Exercise Session button
 * from the default Exercise Tab
 * 
 * @author Joel Botner
 */

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.health.manager.R;
import android.health.pedometer.ExcerciseSession;
import android.health.pedometer.ExcerciseSessionList;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SessionStatusActivity extends Activity {
	//The UI elements
	private Chronometer timer;
	private TextView distanceLabel;
	private TextView caloriesLabel;
	private TextView caloriesLeftLabel;
	private TextView travelType;
	private TextView titleLabel;
	private ExcerciseSessionList sessionList;
	private ExcerciseSession thisSession;
	private int caloriesLeft = 0;
	private long startTime;
	private PedometerDatabaseAdapter theDB;
	public static SessionStatusActivity me;
	public Handler mHandler = new Handler();
	private Bundle myStuff;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
        theDB = new PedometerDatabaseAdapter(this).open();
        sessionList = new ExcerciseSessionList(SessionStatusActivity.this, theDB);
        setContentView(R.layout.session_status);
        myStuff = this.getIntent().getExtras();
        int exerciseType = myStuff.getInt("Exercise Type");

        timer = (Chronometer) findViewById(R.id.time_elapsed);
        distanceLabel = (TextView)findViewById(R.id.distance_traveled);
        caloriesLabel = (TextView)findViewById(R.id.calories_burned);
        caloriesLeftLabel = (TextView)findViewById(R.id.calories_left);
        titleLabel = (TextView)findViewById(R.id.title);
        travelType = (TextView)findViewById(R.id.exercise_type);
        
        titleLabel.setText(myStuff.getString("Session Title"));
        if(exerciseType == 1){
        	travelType.setText("Walking");
        }else if(exerciseType == 2){
        	travelType.setText("Running");
        }else if(exerciseType == 3){
        	travelType.setText("Biking");
        }
        thisSession = sessionList.addSession(exerciseType, myStuff.getBoolean("Use GPS"));
        if(!myStuff.getBoolean("Use GPS")){
        	startTimer();
        }else{
        	Toast.makeText(this, "Waiting for locational lock...", Toast.LENGTH_SHORT).show();
        }
	}
	
	public void stopButtonPresser(View theButton){
		onBackPressed();
	}
	
	public void startTimer(){
		Toast.makeText(this, "Locational tracking has begun...", Toast.LENGTH_SHORT).show();
		timer.setBase(SystemClock.elapsedRealtime());
		startTime = GregorianCalendar.getInstance().getTimeInMillis();
		timer.start();
	}
	
	public void stopMonitoring(){
		thisSession.stopMonitoring();
		timer.stop();
		Calendar today = GregorianCalendar.getInstance();
		today.setTimeInMillis(((long)(today.getTimeInMillis() / 8640000)) * 8640000); //Returns the record to midnight
		
		sessionList.monitoringDone(thisSession, myStuff.getString("Session Title"), GregorianCalendar.getInstance().getTimeInMillis() - startTime, startTime);
	}
	
	@Override
	public void onBackPressed() {
		stopMonitoring();
		this.finish();
	   return;
	}

	
	public void updateValues(int distance){
		int calories = thisSession.getMyCalories();
		Log.i("SessionStatus", "Received calories = " + calories);
		distanceLabel.setText("" + (float)(distance / 100.0) + "m");
		caloriesLabel.setText("" + calories + "");
		caloriesLeft -= calories < caloriesLeft ? calories : caloriesLeft;
		caloriesLeftLabel.setText("" + caloriesLeft);
	}

}