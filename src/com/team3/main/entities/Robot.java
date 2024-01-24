package com.team3.main.entities;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

public class Robot {
	
	private Vector2f position;
	private double rotation;
	private final double base_speed;
	private final Ellipse2D.Double bounds;

	private final Vacuum vacuum;
	private final Whisker left_whisker;
    private final Whisker right_whisker;

	private final Vector2f vacuum_offset = new Vector2f(6.5, 4); // Where the vacuum is relative to the robot
	private final Vector2f left_whisker_offset = new Vector2f(-0.5, 2); // Where the left whisker is relative to the robot
	private final Vector2f right_whisker_offset = new Vector2f(17.5, 2); // Where the right whisker is relative to the robot
	public static final int diameter = 24; // Size of the Robot's bounding circle
	public static final double BATTERY_LIFE = 150 * 60 * 6; // The battery life of the Robot in updates

	public Robot(Vector2f init_position, double init_rotation, double move_speed) {
		position = init_position;
		rotation = init_rotation;
		base_speed = move_speed;

		bounds = new Ellipse2D.Double(init_position.x, init_position.y, diameter, diameter);
		vacuum = new Vacuum(new Vector2f(init_position.x + vacuum_offset.x, init_position.y + vacuum_offset.y));
		left_whisker = new Whisker(new Vector2f(init_position.x + left_whisker_offset.x, init_position.y + left_whisker_offset.y));
		right_whisker = new Whisker(new Vector2f(init_position.x + right_whisker_offset.x, init_position.y + right_whisker_offset.y));
	}

	public void setPosition(Vector2f position) { // Set a new position for the Robot and each child object
		this.position = position;
		bounds.x = this.position.x;
		bounds.y = this.position.y;

		vacuum.bounds.x = this.position.x + vacuum_offset.x;
		vacuum.bounds.y = this.position.y + vacuum_offset.y;

		left_whisker.bounds.x = this.position.x + left_whisker_offset.x;
		left_whisker.bounds.y = this.position.y + left_whisker_offset.y;

		right_whisker.bounds.x = this.position.x + right_whisker_offset.x;
		right_whisker.bounds.y = this.position.y + right_whisker_offset.y;
	}

	public void addPosition(Vector2f position) { // Add to the position of the Robot and each child object
		this.position.add(position);

		bounds.x = this.position.x;
		bounds.y = this.position.y;

		vacuum.bounds.x = this.position.x + vacuum_offset.x;
		vacuum.bounds.y = this.position.y + vacuum_offset.y;

		left_whisker.bounds.x = this.position.x + left_whisker_offset.x;
		left_whisker.bounds.y = this.position.y + left_whisker_offset.y;

		right_whisker.bounds.x = this.position.x + right_whisker_offset.x;
		right_whisker.bounds.y = this.position.y + right_whisker_offset.y;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public void addRotation(double rotation) {
		this.rotation += rotation;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2d getPosition2d() {
		return position.getVector2d();
	}

	public Rectangle2D.Double getVacuumBounds() {
		return vacuum.bounds;
	}

	public Ellipse2D.Double getLeftWhisker() {
		return left_whisker.bounds;
	}

	public Ellipse2D.Double getRightWhisker() {
		return right_whisker.bounds;
	}

	public double getRotation() {
		return rotation;
	}

	public double getSpeed() {
		return base_speed;
	}

    public Ellipse2D.Double getBounds() {
		return bounds;
	}
}
