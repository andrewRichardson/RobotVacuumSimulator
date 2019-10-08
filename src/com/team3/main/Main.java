package com.team3.main;

import java.awt.AlphaComposite;
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
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.team3.main.logic.CollisionModel;
import com.team3.main.logic.Obstacle;
import com.team3.main.logic.Vacuum;
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

	// GRAPHICS VARS
	private BufferStrategy bs;
	private BufferedImage planks_image, chest_image, table_image, dirt_image, table_legs_image, vacuum_image;
	private Font font;
	private Vacuum vacuum;
	private CollisionModel collision_model;
	private GUIHandler gui_handler;
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
		// END GRAPHICS VARS
		
		// Images
		try {
			planks_image = ImageIO.read(new File("res/wood_planks.png"));
			chest_image = ImageIO.read(new File("res/chest.png"));
			table_image = ImageIO.read(new File("res/table.png"));
			table_legs_image = ImageIO.read(new File("res/table_legs.png"));
			dirt_image = ImageIO.read(new File("res/dirt.png"));
			vacuum_image = ImageIO.read(new File("res/vacuum.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// Collision handling for vacuum and obstacles
		collision_model = new CollisionModel(WIDTH, HEIGHT);
		Random random = new Random();
		
		// Generate random obstacles
		for (int r = 0; r < HEIGHT; r += 100) {
			for (int c = 0; c < WIDTH; c += 100) {
				if (random.nextBoolean()) {
					int width = 38;
					int height = 38;
					
					collision_model.obstacles.add(new Obstacle(c, r, width, height, random.nextBoolean()));
				}
			}
		}
		
		// Create Vacuum
		vacuum = new Vacuum(new Vector2f(WIDTH/2, HEIGHT/2), Math.PI+0.4, 0.1, "random", collision_model);
		
		Color background_color = new Color(31, 133, 222);
		Color pressed_color = new Color(30, 80, 130);
		//Color outline_color = new Color(38, 96, 145);
		Color font_color = new Color(241, 241, 241);
		
		gui_handler = new GUIHandler(background_color, pressed_color, background_color, font_color, 0.9f);
		gui_handler.addButton(new Button(25, 25, 80, 30, "▶"), "run");
		gui_handler.addButton(new Button(115, 25, 80, 30, "x1"), "speed");
		gui_handler.addButton(new Button(205, 25, 150, 30, "Toggle Obstacles"), "obstacles");
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

	public synchronized void start() {
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
					frame.setTitle(title + " | " + fps + " fps " + ups + " ups | Simulation " + (run_simulation ? "running at x" + vacuum.getSpeed() : "paused"));
				else
					frame.setTitle(title);
				// System.out.println(ups + " ups, " + fps + " fps");
				fps = 0;
				ups = 0;
			}
		}
		stop();
	}

	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		input.update();
		
		if (gui_handler.getButtons().get("run").isPressed()) {
			run_simulation = !run_simulation;
			
			gui_handler.changeButtonText("run", run_simulation ? "❚❚" : "▶");
		}
		
		if (gui_handler.getButtons().get("speed").isPressed()) {
			vacuum.updateSpeed();
			
			gui_handler.changeButtonText("speed", "x"+vacuum.getSpeed());
		}

		if (gui_handler.getButtons().get("obstacles").isPressed()) {
			vacuum.collide_obstacles = !vacuum.collide_obstacles;
			show_obstacles = !show_obstacles;
		}
		
		if(run_simulation) {
			vacuum.update();
		}
	}

	public void render() {
		// INIT CODE
		bs = getBufferStrategy();
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

		g.drawImage(planks_image, 0, 0, null);
		//renderTables(g);
		
		Graphics2D g_trail = dirt_image.createGraphics();
		g_trail.setRenderingHints(rh);
		
		g_trail.setComposite(AlphaComposite.Src);
		g_trail.setColor(new Color(0, 0, 0, 0));
		g_trail.fillOval(vacuum.getPosition().x, vacuum.getPosition().y, vacuum.diameter, vacuum.diameter);
		
		g_trail.dispose();
		
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		g.drawImage(dirt_image, 0, 0, null);
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

		if (show_obstacles)
			renderObstacles(g);
		
		//g.setColor(Color.black);
		//g.fillOval(vacuum.getPosition().x, vacuum.getPosition().y, vacuum.diameter, vacuum.diameter);
		
		g.drawImage(vacuum_image, vacuum.getPosition().x, vacuum.getPosition().y, null);

		gui_handler.update(input, mouse_x, mouse_y);
		
		gui_handler.render(g);
		
		// CLOSING CODE
		g.dispose();
		bs.show();
		// END CLOSING CODE
	}
	
	public void renderObstacles(Graphics2D g) {
		//g.setColor(Color.GRAY);
		for (Obstacle obstacle : collision_model.obstacles) {
			if (obstacle.is_table_or_chair) {
				//g.setColor(Color.GRAY);
				//g.fillRect(obstacle.colliders[0].x, obstacle.colliders[0].y, obstacle.width + Obstacle.LEG_SIZE, obstacle.height + Obstacle.LEG_SIZE);
				//g.setColor(Color.DARK_GRAY);
				
				g.drawImage(table_legs_image, obstacle.colliders[0].x, obstacle.colliders[0].y, null);
				g.drawImage(table_image, obstacle.colliders[0].x, obstacle.colliders[0].y, null);
			} else {
				g.drawImage(chest_image, obstacle.colliders[0].x, obstacle.colliders[0].y, null);
			}
		}
	}
	
	public void renderTables(Graphics2D g) {
		//g.setColor(Color.GRAY);
		for (Obstacle obstacle : collision_model.obstacles) {
			if (obstacle.is_table_or_chair) {
				//g.setColor(Color.GRAY);
				//g.fillRect(obstacle.colliders[0].x, obstacle.colliders[0].y, obstacle.width + Obstacle.LEG_SIZE, obstacle.height + Obstacle.LEG_SIZE);
				//g.setColor(Color.DARK_GRAY);
				
				g.drawImage(table_image, obstacle.colliders[0].x, obstacle.colliders[0].y, null);
			}
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
