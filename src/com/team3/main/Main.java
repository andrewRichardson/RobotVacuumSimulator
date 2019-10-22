package com.team3.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.team3.main.entities.*;
import com.team3.main.math.Vector2f;
import com.team3.main.ui.Button;
import com.team3.main.ui.GUIHandler;
import com.team3.main.util.InputHandler;

public class Main extends Canvas implements Runnable, MouseMotionListener {

	// INIT VARS
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private final String title = "Robot Vacuum";
	private final int WIDTH = 960;
	private final int HEIGHT = 540;
	private int fps, ups;
	private static boolean running = false;
	private boolean showFPS = true;
	private static Thread thread;
	private int mouse_x = 0, mouse_y = 0;
	
	// END INIT VARS
	
	// UTIL VARS
	private InputHandler input;
	// END UTIL VARS

	private BufferedImage dirt_overlay;
	private Font font;
	private Robot robot;
	private FloorPlan default_floor_plan;
	private GUIHandler gui_handler;
	private SimulationController simulationController;
	private Display display;
	private boolean run_simulation = false, show_obstacles = true;
	
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

		try {
			dirt_overlay = ImageIO.read(new File("res/dirt.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		// END GRAPHICS VARS

		// Collision handling for vacuum and obstacles
		default_floor_plan = new FloorPlan(WIDTH, HEIGHT);
		Random random = new Random();
		
		// Generate random obstacles
		for (int r = 0; r < HEIGHT; r += 100) {
			for (int c = 0; c < WIDTH; c += 100) {
				if (random.nextBoolean()) {
					int width = 44;
					int height = 44;
					if (random.nextBoolean())
						default_floor_plan.obstacles.add(new Table(c, r, width, height));
					else
						default_floor_plan.obstacles.add(new Chest(c, r, width + Obstacle.LEG_SIZE, height + Obstacle.LEG_SIZE));
				}
			}
		}

		// Create Vacuum
		robot = new Robot(new Vector2f(WIDTH/2, HEIGHT/2), Math.PI + 0.4, 0.25);

		// Create SimulationController
		simulationController = new SimulationController(default_floor_plan, robot);

		// Create Display
		display = new Display();
		
		Color background_color = new Color(31, 133, 222);
		Color pressed_color = new Color(30, 80, 130);
		//Color outline_color = new Color(38, 96, 145);
		Color font_color = new Color(241, 241, 241);
		
		gui_handler = new GUIHandler(background_color, pressed_color, background_color, font_color, 0.75f);
		gui_handler.addButton(new Button(25, 25, 80, 30, "▶"), "run");
		gui_handler.addButton(new Button(115, 25, 80, 30, "x1"), "speed");
		gui_handler.addButton(new Button(205, 25, 150, 30, "Toggle Obstacles"), "obstacles");
		gui_handler.addButton(new Button(365, 25, 150, 30, "Path: " + simulationController.getMovementMethod()), "movement");
	}

	public static void main(String[] args) {
		Main game = new Main();

		System.setProperty("sun.java2d.opengl", "true");

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

	private synchronized void start() {
		running = true;

		thread = new Thread(this, "Game Thread");
		thread.start();
	}

	public void run() {
		long oldTime = System.nanoTime();
		long timer = System.currentTimeMillis();

		double ns = 1000000000.0 / 60.0;
		long newTime;
		double delta = 0;

		while (running) {
			newTime = System.nanoTime();
			delta += (double) (newTime - oldTime) / ns;
			oldTime = newTime;
			if (delta >= 1) {
				delta--;
				ups++;
				update();
			}
			render();
			fps++;

			if (System.currentTimeMillis() - timer > 1000) {

				timer += 1000;
				if (showFPS)
					frame.setTitle(title + " | " + fps + " fps " + ups + " ups | Simulation " + (run_simulation ? "running at x" + simulationController.getSpeed() : "paused"));
				else
					frame.setTitle(title);
				// System.out.println(ups + " ups, " + fps + " fps");
				fps = 0;
				ups = 0;
			}
		}
		stop();
	}

	private synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void update() {
		input.update();
		
		if (gui_handler.getButtons().get("run").isPressed()) {
			run_simulation = !run_simulation;
			
			gui_handler.changeButtonText("run", run_simulation ? "❚❚" : "▶");
		}
		
		if(run_simulation) {
			simulationController.update(dirt_overlay, show_obstacles);
		} else {
			if (gui_handler.getButtons().get("speed").isPressed()) {
				simulationController.updateSpeed();

				gui_handler.changeButtonText("speed", "x"+ simulationController.getSpeed());
			}

			if (gui_handler.getButtons().get("obstacles").isPressed()) {
				show_obstacles = !show_obstacles;
			}

			if (gui_handler.getButtons().get("movement").isPressed()) {
				simulationController.updateMovementMethod();

				gui_handler.changeButtonText("movement", "Path: " + simulationController.getMovementMethod());
			}
		}
	}

	private void render() {
		// INIT CODE
		// GRAPHICS VARS
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        
		g.setColor(new Color(255, 202, 128));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setFont(font);
		// END INIT CODE

		display.render(g, simulationController, show_obstacles, dirt_overlay);

		gui_handler.update(input, mouse_x, mouse_y);
		gui_handler.render(g, run_simulation);
		
		// CLOSING CODE
		g.dispose();
		bs.show();
		// END CLOSING CODE
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
