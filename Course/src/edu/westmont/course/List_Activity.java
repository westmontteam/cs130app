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

	LinkedList<String> exampleList = new LinkedList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_menu);

		// populating an example list to import into the list
		// normally, this list would be populated with data from the database
		Intent intent = getIntent();
		exampleList.add(intent.getStringExtra(MainActivity.RUN_NAME));
		exampleList.add("this");
		exampleList.add("is");
		exampleList.add("an");
		exampleList.add("example");
		exampleList.add("of what");
		exampleList.add("can");
		exampleList.add("be");
		exampleList.add("put");
		exampleList.add("into");
		exampleList.add("a list.");
		exampleList.add("Notice");
		exampleList.add("that");
		exampleList.add("it");
		exampleList.add("can");
		exampleList.add("scroll");
		exampleList.add("Should we sort it?");

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