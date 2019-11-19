package com.team3.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.team3.main.entities.*;
import com.team3.main.math.Vector2f;
import com.team3.main.ui.Button;
import com.team3.main.ui.GUIHandler;
import com.team3.main.util.InputHandler;
import com.team3.main.util.MathUtil;

public class Main extends Canvas implements Runnable, MouseMotionListener, ActionListener {

	// INIT VARS
	private static final long serialVersionUID = 1L;
	private static JFrame frame, data_frame, house_frame;
	private final String title = "Robot Vacuum";
	private final int WIDTH = 960;
	private final int HEIGHT = 540;
	private int fps, ups, frame_time;
	private static boolean running = false;
	private boolean showFPS = true;
	private static Thread thread;
	private int mouse_x = 0, mouse_y = 0;
	
	// END INIT VARS
	
	// UTIL VARS
	private InputHandler input;
	// END UTIL VARS

	private BufferedImage dirt_overlay, dirt_data;
	private Font font;
	private Robot robot;
	private House init_house;
	private GUIHandler gui_handler;
	private SimulationController simulation_controller;
	private DataController data_controller;
	private Display display;

	private JTable data_table;
	private final String[] COLUMN_HEADERS = {"Run Id", "House Id", "Random Eff %", "Snake Eff %", "Spiral Eff %", "Wall Follow Eff %"};
	private Object[][] run_data;

	private boolean run_simulation = false, show_obstacles = true, draw_mode = false, data_mode = false, has_started = false, all = true, house_changed = false;
	private int mode_cooldown = 0;
	private String draw_brush = SimulationController.ERASE;

	private Color average_color;
	private double average_color_percentage, random_p, snake_p, spiral_p, wall_follow_p;
	
	public Main() {
		// INIT VARS
		Dimension d = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(d);
		frame = new JFrame(title);
		// END INIT VARS
		
		// UTIL VARS
		input = new InputHandler();
		this.addKeyListener(input);
		this.addMouseListener(input);
		this.addMouseWheelListener(input);
		addMouseMotionListener(this);
		// END UTIL VARS

		// GRAPHICS VARS
		font = new Font("Arial", Font.BOLD, 12);

		// Get dirt_overlay image from file
		try {
			dirt_overlay = ImageIO.read(new File("res/dirt.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}

		// Create dirt_data image used to store dirt/cleaning information for each simulation
		dirt_data = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dirt_data.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.dispose();
		// END GRAPHICS VARS

		// Create DataController
		data_controller = new DataController("data/data.json", "data/houses.json", "data/data_pretty.json", "data/runs.json");

		// Load the default house
		init_house = data_controller.loadHouse("A-0");
		if (init_house == null) { // If the default house is not initialized on the disk, create a blank house
			init_house = new House(WIDTH, HEIGHT);
			init_house.id = data_controller.getHouseId(init_house);
		}

		// Create Vacuum
		robot = new Robot(new Vector2f(WIDTH/2, HEIGHT/2), Math.PI / 2.0, 1);

		// Create SimulationController
		simulation_controller = new SimulationController(init_house, robot);

		// Create Display
		display = new Display();

		// Colors for the GUI buttons
		Color background_color = new Color(31, 133, 222);
		Color pressed_color = new Color(30, 80, 130);
		Color font_color = new Color(241, 241, 241);

		// Create the GUIHandler
		gui_handler = new GUIHandler(background_color, pressed_color, background_color, font_color, 0.75f);

		// Add buttons for Simulation Mode
		gui_handler.addButton(new Button(25, 25, 80, 30, "▶"), "run");
		gui_handler.addButton(new Button(115, 25, 80, 30, "x1"), "speed");
		gui_handler.addButton(new Button(115, 25, 80, 30, "⏹"), "stop");
		gui_handler.addButton(new Button(205, 25, 150, 30, "Toggle Obstacles"), "obstacles");
		gui_handler.addButton(new Button(365, 25, 150, 30, "Path: " + simulation_controller.getMovementMethod()), "movement");
		gui_handler.addButton(new Button(525, 25, 120, 30, "Draw Mode"), "draw");
        gui_handler.addButton(new Button(655, 25, 120, 30, "Data Mode"), "data");
		gui_handler.addButton(new Button(785, 25, 120, 30, "Select House"), "house");

		// Add buttons for Draw Mode
		gui_handler.addButton(new Button(25, 25, 80, 30, "Hold ESC"), "tools");
		gui_handler.addButton(new Button(25, 25, 150, 30, "Simulation Mode"), "simulation");
		gui_handler.addButton(new Button(185, 25, 150, 30, "Brush: " + draw_brush), "brush");
	}

	// Main method
	public static void main(String[] args) {
		Main game = new Main();

		System.setProperty("sun.java2d.opengl", "true");

		// Set properties of the main window
		frame.setResizable(false);
		frame.add(game);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.toFront();
		frame.setState(JFrame.NORMAL);
		frame.requestFocus();

		frame.setVisible(true);
		game.start();
	}

	// Start the game thread
	private synchronized void start() {
		running = true;

		thread = new Thread(this, "Game Thread");
		thread.start();
	}

	// Update the game thread
	public void run() {
		long oldTime = System.nanoTime();
		long timer = System.currentTimeMillis();

		double ns = 1000000000.0 / 60.0;
		long newTime;
		double delta = 0;

		while (running) {
			// Get the deltaTime (time between this frame and the last frame)
			newTime = System.nanoTime();
			delta += (double) (newTime - oldTime) / ns;
			oldTime = newTime;

			// If it has been 1/60th of a second, update the simulation
			if (delta >= 1) {
				delta--;
				ups++;
				update();
			}

			// Render every loop
			render();
			fps++;

			// Every second, output statistics in the window title
			if (System.currentTimeMillis() - timer > 1000) {
				average_color = MathUtil.averageColor(dirt_data);

				average_color_percentage = (255.0 - average_color.getRed()) / 255.0 * 100.0;
				
				double total_seconds = simulation_controller.getTotalSteps() / 6.0;
				int minutes = (int)Math.floor(total_seconds / 60.0);
				int seconds = (int)(total_seconds % 60.0);

				timer += 1000;
				if (showFPS)
					frame.setTitle(title + " | " + fps + " fps " + ups + " ups | Simulation " + (run_simulation ? "running at x" + simulation_controller.getSpeed() + " | Method: " + simulation_controller.getMovementMethod() : "paused") + " | Time elapsed: " + minutes + ":" + seconds + " | Clean: " + String.format("%.2f", average_color_percentage) + "%");
				else
					frame.setTitle(title);
				// System.out.println(ups + " ups, " + fps + " fps");
				fps = 0;
				frame_time = ups;
				ups = 0;
			}
		}
		stop();
	}

	// Stop the game thread
	private synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Update the game
	private void update() {
		input.update();

		// If the simulation is running
		if(run_simulation) {
			if (simulation_controller.getTotalSteps() < Robot.BATTERY_LIFE) { // If the Robot has not run out of battery
				if (gui_handler.getButtons().get("run").isPressed()) { // Pause the simulation
					run_simulation = false;

					gui_handler.changeButtonText("run", "▶");
				}
				if (gui_handler.getButtons().get("stop").isPressed()) { // Stop the simulation and go to Data Mode
					run_simulation = false;
					data_mode = true;

					// Save the run data and reset the simulation
					setPercentages();
					reset();
					simulation_controller.reset(new Robot(new Vector2f(WIDTH/2, HEIGHT/2), Math.PI / 2.0, 1));

					data_controller.saveData(simulation_controller.getFloorPlan().id, random_p, snake_p, spiral_p, wall_follow_p);

					fullReset();
					showData();

					gui_handler.changeButtonText("run", "▶");
				}

				simulation_controller.update(dirt_overlay, show_obstacles, dirt_data);
			} else { // Save the run data and reset the simulation
				setPercentages();
				reset();
				simulation_controller.reset(new Robot(new Vector2f(WIDTH/2, HEIGHT/2), Math.PI / 2.0, 1));
				if (all && (random_p == 0 || snake_p == 0 || spiral_p == 0 || wall_follow_p == 0))
					simulation_controller.updateMovementMethod();
				else {
					run_simulation = false;
					data_mode = true;

					data_controller.saveData(simulation_controller.getFloorPlan().id, random_p, snake_p, spiral_p, wall_follow_p);
					fullReset();
					showData();

					gui_handler.changeButtonText("run", "▶");
				}
			}
		} else {
			if(draw_mode){ // If Draw Mode
				if(mode_cooldown < 100){ // Handle double click issues
					mode_cooldown++;
				} else {
					if (input.escape) { // If not drawing and trying to click buttons
						if (gui_handler.getButtons().get("simulation").isPressed()) { // Go to Simulation Mode
							draw_mode = false;
							draw_brush = SimulationController.ERASE;
						}

						if (gui_handler.getButtons().get("brush").isPressed()) { // Change the current brush
							switch (draw_brush) {
								case SimulationController.ERASE:
									draw_brush = SimulationController.TABLE;
									break;
								case SimulationController.TABLE:
									draw_brush = SimulationController.CHEST;
									break;
								case SimulationController.CHEST:
									draw_brush = SimulationController.ERASE;
									break;
							}

							gui_handler.changeButtonText("brush", "Brush: " + draw_brush);
						}
					} else
						if (simulation_controller.handleDraw(input, mouse_x, mouse_y, draw_brush)) // Draw with the current brush if clicking
							house_changed = true;
				}
			} else if (data_mode) {
                if(mode_cooldown < 100){
                    mode_cooldown++;
                } else {
                    if (gui_handler.getButtons().get("simulation").isPressed()) { // Go to Simulation Mode
                        data_mode = false;
                    }
                }
            } else if (has_started) {
				if (gui_handler.getButtons().get("run").isPressed()) { // Resume the simulation
					run_simulation = true;
					has_started = true;

					gui_handler.changeButtonText("run", "❚❚");
				}

				if (gui_handler.getButtons().get("speed").isPressed()) { // Change the simulation speed
					simulation_controller.updateSpeed();

					gui_handler.changeButtonText("speed", "x" + simulation_controller.getSpeed());
				}
            } else {
				if (gui_handler.getButtons().get("run").isPressed()) { // When the simulation is first started, save the house data and initialize the DataEntry
					run_simulation = true;
					has_started = true;

					display.clearObstacleDirt(init_house, dirt_data);

					if (house_changed) {
						init_house.id = data_controller.getHouseId(init_house);
						data_controller.saveHouse(init_house);
					}

					if (simulation_controller.getMovementMethod() == SimulationController.ALL)
						simulation_controller.updateMovementMethod();
					else
						all = false;

					gui_handler.changeButtonText("run", "❚❚");
				}

				if (gui_handler.getButtons().get("draw").isPressed()) { // Go to Draw Mode
					draw_mode = true;

					mode_cooldown = 0;
				}

				if (gui_handler.getButtons().get("data").isPressed()) { // Go to Data Mode
					data_mode = true;
					showData();

					mode_cooldown = 0;
				}

				if (gui_handler.getButtons().get("speed").isPressed()) { // Change the simulation speed
					simulation_controller.updateSpeed();

					gui_handler.changeButtonText("speed", "x" + simulation_controller.getSpeed());
				}

				if (gui_handler.getButtons().get("obstacles").isPressed()) { // Toggle obstacles on or off **DEBUG**
					show_obstacles = !show_obstacles;
				}

				if (gui_handler.getButtons().get("movement").isPressed()) { // Change the movement method to simulate
					simulation_controller.updateMovementMethod();

					gui_handler.changeButtonText("movement", "Path: " + simulation_controller.getMovementMethod());
				}

				if (gui_handler.getButtons().get("house").isPressed()) { // Open the house selection window
					showHousePicker();
				}
			}
		}
	}

	// Render the game
	private void render() {
		// INIT CODE
		// GRAPHICS VARS
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) { // If first run, create the buffer strategy
			createBufferStrategy(3);
			
			return;
		}

		// Initialize the AWT graphics
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        
		g.setColor(new Color(255, 202, 128));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setFont(font);
		// END INIT CODE

		// Render the main display
		display.render(g, simulation_controller, data_controller, show_obstacles, dirt_overlay);

		// Update and Render the GUI
		gui_handler.update(input, mouse_x, mouse_y, frame_time);
		gui_handler.render(g, run_simulation, draw_mode, input.escape, data_mode, has_started);
		
		// CLOSING CODE
		g.dispose();
		bs.show();
		// END CLOSING CODE
	}

	// When simulation ends, update percentages accordingly
	private void setPercentages() {
		average_color = MathUtil.averageColor(dirt_data);
		average_color_percentage = (255.0 - average_color.getRed()) / 255.0 * 100.0;

		switch (simulation_controller.getMovementMethod()) {
			case SimulationController.RANDOM:
				random_p = average_color_percentage;
				break;
			case SimulationController.SNAKE:
				snake_p = average_color_percentage;
				break;
			case SimulationController.SPIRAL:
				spiral_p = average_color_percentage;
				break;
			case SimulationController.WALL_FOLLOW:
				wall_follow_p = average_color_percentage;
				break;
		}
	}

	// Reset the dirt overlay and data images
	private void reset() {
		try {
			dirt_overlay = ImageIO.read(new File("res/dirt.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}

		dirt_data = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dirt_data.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.dispose();

		display.clearObstacleDirt(init_house, dirt_data);
	}

	// Reset all simulation data
	private void fullReset() {
		house_changed = false;
		has_started = false;
		all = true;

		random_p = 0;
		snake_p = 0;
		spiral_p = 0;
		wall_follow_p = 0;
	}

	// Show the data window
	private void showData() {
		// Get a list of all data entries
		int length = data_controller.getRunData().size();
		run_data = new Object[length][6];
		DataEntry entry;
		for (int i = 0; i < length; i++) {
			entry = data_controller.getRunData().get(i);
			run_data[i][0] = entry.getRunId();
			run_data[i][1] = entry.getHouseId();
			run_data[i][2] = String.format("%.2f", entry.getRandom()) + "%";
			run_data[i][3] = String.format("%.2f", entry.getSnake()) + "%";
			run_data[i][4] = String.format("%.2f", entry.getSpiral()) + "%";
			run_data[i][5] = String.format("%.2f", entry.getWallFollow()) + "%";
		}

		// Create a table from the list of data entries
		data_table = new JTable(run_data, COLUMN_HEADERS){public boolean isCellEditable(int rowIndex, int colIndex) {return false;}};

		// Add this table to a window and display the window
		JScrollPane container = new JScrollPane(data_table);
		data_table.setFillsViewportHeight(true);

		container.setSize(700, 400);
		container.setLocation(185, 25);

		data_frame = new JFrame("Data");

		data_frame.setResizable(true);
		data_frame.add(container);
		data_frame.pack();
		data_frame.setLocationRelativeTo(null);
		data_frame.toFront();
		data_frame.setState(JFrame.NORMAL);
		data_frame.requestFocus();

		data_frame.setVisible(true);
	}

	// Show the house selection window
	private void showHousePicker() {
		// Get a list of all houses
		String[] houses = new String[data_controller.getHouseData().size()];
		for (int i = 0; i < houses.length; i++){
			houses[i] = data_controller.getHouseData().get(i);
		}

		// Create a selection drop down menu using the list of houses
		JComboBox house_selector = new JComboBox(houses);
		house_selector.setSelectedIndex(0);
		house_selector.addActionListener(this);

		// Add the menu to a window and display the window
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(80, 400));
		panel.add(house_selector);

		house_frame = new JFrame("House Selector");

		house_frame.setResizable(false);
		house_frame.setPreferredSize(new Dimension(300, 100));
		house_frame.add(panel);
		house_frame.pack();
		house_frame.setLocationRelativeTo(null);
		house_frame.toFront();
		house_frame.setState(JFrame.NORMAL);
		house_frame.requestFocus();

		house_frame.setVisible(true);
	}

	// Handle clicks for the house selection window
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		String house_id = (String)cb.getSelectedItem();

		// Select house
		House house = data_controller.loadHouse(house_id);
		if (house != null) {
			simulation_controller.reset(new Robot(new Vector2f(WIDTH/2, HEIGHT/2), Math.PI / 2.0, 1), house);
			init_house = house;

			reset();
		}
	}
	
	public int getMouseX(){
		return mouse_x;
	}

	public int getMouseY(){
		return mouse_y;
	}
	
	public void mouseDragged(MouseEvent e){
		mouse_x = e.getX();
		mouse_y = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();
	}
}
