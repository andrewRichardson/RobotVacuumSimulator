package com.team3.main.entities;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

public class Robot {
	
	private Vector2f position;
	private double rotation;
	private final double base_speed;
	private Ellipse2D.Double bounds;

	private Vacuum vacuum;
	private Whisker left_whisker, right_whisker;

	private final Vector2f vacuum_offset = new Vector2f(6.5, 4);
	private final Vector2f left_whisker_offset = new Vector2f(-0.5, 0);
	private final Vector2f right_whisker_offset = new Vector2f(17.5, 0);
	public static final int diameter = 24;

	public Robot(Vector2f init_position, double init_rotation, double move_speed) {
		position = init_position;
		rotation = init_rotation;
		base_speed = move_speed;

		bounds = new Ellipse2D.Double(init_position.x, init_position.y, diameter, diameter);
		vacuum = new Vacuum(new Vector2f(init_position.x + vacuum_offset.x, init_position.y + vacuum_offset.y));
		left_whisker = new Whisker(new Vector2f(init_position.x + left_whisker_offset.x, init_position.y + left_whisker_offset.y));
		right_whisker = new Whisker(new Vector2f(init_position.x + right_whisker_offset.x, init_position.y + right_whisker_offset.y));
	}

	public void setPosition(Vector2f position) {
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

	public void addPosition(Vector2f position) {
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
