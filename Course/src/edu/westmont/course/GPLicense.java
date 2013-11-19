package edu.westmont.course;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GPLicense extends Activity {

	@Override
	protected void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.activity_gplicense);
		showLicense();
	}
	
	private void showLicense(){
		String license = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
		TextView tv = (TextView) findViewById(R.id.gps_license_text);
		if (license != null) tv.setText(license);
		else tv.setText("Google Play Services is not installed on this device.");
	}
	
}
