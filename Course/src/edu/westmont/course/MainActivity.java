package edu.westmont.course;

//import edu.westmont.course.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import android.os.Bundle;
//import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

	private static final

	int GPS_ERRORDIALOG_REQUEST = 0;
	GoogleMap myMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (servicesOk()) {
			Toast.makeText(this, "Ready to map! Yay!", Toast.LENGTH_SHORT).show();
			setContentView(R.layout.activity_map);
		}
		else setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void findCoordinates(View view){
		// Do something in response to button
		Intent intent = new Intent(this, ShowLocationActivity.class);
		startActivity(intent);
	}

	public boolean servicesOk(){
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isAvailable == ConnectionResult.SUCCESS)
			return true;
		else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog d = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
			d.show();
		}
		else Toast.makeText(this, R.string.google_play_error_message, Toast.LENGTH_SHORT).show();
		return false;
	}

}
