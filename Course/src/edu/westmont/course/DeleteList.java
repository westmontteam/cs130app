package edu.westmont.course;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Attribution for parts of this code belongs to Lynda.com, "Building Android Apps with Google Maps API v2" 
 */
public class DeleteList extends ListActivity {

	private PositionsDataSource datasource;
	private LinkedList<String> runList = new LinkedList<String>();
	private LinkedList<String> deleteList = new LinkedList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_menu);
		Log.i("DeleteList","Creating DeleteList class. Loading database...");
		datasource = new PositionsDataSource(this);
		datasource.open();
		runList.addAll(datasource.getAllRuns());
		Collections.sort(runList);
		Log.i("DeleteList","Got the data from the database.  There are " + String.valueOf(runList.size()) + " items on the list.");
		setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_1, R.id.list_content, runList));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.delete_list_menu, menu);
		Log.v("DeleteList","Created options menu for DeleteList activity.");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.deleteButton) {
			Log.i("DeleteList","Done button pressed. There are " + String.valueOf(deleteList.size()) + " routes to be deleted.");
			for (int i = 0; i < deleteList.size(); i++){
				datasource.deleteAllRunEntries(deleteList.get(i));
				Log.v("DeleteList","Deleted " + deleteList.get(i) + " from the database.");
			}
		}
		closeActivity();
		return super.onOptionsItemSelected(item);
	}

	private void closeActivity(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i("DeleteList","Item seletced at position " + String.valueOf(position) + " in the list.");
		ImageView iv = (ImageView) v.findViewById(R.id.delete_check);
		if (deleteList.contains(runList.get(position))) {
			deleteList.remove(runList.get(position));
			iv.setVisibility(4);
		}
		else {
			iv.setVisibility(0);
			deleteList.add(runList.get(position));
		}
	}

	private class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context context, int resource, int textViewResourceId,
				List<String> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v("MyAdapter","Using custom list adaterr to display " + runList.get(position));
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.delete_list_item, parent, false); 
			TextView tv = (TextView) row.findViewById(R.id.delete_list_content);
			ImageView iv = (ImageView) row.findViewById(R.id.delete_check);
			tv.setText(runList.get(position));
			if (!deleteList.contains(runList.get(position))) iv.setVisibility(4);
			else iv.setVisibility(0);
			return row;
		}
	}
}