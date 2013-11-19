package edu.westmont.course;

import android.os.Bundle;
//import android.app.Dialog;
import android.app.Activity;
import android.content.Intent;
//import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	public final static String RUN_NAME = "edu.westmont.course.MESSAGE";
	public final static String USE_METRIC = "edu.westmont.course.MEASUREMENT";


   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void openMap(View view){
		//in response to a button
		//Intent intent = new Intent(this, DrawMap.class);
		Intent intent = new Intent(this, List_Activity.class);
		EditText editText = (EditText) findViewById(R.id.start_run);
		String runName = editText.getText().toString();
		intent.putExtra(RUN_NAME, runName);
		startActivity(intent);
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
		return super.onOptionsItemSelected(item);
	}

	protected void showLicense(){
		Intent intent = new Intent(this, GPLicense.class);
		startActivity(intent);
	}

}
