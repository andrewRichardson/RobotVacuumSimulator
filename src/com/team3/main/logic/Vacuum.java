package com.team3.main.logic;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

public class Vacuum {
	
	private Vector2f position;
	private double rotation, move_speed;
	private final double base_speed;
	public final int diameter = 24;
	
	private String movement_method;
	
	private Random random;
	
	private CollisionModel collision_model;
	private Ellipse2D.Double collider;
	
	public Vacuum(Vector2f init_position, double init_rotation, double move_speed, String movement_method, CollisionModel collision_model) {
		position = init_position;
		rotation = init_rotation;
		this.move_speed = move_speed;
		base_speed = move_speed;
		
		this.movement_method = movement_method;
		
		random = new Random();
		
		this.collision_model = collision_model;
		collider = new Ellipse2D.Double(init_position.x, init_position.y, diameter, diameter);
	}
	
	public int getSpeed() {
		return (int) (move_speed / base_speed);
	}
	
	public void updateSpeed() {
		if(move_speed == base_speed * 100.0)
			move_speed = base_speed;
		else if(move_speed == base_speed)
			move_speed = base_speed * 50.0;
		else if(move_speed == base_speed * 50.0)
			move_speed = base_speed * 100.0;
		else
			move_speed = base_speed;
	}
	
	public Vector2d getPosition() {
		return position.getVector2d();
	}
	
	public double getRotation() {
		return rotation;
	}
	
	public void update() {
		switch (movement_method) {
		case "random":
			random();
			break;
		case "snake":
			snake();
			break;
		case "spiral":
			spiral();
			break;
		case "wall_follow":
			wallFollow();
			break;
		default:
			System.out.println("Error, bad movement method specified.");
			random();
			break;
		}
	}
	
	private void random() {
		Vector2f delta_position = new Vector2f(Math.cos(rotation) * move_speed, Math.sin(rotation) * move_speed);
		position.add(delta_position);
		
		collider.x = position.x;
		collider.y = position.y;
		
		if (collision_detection()) {
			position.add(new Vector2f(-delta_position.x, -delta_position.y));
			
			//  Generate random number between 0.0 and 1.0, scale to PI/2 degrees,
			//  subtract PI/4 degrees so that the number is between -PI/4 and PI/4
			double direction = ( random.nextDouble() * (Math.PI / 2) ) - (Math.PI / 4);

			rotation += direction;
		}
	}
	
	private void snake() {
		
	}
	
	private void spiral() {
		
	}
	
	private void wallFollow() {
		
	}
	
	private boolean collision_detection() {
		boolean has_collided = false;
		
		for (Obstacle obstacle : collision_model.obstacles) {
			for (Rectangle rectangle : obstacle.colliders) {
				if (collider.intersects(rectangle)) {
					has_collided = true;
				}
			}
		}
		
		for (Obstacle obstacle : collision_model.getWalls()) {
			for (Rectangle rectangle : obstacle.colliders) {
				if (collider.intersects(rectangle)) {
					has_collided = true;
				}
			}
		}
		
		return has_collided;
	}
}
