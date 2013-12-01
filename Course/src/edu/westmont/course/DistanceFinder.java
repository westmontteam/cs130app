package edu.westmont.course;

import java.text.DecimalFormat;
import android.location.Location;
import android.util.Log;

public class DistanceFinder {

	private boolean useMetric = true;
	final private double imperialConversion = 3.28084;
	private double totalDistance = 0.0;
	private double altitudeChange = 0.0;
	final private DecimalFormat oneDecimal = new DecimalFormat("#,##0.0");
	final private DecimalFormat noDecimal = new DecimalFormat("#,###");
	private Location previousLocation = null;
	private long startTime = 1;
	private String[] lastString = new String[5];
	private int nameInt = 0;

	public DistanceFinder(boolean useMetricboolean){
		useMetric = useMetricboolean;
		Log.i("DistanceFinder","Created DistanceFinder instance with metric set to " + String.valueOf(useMetric));
	}

	public String getNameInt(){
		Log.i("getNameInt","Getting the name of the DistanceFinder as an ingeger.");
		return String.valueOf(nameInt);
	}

	public String usingMetricOrImperial(){
		Log.i("usingMetricOrImperial","Checking if the DistanceFinder is set to Metric or Imperial measurements");
		if (useMetric)
			return "Metric";
		return "Imperial";
	}

	public void changeMeasurement(){
		Log.w("changeMeasurement","The Measurement of this DistanceFinder is changing!");
		useMetric = !useMetric;
	}

	public Double getTotalDistance(){
		Log.i("getTotalDistance","Returning the current total distance of " + String.valueOf(totalDistance));
		return totalDistance;
	}

	private void addToAltitudeChange(double current){
		altitudeChange += Math.abs(previousLocation.getAltitude()-current);
		Log.i("addToAltitudeChange","Altitude change has grown to " + String.valueOf(altitudeChange));
	}

	public Double getTotalAltitudeChange(){
		Log.i("getTotalAltitudeChange","Returning the total Altitude change, in meters it is " + String.valueOf(altitudeChange));
		if (useMetric) return altitudeChange;
		return altitudeChange*imperialConversion;
	}

	public Double getAverageSpeed(Long timeMillis, double meters){
		Log.i("getAverageSpeed","Calculating the average speed for " + String.valueOf(timeMillis) + " milliseconds and " + String.valueOf(meters) + " meters.");
		double hours = timeMillis.doubleValue()/3600000;
		double distance = meters;
		if (useMetric) distance /= 1000;
		else distance = (distance*imperialConversion)/5280;
		if (hours > 0) return distance/hours;
		return 0.0;
	}


	public String getSpeedString(Double speed){
		Log.v("getSpeedString","Getting the properly formatted speed string for " + String.valueOf(speed));
		String output = oneDecimal.format(speed); 
		if (useMetric) output += " kph";
		else output += " mph";
		return output;
	}

	public String getTotalDistanceString(){
		Log.i("getTotalDistanceString","Getting a string for the total current distance.");
		return getDistanceString(totalDistance);
	}

	public String getDistanceString(Double dist){
		Log.v("getDistanceString","Getting the properly formatted distance string for " + String.valueOf(dist));
		if (useMetric)
			return formatDistanceString(dist);
		return formatDistanceString(dist*imperialConversion);
	}

	public String getAltitudeString(double alt){
		Log.v("getAltitudeString","Getting the properly formatted altitude string for " + String.valueOf(alt));
		if (useMetric)
			return noDecimal.format(alt) + " meters";
		return noDecimal.format(alt*imperialConversion) + " feet";
	}

	private String formatDistanceString(Double dist){
		Log.v("formatDistanceString","Formatting and returning the distance string for the value of " + String.valueOf(dist));
		if ((useMetric) && (dist >= 1000))
			return oneDecimal.format(dist/1000) + " km";
		if ((!useMetric) && (dist >= 5280))
			return oneDecimal.format(dist/5280) + " mi";
		if (useMetric)
			return noDecimal.format(dist) + " meters";
		return noDecimal.format(dist) + " feet";
	}

	public void reset(){
		Log.w("reset","Resetting the DistanceFinder object.");
		nameInt = 0;
		altitudeChange = 0.0;
		totalDistance = 0.0;
		previousLocation = null;
		startTime = 1;
	}

	public void addDistanceToLocation(Location loc){
		Log.i("addDistanceToLocation","Including a new Location in the DistanceFinder.");
		nameInt++;
		double currentDistance = 0.0;
		if (previousLocation != null) {
			addToAltitudeChange(loc.getAltitude());
			currentDistance = loc.distanceTo(previousLocation);
			totalDistance += currentDistance;
		}
		else if (previousLocation == null) startTime = loc.getTime();
		lastString = formatTitleString(loc.getTime(), currentDistance, loc.getAltitude());
		previousLocation = loc;
	}

	public String[] getLastString(){
		Log.i("getLastString","Returning the Array of the last set of String values.");
		return lastString;
	}

	public Long getElapsedTimeMillis(long currentTime){
		Log.v("getElapsedTimeMillis","Getting the elapsed time based on the current time of " + String.valueOf(currentTime));
		return currentTime-startTime;
	}

	public String getElapsedTimeString(long currentTime){
		Log.i("getElapsedTimeString","Formatting and returning the current time into hh:mm:ss");
		Integer time = (Integer) getElapsedTimeMillis(currentTime).intValue()/1000;
		Integer hours = time/3600;
		time = time % 3600;
		Integer minutes = time/60;
		time = time % 60;
		return (addAZero(hours) + ":" + addAZero(minutes) + ":" + addAZero(time));
	}

	private String addAZero(Integer input){
		Log.i("addAZero","Making sure that the input number is a 2-digit number");
		String output = "";
		if (input < 10)
			output += "0";
		return (output += input.toString());
	}

	public String[] formatTitleString(long currentTime, double currentDistance, double currentAltitude){
		Log.i("formatTitleString","Formatting and returning the string array.");
		if (previousLocation != null) {
			return new String[] {getTotalDistanceString(), 
					getElapsedTimeString(currentTime),
					getSpeedString(getAverageSpeed((currentTime-previousLocation.getTime()), currentDistance)), 
					getSpeedString(getAverageSpeed(getElapsedTimeMillis(currentTime), getTotalDistance())),
					getAltitudeString(currentAltitude)};
		}
		return new String[] {"Start","00:00:00",getSpeedString(0.0),getSpeedString(0.0),getAltitudeString(currentAltitude)};
	}
}
