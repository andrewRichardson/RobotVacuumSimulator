package com.team3.main.entities;

import java.util.ArrayList;
import java.util.List;

public class FloorPlan {
	
	public final int width, height;
	public List<Obstacle> obstacles;
	private Obstacle[] walls;
	
	public FloorPlan(int width, int height, ArrayList<Obstacle> obstacles) {
		this.width = width;
		this.height = height;
		this.obstacles = obstacles;
		
		init_bounds();
	}
	
	public FloorPlan(int width, int height) {
		this.width = width;
		this.height = height;
		obstacles = new ArrayList<Obstacle>();
		
		init_bounds();
	}
	
	private void init_bounds() {
		walls = new Obstacle[]{
				new Chest(-(Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), 0, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), height),
				new Chest(width, 0, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), height),
				new Chest(0, -(Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), width, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE)),
				new Chest(0, height, width, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE))
			};
	}
	
	public Obstacle[] getWalls() {
		return walls;
	}
}
