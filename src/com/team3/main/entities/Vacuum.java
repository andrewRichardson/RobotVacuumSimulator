package com.team3.main.entities;

import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Vacuum {

    public Rectangle2D.Double bounds;

    public Vacuum(Vector2f init_position) {
        bounds = new Rectangle2D.Double(init_position.x, init_position.y, 11, 4);
    }
}
