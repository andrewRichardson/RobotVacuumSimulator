package com.team3.main.entities;

import java.awt.Rectangle;

public abstract class Obstacle {
	
	public static final int LEG_SIZE = 6, GAP_SIZE = 38;
	public Rectangle[] collision_bounds;
	public final boolean is_table;
	
	public Obstacle(int x, int y, boolean is_table) {
		this.is_table = is_table;
		
		if (is_table) {
			collision_bounds = new Rectangle[4];
			collision_bounds[0] = new Rectangle(x, y, LEG_SIZE, LEG_SIZE);
			collision_bounds[1] = new Rectangle(x+GAP_SIZE+LEG_SIZE, y, LEG_SIZE, LEG_SIZE);
			collision_bounds[2] = new Rectangle(x+GAP_SIZE+LEG_SIZE, y+GAP_SIZE+LEG_SIZE, LEG_SIZE, LEG_SIZE);
			collision_bounds[3] = new Rectangle(x, y+GAP_SIZE+LEG_SIZE, LEG_SIZE, LEG_SIZE);
		} else {
			collision_bounds = new Rectangle[1];
			collision_bounds[0] = new Rectangle(x, y, GAP_SIZE + LEG_SIZE * 2, GAP_SIZE + LEG_SIZE * 2);
		}
	}

	public Obstacle(int x, int y, int width, int height) {
		this.is_table = false;

		collision_bounds = new Rectangle[1];
		collision_bounds[0] = new Rectangle(x, y, width, height);
	}
	
}
