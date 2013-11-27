package edu.westmont.course;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	public final static String RUN_NAME = "edu.westmont.course.MESSAGE";
	public final static String COMPETE_NAME = "edu.westmont.course.COMPETE";
	public final static String USE_METRIC = "edu.westmont.course.MEASUREMENT";
	private boolean useMetric = false;
	protected Menu menuBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menuBar = menu;
		refreshMenuItems();
		return true;
	}

	private void refreshMenuItems() {
		if (menuBar != null) {
			MenuItem metricButton = menuBar.findItem(R.id.action_use_metric);
			if (useMetric) metricButton.setTitle(R.string.use_metric);
			else metricButton.setTitle(R.string.use_imperial);
		}
	}

	public void openNewMap(View view){
		Intent intent = new Intent(this, DrawMap.class);
		EditText editText = (EditText) findViewById(R.id.new_run1);
		String runName = editText.getText().toString();		
		if (runName.length() > 0) {
			runName = sanitizeInput(runName);
			intent.putExtra(RUN_NAME, runName);
			intent.putExtra(USE_METRIC, useMetric);
			startActivity(intent);
		}
	}

	public void openCompeteMap(View view){
		Intent intent = new Intent(this, List_Activity.class);
		EditText editText = (EditText) findViewById(R.id.new_run2);
		String runName = editText.getText().toString();		
		if (runName.length() > 0) runName = sanitizeInput(runName);
		intent.putExtra(RUN_NAME, runName);
		intent.putExtra(USE_METRIC, useMetric);
		startActivity(intent);
	}

	public String sanitizeInput(String runName){
		runName = runName.trim();

		//if the first thing in the string is a number, this replaces it with an _. (SQLite can't handle numbers first) 
		if (runName.substring(0, 1).matches("[0-9]")) runName = "_" + runName.substring(1);

		//replaces anything that is not a letter or a number with an underscore.
		runName = runName.replaceAll("[^[a-zA-Z_0-9]]", "_");
		return runName;
	}

	public void exitApp(MenuItem item) {
		System.exit(0);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.action_license) showLicense();
		if (item.getItemId() == R.id.action_use_metric) {
			useMetric = !useMetric;
			refreshMenuItems();
		}
		return super.onOptionsItemSelected(item);
	}

	protected void showLicense(){
		Intent intent = new Intent(this, GPLicense.class);
		startActivity(intent);
	}


	@Override
	protected void onStop() {
		super.onStop();
		UnitManager mgr = new UnitManager(this);
		mgr.saveUserState(useMetric);
	}

	@Override
	protected void onResume() {
		super.onResume();
		UnitManager mgr = new UnitManager(this);
		if (mgr.checkSavedStatus()) {
			useMetric = mgr.getUseMetric();
		}
	}
}
