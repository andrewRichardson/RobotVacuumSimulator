package com.team3.main.logic;

import java.util.ArrayList;
import java.util.List;

public class CollisionModel {
	
	public final int width, height;
	public List<Obstacle> obstacles;
	private Obstacle[] walls;
	
	public CollisionModel(int width, int height, ArrayList<Obstacle> obstacles) {
		this.width = width;
		this.height = height;
		this.obstacles = obstacles;
		
		init_bounds();
	}
	
	public CollisionModel(int width, int height) {
		this.width = width;
		this.height = height;
		obstacles = new ArrayList<Obstacle>();
		
		init_bounds();
	}
	
	private void init_bounds() {
		walls = new Obstacle[]{
				new Obstacle(-(Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), 0, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), height, false),
				new Obstacle(width, 0, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), height, false),
				new Obstacle(0, -(Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), width, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), false),
				new Obstacle(0, height, width, (Obstacle.MINIMUM_GAP_SIZE + Obstacle.LEG_SIZE), false)
			};
	}
	
	public Obstacle[] getWalls() {
		return walls;
	}
}
