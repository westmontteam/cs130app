package edu.westmont.course;

import java.util.Iterator;
import java.util.LinkedList;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

public class LocationChanger implements Iterator<Location> {

	public static final float NORMALIZER = 0.001f;
	private double latitude = 90-(Math.random()*180);
	private double longitude = 180-(Math.random()*360);
	private double altitude = 100;
	private float speed = 0;
	private float accuracy = 10;
	private Integer nameInt = 1;
	private String provider = "mockLocationGenerator";

	public LocationChanger(){
		Log.i("LocationChanger","Created LocationChanger object with random initial lotation");
	}

	public LocationChanger(double lat, double lng){
		Log.i("LocationChanger","Created LocationChanger with initial location of " + String.valueOf(lat) + " (lat) and " + String.valueOf(lng) + " (lng).");
		latitude = lat;
		longitude = lng;
	}

	public void setLatLng(double lat, double lng){
		Log.i("LocationChanger","Set the latitude and longitude to " + String.valueOf(lat) + ", " + String.valueOf(lng));
		latitude = lat;
		longitude = lng;
	}

	@Override
	public boolean hasNext() {
		Log.v("LocationChanger","Iterator always has another object to return.");
		return true;
	}

	public Integer getNameInt(){
		Log.i("LocationChanger","Retturn the current name of the LocationChanger as an Integer, which is " + String.valueOf(nameInt));
		return nameInt;
	}

	public String getName(){
		Log.i("LocationChanger","Retturn the current name of the LocationChanger as a String, which is " + String.valueOf(nameInt));
		return nameInt.toString();
	}

	public LatLng getLatLng(){
		Log.i("LocationChanger","Return the current location as a LatLng object.");
		return new LatLng(latitude, longitude);
	}

	@Override
	public Location next() {
		Log.i("LocationChanger","Return the next Location as a Location object.");
		Location loc = new Location(provider);
		loc.setLongitude(longitude);
		loc.setLatitude(latitude);
		loc.setAltitude(altitude);
		loc.setSpeed(speed);
		loc.setAccuracy(accuracy);
		loc.setTime(System.currentTimeMillis());
		nameInt++;
		altitude += 2-(Math.random()*4);
		longitude += (1-(2*Math.random())) * NORMALIZER;
		latitude += (1-(2*Math.random())) * NORMALIZER;
		speed = (float) (100 - (Math.random() * 100));
		if ((latitude >= 90) || (latitude <= -90)) latitude = 0;
		if ((longitude >= 180) || (longitude <= -180)) longitude = 0;
		return loc;
	}

	@Override
	public void remove() {
		Log.i("LocationChanger","Set the latitude and longitude for the LocationChanger to 0, 0.");
		latitude = 0;
		longitude = 0;
	}

	public LinkedList<Location> getBatch(int number){
		Log.i("LocationChanger","Return a batch (LinkedList) of " + String.valueOf(number) + " Location objects.");
		LinkedList<Location> output = new LinkedList<Location>();
		for (int i = 0; i < number; i++){
			output.add(next());
		}
		return output;
	}
}