package com.team3.main.math;

public class Vector2f {

	public double x, y;

	@SuppressWarnings("unused")
	public Vector2f() {
		x = 0;
		y = 0;
	}
	
	public Vector2f(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@SuppressWarnings("unused")
	public void multiply(double factor) {
		x *= factor;
		y *= factor;
	}

	@SuppressWarnings("unused")
	public double dot(Vector2f other) {
		return (other.x * x) + (other.y * y);
	}
	
	public void add(Vector2f other) {
		x += other.x;
		y += other.y;
	}
	
	public Vector2d getVector2d() {
		return new Vector2d((int)x, (int)y);
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
}
