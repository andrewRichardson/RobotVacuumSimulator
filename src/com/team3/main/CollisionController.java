package com.team3.main;

import com.team3.main.entities.House;
import com.team3.main.entities.Obstacle;
import com.team3.main.entities.Robot;

import java.awt.*;

class CollisionController {

    static boolean collisionDetection(House floor_plan, Robot robot, boolean collide_obstacles) {
        // If obstacle collision is enabled, check it
        if (collide_obstacles) {
            for (Obstacle obstacle : floor_plan.obstacles.values()) { // Loop through obstacles
                for (Rectangle rectangle : obstacle.collision_bounds) {
                    if (robot.getBounds().intersects(rectangle)) { // If the robot intersects with the obstacle, it has collided
                        return true;
                    }
                }
            }

            for (Obstacle obstacle : floor_plan.getWalls()) { // Loop through walls
                for (Rectangle rectangle : obstacle.collision_bounds) {
                    if (robot.getBounds().intersects(rectangle)) { // If the robot intersects with the obstacle, it has collided
                        return true;
                    }
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                Obstacle obstacle = floor_plan.getWalls()[i];
                for (Rectangle rectangle : obstacle.collision_bounds) {
                    if (robot.getBounds().intersects(rectangle)) { // If the robot intersects with the obstacle, it has collided
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
