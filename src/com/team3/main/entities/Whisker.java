package com.team3.main.entities;

import java.awt.geom.Ellipse2D;

import com.team3.main.math.Vector2f;

public class Whisker {

    public Ellipse2D.Double bounds;

    public Whisker(Vector2f init_position) { // Set the bounds to the initial position and the standard sized whisker circle
        bounds = new Ellipse2D.Double(init_position.x, init_position.y, 7, 7);
    }
}
