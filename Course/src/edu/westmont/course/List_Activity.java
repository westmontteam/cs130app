package edu.westmont.course;

import java.util.LinkedList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class List_Activity extends ListActivity {

	private PositionsDataSource datasource;
	LinkedList<String> exampleList = new LinkedList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_menu);
		
		datasource = new PositionsDataSource(this);
		datasource.open();
		
		Intent intent = getIntent();
		exampleList.add(intent.getStringExtra(MainActivity.RUN_NAME));
		exampleList.addAll(datasource.getAllRuns());

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exampleList));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Toast.makeText(this, exampleList.get(position), Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(this, DrawMap.class);
		intent.putExtra(MainActivity.RUN_NAME, exampleList.get(position));
    	startActivity(intent);		
	
	}
}