package com.team3.main.util;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

public class MathUtil {
	
	public static double clampf(double value, double min, double max) {
		if (value < min) {
			return min;
		}else if (value > max) {
			return max;
		}
		
		return value;
	}
	
	public static Vector2f clamp2f(Vector2f value, double min, double max) {
		double x = clampf(value.x, min, max);
		double y = clampf(value.y, min, max);
		
		return new Vector2f(x, y);
	}
	
	public static int clampd(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		
		if (value > max) {
			return max;
		}
		
		return value;
	}
	
	public static Vector2d clamp2d(Vector2d value, int min, int max) {
		int x = clampd(value.x, min, max);
		int y = clampd(value.y, min, max);
		
		return new Vector2d(x, y);
	}

}
