package edu.westmont.course;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jjoe64.graphview.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

public class RunStatistics extends Activity {

	private String runName="";
	private PositionsDataSource datasource;
	private boolean useMetric = false;
	final private double metersToMph = 2.236936364;
	final private double metersToKph = 3.6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_statistics);

		Intent intent = getIntent();
		runName = intent.getStringExtra(MainActivity.RUN_NAME);
		useMetric = intent.getBooleanExtra(MainActivity.USE_METRIC, false);
		datasource = new PositionsDataSource(this);
		datasource.open();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run_statistics, menu);
		return true;
	}
	
	private double convertSpeed(double value){
		if (useMetric){
			return value*metersToKph;
		}
		return value*metersToMph;
	}

	private void displayGraph(String type){
		String yLabel = "";
		int i;
		if (type.equals("Altitude")) {
			if (useMetric) yLabel = " (meters)";
			if (!useMetric) yLabel = " (feet)";
		}
		if (type.equals("Speed")) {
			if (useMetric) yLabel = " (kph)";
			if (!useMetric) yLabel = " (mph)";
		}
		double x,y=0;
		long startTime;
		List<Position> positions = datasource.getAllPositions(runName);
		if (positions.size()>0){
			startTime = positions.get(0).getTime();
		} else startTime = 0;
		Point[] points = new Point[positions.size()];
		for (i=0;i<positions.size();i++){
			Position position = positions.get(i);
			x = (position.getTime() - startTime) / 1000;
			if (type.equals("Altitude")) y = (double) position.getAltitude();
			if (type.equals("Speed")) y = (double) position.getSpeed();
			points[i] = new Point(x,y);
		}
		Log.w("RunStatistics","Made it past the point initialization. There are: " + points.length + " points");
		GraphViewSeries exampleSeries = new GraphViewSeries(points);
		GraphView graphView = new LineGraphView(this,type+yLabel);
		//graphView.setPadding(50, 50, 50, 50);
		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			public String formatLabel(double value, boolean isValueX) {
				if (!isValueX) {
					return ""+(int) convertSpeed(value);
				}
				return null; // let graphview generate X-axis label for us
			}
		});
		graphView.addSeries(exampleSeries); // data  
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		graphView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		layout.removeViewAt(1);
		layout.addView(graphView, 1);
	}
	
	public void showStats(View view){
		StringBuilder message = new StringBuilder();
		Log.w("RunStatistics","Displaying Statistics");
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		//TextView textView = (TextView) findViewById(R.id.stats);
		Log.w("Stats","Constructing TextView");
		Log.w("Stats","run name is: " + runName);
		TextView textView = new TextView(getBaseContext());
		message.append("Total time: " + datasource.totalTime(runName).toString() + " seconds. \n");
		message.append("Highest speed: " + Math.round(datasource.highest(runName,MySQLiteHelper.COLUMN_SPEED)) + " m/s \n");
		message.append("Highest Altitude: " + Math.round(datasource.highest(runName, MySQLiteHelper.COLUMN_HIGHEST_ALTITUDE)) + " meters. \n");
		message.append("Total distance: " + Math.round(datasource.totalDistance(runName)) + " meters. \n");
		message.append("Average speed: " + Math.round(datasource.averageSpeed(runName)) + " m/s.");
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
