package edu.westmont.course;


import java.util.ArrayList;
import java.util.List;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

public class RunStatistics extends Activity {

	private String runName="";
	private String competeName = "";
	private PositionsDataSource datasource;
	private boolean useMetric = false;
	List<Position> runPositions = new ArrayList<Position>();
	List<Position> competePositions = new ArrayList<Position>();;
	final private double metersToMph = 2.236936364;
	final private double metersToKph = 3.6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_statistics);

		Intent intent = getIntent();
		runName = intent.getStringExtra(MainActivity.RUN_NAME);
		competeName = intent.getStringExtra(MainActivity.COMPETE_NAME);
		useMetric = intent.getBooleanExtra(MainActivity.USE_METRIC, false);
		
		Log.w("onCreate","Started RunStatistics. The Run is--" + runName + "-- and the competeName is--" + competeName + "--");
		
		datasource = new PositionsDataSource(this);
		datasource.open();
		Log.w("onCreate","Gathering data from DB for " + runName);
		if (runName.length() > 0) runPositions = datasource.getCurrentRun(runName);
		Log.w("onCreate","Gathering data from DB for " + competeName);
		if (competeName.length() > 0) competePositions = datasource.getCurrentRun(competeName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.run_statistics, menu);
		return true;
	}
	
	private double convertSpeed(double value){
		if (useMetric){
			return value*metersToKph;
		}
		return value*metersToMph;
	}
	
	private GraphViewSeries getGraphViewSeries(List<Position> input, String graphType, String lineName, int color) {
		Log.w("getGraphViewSeries","Starting to create graph view for " + lineName);
		long startTime = 0;
		if (input.size()>0){
			startTime = input.get(0).getTime();
		}
		double x,y=0;
		Point[] points = new Point[input.size()];
		for (int i=0; i<input.size(); i++){
			Position position = input.get(i);
			x = (position.getTime() - startTime) / 1000;
			if (graphType.equals("Altitude")) y = (double) position.getAltitude();
			if (graphType.equals("Speed")) y = (double) position.getSpeed();
			points[i] = new Point(x,y);
		}
		GraphViewSeries gvSeries = null;
		if (points.length > 0) gvSeries = new GraphViewSeries(lineName, new GraphViewSeriesStyle(color, 5),points);
		Log.w("getGraphViewSeries","Successfully created graph view for " + lineName);
		return gvSeries;
	}
	
	private void displayGraph(String type){
		String yLabel = "";
		if (type.equals("Altitude")) {
			if (useMetric) yLabel = " (meters)";
			if (!useMetric) yLabel = " (feet)";
		}
		if (type.equals("Speed")) {
			if (useMetric) yLabel = " (kph)";
			if (!useMetric) yLabel = " (mph)";
		}
		GraphViewSeries runSeries = getGraphViewSeries(runPositions, type, runName, Color.BLUE);
		GraphViewSeries competeSeries = getGraphViewSeries(competePositions, type, competeName, Color.RED);
		Log.w("RunStatistics","Made it past the point initialization. There are: " + competePositions.size() + " points");
		
		GraphView graphView = new LineGraphView(this,type+yLabel);
		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			public String formatLabel(double value, boolean isValueX) {
				if (!isValueX) {
					return ""+(int) convertSpeed(value);
				}
				return null; // let graphview generate X-axis label for us
			}
		});
		if (runSeries != null) graphView.addSeries(runSeries);
		if (competeSeries != null) graphView.addSeries(competeSeries);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		graphView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		layout.removeViewAt(1);
		layout.addView(graphView, 1);
	}
	
	public void showStats(View view){
		DistanceFinder ranger = new DistanceFinder(useMetric);
		StringBuilder message = new StringBuilder();
		Log.w("RunStatistics","Displaying Statistics");
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		//TextView textView = (TextView) findViewById(R.id.stats);
		Log.w("Stats","Constructing TextView");
		Log.w("Stats","run name is: " + runName);
		TextView textView = new TextView(getBaseContext());
		message.append("Total time: " + ranger.getElapsedTimeString(datasource.totalTime(runName)*1000) + "\n");
		message.append("Highest speed: " + ranger.getSpeedString(datasource.highest(runName,MySQLiteHelper.COLUMN_SPEED)) + "\n");
		message.append("Highest Altitude: " + ranger.getAltitudeString(datasource.highest(runName, MySQLiteHelper.COLUMN_HIGHEST_ALTITUDE)) + "\n");
		message.append("Total distance: " + ranger.getDistanceString(datasource.totalDistance(runName)) + "\n");
		message.append("Average speed: " + ranger.getSpeedString(datasource.averageSpeed(runName)));
		Log.w("Stats","setting Text.");
		textView.setText(message.toString(), null);
		Log.w("Stats","adding view");
		layout.removeViewAt(1);
		layout.addView(textView,1);
	}

	public void graphAltitude(View view){
		Log.w("Run Statistics","Graphing Altitude");
		displayGraph("Altitude");
	}

	public void graphSpeed(View view){
		Log.w("Run Statistics","Graphing Speed");
		displayGraph("Speed");
	}
	
	public void showOlderTimes(View view){
		Log.w("Run Statistics","Displaying previous times");
		Point[] points = datasource.timeVsNumber(runName);
		GraphViewSeries exampleSeries = new GraphViewSeries(points);
		GraphView graphView = new LineGraphView(this,"Previous Times");
		graphView.addSeries(exampleSeries); // data  
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		graphView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		layout.removeViewAt(1);
		layout.addView(graphView, 1);
	}
	

}
