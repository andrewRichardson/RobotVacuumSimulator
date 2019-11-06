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

	public static Color averageColor(BufferedImage bi, int x0, int y0, int w, int h) {
		int x1 = x0 + w;
		int y1 = y0 + h;
		long sumr = 0, sumg = 0, sumb = 0;
		for (int x = x0; x < x1; x++) {
			for (int y = y0; y < y1; y++) {
				Color pixel = new Color(bi.getRGB(x, y));
				sumr += pixel.getRed();
				sumg += pixel.getGreen();
				sumb += pixel.getBlue();
			}
		}
		int num = w * h;

		return new Color((int)sumr / num, (int)sumg / num, (int)sumb / num);
	}

}
