package com.team3.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.team3.main.entities.House;
import com.team3.main.entities.Obstacle;
import com.team3.main.entities.Robot;

public class Display {

    private BufferedImage planks_image, chest_image, table_image, table_legs_image, vacuum_image, floor_a, floor_b, door_a, door_b;

    // Try to get all image files
    public Display() {
        // Images
        try {
            planks_image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/wood_planks.png")));
            chest_image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/chest.png")));
            table_image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/table.png")));
            table_legs_image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/table_legs.png")));
            vacuum_image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/vacuum.png")));
            floor_a = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/floor_a.png")));
            floor_b = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/floor_b.png")));
            door_a = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/door_a.png")));
            door_b = ImageIO.read(Objects.requireNonNull(getClass().getResource("/res/door_b.png")));
        } catch(IOException e) {
            System.out.println("Error:" + Arrays.toString(e.getStackTrace()));
        }
    }

    // Render main display
    public void render(Graphics2D g, SimulationController simulationController, boolean show_obstacles, BufferedImage dirt_overlay) {
        // Render background
        g.drawImage(planks_image, 0, 0, null);

        // Render dirt
        g.drawImage(dirt_overlay, 0, 0, null);

        // Render obstacles if enabled
        if (show_obstacles) {
            renderObstacles(g, simulationController.getFloorPlan());

            // Render walls and doors according to floor plan
            if (simulationController.getFloorPlan().floorPlan.equals("A")) {
                g.drawImage(floor_a, 0, 0, null);
                g.drawImage(door_a, 0, 0, null);
            } else {
                g.drawImage(floor_b, 0, 0, null);
                g.drawImage(door_b, 0, 0, null);
            }
        }

        // Render the Robot's vacuum and whiskers with correct rotation
        g.rotate(simulationController.getRobot().getRotation() + (Math.PI / 2.0), simulationController.getRobot().getPosition2d().x + Robot.diameter / 2.0, simulationController.getRobot().getPosition2d().y + Robot.diameter / 2.0);

        g.setColor(Color.BLACK);
        g.fill(simulationController.getRobot().getVacuumBounds());
        g.fill(simulationController.getRobot().getLeftWhisker());
        g.fill(simulationController.getRobot().getRightWhisker());

        g.rotate(-simulationController.getRobot().getRotation() - (Math.PI / 2.0), simulationController.getRobot().getPosition2d().x + Robot.diameter / 2.0, simulationController.getRobot().getPosition2d().y + Robot.diameter / 2.0);

        // Render the Robot
        g.drawImage(vacuum_image, simulationController.getRobot().getPosition2d().x, simulationController.getRobot().getPosition2d().y, null);
    }

    // Render obstacles
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

    // Clear the dirt under obstacles
    public void clearObstacleDirt(House house, BufferedImage dirt_image) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g = dirt_image.createGraphics();
        g.setRenderingHints(rh);
        g.setComposite(AlphaComposite.Src);
        g.setColor(new Color(0, 0, 0, 0));

        renderObstacle(house, g);

        g.dispose();
    }

    private void renderObstacle(House house, Graphics2D g) {
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
}
