package edu.westmont.course;



import junit.framework.TestCase;


public class DistanceFinderTest extends TestCase {
	
	
	public void testDistanceFinder() {
		String expected = "Imperial";
		DistanceFinder actual = new DistanceFinder(false);
		assertEquals(actual.usingMetricOrImperial(),expected);
		String expected2 = "Metric";
		DistanceFinder actual2 = new DistanceFinder(true);
		assertEquals(actual2.usingMetricOrImperial(),expected2);
	
	}

	public void testGetNameInt() {
		String actual = new DistanceFinder(true).getNameInt();
		String expected = "0";
		assertEquals(actual,expected);
	}

	public void testUsingMetricOrImperial() {
		String actual = new DistanceFinder(true).usingMetricOrImperial();
		String expected = "Metric";
		assertEquals(actual,expected);
		String actual2 = new DistanceFinder(false).usingMetricOrImperial();
		String expected2 = "Imperial";
		assertEquals(actual2,expected2);
	
	}

	public void testChangeMeasurement() {
		String expected = "Imperial";
		DistanceFinder actual = new DistanceFinder(true);
		actual.changeMeasurement();
		assertEquals(actual.usingMetricOrImperial(),expected);
		actual.changeMeasurement();
		String expected2 = "Metric";
		assertEquals(actual.usingMetricOrImperial(),expected2);
		
	}

	public void testGetTotalDistance() {
		Double expected = 0.0;
		Double actual = new DistanceFinder(true).getTotalDistance();
		assertEquals(actual,expected);
			
	}

	public void testGetTotalAltitudeChange() {
		Double expected = 0.0;
		Double actual = new DistanceFinder(true).getTotalAltitudeChange();
		assertEquals(actual,expected);
	}

	public void testGetAverageSpeed() {
		double DISTANCE = 5;
		long MILISECONDS = 10;
		double expected = 1800.0;
		double actual = new DistanceFinder(true).getAverageSpeed(MILISECONDS, DISTANCE);
		assertEquals(actual,expected);
		double expected2 = 1118.4681818181816;
		double actual2 = new DistanceFinder(false).getAverageSpeed(MILISECONDS, DISTANCE);
		assertEquals(actual2,expected2);
	}

	public void testGetSpeedString() {
		double SPEED = 10;
		String expected = "10.0 kph";
		String actual = new DistanceFinder(true).getSpeedString(SPEED);
		assertEquals(actual,expected);
		String expected2 = "10.0 mph";
		String actual2 = new DistanceFinder(false).getSpeedString(SPEED);
		assertEquals(actual2,expected2);
		
	}

	public void testGetTotalDistanceString() {
		String expected = "0 meters";
		String actual = new DistanceFinder(true).getTotalDistanceString();
		assertEquals(actual,expected);
		String expected2 = "0 feet";
		String actual2 = new DistanceFinder(false).getTotalDistanceString();
		assertEquals(actual2,expected2);
		
	}

	public void testGetDistanceString() {
		double DISTANCE = 5;
		String expected = "5 meters";
		String actual = new DistanceFinder(true).getDistanceString(DISTANCE);
		assertEquals(actual,expected);
		String expected2 = "16 feet";
		String actual2 = new DistanceFinder(false).getDistanceString(DISTANCE);
		assertEquals(actual2,expected2);
		double DISTANCE2 = 50000;
		String expected3 = "50.0 km";
		String actual3 = new DistanceFinder(true).getDistanceString(DISTANCE2);
		assertEquals(actual3,expected3);
		String expected4 = "31.1 mi";
		String actual4 = new DistanceFinder(false).getDistanceString(DISTANCE2);
		assertEquals(actual4,expected4);
		
		
	}

	public void testGetAltitudeString() {
		double ALTITUDE = 5;
		String expected = "5 meters";
		String actual = new DistanceFinder(true).getAltitudeString(ALTITUDE);
		assertEquals(actual,expected);
		String expected2 = "16 feet";
		String actual2 = new DistanceFinder(false).getDistanceString(ALTITUDE);
		assertEquals(actual2,expected2);
		double ALTITUDE2 = 50000;
		String expected3 = "50.0 km";
		String actual3 = new DistanceFinder(true).getDistanceString(ALTITUDE2);
		assertEquals(actual3,expected3);
		String expected4 = "31.1 mi";
		String actual4 = new DistanceFinder(false).getDistanceString(ALTITUDE2);
		assertEquals(actual4,expected4);
		
	}

	public void testReset() {
		fail("Not yet implemented");
	}

	public void testAddDistanceToLocation() 
	{
		 
		fail("do not know how use Location variable types"); 	
		
	}

	public void testGetLastString() {
		
		fail("Different string each time"); 	
		
	}

	public void testGetElapsedTimeMillis() {
		long TIME = 6;
		long expected = 5;
		long actual = new DistanceFinder(true).getElapsedTimeMillis(TIME);
		assertEquals(actual,expected);
	}

	public void testGetElapsedTimeString() {
		long TIME = 1000000000;
		String expected = "277:46:39";
		String actual = new DistanceFinder(true).getElapsedTimeString(TIME);
		assertEquals(actual,expected);
		
	}
	
	public void testAddAZero() {
		int INPUT = 5;
		String expected = "05";
		String actual = new DistanceFinder(true).addAZero(INPUT);
		assertEquals(actual,expected);	
	
	}
	
	

	public void testFormatTitleString() {
		fail("Gives a different string each time");
	}

}
