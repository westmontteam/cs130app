package edu.westmont.course;

import java.util.LinkedList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class List_Activity extends ListActivity {

	private PositionsDataSource datasource;
	LinkedList<String> runList = new LinkedList<String>();
	String newRunName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_menu);

		datasource = new PositionsDataSource(this);
		datasource.open();

		Intent intent = getIntent();
		newRunName = intent.getStringExtra(MainActivity.RUN_NAME);
		runList.addAll(datasource.getAllRuns());
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, runList));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, DrawMap.class);
		intent.putExtra(MainActivity.RUN_NAME, newRunName);
		intent.putExtra(MainActivity.COMPETE_NAME, runList.get(position));
		startActivity(intent);
	}
}