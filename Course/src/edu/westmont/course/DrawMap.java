
package edu.westmont.course;


import java.util.Iterator;
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
	protected boolean useMetric = false;
	protected DistanceFinder ranger;
	protected String runName = "";
	protected String competeName = "";
	protected LinkedList<Location> listLocation = new LinkedList<Location>();
	protected LinkedList<Marker> listMarker = new LinkedList<Marker>();
	protected LinkedList<Polyline> listLine = new LinkedList<Polyline>();
	protected LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
	protected LinkedList<String[]> markerStrings = new LinkedList<String[]>();

	protected LinkedList<Location> competeListLocation = new LinkedList<Location>();
	protected LinkedList<Marker> competeListMarker = new LinkedList<Marker>();
	protected LinkedList<Polyline> competeListLine = new LinkedList<Polyline>();
	protected LinkedList<String[]> competeMarkerStrings = new LinkedList<String[]>();	

	protected boolean showCurrentLocation = false;
	protected boolean moveCamera = true;
	protected boolean runAgain = true;
	protected boolean rebooted = true;
	protected boolean isARace = false;
	protected PositionsDataSource datasource;
	protected Menu menuBar;

	/**
	 * Initiates an instance of the class and if the mapping service is available
	 * it changes the view to the map view.  Otherwise it displays the main activity view.
	 * Attribution for this code belongs to Lynda.com, "Building Android Apps with Google Maps API v2" 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get data from intent and display the run and/or compete name
		Intent intent = getIntent();
		runName = intent.getStringExtra(MainActivity.RUN_NAME);
		competeName = intent.getStringExtra(MainActivity.COMPETE_NAME);
		useMetric = intent.getBooleanExtra(MainActivity.USE_METRIC, false);
		ranger = new DistanceFinder(useMetric);
		String displayName = "";
		if (runName.length() > 0) {
			isARace = true;
			displayName += runName;
			if (competeName.length() > 0) displayName += " vs. ";
		}
		displayName += competeName;
		Toast.makeText(this, displayName, Toast.LENGTH_LONG).show();

		if (servicesOk()) {
			setContentView(R.layout.activity_map);
			if (initMap()){
				if (isARace) {
					myLocationClient = new LocationClient(this, this, this);
					myLocationClient.connect();
				}
			}
			else Toast.makeText(this, "The map in not available right now.", Toast.LENGTH_SHORT).show();
		}
		else setContentView(R.layout.activity_main);

		Log.w("DrawMap","opening database");
		datasource = new PositionsDataSource(this);
		datasource.open();
		if (isARace) datasource.setRunName(runName);
		//datasource.makeRun();
		datasource.displayAllTables();//to the Log
		if (competeName.length() > 0) {
			addBatch(datasource.getBestRun(competeName,MySQLiteHelper.COLUMN_BEST_TIME), false, new DistanceFinder(useMetric), competeListLocation, competeListMarker, competeListLine, competeMarkerStrings, Color.RED);
			//if (competeListLocation.size() > 0) gotoLatLng(new LatLng(competeListLocation.getLast().getLatitude(), competeListLocation.getLast().getLongitude()));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_menu, menu);
		menuBar = menu;
		refreshMenuItems();
		return true;
	}

	public void refreshMenuItems(){
		if (menuBar != null) {
			MenuItem stopButton = menuBar.findItem(R.id.stopButton);
			MenuItem resetButton = menuBar.findItem(R.id.resetButton);
			MenuItem updateCameraButton = menuBar.findItem(R.id.updateMapCamera);
			MenuItem showLocationButton = menuBar.findItem(R.id.showCurrentLocation);
			if (!isARace) {
				stopButton.setTitle("");
				resetButton.setTitle("");
			}
			else {
				if (runAgain) stopButton.setTitle(R.string.stop);
				else stopButton.setTitle(R.string.resume);
			}
			if (moveCamera) updateCameraButton.setTitle(R.string.stay_put);
			else updateCameraButton.setTitle(R.string.fly_to);

			if (showCurrentLocation) showLocationButton.setTitle(R.string.show_all);
			else showLocationButton.setTitle(R.string.show_current);
		}
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
			if (moveCamera) gotoCurrentLocation();
			refreshMenuItems();
			break;
		case R.id.showCurrentLocation:
			showCurrentLocation = !showCurrentLocation;
			if (showCurrentLocation) useDefaultZoom = true;
			gotoCurrentLocation();
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
			datasource.done(runName);
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
		if (myLocationClient != null) myLocationClient.disconnect();
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
					TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
					TextView tv1 = (TextView) v.findViewById(R.id.tv_text1);
					TextView tv2 = (TextView) v.findViewById(R.id.tv_text2);
					TextView tv3 = (TextView) v.findViewById(R.id.tv_text3);
					TextView tv4 = (TextView) v.findViewById(R.id.tv_text4);
					TextView tv5 = (TextView) v.findViewById(R.id.tv_text5);
					int ref = Integer.parseInt(marker.getTitle())-1;
					int type = Integer.parseInt(marker.getSnippet());
					String[] content = null;
					if ((type == Color.BLUE) && (ref < markerStrings.size())) {
						content = markerStrings.get(ref);
						tvTitle.setText(runName);
					}
					else if ((type == Color.RED) && (ref < competeMarkerStrings.size())) {
						content = competeMarkerStrings.get(ref);
						tvTitle.setText(competeName);
					}
					if (content != null) {
						tv1.setText("Distance: " + content[0]);
						tv2.setText("Time: " + content[1]);
						tv3.setText("Current Speed: " + content[2]);
						tv4.setText("Avg. Speed: " + content[3]);
						tv5.setText("Altitude: " + content[4]);
					}
					return v;
				}
			});
		}
		return (myMap != null);
	}

	protected void gotoCurrentLocation(){
		Location location = myLocationClient.getLastLocation();
		if (location == null) Toast.makeText(this, "Sorry, your current location is not available",Toast.LENGTH_LONG).show();
		else gotoLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
	}

	protected void updateMap(boolean includeDatabase, Location loc, DistanceFinder df, LinkedList<Location> locations, LinkedList<Marker> markers, LinkedList<Polyline> lines, LinkedList<String[]> strings, int color) {
		if (loc.getAccuracy() < 100) {
			LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());
			locations.add(loc);
			df.addDistanceToLocation(loc);
			if (includeDatabase) datasource.createPosition(loc,runName);
			strings.add(df.getLastString());
			addMarkerToMap(ll, markers, df, color);
			if (locations.size() > 1) drawLine(locations.get(locations.size()-2), locations.getLast(), lines, color);
			gotoLatLng(ll);
		}
	}

	protected void gotoLatLng(LatLng ll){
		boundsBuilder.include(ll);
		if (moveCamera) {
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

	protected void addMarkerToMap(LatLng ll, LinkedList<Marker> list, DistanceFinder df, int type){
		int point = R.drawable.ic_point;
		if (type == Color.RED) point = R.drawable.ic_point_red; 
		MarkerOptions options = new MarkerOptions()
		.title(df.getNameInt())
		.snippet(String.valueOf(type))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
		.anchor(.5f,.5f)
		.position(ll);
		list.add(myMap.addMarker(options));
		if (list.size() > 1){
			list.get(list.size()-2).setIcon(BitmapDescriptorFactory.fromResource(point));
		}
	}

	/*
	 * Draw a line on the map between the last two objects on the listLatLng list.
	 */
	private void drawLine(Location a, Location b, LinkedList<Polyline> list, int color){
		PolylineOptions plo = new PolylineOptions()
		.add(new LatLng(a.getLatitude(), a.getLongitude()))
		.add(new LatLng(b.getLatitude(), b.getLongitude()))
		.color(color)
		.width(5);
		list.add(myMap.addPolyline(plo));
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
			ranger = new DistanceFinder(useMetric);
			markerStrings = new LinkedList<String[]>();
			boundsBuilder = LatLngBounds.builder();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Error connecting to GPS.", Toast.LENGTH_SHORT).show();
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
		Toast.makeText(this, "Error connecting to GPS.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (rebooted) {
			//addBatch(datasource.getCurrentRun(runName), false, ranger, listLocation, listMarker, listLine, markerStrings, Color.BLUE);
			rebooted = false;
		}
		if (runAgain){
			//gotoLocation(loc,true,true,true);
			updateMap(true, lc.next(), ranger, listLocation, listMarker, listLine, markerStrings, Color.BLUE);
			//addBatch(lc.getBatch(100),false,true,true);
		}
	}

	public void addBatch(java.util.Collection<? extends Location> list, boolean includeDatabase, DistanceFinder df, LinkedList<Location> locations, LinkedList<Marker> markers, LinkedList<Polyline> lines, LinkedList<String[]> strings, int color){
		Log.i("drawMap","made james it to addBatch");
		Iterator<? extends Location> iterator = list.iterator();
		moveCamera = false;
		while (iterator.hasNext()){
			Log.i("drawMap", String.valueOf(locations.size()));
			updateMap(includeDatabase, iterator.next(), df, locations, markers, lines, strings, color);
		}
		moveCamera = true;
	}

	public void startRunStatistics(){
		if (myLocationClient != null) myLocationClient.disconnect();
		Intent intent = new Intent(this,RunStatistics.class);
		intent.putExtra(MainActivity.RUN_NAME, runName);
		intent.putExtra(MainActivity.USE_METRIC, useMetric);
		startActivity(intent);
	}
}
