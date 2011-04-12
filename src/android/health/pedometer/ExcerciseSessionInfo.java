package android.health.pedometer;

import android.health.pedometer.DistanceChecker.DistanceCheckerListener;

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
	private int distanceTraveled = 0; //Distance in hundredths of a mile
	private DistanceChecker sensorWatcher;
	
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
	 * @return The current distance traveled in hundredths of a mile.
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
		accessDistance(true, finalDistance);
	}

	@Override
	/**
	 * This method just updates the distance
	 */
	public void updateDistance(int distance) {
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
}
