package edu.westmont.course;

import java.util.LinkedList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
//

import android.location.Location;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
//import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements 
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private static final int GPS_ERRORDIALOG_REQUEST = 0;
	GoogleMap myMap;
	LocationClient myLocationClient;
	int defaultZoom = 5;
	LocationChanger lc = new LocationChanger(50,55);
	LinkedList<LatLng> listLatLng = new LinkedList<LatLng>();
	
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
				myLocationClient = new LocationClient(this, this, this);
				myLocationClient.connect();
				//gotoLocation(34.44914,-119.661673,15);
				//myMap.setMyLocationEnabled(true);
			}
			else Toast.makeText(this, "The map in not available right now.", Toast.LENGTH_SHORT).show();


		}
		else setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_menu, menu);
		return true;
	}

	/**
	 * Credit for this method belongs to lynda.com, "Building Android Apps with Google Maps API v2"
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.showCurrentLocation:
			gotoCurrentLocation();
		case R.id.mapTypeNormal:
			myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.mapTypeSatellite:
			myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.mapTypeHybrid:
			myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case R.id.mapTypeTerrain:
			myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	};

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

	public void gotoLatLng(LatLng ll, float zoom){
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
		myMap.moveCamera(update);
	}

	protected void gotoCurrentLocation(){
		Location location = myLocationClient.getLastLocation();
		if (location == null){
			Toast.makeText(this, "Sorry, your current location is not available",Toast.LENGTH_LONG).show();
		}
		else {
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(ll, defaultZoom);
			myMap.animateCamera(cu);
			//Display current altitude
			//Double d = location.getAltitude();
			//Toast.makeText(this, "Alt: " + d.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onConnected(Bundle arg0) {
		//Toast.makeText(this, "Connected to Location Services", Toast.LENGTH_LONG).show();
		gotoCurrentLocation();
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(10000);
		request.setFastestInterval(5000);
		myLocationClient.requestLocationUpdates(request, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location loc) {
		LatLng ll = lc.next();
		listLatLng.add(ll);
		Toast.makeText(this,ll.toString(),Toast.LENGTH_SHORT).show();	
		gotoLatLng(ll,8);

		//add a marker to the map using MarkerOptions object
		MarkerOptions options = new MarkerOptions()
		.title(lc.getName())
		.position(ll);
		myMap.addMarker(options);
		drawLine();

	}
	
	/*
	 * Drow a line on the map between the last two objects on the listLatLng list.
	 */
	private void drawLine(){
		if (listLatLng.size() > 1) {
		PolylineOptions plo = new PolylineOptions()
		.add(listLatLng.get(listLatLng.size()-2))
		.add(listLatLng.getLast())
		.color(Color.BLUE)
		.width(5);
		myMap.addPolyline(plo);
		}
	}
	
	/*
	 * Drow a line on the map
	 */
	private void drawAllLine(){		
		if (listLatLng.size() > 1) {
		PolylineOptions plo = new PolylineOptions()
		.addAll(listLatLng)
		.color(Color.BLUE)
		.width(5);
		myMap.addPolyline(plo);
		}	
	}
}
