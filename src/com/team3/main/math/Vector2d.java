package com.team3.main.math;

public class Vector2d {

	public int x, y;
	
	public Vector2d() {
		x = 0;
		y = 0;
	}
	
	public Vector2d(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void multiply(double factor) {
		x = (int)factor*x;
		y = (int)factor*y;
	}
	
	public double dot(Vector2d other) {
		return (int)((other.x * x) + (other.y * y));
	}
	
	public void add(Vector2d other) {
		x += other.x;
		y += other.y;
	}
	
}
