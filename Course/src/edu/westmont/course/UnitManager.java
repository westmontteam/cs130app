package edu.westmont.course;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

@SuppressWarnings("unused")
public class UnitManager {
	private static final String DATASAVED = "DATASAVED";
	private static final String USEMETRIC = "useMetric";
	private static final String PREFS_NAME = "unitState";
	private SharedPreferences unitPrefs;

	public UnitManager(Context context) {
		unitPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Log.i("UnitManager","Created UnitManager object to manage the shared preferences of the useMetric setting.");
	}

	public void saveUserState(boolean useMetric){
		Log.i("UnitManager","Saving useMetric preference to " + String.valueOf(useMetric));
		SharedPreferences.Editor editor = unitPrefs.edit();
		editor.putBoolean(USEMETRIC, useMetric);
		editor.putBoolean(DATASAVED, true);
		editor.commit();
	}

	public boolean checkSavedStatus(){
		Log.v("UnitManager","Checking if data has been stored in this object.");
		return unitPrefs.getBoolean(DATASAVED, false);
	}

	public boolean getUseMetric(){
		Log.v("UnitManager","Return the stored value of the UnitManager object.");
		return unitPrefs.getBoolean(USEMETRIC, false);
	}
}