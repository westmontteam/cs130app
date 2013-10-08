package edu.westmont.course;

import java.util.Iterator;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.model.LatLng;

public class LocationChanger extends FragmentActivity implements Iterator<LatLng> {

	private double latitude = 0;
	private double longitude = 0;
	
	public LocationChanger(){
		longitude = 180-(Math.random()*360);
		latitude = 90-(Math.random()*180);
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

	@Override
	public LatLng next() {
		longitude += 0.5-Math.random();
		latitude += 0.5-Math.random();
		if ((latitude >= 90) || (latitude <= -90)) latitude = 0;
		if ((longitude >= 180) || (longitude <= -180)) longitude = 0;
		return new LatLng(latitude, longitude);
	}
	
	public LatLng getCurrent(){
		return new LatLng(latitude, longitude);
	}

	@Override
	public void remove() {
		latitude = 0;
		longitude = 0;
	}

}
