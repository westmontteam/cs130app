package edu.westmont.course;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
		Log.i("MapStateManager","Created MapStateManager to store the preferences of the DrawMap class.");
	}

	public void saveUserState(GoogleMap map, boolean showCurrentLocation, boolean moveCamera, boolean runAgain){
		Log.i("saveUserState","Saving the state of the map in the MapStateManager.");
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
		Log.v("checkSavedStatus","Checking if data has already been stored in the MapStateManager.");
		return mapStatePrefs.getBoolean(DATASAVED, false);
	}

	public int getMapType(){
		Log.v("getMapType","Returning the map ");
		return mapStatePrefs.getInt(MAPTYPE, GoogleMap.MAP_TYPE_NORMAL);
	}

	public boolean getShowCurrentPosition(){
		Log.v("getShowCurrentPosition","Return the saved boolean of whether to show the current position.");
		return mapStatePrefs.getBoolean(SHOWCURRENT, false);
	}

	public boolean getMoveCamera(){
		Log.v("getMoveCamera","Return the boolean of whether to move the camera.");
		return mapStatePrefs.getBoolean(MOVECAMERA, true);
	}

	public boolean getRunState(){
		Log.v("getRunState","Return the boolean of whether the run is active or not.");
		return mapStatePrefs.getBoolean(RUNAGAIN, false);
	}
}