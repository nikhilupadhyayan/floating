package android.health.pedometer;

import android.health.gui.SessionStatusActivity;
import android.health.pedometer.DistanceChecker.DistanceCheckerListener;
import android.preference.PreferenceManager;

/**
 * This class stores the core information about the exercise itself from the
 * exercise session: estimated distance, time taken, method taken to estimate
 * distance, average estimated speed, max estimated speed, minimum estimated
 * speed, etc.
 * @author Joel Botner
 *
 */
public class ExcerciseSessionInfo implements DistanceCheckerListener{
	/** Specifies whether the user walked, ran, or biked (1, 2, or 3 respectively)*/
	private int typeOfTravel = 0;
	private int distanceTraveled = 0; //Distance in centimeters
	private DistanceChecker sensorWatcher;
	private double calories = 0;
	private long lastTimeUpdate = System.currentTimeMillis();
	private double userWeight;
	
	/**
	 * This creates a new ExcerciseSessionInfo to store the important details about the 
	 * specified means of exercise. After creation, it will begin to poll the DistanceChecker
	 * for updated distance counts until the session ends.
	 * 
	 * @param travelType - The type of exercise the user is doing: 1 for walking, 2 for running, and 3 for biking
	 * @param checker - The specific DistanceChecker to obtain distance counts from.
	 */
	public ExcerciseSessionInfo(int travelType, DistanceChecker checker){
		typeOfTravel = travelType;
		sensorWatcher = checker;
		
		sensorWatcher.registerListener(this);
		userWeight = Double.valueOf(PreferenceManager.getDefaultSharedPreferences(SessionStatusActivity.me).getString("bodyWeight", "60"));
	}
	
	/**
	 * Returns the type of travel the user indicated they were using for this exercise session.
	 * 
	 * @return 1 for walking, 2 for running, and 3 for biking
	 */
	public int getTravel(){
		return typeOfTravel;
	}
	
	/**
	 * Returns the current tallied distance traveled as estimated by the
	 * DistanceChecker.
	 * 
	 * @return The current distance traveled in centimeters.
	 */
	public int getDistance(){
		return accessDistance(false, 0);
	}

	@Override
	/**
	 * This method just updates the final distance count registered.
	 * 
	 * @param finalDistance - The final distance count
	 * 
	 */
	public void onMonitoringEnd(int finalDistance) {
		accessCalories(true, finalDistance);
		accessDistance(true, finalDistance);
		
	}

	@Override
	/**
	 * This method just updates the distance
	 */
	public void updateDistance(int distance) {
		accessCalories(true, distance);
		accessDistance(true, distance);
	}
	
	/**
	 * This method should be the only method that touches the distance variable directly,
	 * all other methods should work through this method to maintain thread-safety.
	 * 
	 * 
	 * @param change - Whether or not to replace the current distance value with the new one.
	 * @param newValue - The new value to potentially replace the current distance with.
	 * @return The distance value after this method is done processing (whether changed or not).
	 */
	private synchronized int accessDistance(boolean change, int newValue){
		distanceTraveled = change ? newValue : distanceTraveled;
		return distanceTraveled;
	}
	
	/**
	 * This method should be the only method that touches the calories burned variable estimate,
	 * all other methods should work through this method to maintain thread-safety.
	 * 
	 * 
	 * @param change - Whether or not to replace the current calories burned value with the new one.
	 * @param newValue - The new value to potentially replace the current calories burned estimate.
	 * @return The calories burned estimate after this method is done processing (whether changed or not).
	 */
	private synchronized int accessCalories(boolean update, int newDistance){
		double timeElapsed = (System.currentTimeMillis() - lastTimeUpdate) / 1000.0 /  60.0 / 60.0; //Now in hours
		double distanceTraveled = (newDistance - accessDistance(false, 0)) / 100.0 / 1000.0; //Now in km
		double speed = distanceTraveled / timeElapsed; //stored in meters/minute
		double MET = -1;
		if(update && distanceTraveled != 0){
			//Here we must determine the current metabolic equivalent (MET)
			if(typeOfTravel == 1){
				MET = (speed - 3.2) * 1.1458 + 2.5;				
			}else if(typeOfTravel == 2){
				//Running is a lot pickier on the rates of calories burned, so there needs to be a lot more detail
				//MET's are given at a significant number of points, so linear regression allows for the closest estimate for an exact speed
				if (speed < 8.3686){
					MET = (speed - 8.04672) * 3.1066 + 8;
				} 
				else if(speed < 10.7826){
					MET = (speed - 8.3686) * 0.8285 + 9;
				} 
				else if(speed < 12.0701){
					MET = (speed - 10.7826) * 1.1650 + 11;
				} 
				else if(speed < 12.8748){
					MET = (speed - 12.0701) * 1.2427 + 12.5;
				} 
				else if(speed < 13.8404){
					MET = (speed - 12.8748) * 0.5178 + 13.5;
				} 
				else if(speed < 14.4841){
					MET = (speed - 13.8404) * 1.5535 + 14;
				}
				else if(speed < 16.0934){
					MET = (speed - 14.4841) * 0.6214 + 15;
				}
				else{
					MET = (speed - 16.0934) * 1.3808 + 16;
				}
			} else{
				//The study offered exact MET's for exact speed cutoffs, so these are applied verbatim from the study
				if(speed < 16.0934){
					MET = 4.0;
				}
				else if(speed < 19.1512){
					MET = 6.0;
				}
				else if(speed < 22.3699){
					MET = 8.0;
				}
				else if(speed < 25.5886){
					MET = 10.0;
				}
				else if(speed < 30.5775){
					MET = 12.0;
				}
				else{
					MET = 16.0;
				}
			}
			double additionalCalories = ((userWeight * 3.5) * (timeElapsed * 60) * MET) / 200;
			calories += additionalCalories > 0 ? additionalCalories : 0;
			lastTimeUpdate = System.currentTimeMillis();
		}
		return (int)calories;
	}
	
	public int getCalories() {
		return accessCalories(false, 0);
	}
}
