package android.health.gui;

/**
 * This class displays the details of the exercise session tab selected from the list
 * of logged sessions and gives the user the ability to remove it if they so desire. 
 * 
 * @author Dan Abrams - Initial creation and skeleton
 * @author Joel Botner - Placement and functionality
 */

import java.util.StringTokenizer;
import android.app.Activity;
import android.content.Intent;
import android.health.manager.R;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ExerciseStatisticsActivity extends Activity {

	//UI Elements
	private TextView sessionLength;
	private TextView startLabel;
	private TextView distanceLabel;
	private TextView caloriesLabel;
	private TextView typeLabel;
	private TextView titleLabel;
	private PedometerDatabaseAdapter theDB;
	private long tempNum;
	private String tempString;
	private long recordID;
	public static ExerciseStatisticsActivity me;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_exercise_stat_tab);
        me = this;
        theDB = new PedometerDatabaseAdapter(this).open();
        recordID = this.getIntent().getExtras().getLong("Row ID");
        String statisticsString = theDB.fetchSession(recordID);
        StringTokenizer attributeChopper = new StringTokenizer(statisticsString);
        
        final Button button_back_stat = (Button) findViewById(R.id.button_stop_viewing);
        button_back_stat.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
                
        //Identify the elements
        sessionLength = (TextView)findViewById(R.id.det_time_taken);
        startLabel = (TextView)findViewById(R.id.det_start_time);
        distanceLabel = (TextView)findViewById(R.id.det_distance_traveled);
        caloriesLabel = (TextView)findViewById(R.id.det_calories_burned);
        typeLabel = (TextView)findViewById(R.id.det_exerc_type);
        titleLabel = (TextView)findViewById(R.id.det_title);
        
        //Set the UI elements
        titleLabel.setText(attributeChopper.nextToken("\t"));
        distanceLabel.setText(attributeChopper.nextToken("\t"));
        
        /*tempNum = Long.valueOf(attributeChopper.nextToken("\t"));	//Session length time in milliseconds
        tempString = "" + (long)(tempNum / 3600000); 				//Hours
        tempNum = ((long)(tempNum / 60000) % 3600000); 				//Minutes
        tempString += tempNum > 9 ? ":" + tempNum : ":0" + tempNum; //Ensures format of "MM"
        tempNum = ((long)(tempNum / 1000) % 60000); 				//Seconds
        tempString += tempNum > 9 ? ":" + tempNum : ":0" + tempNum;	//Ensures format of "MM"
        sessionLength.setText(tempString);*/
        sessionLength.setText(attributeChopper.nextToken("\t"));
        
        tempNum = Long.valueOf(attributeChopper.nextToken("\t")); 	//Date of session in milliseconds (Java's GregorianCalendar format)
        tempString = DateFormat.format("MM/dd/yy h:mmaa" , tempNum).toString();
        startLabel.setText(tempString);
        
        typeLabel.setText(attributeChopper.nextToken("\t"));
        caloriesLabel.setText(attributeChopper.nextToken("\t"));
	}
	
	public void deleteSessionPressed(View theButton){
		theDB.deleteSession(recordID);
        this.finish();
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