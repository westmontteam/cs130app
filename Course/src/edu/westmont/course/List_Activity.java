package edu.westmont.course;

import java.util.LinkedList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class List_Activity extends ListActivity {

	private PositionsDataSource datasource;
	private LinkedList<String> runList = new LinkedList<String>();
	private String newRunName = "";
	private boolean useMetric = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_menu);
		Log.i("onCreate","Creating List_Activity class. Loading database...");
		datasource = new PositionsDataSource(this);
		datasource.open();
		Log.i("onCreate","Getting the intent for the List_Activity class.");
		Intent intent = getIntent();
		newRunName = intent.getStringExtra(MainActivity.RUN_NAME);
		useMetric = intent.getBooleanExtra(MainActivity.USE_METRIC, false);
		runList.addAll(datasource.getAllRuns());
		Log.i("onCreate","Got the data from the database.  There are " + String.valueOf(runList.size()) + " items on the list.");
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, runList));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, DrawMap.class);
		intent.putExtra(MainActivity.RUN_NAME, newRunName);
		intent.putExtra(MainActivity.USE_METRIC, useMetric);
		intent.putExtra(MainActivity.COMPETE_NAME, runList.get(position));
		Log.i("onListItemClick","Starting activity DrawMap with the compete name of " + runList.get(position));
		startActivity(intent);
	}
}