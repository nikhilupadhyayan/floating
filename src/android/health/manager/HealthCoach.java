package android.health.manager;

import java.util.Currency;

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
	double burningPercentage;
	
	public HealthCoach(SharedPreferences thePrefs, PedometerDatabaseAdapter exerciseDB){   //JOHNTODO Add your meal database object so HealthCoach is able to query both of them
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
		long time = System.currentTimeMillis();
		if(window == 1){
			time -= 1000 * 3600 * 24;
			return pedometerDB.getCalsBurnedSince(System.currentTimeMillis() - time);
		}else if(window == 2){
			time -= 1000 * 3600 * 24 * 7;
			return pedometerDB.getCalsBurnedSince(System.currentTimeMillis() - time);
		}else{
			time -= 1000 * 3600 * 24 * 30;
			return pedometerDB.getCalsBurnedSince(System.currentTimeMillis() - time);
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
		if(window == 1){
			//JOHNTODO Get the calories eaten within the last 24 hours
		}else if(window == 2){
			//JOHNTODO Get the calories eaten within the last 7 days
		}else{
			//JOHNTODO Get the calories eaten within the last 30 days
		}
		
		return 80; //PLACEHOLDER: Just so the code compiles, remove this when you pop in your code
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
