package android.health.manager;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.health.pedometer.PedometerDatabaseAdapter;

/**
 * This class is responsible for handling all application interaction with the actual calorie
 * summaries of both intake and expenditure through exercise. This class also makes this information
 * available to the rest of the application, as well as providing all the caloric balancing advice. 
 * 
 * @author John Mauldin - Initial code stub and Diet Manager query support
 * @author Joel Botner - Pedometer Database query support and caloric balancer functionality
 */

public class HealthCoach {

	//Variables
	PedometerDatabaseAdapter pedometerDB;
	int balancingWindow;
	SharedPreferences preferences;
	Activity theContext;
	double burningPercentage;
	
	public HealthCoach(SharedPreferences thePrefs, PedometerDatabaseAdapter exerciseDB, Activity callingActivity){
		pedometerDB = exerciseDB;
		preferences = thePrefs;
		balancingWindow = Integer.valueOf(preferences.getString("balancingWindow", "1"));
		burningPercentage = Double.valueOf(preferences.getString("caloriePercentage", "30")) / 100.0;
	}
		
	/**
	 * This queries the exercise logs database to obtain how many calories were burned
	 * through exercise over the window specified (1 is the last 24 hours, 2 is the last
	 * 7 days, and 3 is the last 30 days)
	 * 
	 * @param window - Which window to obtain calorie burned during.
	 * @return The number of calories the user burned over the course of this window.
	 */
	public int getTotalCaloriesBurned(int window){
		long time = 1000 * 3600 * 24;
		if(window == 1){
			time = System.currentTimeMillis() - time;
			return pedometerDB.getCalsBurnedSince(time);
		}else if(window == 2){
			time = System.currentTimeMillis() - (time * 7);
			return pedometerDB.getCalsBurnedSince(time);
		}else{
			time = System.currentTimeMillis() - (time * 30);
			return pedometerDB.getCalsBurnedSince(time);
		}
	}
	
	/**
	 * This queries the logged meals database to obtain how many calories were consumed
	 * over the window specified (1 is the last 24 hours, 2 is the last 7 days, and 3 is
	 * the last 30 days)
	 * 
	 * @param window - Which window to obtain caloric consumption for.
	 * @return The number of calories the user ate over this window.
	 */
	public int getTotalCaloriesEaten(int window){
		long time = 1000 * 3600 * 24;
		if(window == 1){
			time = GregorianCalendar.getInstance().getTimeInMillis() - time;
			return pedometerDB.getCalsEatenSince(time);
		}else if(window == 2){
			time = GregorianCalendar.getInstance().getTimeInMillis() - (time * 7);
			return pedometerDB.getCalsEatenSince(time);
		}else{
			time = GregorianCalendar.getInstance().getTimeInMillis() - (time * 30);
			return pedometerDB.getCalsEatenSince(time);
		}
	}
	
	/**
	 * Identifies how many calories still need to be burned to achieve the minimal
	 * calories burned goal.
	 * 
	 * @return How many calories must still be burned to achieve the minimal calories
	 * burned goal over this window. A negative value means you have surpassed your
	 * goal by the specified amount.
	 */
	public int recommendCalories(){
		int target = (int)((getTotalCaloriesEaten(balancingWindow) * burningPercentage) - getTotalCaloriesBurned(balancingWindow));
		return target;
	}
	
	/**
	 * Identifies how many calories still need to be burned to achieve the minimal
	 * calories burned goal. Same as above recommendCalories(), but you can 
	 * 
	 * @return How many calories must still be burned to achieve the minimal calories
	 * burned goal over the specified window. A negative value means you have surpassed
	 * your goal by the specified amount.
	 */
	public int recommendCalories(int window){
		int target = (int)((getTotalCaloriesEaten(window) * burningPercentage) - getTotalCaloriesBurned(window));
		return target;
	}
}
