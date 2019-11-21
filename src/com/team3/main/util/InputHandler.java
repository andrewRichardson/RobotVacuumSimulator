package com.team3.main.util;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputHandler implements KeyListener, MouseListener, MouseWheelListener {
	
	private boolean[] keys = new boolean[65536];
	public boolean[] mouse = new boolean[508];
	public boolean up, down, left, right, space, escape, mouseLeft, mouseRight, mouseScrollUp, mouseScrollDown, mouseClicked = false;
	public boolean focus = false;

	public void update() { // Set each boolean to the keys array equivalent
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
		space = keys[KeyEvent.VK_SPACE];
		escape = keys[KeyEvent.VK_ESCAPE];
		mouseLeft = mouse[MouseEvent.BUTTON1];
		mouseRight = mouse[MouseEvent.BUTTON3];
		mouseClicked = mouse[MouseEvent.MOUSE_CLICKED];
	}

	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void releaseAll() {
		up = down = left = right = space = escape = false;
	}

	public void focusGained(FocusEvent e) {
		focus = true;
	}

	public void focusLost(FocusEvent e) { // No input is accepted when window is out of focus
		focus = false;
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}

	public void mousePressed(MouseEvent e) {
		mouse[e.getButton()] = true;
	}
	
	public void mouseReleased(MouseEvent e) {
		mouse[e.getButton()] = false;
	}

	public void mouseClicked(MouseEvent e) {
		mouse[MouseEvent.MOUSE_CLICKED] = !mouseClicked;
	} // Flip mouseClicked if clicked

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() > 0)
			mouseScrollDown = true;
		else if(e.getWheelRotation() < 0)
			mouseScrollUp = true;
	}
	
}
