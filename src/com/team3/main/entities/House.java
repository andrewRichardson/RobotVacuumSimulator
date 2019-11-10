package com.team3.main.entities;

import com.team3.main.math.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class House {

	public static enum FloorPlan {
		A, B
	}

	public String id;
	public final FloorPlan floorPlan;
	public final int width, height;
	public static final int grid_size = 60;
	public HashMap<Integer, Obstacle> obstacles;

	private Obstacle[] walls;
	
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
		if (floorPlan == FloorPlan.A) {
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
		} else {
			walls = new Obstacle[]{
					new Barrier(-(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), height),
					new Barrier(width, 0, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE), height),
					new Barrier(0, -(Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2), width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2)),
					new Barrier(0, height, width, (Obstacle.GAP_SIZE + Obstacle.LEG_SIZE * 2)),
					new Barrier(0, 180, 130, 10),
					new Barrier(120, 190, 10, 60),
					new Barrier(180, 180, 240, 10),
					new Barrier(300, 120, 10, 60),
					new Barrier(360, 120, 60, 10),
					new Barrier(420, 0, 10, 250),
					new Barrier(0, 300, 250, 10),
					new Barrier(300, 300, 130, 10),
					new Barrier(300, 310, 10, 120),
					new Barrier(300, 480, 10, 61),
					new Barrier(420, 310, 10, 231),
					new Barrier(610, 240, 351, 10)
			};
		}
	}
	
	public Obstacle[] getWalls() {
		return walls;
	}
}
