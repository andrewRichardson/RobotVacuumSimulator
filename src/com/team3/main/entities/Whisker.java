package com.team3.main.entities;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

import java.awt.geom.Ellipse2D;

public class Whisker {

    public Ellipse2D.Double bounds;

    public Whisker(Vector2f init_position) {
        bounds = new Ellipse2D.Double(init_position.x, init_position.y, 7, 7);
    }
}
