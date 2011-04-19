package android.health.pedometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.health.gui.SessionStatusActivity;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class is responsible for abstracting all sensor handling away from the rest
 * of the core pedometer components (ExerciseSession, ExcerciseSessionList, and ExcerciseSessionInfo).
 * This class handles the sensors and estimates the distance traveled according to the specific
 * monitoring mode it is assigned to use.
 * @author Joel Botner
 */
public class DistanceChecker implements DistanceNotifier.Listener{
	/** Stores the distance currently traveled (in hundredths of a mile).
	 *  IMPORTANT: Must only be accessed through accessDistance() to maintain
	 *  thread-safe operations */
	private int distanceTraveled = 0;
	private static SessionStatusActivity callingActivity;
	private static StepService mService;
	private static Messenger mGPSService;
	private static DistanceChecker currentChecker = null;
	private static boolean serviceRunning = false;
	private static boolean gpsRunning = false;
	private static DistanceCheckerListener theListener = null;
	
	/**
     * Handler to manage communication with the GPS/Network monitoring service
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DistanceService.MSG_UPDATE_DISTANCE:
                    accessDistance(true, msg.arg1);
                    final int theDistance = msg.arg1;
                    callingActivity.mHandler.post(new Runnable(){
						@Override
						public void run() {
							callingActivity.updateValues(theDistance);
						}
                    });
                    break;
                case DistanceService.MSG_LOCK_ACQUIRED:
                    callingActivity.mHandler.post(new Runnable(){
						@Override
						public void run() {
							callingActivity.startTimer();
						}
                    });
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

	//This listener interface simply allows other objects to know when monitoring ends
	/**
	 * This simple interface simply forms a mechanism for {@link DistanceChecker} to update
	 * external objects as the distance traveled figure changes. 
	 */
	public interface DistanceCheckerListener{
		/**
		 * This method is called when the user ends the Exercise Session, this has the same
		 * effect as updateDistance(), but is only called when monitoring is shut down.
		 * 
		 * @param finalDistance - The final estimated distance covered as registered by the sensors.
		 */
		public void onMonitoringEnd(int finalDistance);
		
		/**
		 * This method simply updates the listener that the current distance traveled has changed.
		 * 
		 * @param distance - New distance traveled estimate.
		 */
		public void updateDistance(int distance);
	}
	
	//This callback is responsible for keeping the logging AND interface up to date with
	private static StepService.ICallback mCallback = new StepService.ICallback() {
        public void distanceChanged(float value) {
        	Log.i("ICallback", "Callback called: " + value);
        	final int convertedValue = (int)(value);
        	currentChecker.accessDistance(true, convertedValue);
        	
        	callingActivity.mHandler.post(new Runnable(){
				@Override
				public void run() {
					callingActivity.updateValues(convertedValue);
				}        		
        	});
        }
    };
	
    //This establishes the criteria for binding to the monitoring service
	private static ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();
            mService.registerCallback(mCallback);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
	
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mGPSConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	mGPSService = new Messenger(service);
            
            try {
                Message msg = Message.obtain(null, DistanceService.MSG_REGISTER_CHECKER, this.hashCode(), 0);
                msg.replyTo = mMessenger;
                mGPSService.send(msg);
            } catch (RemoteException e) {
            	
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        	mGPSService = null;
        }
    };
    
    /**
     * Constructs a new DistanceChecker object. <b>Note:</b> Only one DistanceChecker remains
     * active at a time, creating a second one will stop and remove the first. You must finish
     * operations with the current DistanceChecker if you do not wish to lose data.
     * 
     * @param theActivity - The Activity that began the monitoring process (used to anchor
     * the monitoring service to the same process).
     */
	public DistanceChecker(Activity theActivity){
		//End any current DistanceChecker objects, if they exist
		if (currentChecker != null){
			currentChecker.stopMonitoring();
		}
		
		callingActivity = (SessionStatusActivity)theActivity;
		currentChecker = this;
		
	}
	
	/**
	 * Initialize and utilize the GPS sensors to estimate the distance traveled by the
	 * user.
	 */
	public void useGPS(){
		//This calls the GPS service and registers itself with the service
		 callingActivity.bindService(new Intent(callingActivity, DistanceService.class), mGPSConnection, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * Initialize and utilize the built-in accelerometer to estimate the distance traveled by the
	 * user.
	 */
	public void useAccel(){
		//Initialize and bind the step monitoring service
		if (!serviceRunning){
			Context myContext = callingActivity.getApplicationContext();
			myContext.startService(new Intent(callingActivity,StepService.class));
			serviceRunning = true;
			Log.i("DistanceChecker", "Step Monitoring Service Started");
		}
		callingActivity.bindService(new Intent(callingActivity, StepService.class), mConnection, Context.BIND_AUTO_CREATE);
		Log.i("DistanceChecker", "Bound to Step Monitoring Service");
		
	}
	
	/**
	 * Returns the latest estimate on the distance traveled by the user during this session.
	 * 
	 * @return The estimated currently traveled distance.
	 */
	public int getDistance(){
		return distanceTraveled;
	}
	
	/**
	 * Registers a {@link DistanceCheckerListener} with DistanceChecker. There can only be a maximum of
	 * one listener at a time, adding a new one will replace the current one (if any).
	 * 
	 * @param listener - The new DistanceCheckerListener to monitor the distance traveled estimates with.
	 */
	public static void registerListener(DistanceCheckerListener listener){
		theListener = listener;
	}
	
	/**
	 * This method is called when the user has ended the exercise session. It will close the
	 * monitoring services and clean up all the related data.
	 */
	public void stopMonitoring(){
		if (serviceRunning) {
			// Unbind and stop the step monitoring service
			Context myContext = callingActivity.getApplicationContext();
			serviceRunning = false;
			theListener.onMonitoringEnd(accessDistance(false, 0));
			callingActivity.unbindService(mConnection);
			mService.stopSelf();
		}else if (gpsRunning){
			theListener.onMonitoringEnd(accessDistance(false, 0));
			callingActivity.unbindService(mGPSConnection);
		}
	}

	@Override
	/**
	 * This method receives updated distance values from the service's distance notifier
	 * @param value - The updated distance value. 
	 */
	public void valueChanged(float value) {
		accessDistance(true, (int)(value * 100));
	}
	
	/**
	 * 
	 * @param isChange - Whether or not to update the distance counter with the new value
	 * @param newValue - The updated distance value to potentially replace with.
	 */
	private synchronized int accessDistance(boolean isChange, int newValue){
		distanceTraveled = isChange ? newValue : distanceTraveled;
		if(isChange && theListener != null){
			theListener.updateDistance(newValue);
		}
		
		return distanceTraveled;
	}

	@Override
	public void passValue() {
	}
}
