package edu.westmont.course;

import com.google.android.gms.common.GooglePlayServicesUtil;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GPLicense extends Activity {

	@Override
	protected void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.activity_gplicense);
		Log.i("onCreate","Started GPLicense class.");
		showLicense();
	}

	private void showLicense(){
		Log.i("showLicense","Getting the Google Play License from the Google Play Services library.");
		String license = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
		TextView tv = (TextView) findViewById(R.id.gps_license_text);
		if (license != null) tv.setText(license);
		else tv.setText("Google Play Services is not installed on this device.");
	}
}