package com.team3.main.entities;

public class DataEntry {

    private final String run_id; // Unique run identifier
    private final String house_id; // ID of the house used
    private final double random;
    private final double snake;
    private final double spiral;
    private final double wall_follow; // Percentages for each movement method

    public DataEntry(String run_id, String house_id, double random, double snake, double spiral, double wall_follow) {
        this.run_id = run_id;
        this.house_id = house_id;
        this.random = random;
        this.snake = snake;
        this.spiral = spiral;
        this.wall_follow = wall_follow;
    }


    public String getRunId() {
        return run_id;
    }

    public String getHouseId() {
        return house_id;
    }

    public double getRandom() {
        return random;
    }

    public double getSnake() {
        return snake;
    }

    public double getSpiral() {
        return spiral;
    }

    public double getWallFollow() {
        return wall_follow;
    }
}
