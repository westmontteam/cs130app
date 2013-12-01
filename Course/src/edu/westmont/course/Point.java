package edu.westmont.course;

import android.util.Log;
import com.jjoe64.graphview.GraphViewDataInterface;

public class Point implements GraphViewDataInterface {
	public double x;
	public double y;

	public Point(double x, double y){
		this.x = x;
		this.y = y;
		Log.i("Point","Create Point object with x,y values of " + String.valueOf(x) + " and " + String.valueOf(y));
	}

	@Override
	public double getX() {
		Log.v("Point","Returning x vith the value of " + String.valueOf(x));
		return x;
	}

	@Override
	public double getY() {
		Log.v("Point","Returning y vith the value of " + String.valueOf(y));
		return y;
	}
}