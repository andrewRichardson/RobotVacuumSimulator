package com.team3.main.entities;

import java.util.HashMap;

public class House {

	public static enum FloorPlan {
		A, B
	}

	public String id; // Unique identifier
	public final FloorPlan floorPlan; // Floor plan A or B
	public final int width, height; // Size of room
	public static final int GRID_SIZE = 60; // Size of each square in the obstacle grid
	public HashMap<Integer, Obstacle> obstacles; // Array of obstacles for the House

	private Obstacle[] walls; // Array of walls (boundary walls and inner walls)
	
	public House(int width, int height, HashMap<Integer, Obstacle> obstacles, FloorPlan floorPlan) {
		this.width = width;
		this.height = height;
		this.obstacles = obstacles;
		this.floorPlan = floorPlan;
		
		init_bounds();
	}

	public House(int width, int height, FloorPlan floorPlan) {
		this.width = width;
		this.height = height;
		obstacles = new HashMap<Integer, Obstacle>();
		this.floorPlan = floorPlan;

		init_bounds();
	}

	public House(int width, int height) {
		this.width = width;
		this.height = height;
		obstacles = new HashMap<Integer, Obstacle>();
		floorPlan = FloorPlan.A;
		
		init_bounds();
	}

	private void init_bounds() {
		if (floorPlan == FloorPlan.A) { // Add floor plan A walls
			walls = new Obstacle[]{
					new Barrier(-(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), height),
					new Barrier(width, 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE), height),
					new Barrier(0, -(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2)),
					new Barrier(0, height, width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2)),
					new Barrier(0, 240, 299, 10),
					new Barrier(600, 0, 10, 250),
					new Barrier(600, 300, 10, 241),
					new Barrier(480, 120, 120, 10),
					new Barrier(480, 130, 10, 60),
					new Barrier(540, 180, 130, 10),
					new Barrier(540, 300, 130, 10),
					new Barrier(480, 300, 10, 60),
					new Barrier(480, 360, 120, 10),
					new Barrier(720, 300, 240, 10),
					new Barrier(720, 180, 240, 10),
					new Barrier(720, 190, 10, 60),
					new Barrier(840, 0, 10, 69),
					new Barrier(900, 60, 61, 10),
					new Barrier(840, 480, 70, 10),
					new Barrier(840, 490, 10, 51)
			};
		} else { // Add floor plan B walls
			walls = new Obstacle[]{
					new Barrier(-(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), height),
					new Barrier(width, 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE), height),
					new Barrier(0, -(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2)),
					new Barrier(0, height, width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2)),
					new Barrier(0, 240, 250, 10),
					new Barrier(240, 250, 10, 60),
					new Barrier(420, 180, 10, 60),
					new Barrier(480, 180, 60, 10),
					new Barrier(350, 240, 190, 10),
					new Barrier(540, 0, 10, 309),
					new Barrier(0, 420, 320, 10),
					new Barrier(420, 420, 130, 10),
					new Barrier(420, 430, 10, 120),
					new Barrier(480, 480, 60, 10),
					new Barrier(540, 430, 10, 246),
					new Barrier(420, 650, 10, 26),
					new Barrier(730, 300, 472, 10)
			};
		}
	}
	
	public Obstacle[] getWalls() {
		return walls;
	}
}
