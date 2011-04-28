package android.health.gui;

/**
 * This class takes care of the display for the New Exercise Session button
 * from the default Exercise Tab
 * 
 * @author Joel Botner
 */

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.health.manager.HealthCoach;
import android.health.manager.R;
import android.health.pedometer.ExcerciseSession;
import android.health.pedometer.ExcerciseSessionList;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
	private TextView caloriesLeftLabeler;
	private ExcerciseSessionList sessionList;
	private ExcerciseSession thisSession;
	private int caloriesLeft = 0;
	private long startTime;
	private Timer theTimer;
	private HealthCoach theCoach;
	
	private PedometerDatabaseAdapter theDB;
	public static SessionStatusActivity me;
	public Handler mHandler = new Handler();
	public Bundle myStuff;
	private TimerTask stopRunnable = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
        theDB = new PedometerDatabaseAdapter(this).open();
        sessionList = new ExcerciseSessionList(SessionStatusActivity.this, theDB);
        theCoach = new HealthCoach(PreferenceManager.getDefaultSharedPreferences(this), theDB, this);
        caloriesLeft = theCoach.recommendCalories();
        setContentView(R.layout.session_status);
        myStuff = this.getIntent().getExtras();
        int exerciseType = myStuff.getInt("Exercise Type");
        theTimer = new Timer();

        timer = (Chronometer) findViewById(R.id.time_elapsed);
        distanceLabel = (TextView)findViewById(R.id.distance_traveled);
        caloriesLabel = (TextView)findViewById(R.id.calories_burned);
        caloriesLeftLabel = (TextView)findViewById(R.id.calories_left);
        caloriesLeftLabeler = (TextView)findViewById(R.id.calories_left_label);
        if(caloriesLeft > 0){
        	caloriesLeftLabel.setTextColor(Color.RED);
        	caloriesLeftLabeler.setText("Calories Left:");
        }
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
        	Toast.makeText(this, "Step monitoring has begun...", Toast.LENGTH_SHORT).show();
        }else{
        	Toast.makeText(this, "Waiting for locational lock...", Toast.LENGTH_SHORT).show();
        }
        
	}
		
	public void stopButtonPresser(View theButton){
		onBackPressed();
	}
	
	public void startTimer(){
		me = this;
		if(myStuff.getBoolean("Use GPS")){
			Toast.makeText(this, "Locational tracking has begun...", Toast.LENGTH_SHORT).show();
		}
		((Vibrator)getSystemService("vibrator")).vibrate(100);
		timer.setBase(SystemClock.elapsedRealtime());
		startTime = GregorianCalendar.getInstance().getTimeInMillis();
		timer.start();
		long numberLimit = (long)(Double.valueOf(myStuff.getString("Limit Number")) * 61000);
		Log.i("NumberLimit", numberLimit + "");
		if (myStuff.getBoolean("Limit Checked", false) && numberLimit > 0){
			((Vibrator)getSystemService("vibrator")).vibrate(500);
			stopRunnable = new TimerTask(){
				@Override
				public void run() {
					if (SessionStatusActivity.me != null){
						SessionStatusActivity.me.stopMonitoring();
						((Vibrator)getSystemService("vibrator")).vibrate(200);
						try {Thread.sleep(500);} catch (InterruptedException e) {}
						((Vibrator)getSystemService("vibrator")).vibrate(200);
						try {Thread.sleep(500);} catch (InterruptedException e) {}
						((Vibrator)getSystemService("vibrator")).vibrate(200);
						try {Thread.sleep(500);} catch (InterruptedException e) {}
						((Vibrator)getSystemService("vibrator")).vibrate(200);
						try {Thread.sleep(2000);} catch (InterruptedException e) {}
						SessionStatusActivity.me.finish();
					}
				}
			};
			theTimer.schedule(stopRunnable, numberLimit);
		}
	}
	
	public void stopMonitoring(){
		thisSession.stopMonitoring();
		timer.stop();
		if (stopRunnable != null){
			stopRunnable.cancel();
			stopRunnable = null;
		}
		Calendar today = GregorianCalendar.getInstance();
		today.setTimeInMillis(((long)(today.getTimeInMillis() / 8640000)) * 8640000); //Returns the record to midnight
		
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("foodDatabaseImported", false)){
    		// Perform action on clicks
			sessionList.monitoringDone(thisSession, myStuff.getString("Session Title"), timer.getText().toString(), startTime, true);
			SessionStatusActivity.this.finish();
    	}else{
    		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setMessage("Sorry, Exercise Sessions cannot be logged until the Food Database has been completely imported.");
    		final AlertDialog theDialog = alertDialog;
    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    		   public void onClick(DialogInterface dialog, int which) {
    			  sessionList.monitoringDone(thisSession, myStuff.getString("Session Title"), timer.getText().toString(), startTime, false);
    		      SessionStatusActivity.this.finish();
    		   }
    		});
    		alertDialog.show();
    	}
		
		
	}
	
	@Override
	public void onBackPressed() {
		stopMonitoring();
	   return;
	}

	public void finish(){
		me = null;
		super.finish();
	}
	
	public void updateValues(int distance){
		int calories = thisSession.getMyCalories();
		Log.i("SessionStatus", "Received calories = " + calories);
		distanceLabel.setText("" + (float)(distance / 100.0) + "m");
		caloriesLabel.setText("" + calories + "");
		if (caloriesLeft > calories){
			caloriesLeftLabel.setText("" + (caloriesLeft - calories));
		}else{
			caloriesLeftLabel.setTextColor(Color.GREEN);
			caloriesLeftLabeler.setText("Calorie Surplus:");
			caloriesLeftLabel.setText("" + (calories - caloriesLeft));
		}
		
	}

}