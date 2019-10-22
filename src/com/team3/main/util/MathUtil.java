package com.team3.main.util;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

public class MathUtil {
	
	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		}else if (value > max) {
			return max;
		}
		
		return value;
	}
	
	public static Vector2f clamp2(Vector2f value, double min, double max) {
		double x = clamp(value.x, min, max);
		double y = clamp(value.y, min, max);
		
		return new Vector2f(x, y);
	}
	
	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		
		if (value > max) {
			return max;
		}
		
		return value;
	}
	
	public static Vector2d clamp2(Vector2d value, int min, int max) {
		int x = clamp(value.x, min, max);
		int y = clamp(value.y, min, max);
		
		return new Vector2d(x, y);
	}

}
