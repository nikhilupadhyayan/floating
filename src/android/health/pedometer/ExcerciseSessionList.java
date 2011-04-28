package android.health.pedometer;

import java.util.GregorianCalendar;

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
	 * @param thatSession - The {@link ExcerciseSession} that was just completed.
	 * @param title - The name of this exercise session.
	 * @param secondsTaken - How many seconds the user took to complete this session
	 * @param startTime - The start time of this exercise session according to {@link GregorianCalendar} format
	 * @param logThis - Whether or not to log this session to the database
	 */
	public void monitoringDone(ExcerciseSession thatSession, String title, String secondsTaken, long startTime, boolean logThis){
		String ExerciseType = thatSession.getTypeOfTravel();
		if(logThis){
			theDB.createSession(title, thatSession.getDistance() + "m", secondsTaken + "", startTime, thatSession.getTypeOfTravel(), thatSession.getMyCalories() + "");
		}
	}
	
}
