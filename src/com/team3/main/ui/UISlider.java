package com.team3.main.ui;

import java.awt.Color;

public class UISlider extends UIPanel {

	public float value = 0;
	
	public UISlider(int x, int y, int width, int height, boolean staticSize, Color backgroundColor, float alpha) {
		super(x, y, width, height, staticSize, backgroundColor, alpha);
		
		value = .5f;
	}
	
	public UISlider(int x, int y, int width, int height, boolean staticSize, Color backgroundColor, float alpha, float initValue) {
		super(x, y, width, height, staticSize, backgroundColor, alpha);
		
		value = initValue;
	}
	
}
