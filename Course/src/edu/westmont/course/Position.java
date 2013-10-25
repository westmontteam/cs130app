package edu.westmont.course;

import com.google.android.gms.maps.model.LatLng;

public class Position {
	private long id;
	private double height;
	private LatLng ll;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getHeight(){
		return height;
	}

	public void setHeight(double height){
		this.height = height;
	}

	public LatLng getll(){
		return ll;
	}

	public void setll(LatLng ll){
		this.ll = ll;
	}
}
