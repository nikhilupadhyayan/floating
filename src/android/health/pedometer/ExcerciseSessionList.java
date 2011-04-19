package android.health.pedometer;

import android.app.Activity;
import android.health.gui.SessionStatusActivity;

/**
 * 	This class is responsible for storing, managing, and allowing access to the history
 * of all the user's exercise sessions recorded by this session. Ultimately, all non-pedometer
 * components of the application that wish to access pedometer-related information <b>must use</b>
 * this class to access the data further down.
 * 
 * @author Joel Botner
 *
 */
public class ExcerciseSessionList {
	
	//Variables
	Activity mActivity;
	PedometerDatabaseAdapter theDB;
	
	public ExcerciseSessionList(Activity theActivity, PedometerDatabaseAdapter newDB){
		mActivity = theActivity;
		theDB = newDB;
	}
	
	/**
	 * Creates a new ExerciseSession according to the specified type and monitoring method.
	 * 
	 * @param travelType - The type of exercise the user is doing: 1 for walking, 2 for running, and 3 for biking.
	 * @param useGPS - Boolean value whether to use the GPS to monitor the exercise or not. If the GPS is not used,
	 * the application will fall back to using the built-in accelerometer.
	 * @return The new ExcerciseSession created.
	 */
	public ExcerciseSession addSession(int travelType, boolean useGPS){
		ExcerciseSession newSession = new ExcerciseSession(travelType, useGPS, mActivity);
		return newSession;
	}
	
	/**
	 * Must be called when a exercise session is complete so it can be logged in the database.
	 * 
	 * @param thatSession - The newly completed session to log.
	 */
	public void monitoringDone(ExcerciseSession thatSession, String title, long secondsTaken, long startTime){
		String ExerciseType = thatSession.getTypeOfTravel();
		theDB.createSession(title, thatSession.getDistance() + "", secondsTaken + "", startTime, thatSession.getTypeOfTravel(), thatSession.getMyCalories() + "");
	}
	
	/**
	 * Removes the specified ExerciseSession from the user's history.
	 * 
	 * @param session The specific session to remove.
	 */
	public void deleteSession(ExcerciseSession session){
		//TODO: Remove the session
	}
	/**
	 * Queries the database for the stored information about a specific exercise session.
	 * 
	 * @param startTime - The start time of the session to receive info about
	 * @return A String containing the data for this session in tab-delimited format
	 */
	public String getExcerciseStatistics(long startTime){
		//TODO: Implement the exercise statistics feature
		return null;
	}
	/**
	 * Returns the ExcerciseSession that occurred on a specific day and sequence number.
	 * 
	 * @param date - The date of the desired exercise session (with the session 
	 * number for that day concatenated e.g. <b>"4-4-2011 3"</b> for the third
	 * session on April 4th, 2011).
	 * @return The ExerciseSession specified by that specific date string, null if not found.
	 */
	public ExcerciseSession getExcerciseSession(String date){
		//TODO: Implement the search feature
		return null;
	}
}
