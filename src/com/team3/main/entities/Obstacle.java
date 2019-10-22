package com.team3.main.entities;

import java.awt.Rectangle;

public abstract class Obstacle {
	
	public static final int LEG_SIZE = 6, MINIMUM_GAP_SIZE = 30;
	public final int width, height;
	public Rectangle[] colliders;
	public final boolean is_table;
	
	public Obstacle(int x, int y, int width, int height, boolean is_table) {
		if (width - LEG_SIZE < MINIMUM_GAP_SIZE)
			width = MINIMUM_GAP_SIZE + LEG_SIZE;
		if (height - LEG_SIZE < MINIMUM_GAP_SIZE)
			height = MINIMUM_GAP_SIZE + LEG_SIZE;
		
		this.width = width;
		this.height = height;
		
		this.is_table = is_table;
		
		if (is_table) {
			colliders = new Rectangle[4];
			colliders[0] = new Rectangle(x, y, LEG_SIZE, LEG_SIZE);
			colliders[1] = new Rectangle(x+width, y, LEG_SIZE, LEG_SIZE);
			colliders[2] = new Rectangle(x+width, y+height, LEG_SIZE, LEG_SIZE);
			colliders[3] = new Rectangle(x, y+height, LEG_SIZE, LEG_SIZE);
		} else {
			colliders = new Rectangle[1];
			colliders[0] = new Rectangle(x, y, width, height);
		}
	}
	
}
