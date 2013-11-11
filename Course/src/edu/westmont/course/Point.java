package edu.westmont.course;

import com.jjoe64.graphview.GraphViewDataInterface;

public class Point implements GraphViewDataInterface {
	public double x;
	public double y;
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}

	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return y;
	}

}
