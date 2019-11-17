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
    private double spiral_move;

    private Color vacuum_color = new Color(0.1f, 0.1f, 0.1f);
    private Color whisker_color = new Color(0.4f, 0.4f, 0.4f);

    public SimulationController(House init_house, Robot robot) {
        random = new Random();
        current_house = init_house;
        this.robot = robot;
        spiral_move = 0;
    }

    public boolean handleDraw(InputHandler inputHandler, int mouse_x, int mouse_y, String draw_brush) {
        boolean drawing = false;
        int grid_x = (int)Math.floor((double)mouse_x / (double) House.grid_size);
        int grid_y = (int)Math.floor((double)mouse_y / (double) House.grid_size);

        int index = 16 * grid_y + grid_x;
        Vector2d position = new Vector2d(grid_x * House.grid_size + 9, grid_y * House.grid_size + 9);

        if (inputHandler.mouseClicked != last_click_status) {
            drawing = true;
            if (first_click) {
                first_click = false;
                last_click_status = inputHandler.mouseClicked;
            } else {
                last_click_status = inputHandler.mouseClicked;

                current_house.obstacles.remove(index);
                switch (draw_brush) {
                    case TABLE:
                        current_house.obstacles.put(index, new Table(position.x, position.y));
                        break;
                    case CHEST:
                        current_house.obstacles.put(index, new Chest(position.x, position.y));
                        break;
                }
            }
        }
        return drawing;
    }

    public void update(BufferedImage dirt_image, boolean collide_obstacles, BufferedImage dirt_data) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g_trail = dirt_image.createGraphics();
        g_trail.setRenderingHints(rh);
        g_trail.setComposite(AlphaComposite.Src);
        g_trail.setColor(new Color(0, 0, 0, 0));

        for (int i = 0; i < move_steps; i++) {
            if (reset_complete) {
                if (total_steps++ > Robot.BATTERY_LIFE) {
                    i = move_steps;
                }

                boolean did_collide = false;
                switch (movement_method) {
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

                if (!did_collide) {
                    draw_trail(g_trail);
                    collect_dirt(dirt_data);
                }
            } else {
                reset_complete = true;
                i = move_steps;
            }
        }

        g_trail.dispose();
    }

    private boolean random(boolean collide_obstacles) {
        Vector2f delta_position = new Vector2f(Math.cos(robot.getRotation()) * robot.getSpeed(), Math.sin(robot.getRotation()) * robot.getSpeed());
        robot.addPosition(delta_position);

        if (CollisionController.collisionDetection(current_house, robot, collide_obstacles)) {
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
        // when Vacuum bumps into something, rotate 90 degrees.
        Vector2f delta_position = new Vector2f(Math.cos(robot.getRotation()) * robot.getSpeed(), Math.sin(robot.getRotation()) * robot.getSpeed());; // unfinished
        robot.addPosition(delta_position);

        if (CollisionController.collisionDetection(current_house,  robot,  collide_obstacles)) {
            robot.addPosition(new Vector2f(-delta_position.x, -delta_position.y));
            double direction = Math.PI/2;
            robot.addRotation(direction);

            return true;
        }
        return false;
    }

    private boolean spiral(boolean collide_obstacles) {
        Vector2f delta_position = new Vector2f(Math.cos(robot.getRotation()) * (robot.getSpeed() + spiral_move), Math.sin(robot.getRotation()) * (robot.getSpeed() + spiral_move));
        robot.addPosition(delta_position);

        robot.addRotation(Math.PI/180);
        spiral_move += Math.PI / 3600.0;

        if (CollisionController.collisionDetection(current_house, robot, collide_obstacles)) {
            robot.addPosition(new Vector2f(-delta_position.x, -delta_position.y));

            //  Generate random number between 0.0 and 1.0, scale to PI/2 degrees,
            //  subtract PI/4 degrees so that the number is between -PI/4 and PI/4
            double direction = ( random.nextDouble() * (Math.PI / 2) ) - (Math.PI / 4);

            robot.addRotation(direction);

            spiral_move = 0;

            return true;
        }

        return false;
    }


    private boolean wallFollow(boolean collide_obstacles) {
        return false;
    }

    private void draw_trail(Graphics2D g_trail) {
        g_trail.rotate(robot.getRotation() + (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);
        g_trail.fill(robot.getVacuumBounds());
        g_trail.fill(robot.getLeftWhisker());
        g_trail.fill(robot.getRightWhisker());
        g_trail.rotate(-robot.getRotation() - (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);
    }

    private void collect_dirt(BufferedImage dirt_data) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g = dirt_data.createGraphics();
        g.setRenderingHints(rh);
        g.setComposite(MultiplyComposite.Multiply);


        g.rotate(robot.getRotation() + (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);

        g.setColor(vacuum_color);
        g.fill(robot.getVacuumBounds());

        g.setColor(whisker_color);
        g.fill(robot.getLeftWhisker());
        g.fill(robot.getRightWhisker());

        g.rotate(-robot.getRotation() - (Math.PI / 2.0), robot.getPosition2d().x + Robot.diameter / 2.0, robot.getPosition2d().y + Robot.diameter / 2.0);

        g.dispose();
    }

    public void updateMovementMethod() {
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

    public void reset(Robot new_robot) {
        total_steps = 0;
        spiral_move = 0;
        robot = new_robot;
        reset_complete = false;
    }

    public void reset(Robot new_robot, House new_house) {
        current_house = new_house;
        reset(new_robot);
    }

    public void updateSpeed() {
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