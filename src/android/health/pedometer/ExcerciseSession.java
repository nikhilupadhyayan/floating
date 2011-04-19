package android.health.pedometer;

import android.app.Activity;

/**
 * This class encapsulates the core information about a particular exercise session
 * and obtains the estimated number of calories the user burned during the session.
 * 
 * @author Joel Botner
 *
 */
public class ExcerciseSession {

	private DistanceChecker theChecker;
	private ExcerciseSessionInfo theInfo;
	
	/**
	 * This method calculates the estimated number of calories consumed for the given 
	 * exercise session data if not already calculated.
	 * 
	 * @param info The exercise session data to obtain the estimated calories for.
	 * @return The estimated number of calories burned during the exercise session.
	 */
	public static int getCalories(ExcerciseSessionInfo info){
		info.getCalories();
		return 0;
	}
	
	/**
	 * Start up a new exercise session. Type of exercise and method of monitoring must be
	 * specified here.
	 * 
	 * @param excerciseType - The type of exercise this session will be. 1 is walking, 2 is running, 3 is biking.
	 * @param useGPS - Boolean value whether to use the GPS to monitor the exercise or not. If the GPS is not used,
	 * the application will fall back to using the built-in accelerometer.
	 */
	public ExcerciseSession(int excerciseType, boolean useGPS, Activity theActivity){
		theChecker = new DistanceChecker(theActivity);
		theInfo = new ExcerciseSessionInfo(excerciseType, theChecker);
		
		if (useGPS){
			theChecker.useGPS();
		} else{
			theChecker.useAccel();
		}
	}
	
	/**
	 * This is the recommended way of accessing the estimated number of calories burned
	 * through this exercise session.
	 * 
	 * @return The estimated number of calories burned during this information session.
	 */
	public int getMyCalories(){
		return theInfo.getCalories();
	}
	
	/**
	 * Returns the currently tallied distance traveled (in meters).
	 * 
	 * @return The current estimate of meters traveled.
	 */
	public double getDistance(){
		return (double)(theInfo.getDistance() / 100.0);
	}
	
	/**
	 * Returns the type of travel for this session (string form for readability)
	 * 
	 * @return A string saying what type of exercise this session represented.
	 */
	public String getTypeOfTravel(){
		if(theInfo.getTravel() == 1){
			return "Walking";
		}
		else if(theInfo.getTravel() == 2){
			return "Running";
		}
		else{
			return "Biking";
		}
	}
	
	/**
	 * Ends the current session and cleans up all monitoring components
	 */
	public void stopMonitoring(){
		theChecker.stopMonitoring();
	}

}
