package edu.westmont.course;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
//

import android.os.Bundle;
import android.app.Dialog;
import android.content.Intent;
//import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

	private static final int GPS_ERRORDIALOG_REQUEST = 0;
	GoogleMap myMap;

	/**
	 * Initiates an instance of the class and if the mapping service is available
	 * it changes the view to the map view.  Otherwise it displays the main activity view.
	 * Attribution for this code belongs to Lynda.com, "Building Android Apps with Google Maps API v2" 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (servicesOk()) {
			setContentView(R.layout.activity_map);
			if (initMap()){
				Toast.makeText(this, "Ready to map! This is a test.", Toast.LENGTH_SHORT).show();
				gotoLocation(34.44914,-119.661673,15);
			}
			else Toast.makeText(this, "The map in not available right now.", Toast.LENGTH_SHORT).show();


		}
		else setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void findCoordinates(View view){
		// Do something in response to button
		Intent intent = new Intent(this, ShowLocationActivity.class);
		startActivity(intent);
	}

	/**
	 * Determines if the Google Play Services are available in the current operating environment and returns
	 * a boolean.
	 * Attribution for this method belongs to Lynda.com, "Building Android Apps with the Google Maps API v2"
	 * @return Returns True if the Google Play Services are available in the current environment.  Otherwise returns false.
	 * 
	 */
	public boolean servicesOk(){
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isAvailable == ConnectionResult.SUCCESS)
			return true;
		else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog d = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
			d.show();
		}
		else Toast.makeText(this, R.string.google_play_error_message, Toast.LENGTH_SHORT).show();
		return false;
	}
	/**
	 * Initiates the map with the purpose of getting a reference to it.
	 * @return a boolean indicating if the map has been initiated.
	 */
	public boolean initMap(){
		if (myMap == null){
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			myMap = mapFrag.getMap();
		}
		return (myMap != null);
	}
	
	public void gotoLocation(double lat, double lng, float zoom){
		LatLng ll = new LatLng(lat,lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
		myMap.moveCamera(update);
	}

}
