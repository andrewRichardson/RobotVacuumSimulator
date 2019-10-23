package com.team3.main;

import com.team3.main.entities.House;
import com.team3.main.entities.Obstacle;

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

    private void renderObstacles(Graphics2D g, House house) {
        for (Obstacle obstacle : house.obstacles.values()) {
            if (obstacle.is_table) {
                g.drawImage(table_legs_image, obstacle.collision_bounds[0].x, obstacle.collision_bounds[0].y, null);
                g.drawImage(table_image, obstacle.collision_bounds[0].x, obstacle.collision_bounds[0].y, null);
            } else {
                g.drawImage(chest_image, obstacle.collision_bounds[0].x, obstacle.collision_bounds[0].y, null);
            }
        }
    }

    private void renderObstacleBounds(Graphics2D g, House house) {
        g.setColor(Color.RED);
        for (Obstacle obstacle : house.obstacles.values()) {
            if (obstacle.is_table) {
                g.fillRect(obstacle.collision_bounds[0].x, obstacle.collision_bounds[0].y, obstacle.collision_bounds[0].width, obstacle.collision_bounds[0].height);
                g.fillRect(obstacle.collision_bounds[1].x, obstacle.collision_bounds[1].y, obstacle.collision_bounds[1].width, obstacle.collision_bounds[1].height);
                g.fillRect(obstacle.collision_bounds[2].x, obstacle.collision_bounds[2].y, obstacle.collision_bounds[2].width, obstacle.collision_bounds[2].height);
                g.fillRect(obstacle.collision_bounds[3].x, obstacle.collision_bounds[3].y, obstacle.collision_bounds[3].width, obstacle.collision_bounds[3].height);
            } else {
                g.fillRect(obstacle.collision_bounds[0].x, obstacle.collision_bounds[0].y, obstacle.collision_bounds[0].width, obstacle.collision_bounds[0].height);
            }
        }
    }

    public void clearObstacleDirt(House house, BufferedImage dirt_image) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g = dirt_image.createGraphics();
        g.setRenderingHints(rh);
        g.setComposite(AlphaComposite.Src);
        g.setColor(new Color(0, 0, 0, 0));

        for (Obstacle obstacle : house.obstacles.values()) {
            if (obstacle.is_table) {
                g.fillRect(obstacle.collision_bounds[0].x, obstacle.collision_bounds[0].y, obstacle.collision_bounds[0].width, obstacle.collision_bounds[0].height);
                g.fillRect(obstacle.collision_bounds[1].x, obstacle.collision_bounds[1].y, obstacle.collision_bounds[1].width, obstacle.collision_bounds[1].height);
                g.fillRect(obstacle.collision_bounds[2].x, obstacle.collision_bounds[2].y, obstacle.collision_bounds[2].width, obstacle.collision_bounds[2].height);
                g.fillRect(obstacle.collision_bounds[3].x, obstacle.collision_bounds[3].y, obstacle.collision_bounds[3].width, obstacle.collision_bounds[3].height);
            } else {
                g.fillRect(obstacle.collision_bounds[0].x, obstacle.collision_bounds[0].y, obstacle.collision_bounds[0].width, obstacle.collision_bounds[0].height);
            }
        }

        g.dispose();
    }
}