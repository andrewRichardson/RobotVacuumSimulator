package com.team3.main;

import com.team3.main.entities.FloorPlan;
import com.team3.main.entities.Obstacle;
import com.team3.main.ui.GUIHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Display {

    private BufferedImage planks_image, chest_image, table_image, table_legs_image, vacuum_image;

    public Display() {
        // Images
        try {
            planks_image = ImageIO.read(new File("res/wood_planks.png"));
            chest_image = ImageIO.read(new File("res/chest.png"));
            table_image = ImageIO.read(new File("res/table.png"));
            table_legs_image = ImageIO.read(new File("res/table_legs.png"));
            vacuum_image = ImageIO.read(new File("res/vacuum.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics2D g, SimulationController simulationController, boolean show_obstacles, BufferedImage dirt_overlay) {
        g.drawImage(planks_image, 0, 0, null);

        g.drawImage(dirt_overlay, 0, 0, null);

        if (show_obstacles)
            renderObstacles(g, simulationController.getFloorPlan());

        g.drawImage(vacuum_image, simulationController.getRobot().getPosition2d().x, simulationController.getRobot().getPosition2d().y, null);
    }

    private void renderObstacles(Graphics2D g, FloorPlan floorPlan) {
        for (Obstacle obstacle : floorPlan.obstacles) {
            if (obstacle.is_table) {
                g.drawImage(table_legs_image, obstacle.colliders[0].x, obstacle.colliders[0].y, null);
                g.drawImage(table_image, obstacle.colliders[0].x, obstacle.colliders[0].y, null);
            } else {
                g.drawImage(chest_image, obstacle.colliders[0].x, obstacle.colliders[0].y, null);
            }
        }
    }

    private void renderObstacleBounds(Graphics2D g, FloorPlan floorPlan) {
        g.setColor(Color.RED);
        for (Obstacle obstacle : floorPlan.obstacles) {
            if (obstacle.is_table) {
                g.fillRect(obstacle.colliders[0].x, obstacle.colliders[0].y, obstacle.colliders[0].width, obstacle.colliders[0].height);
                g.fillRect(obstacle.colliders[1].x, obstacle.colliders[1].y, obstacle.colliders[1].width, obstacle.colliders[1].height);
                g.fillRect(obstacle.colliders[2].x, obstacle.colliders[2].y, obstacle.colliders[2].width, obstacle.colliders[2].height);
                g.fillRect(obstacle.colliders[3].x, obstacle.colliders[3].y, obstacle.colliders[3].width, obstacle.colliders[3].height);
            } else {
                g.fillRect(obstacle.colliders[0].x, obstacle.colliders[0].y, obstacle.colliders[0].width, obstacle.colliders[0].height);
            }
        }
    }
}
