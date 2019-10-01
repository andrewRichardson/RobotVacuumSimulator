package com.team3.main.ui;

import java.awt.Color;

public class UILabel extends UIComponent {

	public String text;
	public Color textColor;
	
	public UILabel(int x, int y, int width, int height, boolean staticSize, Color backgroundColor, Color textColor, float alpha, String text) {
		super(x, y, width, height, staticSize, backgroundColor, alpha);
		this.text = text;
		this.textColor = textColor;
	}

}
