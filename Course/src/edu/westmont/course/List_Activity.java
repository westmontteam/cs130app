package edu.westmont.course;

import java.util.LinkedList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
		setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_1, R.id.list_content, runList));
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

	private class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context context, int resource, int textViewResourceId,
				List<String> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v("getView","Using custom list adaterr to display " + runList.get(position));
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.list_item, parent, false); 
			TextView tv = (TextView) row.findViewById(R.id.list_content);
			tv.setText(runList.get(position));
			return row;
		}
	}
}