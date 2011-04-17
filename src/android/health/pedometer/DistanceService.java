package android.health.pedometer;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import android.os.Messenger;


public class DistanceService extends Service {
	//Variables
    Messenger theChecker;
    LocationListener theMonitor;
    private static long GPS_MILLIS_UPDATE_INTERVAL = 10000;
	private LocationManager locationManager;

    //Constants used for passing messages between service and activity
    static final int MSG_REGISTER_CHECKER = 1;
    static final int MSG_UPDATE_DISTANCE = 2;
    final Messenger mMessenger = new Messenger(new StartupHandler());
	private int distanceTraveled = 0;

    /**
     * Handles the initial setup of communication between the service and the
     * distance checker.
     */
    class StartupHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CHECKER:
                    theChecker = msg.replyTo;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	theMonitor = new LocationListener(){

			Location lastLocation = null;			
			@Override
			public void onLocationChanged(Location newLocation) {
				//Unless this is the initial location, update the distance count
				if (lastLocation != null){ //TODO: Add in accuracy stipulations
					final int recentDistance = (int) (newLocation.distanceTo(lastLocation) * 100) + accessDistance(false, 0);
					accessDistance(true, recentDistance);
					try {
						theChecker.send(Message.obtain(null, MSG_UPDATE_DISTANCE, recentDistance, 0));
					} catch (RemoteException e) {
						// If this happens, the Activity closed and the service should as well
						onDestroy();
					}
				}
				lastLocation = newLocation;
			}

			@Override
			public void onProviderDisabled(String provider) {				
			}

			@Override
			public void onProviderEnabled(String provider) {				
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {				
			}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MILLIS_UPDATE_INTERVAL, 2, theMonitor);
    }

    /**
     * Ends all the location monitoring items and ends the service.
     */
    @Override
    public void onDestroy() {
        locationManager.removeUpdates(theMonitor);
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
    
    /**
     * Ensures thread-safe access to the currently estimated distance traveled.
     * 
     * @param isChange - Whether or not to change the current value.
     * @param newValue - If changing, what the new value should be.
     * @return The distance value after any potential changes have been made.
     */
    
    private synchronized int accessDistance(boolean isChange, int newValue){
		distanceTraveled = isChange ? newValue : distanceTraveled;
		return distanceTraveled;
	}

}
