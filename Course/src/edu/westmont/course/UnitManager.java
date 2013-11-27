package edu.westmont.course;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class UnitManager {
	private static final String DATASAVED = "DATASAVED";
	private static final String USEMETRIC = "useMetric";
	private static final String PREFS_NAME = "unitState";
	private SharedPreferences unitPrefs;

	public UnitManager(Context context) {
		unitPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveUserState(boolean useMetric){
		SharedPreferences.Editor editor = unitPrefs.edit();
		editor.putBoolean(USEMETRIC, useMetric);
		editor.putBoolean(DATASAVED, true);
		editor.commit();
	}
	
	public boolean checkSavedStatus(){
		return unitPrefs.getBoolean(DATASAVED, false);
	}
	
	public boolean getUseMetric(){
		return unitPrefs.getBoolean(USEMETRIC, false);
	}
}