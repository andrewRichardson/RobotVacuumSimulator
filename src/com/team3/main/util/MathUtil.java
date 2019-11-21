package com.team3.main.util;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;

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

	public static Color averageColor(BufferedImage bi) {
		long sum_r = 0, sum_g = 0, sum_b = 0;
		for (int x = 0; x < bi.getWidth(); x++) {
			for (int y = 0; y < bi.getHeight(); y++) { // For each pixel, add the RGB values to the sums
				Color pixel = new Color(bi.getRGB(x, y));
				sum_r += pixel.getRed();
				sum_g += pixel.getGreen();
				sum_b += pixel.getBlue();
			}
		}
		int num = bi.getWidth() * bi.getHeight();

		// The average color is the total RGB sums divided by the number of pixels sampled
		return new Color((int)sum_r / num, (int)sum_g / num, (int)sum_b / num);
	}

}
