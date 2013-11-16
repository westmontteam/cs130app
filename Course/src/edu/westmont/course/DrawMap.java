
package edu.westmont.course;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import android.location.Location;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DrawMap extends FragmentActivity implements 
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private static final int GPS_ERRORDIALOG_REQUEST = 0;
	GoogleMap myMap;
	protected LocationClient myLocationClient;
	protected int defaultZoom = 15;
	protected boolean useDefaultZoom = true;
	protected LocationChanger lc = new LocationChanger(40.715842,-74.006237);
	protected DistanceFinder ranger = new DistanceFinder();
	protected String userDefinedName = "";
	protected LinkedList<Location> listLocation = new LinkedList<Location>();
	protected LinkedList<Marker> listMarker = new LinkedList<Marker>();
	protected LinkedList<Polyline> listLine = new LinkedList<Polyline>();
	protected LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
	protected boolean showCurrentLocation = false;
	protected boolean moveCamera = true;
	protected boolean runAgain = true;
	protected boolean rebooted = true;
	protected PositionsDataSource datasource;
	protected LinkedList<String[]> MarkerStrings = new LinkedList<String[]>();
	protected Menu menuBar;

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
				myLocationClient = new LocationClient(this, this, this);
				myLocationClient.connect();
			}
			else Toast.makeText(this, "The map in not available right now.", Toast.LENGTH_SHORT).show();
		}
		else setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		userDefinedName = intent.getStringExtra(MainActivity.RUN_NAME);
		Toast.makeText(this, userDefinedName, Toast.LENGTH_SHORT).show();

		Log.w("DrawMap","opening database");
		datasource = new PositionsDataSource(this);
		datasource.open();
		datasource.setRunName(userDefinedName);
		datasource.makeRun();
		datasource.displayAllTables();//to the Log
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_menu, menu);
		menuBar = menu;
		if (menuBar != null) refreshMenuItems();
		return true;
	}

	public void refreshMenuItems(){
		MenuItem stopButton = menuBar.findItem(R.id.stopButton);
		MenuItem updateCameraButton = menuBar.findItem(R.id.updateMapCamera);
		MenuItem showLocationButton = menuBar.findItem(R.id.showCurrentLocation);

		if (runAgain) stopButton.setTitle(R.string.stop);
		else stopButton.setTitle(R.string.resume);

		if (moveCamera) updateCameraButton.setTitle(R.string.stay_put);
		else updateCameraButton.setTitle(R.string.fly_to);

		if (showCurrentLocation) showLocationButton.setTitle(R.string.show_all);
		else showLocationButton.setTitle(R.string.show_current);
	}

	/**
	 * Credit for this method belongs to lynda.com, "Building Android Apps with Google Maps API v2"
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.stopButton:
			runAgain = !runAgain;
			refreshMenuItems();
			break;
		case R.id.resetButton:
			resetMap(true,true,true);
			break;
		case R.id.updateMapCamera:
			moveCamera = !moveCamera;
			if (moveCamera) gotoCurrentLocation(false);
			refreshMenuItems();
			break;
		case R.id.showCurrentLocation:
			showCurrentLocation = !showCurrentLocation;
			if (showCurrentLocation) useDefaultZoom = true;
			gotoCurrentLocation(false);
			refreshMenuItems();
			break;
		case R.id.mapTypeNormal:
			changeMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.mapTypeSatellite:
			changeMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.mapTypeHybrid:
			changeMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case R.id.mapTypeTerrain:
			changeMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case R.id.doneButton:
			datasource.done();
			runAgain = false;
			startRunStatistics();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	};

	private void changeMapType(int mapType){
		useDefaultZoom = true;
		myMap.setMapType(mapType);
	}

	@Override
	protected void onStop() {
		super.onStop();
		MapStateManager mgr = new MapStateManager(this);
		mgr.saveUserState(myMap, showCurrentLocation, moveCamera, runAgain);
		myLocationClient.disconnect();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MapStateManager mgr = new MapStateManager(this);
		if (mgr.checkSavedStatus()) {
			showCurrentLocation = mgr.getShowCurrentPosition();
			moveCamera = mgr.getMoveCamera();
			runAgain = mgr.getRunState();
			changeMapType(mgr.getMapType());
		}
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
			//mapFrag.setRetainInstance(true);
			myMap = mapFrag.getMap();
		}
		if (myMap != null){
			myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

				@Override
				public View getInfoWindow(Marker arg0) {
					return null;
				}

				@Override
				public View getInfoContents(Marker marker) {
					View v = getLayoutInflater().inflate(R.layout.info_window, null);
					TextView tvtitle = (TextView) v.findViewById(R.id.tv_title);
					TextView tv1 = (TextView) v.findViewById(R.id.tv_text1);
					TextView tv2 = (TextView) v.findViewById(R.id.tv_text2);
					TextView tv3 = (TextView) v.findViewById(R.id.tv_text3);
					TextView tv4 = (TextView) v.findViewById(R.id.tv_text4);
					TextView tv5 = (TextView) v.findViewById(R.id.tv_text5);
					int ref = Integer.parseInt(marker.getTitle())-1;
					tvtitle.setText(marker.getTitle() + ". " + userDefinedName);
					if (ref < MarkerStrings.size()){
						tv1.setText("Distance: " + MarkerStrings.get(ref)[0]);
						tv2.setText("Time: " + MarkerStrings.get(ref)[1]);
						tv3.setText("Speed: " + MarkerStrings.get(ref)[2]);
						tv4.setText("Avg. Speed: " + MarkerStrings.get(ref)[3]);
						tv5.setText("Altitude: " + MarkerStrings.get(ref)[4]);
					}
					return v;
				}
			});
		}
		return (myMap != null);
	}


	protected void gotoCurrentLocation(boolean add){
		Location location = myLocationClient.getLastLocation();
		if (location == null) Toast.makeText(this, "Sorry, your current location is not available",Toast.LENGTH_LONG).show();
		else gotoLocation(location,add,add,add);
	}

	protected void gotoLocation(Location loc, boolean includeDatabase, boolean addMarker, boolean addLine){
		LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());
		if (loc.getAccuracy() < 100) {
			if (includeDatabase || addMarker || addLine) {
				boundsBuilder.include(ll);
				listLocation.add(loc);
				ranger.addDistanceToLocation(loc);
			}
			if (includeDatabase) datasource.createPosition(loc);
			if (addMarker) {
				MarkerStrings.add(ranger.getLastString());
				addLatLngToMap(ll);
			}
			if (addLine && listLocation.size() > 1) drawLine(listLocation.get(listLocation.size()-2), listLocation.getLast());
		}
		if (moveCamera){
			CameraUpdate update;
			if (showCurrentLocation && useDefaultZoom) {
				update = CameraUpdateFactory.newLatLngZoom(ll, defaultZoom);
				useDefaultZoom = false;
			}
			else if (showCurrentLocation) update = CameraUpdateFactory.newLatLng(ll);
			else {
				LatLngBounds bounds = boundsBuilder.build();
				update = CameraUpdateFactory.newLatLngBounds(bounds, 70);
			}
			myMap.animateCamera(update);
		}
	}

	protected void addLatLngToMap(LatLng ll){
		MarkerOptions options = new MarkerOptions()
		.title(ranger.getNameInt())
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
		.anchor(.5f,.5f)
		.position(ll);
		listMarker.add(myMap.addMarker(options));
	}

	/*
	 * Draw a line on the map between the last two objects on the listLatLng list.
	 */
	private void drawLine(Location a, Location b){
		PolylineOptions plo = new PolylineOptions()
		.add(new LatLng(a.getLatitude(), a.getLongitude()))
		.add(new LatLng(b.getLatitude(), b.getLongitude()))
		.color(Color.BLUE)
		.width(5);
		listLine.add(myMap.addPolyline(plo));
	}

	public void resetMap(boolean resetLocations, boolean resetMarkers, boolean resetLines){
		if (resetMarkers){
			Iterator<Marker> markerI = listMarker.iterator();
			while (markerI.hasNext()){
				markerI.next().remove();
			}
			listMarker = new LinkedList<Marker>();
		}
		if (resetLines){
			Iterator<Polyline> lineI = listLine.iterator();
			while (lineI.hasNext()){
				lineI.next().remove();
			}
			listLine = new LinkedList<Polyline>();
		}
		if (resetLocations){
			listLocation = new LinkedList<Location>();
			ranger = new DistanceFinder();
			MarkerStrings = new LinkedList<String[]>();
			boundsBuilder = LatLngBounds.builder();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// todo: implement this code by checking if it can be resolved.
	}

	@Override
	public void onConnected(Bundle arg0) {
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(10000);
		request.setFastestInterval(5000);
		myLocationClient.requestLocationUpdates(request, this);
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Error connecting to GPS.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (rebooted) {addBatch(datasource.getAllPositions(),false,true,true);rebooted = false;}
		if (runAgain){
			//gotoLocation(loc,true,true,true);
			gotoLocation(lc.next(),true,true,true);
			//addBatch(lc.getBatch(100),false,true,true);
		}
	}

	public void addBatch(java.util.Collection<? extends Location> list, boolean addDatabase, boolean addMarker, boolean addLine){
		Log.w("drawMap","made it to addBatch");
		Iterator<? extends Location> iterator = list.iterator();
		moveCamera = false;
		while (iterator.hasNext()){
			gotoLocation(iterator.next(),addDatabase,addMarker,addLine);
		}
		moveCamera = true;
		if (listLocation.size() > 0) gotoLocation(listLocation.getLast(),false,false,false);
	}
	public void startRunStatistics(){
		Intent intent = new Intent(this,RunStatistics.class);
		intent.putExtra(MainActivity.RUN_NAME, userDefinedName);
		startActivity(intent);
	}
}
