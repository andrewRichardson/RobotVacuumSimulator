package com.team3.main.entities;

import java.awt.geom.Rectangle2D;

import com.team3.main.math.Vector2f;

public class Vacuum {

    public Rectangle2D.Double bounds;

    public Vacuum(Vector2f init_position) {
        bounds = new Rectangle2D.Double(init_position.x, init_position.y, 11, 4);
    }
}
