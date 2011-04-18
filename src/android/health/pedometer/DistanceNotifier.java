/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package android.health.pedometer;

import android.content.Context;
import android.health.gui.SessionStatusActivity;
import android.preference.PreferenceManager;

/**
 * Calculates and displays the distance walked.  
 * @author Levente Bagi - original foundation for code
 * @author Joel Botner - modified and adapted this version for the Android Health Manager
 */
public class DistanceNotifier implements StepListener{

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    private final long MIN_STEP_THRESHOLD = 150;
    private long lastStepTime = 0;
    
    float mDistance = 0;
    
    boolean mIsMetric = true;
    float mStepLength = Float.valueOf(PreferenceManager.getDefaultSharedPreferences(SessionStatusActivity.me).getString("stepLength", "90"));

    public DistanceNotifier(Listener listener) {
        mListener = listener;
    }
    public void setDistance(float distance) {
        mDistance = distance;
        notifyListener();
    }
    
    public void reloadSettings() {
    	mStepLength = Float.valueOf(PreferenceManager.getDefaultSharedPreferences(SessionStatusActivity.me).getString("stepLength", "90"));
    }
    
    public void onStep() {        
        if (mIsMetric) {
        	mDistance += mStepLength;
        }
        else {
            mDistance += (float)(// miles
                mStepLength // inches
                / 63360.0); // inches/mile
        }
        if(System.currentTimeMillis() - lastStepTime < MIN_STEP_THRESHOLD){
        	mDistance -= mStepLength;
        }else{
        	notifyListener();
        }
        lastStepTime = System.currentTimeMillis();
    }
    
    private void notifyListener() {
        mListener.valueChanged(mDistance);
    }
    
    public void passValue() {
    }
}
