package com.team3.main.entities;

import com.team3.main.math.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class House {
	
	public final int width, height;
	public static final int grid_size = 60;
	public HashMap<Integer, Obstacle> obstacles;

	private Obstacle[] walls;
	
	public House(int width, int height, HashMap<Integer, Obstacle> obstacles) {
		this.width = width;
		this.height = height;
		this.obstacles = obstacles;
		
		init_bounds();
	}
	
	public House(int width, int height) {
		this.width = width;
		this.height = height;
		obstacles = new HashMap<Integer, Obstacle>();
		
		init_bounds();
	}
	
	private void init_bounds() {
		walls = new Obstacle[]{
				new Barrier(-(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), height),
				new Barrier(width, 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE), height),
				new Barrier(0, -(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2)),
				new Barrier(0, height, width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2))
			};
	}
	
	public Obstacle[] getWalls() {
		return walls;
	}
}
