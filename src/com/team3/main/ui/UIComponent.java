package com.team3.main.ui;

import java.awt.Color;

public abstract class UIComponent {

	public int x, y, width, height;
	public Color background_color;
	public float alpha;
	public boolean static_size, clicked;
	
	public UIComponent(int x, int y, int width, int height, boolean static_size, Color background_color, float alpha){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.static_size = static_size;
		this.background_color = background_color;
		this.alpha = alpha;
	}
	
}
