package com.team3.main.ui;

import java.awt.geom.RoundRectangle2D;

public class Button {

	private RoundRectangle2D.Double bounds;
	public String text;
	public boolean pressed = false;
	private boolean callback_complete = true;
	private int timer = 0;
	private int PRESSED_DURATION = 100;
	
	public Button(int x, int y, int width, int height, String name) {
		bounds = new RoundRectangle2D.Double(x, y, width, height, 5, 5); // Set bounds for button
		this.text = name;
	}

	/**
	 * @return Rectangle bounds of button
	 */
	public RoundRectangle2D.Double getBounds() {
		return bounds;
	}
	
	public void press(int pressed_duration) { // If button is pressed, initiate callback routine and update pressed_duration
		callback_complete = false;
		PRESSED_DURATION = pressed_duration;
	}
	
	public void pressedTimer() {
		if(timer >= PRESSED_DURATION) { // While the button is still "pressed", update.  When done, finish callback
			pressed = false;
			callback_complete = true;
			timer = 0;
		} else {
			timer ++;
		}
	}
	
	public boolean isPressed() {
		if (!callback_complete) { // If the callback is not complete, return false. Otherwise, return true
			callback_complete = true;
			return true;
		}
		
		return false;
	}
}
