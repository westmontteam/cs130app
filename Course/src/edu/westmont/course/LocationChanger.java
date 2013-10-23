package edu.westmont.course;

import java.util.Iterator;
import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

public class LocationChanger implements Iterator<Location> {

	private double latitude = 90-(Math.random()*180);;
	private double longitude = 180-(Math.random()*360);
	private double altitude = 100;
	private Integer nameInt = 1;
	private String provider = "mockLocationGenerator";
	
	public LocationChanger(){
	}
	
	public LocationChanger(double lat, double lng){
		latitude = lat;
		longitude = lng;
	}
	
	public void setLatLng(double lat, double lng){
		latitude = lat;
		longitude = lng;
	}
	
	@Override
	public boolean hasNext() {
		return true;
	}
	
	public Integer getNameInt(){
		return nameInt;
	}
	
	public String getName(){
		return nameInt.toString();
	}
	
	public LatLng getLatLng(){
		return new LatLng(latitude, longitude);
	}

	@Override
	public Location next() {
		Location loc = new Location(provider);
		loc.setLongitude(longitude);
		loc.setLatitude(latitude);
		loc.setAltitude(altitude);
		loc.setTime(System.currentTimeMillis());
		nameInt++;
		altitude += 2-(Math.random()*4);
		longitude += 1-(2*Math.random());
		latitude += 1-(2*Math.random());
		if ((latitude >= 90) || (latitude <= -90)) latitude = 0;
		if ((longitude >= 180) || (longitude <= -180)) longitude = 0;
		return loc;
		//return new LatLng(latitude, longitude);
	}
	
	@Override
	public void remove() {
		latitude = 0;
		longitude = 0;
	}

}
