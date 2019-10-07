package com.team3.main.ui;

import java.awt.geom.RoundRectangle2D;

public class Button {

	private RoundRectangle2D.Double bounds;
	public String text;
	public boolean pressed = false;
	private boolean callback_complete = true;
	private int timer = 0;
	private final int PRESSED_DURATION = 100;
	
	public Button(int x, int y, int width, int height, String name) {
		bounds = new RoundRectangle2D.Double(x, y, width, height, 5, 5);
		this.text = name;
	}
	
	
	/**
	 * @return Rectangle bounds of button
	 */
	public RoundRectangle2D.Double getBounds() {
		return bounds;
	}
	
	public void press() {
		callback_complete = false;
	}
	
	public void pressedTimer() {
		if(timer >= PRESSED_DURATION) {
			pressed = false;
			callback_complete = true;
			timer = 0;
		} else {
			timer ++;
		}
	}
	
	public boolean isPressed() {
		if (!callback_complete) {
			callback_complete = true;
			return true;
		}
		
		return false;
	}
}
