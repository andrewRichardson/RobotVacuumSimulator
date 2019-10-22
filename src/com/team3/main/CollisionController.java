package com.team3.main;

import com.team3.main.entities.House;
import com.team3.main.entities.Obstacle;
import com.team3.main.entities.Robot;

import java.awt.*;

class CollisionController {

    static boolean collisionDetection(House floor_plan, Robot robot, boolean collide_obstacles) {
        boolean has_collided = false;

        if (collide_obstacles) {
            for (Obstacle obstacle : floor_plan.obstacles.values()) {
                for (Rectangle rectangle : obstacle.collision_bounds) {
                    if (robot.getBounds().intersects(rectangle)) {
                        has_collided = true;
                    }
                }
            }
        }

        for (Obstacle obstacle : floor_plan.getWalls()) {
            for (Rectangle rectangle : obstacle.collision_bounds) {
                if (robot.getBounds().intersects(rectangle)) {
                    has_collided = true;
                }
            }
        }

        return has_collided;
    }
}
