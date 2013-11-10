package edu.westmont.course;

import android.location.Location;

public class Position extends Location {
	
	private long id;

	public Position(String provider) {
		super(provider);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
