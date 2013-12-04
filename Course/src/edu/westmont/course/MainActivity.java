package edu.westmont.course;

import java.util.LinkedList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	public final static String RUN_NAME = "edu.westmont.course.MESSAGE";
	public final static String COMPETE_NAME = "edu.westmont.course.COMPETE";
	public final static String USE_METRIC = "edu.westmont.course.MEASUREMENT";
	private boolean useMetric = false;
	protected Menu menuBar;
	private PositionsDataSource datasource;
	private LinkedList<String> runList = new LinkedList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		datasource = new PositionsDataSource(this);
		datasource.open();
		runList.addAll(datasource.getAllRuns());
		Log.i("MainActivity","Welcome to Course!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		Log.v("MainActivity","Created options menu for MainActivity.");
		menuBar = menu;
		refreshMenuItems();
		return true;
	}

	private void refreshMenuItems() {
		Log.v("MainActivity","Refreshing the menu bar to reflect the current settings.");
		if (menuBar != null) {
			MenuItem metricButton = menuBar.findItem(R.id.action_use_metric);
			if (useMetric) metricButton.setTitle(R.string.use_metric);
			else metricButton.setTitle(R.string.use_imperial);
		}
	}

	public void openNewMap(View view){
		Log.i("MainActivity","Creating intent and starting DrawMap activity with a brand new run; do not display another run concurrently.");
		EditText editText = (EditText) findViewById(R.id.new_run1);
		String runName = editText.getText().toString();		
		if (runName.length() > 0) {
			runName = sanitizeInput(runName);
			if ((runName.length() > 0) && (verifyRunIsUnique(runName))) {
				Intent intent = new Intent(this, DrawMap.class);
				intent.putExtra(RUN_NAME, runName);
				intent.putExtra(COMPETE_NAME, "");
				intent.putExtra(USE_METRIC, useMetric);
				startActivity(intent);
			}
		} else Log.e("MainActivity","Error: You must enter a run name in order to create a new route.");
	}

	public void openCompeteAndRunMap(View view) {
		Log.i("MainActivity","Clicked the second button; running a new course and comparing it with a saved route.");
		EditText editText = (EditText) findViewById(R.id.new_run2);
		String runName = editText.getText().toString();		
		if (runName.length() > 0) {
			runName = sanitizeInput(runName);
			if ((runName.length() > 0) && (verifyRunIsUnique(runName))) openCompeteMap(view);
		} else Log.e("MainActivity","Error: You must enter text into the TextEdit field in order to create a new run.");
	}

	public void openCompeteMap(View view){
		Log.i("MainActivity","Creating intent and starting DrawMap activity for Competitive view.");
		Intent intent = new Intent(this, List_Activity.class);
		EditText editText = (EditText) findViewById(R.id.new_run2);
		String runName = "";
		if (view.getId() != R.id.button3) {
			runName = editText.getText().toString();		
			if (runName.length() > 0) runName = sanitizeInput(runName);
		}
		intent.putExtra(RUN_NAME, runName);
		intent.putExtra(USE_METRIC, useMetric);
		startActivity(intent);
	}

	private boolean verifyRunIsUnique(String name){
		if (runList.contains(name)) {
			Log.e("MainActivity","Error: Database already has a route using the name " + name);
			Toast.makeText(this,"That name is already being used. Try another name.", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	public String sanitizeInput(String runName){
		Log.v("MainActivity","Cleaning up the string in the TextEdit to prevent database errors.");
		runName = runName.trim();
		if (runName.length() > 0) {
			//if the first thing in the string is a number, this replaces it with an _. (SQLite can't handle numbers first) 
			if (runName.substring(0, 1).matches("[0-9]")) runName = "_" + runName.substring(1);

			//replaces anything that is not a letter or a number with an underscore.
			runName = runName.replaceAll("[^[a-zA-Z_0-9]]", "_");
		}
		return runName;
	}

	public void exitApp(MenuItem item) {
		Log.i("MainActivity","Exiting Course.");
		System.exit(0);
	}

	@Override
	public void onClick(View v) {
		Log.w("MainActivity","This action has no effect.");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.action_use_metric) {
			Log.v("MainActivity","Switching the measurement system that will be used in this app.  This setting will be passed to each other activity in the application.");
			useMetric = !useMetric;
			refreshMenuItems();
		}
		else if (item.getItemId() == R.id.action_delete) {
			Log.v("MainActivity","Delete button pressed. Loading DeleteList activity.");
			Intent intent = new Intent(this, DeleteList.class);
			startActivity(intent);
		}
		else if (item.getItemId() == R.id.action_license) {
			Log.v("MainActivity","Selected options menu item to display Google Play Services License.");
			showLicense();
		}
		return super.onOptionsItemSelected(item);
	}

	protected void showLicense(){
		Log.i("MainActivity","Starting Activity GPLicense.");
		Intent intent = new Intent(this, GPLicense.class);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("MainActivity","Saving preferences for activity before stopping.");
		UnitManager mgr = new UnitManager(this);
		mgr.saveUserState(useMetric);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("MainActivity","Loading settings from UnitManager.");
		UnitManager mgr = new UnitManager(this);
		if (mgr.checkSavedStatus()) useMetric = mgr.getUseMetric();
	}
}