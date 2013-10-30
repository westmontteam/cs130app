package edu.westmont.course;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import android.location.Location;

public class DistanceFinder {

	private boolean useMetric = true;
	final private double imperialConversion = 3.28084;
	private Double currentDistance = 0.0;
	final private DecimalFormat oneDecimal = new DecimalFormat("#,##0.0");
	final private DecimalFormat noDecimal = new DecimalFormat("#,###");
	private Location previousLocation = null;
	private long startTime = 1;
	private long currentTime = 1;

	public String usingMetricOrImperial(){
		if (useMetric)
			return "Metric";
		return "Imperial";
	}

	public void changeMeasurement(){
		useMetric = !useMetric;
	}

	public Double getCurrentDistance(){
		return currentDistance;
	}

	public String getCurrentDistanceString(){
		return getDistanceString(currentDistance);
	}

	public String getDistanceString(Double dist){
		if (useMetric)
			return formatDistanceString(dist);
		return formatDistanceString(dist*imperialConversion);
	}

	private String formatDistanceString(Double dist){
		if ((useMetric) && (dist >= 1000))
			return oneDecimal.format(dist/1000) + " km";
		if ((!useMetric) && (dist >= 5280))
			return oneDecimal.format(dist/5280) + " mi";
		if (useMetric)
			return noDecimal.format(dist) + " meters";
		return noDecimal.format(dist) + " feet";
	}

	public void reset(){
		currentDistance = 0.0;
		previousLocation = null;
		startTime = 1;
		currentTime = 1;
	}

	public void setDistance(Double dist){
		currentDistance = dist;
	}

	public void addDistanceToLocation(Location loc){
		if (previousLocation != null) {
			currentDistance += loc.distanceTo(previousLocation);
			currentTime = loc.getTime();
		}
		if (previousLocation == null)
			startTime = loc.getTime();
		previousLocation = loc;
	}

	public Double calculateAllDistances(Collection<Location> list){
		Double distance = 0.0;
		if (list.size() > 1) {
			Iterator<Location> iterator = list.iterator();
			Location previous = iterator.next();
			while (iterator.hasNext()) {
				Location current = iterator.next();
				distance += current.distanceTo(previous);
				previous = current;
			}
		}
		return distance;
	}

	public Long getElapsedTimeMillis(){
		return currentTime-startTime;
	}

	public String getElapsedTimeString(){
		Integer time = (Integer) getElapsedTimeMillis().intValue()/1000;
		Integer hours = time/3600;
		time = time % 3600;
		Integer minutes = time/60;
		time = time % 60;
		return (addAZero(hours) + ":" + addAZero(minutes) + ":" + addAZero(time));
	}

	private String addAZero(Integer input){
		String output = "";
		if (input < 10)
			output += "0";
		return (output += input.toString());
	}


}
