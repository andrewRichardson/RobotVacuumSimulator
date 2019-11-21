package com.team3.main;

import com.team3.main.entities.Chest;
import com.team3.main.entities.House;
import com.team3.main.entities.Robot;
import com.team3.main.entities.Table;
import com.team3.main.math.Vector2d;
import com.team3.main.math.Vector2f;
import com.team3.main.util.InputHandler;
import com.team3.main.util.MultiplyComposite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SimulationController {

    public final static String RANDOM = "Random", SPIRAL = "Spiral", SNAKE = "Snake", WALL_FOLLOW = "Wall Follow", ALL = "All";
    public final static String ERASE = "ERASE", TABLE = "TABLE", CHEST = "CHEST";

    private House current_house;
    private Robot robot;
    private String movement_method = ALL;
    private Random random;
    private int move_steps = 1, total_steps = 0;
    private boolean last_click_status = false, first_click = true, reset_complete = false;
    private double spiral_move, snake_move;
    private boolean snake_turn_switch = false;

    private Color vacuum_color = new Color(0.1f, 0.1f, 0.1f);
    private Color whisker_color = new Color(0.4f, 0.4f, 0.4f);

    public SimulationController(House init_house, Robot robot) {
        random = new Random();
        current_house = init_house;
        this.robot = robot;
        spiral_move = 0;
        snake_move = -1;
    }

    public boolean handleDraw(InputHandler inputHandler, int mouse_x, int mouse_y, String draw_brush) {
        boolean drawing = false;
        // Get the 60x60 grid location of the mouse click
        int grid_x = (int)Math.floor((double)mouse_x / (double) House.GRID_SIZE);
        int grid_y = (int)Math.floor((double)mouse_y / (double) House.GRID_SIZE);

        int index = (current_house.floorPlan == House.FloorPlan.A ? 16 : 20) * grid_y + grid_x; // 2D array index to 1D array index
        Vector2d position = new Vector2d(grid_x * House.GRID_SIZE + 9, grid_y * House.GRID_SIZE + 9); // Translate position to obstacle position

        if (inputHandler.mouseClicked != last_click_status) { // If the mouse was clicked, draw
            drawing = true;
            if (first_click) {
                first_click = false;
                last_click_status = inputHandler.mouseClicked;
            } else {
                last_click_status = inputHandler.mouseClicked;

                System.out.println(current_house.obstacles.remove(index)); // Remove the obstacle
                switch (draw_brush) { // Add back a table or chest according to brush, if erase, do nothing
                    case TABLE:
                        current_house.obstacles.put(index, new Table(position.x, position.y));
                        break;
                    case CHEST:
                        current_house.obstacles.put(index, new Chest(position.x, position.y));
                        break;
                }
            }
        }
        return drawing; // Whether or not anything was actually changed
    }

    public void update(BufferedImage dirt_image, boolean collide_obstacles, BufferedImage dirt_data) {
        // Initialize the dirt overlay graphics
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g_trail = dirt_image.createGraphics();
        g_trail.setRenderingHints(rh);
        g_trail.setComposite(AlphaComposite.Src);
        g_trail.setColor(new Color(0, 0, 0, 0));

        // Update move_steps times each call
        for (int i = 0; i < move_steps; i++) {
            if (reset_complete) { // If a reset was not called
                if (total_steps++ > Robot.BATTERY_LIFE) { // If the Robot's battery has died, exit the loop
                    i = move_steps;
                }

                boolean did_collide = false;
                switch (movement_method) { // Use appropriate movement method
                    case "Random":
                        did_collide = random(collide_obstacles);
                        break;
                    case "Snake":
                        did_collide = snake(collide_obstacles);
                        break;
                    case "Spiral":
                        did_collide = spiral(collide_obstacles);
                        break;
                    case "Wall Follow":
                        did_collide = wallFollow(collide_obstacles);
                        break;
                    default:
                        System.out.println("Error, bad movement method specified.");
                        did_collide = random(collide_obstacles);
                        break;
                }

                if (!did_collide) { // If the obstacle did not collide, collect dirt and update dirt overlay
                    draw_trail(g_trail);
                    collect_dirt(dirt_data);
                }
            } else { // If a reset was called, end the loop
                reset_complete = true;
                i = move_steps;
            }
        }

        g_trail.dispose();
    }

    private boolean random(boolean collide_obstacles) {
        // Go straight robot.getSpeed() units
        Vector2f delta_position = new Vector2f(Math.cos(robot.getRotation()) * robot.getSpeed(), Math.sin(robot.getRotation()) * robot.getSpeed());
        robot.addPosition(delta_position);

        if (CollisionController.collisionDetection(current_house, robot, collide_obstacles)) { // If the Robot collided
            // Return to the previous position
            robot.addPosition(new Vector2f(-delta_position.x, -delta_position.y));

            //  Generate random number between 0.0 and 1.0, scale to PI/2 degrees,
            //  subtract PI/4 degrees so that the number is between -PI/4 and PI/4
            double direction = ( random.nextDouble() * (Math.PI / 2) ) - (Math.PI / 4);

            robot.addRotation(direction);

            return true;
        }

        return false; 
    }

    private boolean snake(boolean collide_obstacles) {
    	// Go straight robot.getSpeed() units
        Vector2f delta_position = new Vector2f(Math.cos(robot.getRotation()) * robot.getSpeed(), Math.sin(robot.getRotation()) * robot.getSpeed());
        robot.addPosition(delta_position);

        if (snake_move < 24 && snake_move >= 0) {
        	snake_move++;
        } else {
            if (snake_move == 24) {
                snake_move = -1;
                robot.addRotation(snake_turn_switch ? Math.PI/2 : -Math.PI/2);
            }
        }
        // currently rotates whenever it bumps into a leg, even if it passes it. Why?
        if (CollisionController.collisionDetection(current_house, robot, collide_obstacles)) { // If the Robot collided
            robot.addPosition(new Vector2f(-delta_position.x, -delta_position.y));
            //use turn switch

            snake_turn_switch = !snake_turn_switch;
            robot.addRotation(snake_turn_switch ? Math.PI/2 : -Math.PI/2);
            snake_move = 0;

            return true;
        }
        
        return false;
    }

    private boolean spiral(boolean collide_obstacles) {
        // Go straight robot.getSpeed() units
        Vector2f delta_position = new Vector2f(Math.cos(robot.getRotation()) * (robot.getSpeed() + spiral_move), Math.sin(robot.getRotation()) * (robot.getSpeed() + spiral_move));
        robot.addPosition(delta_position);

        // Increase rotation
        robot.addRotation(Math.PI/180);
        spiral_move += Math.PI / 3600.0;

        if (CollisionController.collisionDetection(current_house, robot, collide_obstacles)) { // If the Robot collided
            robot.addPosition(new Vector2f(-delta_position.x, -delta_position.y));

            //  Generate random number between 0.0 and 1.0, scale to PI/2 degrees,
            //  subtract PI/4 degrees so that the number is between -PI/4 and PI/4
            double direction = ( random.nextDouble() * (Math.PI / 2) ) - (Math.PI / 4);

            robot.addRotation(direction);

            // Reset rotation increment
            spiral_move = 0;

            return true;
        }

        return false;
    }


    private boolean wallFollow(boolean collide_obstacles) {
        return false;
    }

    private void draw_trail(Graphics2D g_trail) { // Clear dirt overlay in location of robot vacuum and whiskers
        g_trail.rotate(robot.getRotation() + (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);
        g_trail.fill(robot.getVacuumBounds());
        g_trail.fill(robot.getLeftWhisker());
        g_trail.fill(robot.getRightWhisker());
        g_trail.rotate(-robot.getRotation() - (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);
    }

    private void collect_dirt(BufferedImage dirt_data) {
        // Create dirt data image graphics
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g = dirt_data.createGraphics();
        g.setRenderingHints(rh);
        g.setComposite(MultiplyComposite.Multiply);

        // Clear dirt data image in location of robot vacuum and whiskers
        g.rotate(robot.getRotation() + (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);

        g.setColor(vacuum_color); // 10% of previous color
        g.fill(robot.getVacuumBounds());

        g.setColor(whisker_color); // 40% of previous color
        g.fill(robot.getLeftWhisker());
        g.fill(robot.getRightWhisker());

        g.rotate(-robot.getRotation() - (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);

        g.dispose();
    }

    public void updateMovementMethod() { // Set movement method to next method in order
        switch (movement_method) {
            case RANDOM:
                movement_method = SNAKE;
                total_steps = 0;
                break;
            case SNAKE:
                movement_method = SPIRAL;
                total_steps = 0;
                break;
            case SPIRAL:
                movement_method = WALL_FOLLOW;
                total_steps = 0;
                break;
            case WALL_FOLLOW:
                movement_method = ALL;
                total_steps = 0;
                break;
            case ALL:
                movement_method = RANDOM;
                total_steps = 0;
                break;
        }
    }

    public void reset(Robot new_robot) { // Reset the simulation
        total_steps = 0;
        spiral_move = 0;
        robot = new_robot;
        reset_complete = false;
        snake_move = -1;
    }

    public void reset(Robot new_robot, House new_house) { // Reset the simulation with a new house
        current_house = new_house;
        reset(new_robot);
    }

    public void updateSpeed() { // Set simulation speed to next speed in order
        if(move_steps == 100)
            move_steps = 1;
        else if(move_steps == 1)
            move_steps = 50;
        else if(move_steps == 50)
            move_steps = 100;
        else
            move_steps = 1;
    }

    public int getSpeed() {
        return move_steps;
    }

    public String getMovementMethod() {
        return movement_method;
    }

    public Robot getRobot() {
        return robot;
    }

    public House getFloorPlan() {
        return current_house;
    }

    public int getTotalSteps() {
        return total_steps;
    }
}