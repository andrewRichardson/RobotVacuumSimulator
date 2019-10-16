package com.team3.main.logic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

public class Vacuum {
	
	private Vector2f position;
	private double rotation, move_steps;
	private final double base_speed;

	private String movement_method;
	private Random random;
	private CollisionModel collision_model;
	private Ellipse2D.Double collider;

	private BufferedImage dirt_image;

	public boolean collide_obstacles = true;
    public final int diameter = 24;
	public final static String RANDOM = "Random", SPIRAL = "Spiral", SNAKE = "Snake", WALL_FOLLOW = "Wall Follow";

	public Vacuum(Vector2f init_position, double init_rotation, double move_speed, String movement_method, CollisionModel collision_model) {
		position = init_position;
		rotation = init_rotation;
		base_speed = move_speed;
		move_steps = 1;

		this.movement_method = movement_method;

		random = new Random();

		this.collision_model = collision_model;
		collider = new Ellipse2D.Double(init_position.x, init_position.y, diameter, diameter);
	}

	public int getSpeed() {
		return (int) (move_steps);
	}
	
	public void updateSpeed() {
		if(move_steps == 100.0)
			move_steps = 1.0;
		else if(move_steps == 1.0)
			move_steps = 50.0;
		else if(move_steps == 50.0)
			move_steps = 100.0;
		else
			move_steps = 1.0;
	}

    public void updateMovementMethod() {
        switch (movement_method) {
            case RANDOM:
                movement_method = SNAKE;
                break;
            case SNAKE:
                movement_method = SPIRAL;
                break;
            case SPIRAL:
                movement_method = WALL_FOLLOW;
                break;
            case WALL_FOLLOW:
                movement_method = RANDOM;
                break;
            default:
                movement_method = RANDOM;
                break;
        }
    }
	
	public Vector2d getPosition() {
		return position.getVector2d();
	}
	
	public double getRotation() {
		return rotation;
	}
	
	public void update(BufferedImage dirt_image) {
	    this.dirt_image = dirt_image;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g_trail = dirt_image.createGraphics();
        g_trail.setRenderingHints(rh);
        g_trail.setComposite(AlphaComposite.Src);
        g_trail.setColor(new Color(0, 0, 0, 0));

	    for (int i = 0; i < move_steps; i++) {
            switch (movement_method) {
                case "Random":
                    random();
                    break;
                case "Snake":
                    snake();
                    break;
                case "Spiral":
                    spiral();
                    break;
                case "Wall Follow":
                    wallFollow();
                    break;
                default:
                    System.out.println("Error, bad movement method specified.");
                    random();
                    break;
            }

            g_trail.fillOval(getPosition().x, getPosition().y, diameter, diameter);
        }

        g_trail.dispose();
	}
	
	private void random() {
		Vector2f delta_position = new Vector2f(Math.cos(rotation) * base_speed, Math.sin(rotation) * base_speed);
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

		if (collide_obstacles) {
			for (Obstacle obstacle : collision_model.obstacles) {
				for (Rectangle rectangle : obstacle.colliders) {
					if (collider.intersects(rectangle)) {
						has_collided = true;
					}
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

    public String getMovementMethod() {
	    return movement_method;
    }
}
