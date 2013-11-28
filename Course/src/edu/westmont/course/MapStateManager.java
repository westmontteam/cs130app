package edu.westmont.course;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class MapStateManager {
	private static final String DATASAVED = "DATASAVED";
	private static final String MAPTYPE = "MAPTYPE";
	private static final String SHOWCURRENT = "SHOWCURRENT";
	private static final String MOVECAMERA = "MOVECAMERA";
	private static final String RUNAGAIN = "RUNAGAIN";
	private static final String PREFS_NAME = "mapState";
	private SharedPreferences mapStatePrefs;

	public MapStateManager(Context context) {
		mapStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}

	public void saveUserState(GoogleMap map, boolean showCurrentLocation, boolean moveCamera, boolean runAgain, boolean useMetric){
		SharedPreferences.Editor editor = mapStatePrefs.edit();
		CameraPosition position = map.getCameraPosition();
		editor.putInt(MAPTYPE, map.getMapType());
		editor.putBoolean(SHOWCURRENT, showCurrentLocation);
		editor.putBoolean(MOVECAMERA, moveCamera);
		editor.putBoolean(RUNAGAIN, runAgain);
		editor.putBoolean(DATASAVED, true);
		editor.commit();
	}

	public boolean checkSavedStatus(){
		return mapStatePrefs.getBoolean(DATASAVED, false);
	}

	public int getMapType(){
		return mapStatePrefs.getInt(MAPTYPE, GoogleMap.MAP_TYPE_NORMAL);
	}

	public boolean getShowCurrentPosition(){
		return mapStatePrefs.getBoolean(SHOWCURRENT, false);
	}

	public boolean getMoveCamera(){
		return mapStatePrefs.getBoolean(MOVECAMERA, true);
	}

	public boolean getRunState(){
		return mapStatePrefs.getBoolean(RUNAGAIN, false);
	}
}