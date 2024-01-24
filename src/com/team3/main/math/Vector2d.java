package com.team3.main.math;

public class Vector2d {

	public int x, y;

	@SuppressWarnings("unused")
	public Vector2d() {
		x = 0;
		y = 0;
	}
	
	public Vector2d(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@SuppressWarnings("unused")
	public void multiply(double factor) {
		x = (int)factor*x;
		y = (int)factor*y;
	}

	@SuppressWarnings("unused")
	public double dot(Vector2d other) {
		return (other.x * x) + (other.y * y);
	}

	@SuppressWarnings("unused")
	public void add(Vector2d other) {
		x += other.x;
		y += other.y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
}
