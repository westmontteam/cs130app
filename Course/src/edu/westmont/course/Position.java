package edu.westmont.course;

import android.location.Location;
import android.util.Log;

public class Position extends Location {

	private long storedID;

	public Position(String provider) {
		super(provider);
		Log.i("Position","Created Position object with provider of " + provider);
	}

	public long getId() {
		Log.i("Position","Getting the ID of the Position object: " + String.valueOf(storedID));
		return storedID;
	}

	public void setId(long id) {
		Log.i("Position","Setting the ID of the Position object to " + String.valueOf(id));
		this.storedID = id;
	}
}