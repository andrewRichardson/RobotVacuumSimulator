package com.team3.main;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import com.team3.main.logic.CollisionModel;
import com.team3.main.logic.Obstacle;
import com.team3.main.logic.Vacuum;
import com.team3.main.math.Vector2f;
import com.team3.main.util.InputHandler;

public class Main extends Canvas implements Runnable {

	// INIT VARS
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private final String title = "Robot Vacuum";
	private final int WIDTH = 960;
	private final int HEIGHT = 540;
	public final static int SCL_WIDTH = 480;
	public final static int SCL_HEIGHT = 270;
	private int fps, ups;
	private static boolean running = false;
	private boolean showFPS = true;
	private static Thread thread;
	public static final double GRAVITY = 1.0;
	// END INIT VARS
	
	// UTIL VARS
	private InputHandler input;
	// END UTIL VARS

	// GRAPHICS VARS
	private BufferStrategy bs;
	private BufferedImage vacuum_trail;
	private Font font;
	private Vacuum vacuum;
	private CollisionModel collision_model;

	public Main() {
		// INIT VARS
		Dimension d = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(d);
		frame = new JFrame(title);
		// END INIT VARS
		
		// UTIL VARS
		input = new InputHandler();
		this.addKeyListener(input);
		// END UTIL VARS

		// GRAPHICS VARS
		font = new Font("Arial", Font.BOLD, 12);
		// END GRAPHICS VARS

		vacuum_trail = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		collision_model = new CollisionModel(WIDTH, HEIGHT);
		Random random = new Random();
		
		for (int r = 0; r < HEIGHT; r += 100) {
			for (int c = 0; c < WIDTH; c += 100) {
				if (random.nextBoolean()) {
					int width = random.nextInt(Obstacle.MINIMUM_GAP_SIZE) + Obstacle.MINIMUM_GAP_SIZE;
					int height = random.nextInt(Obstacle.MINIMUM_GAP_SIZE) + Obstacle.MINIMUM_GAP_SIZE;
					
					collision_model.obstacles.add(new Obstacle(c, r, width, height, random.nextBoolean()));
				}
			}
		}
		
		vacuum = new Vacuum(new Vector2f(WIDTH/2, HEIGHT/2), Math.PI+0.4, 2.0, "random", collision_model);
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
					frame.setTitle(title + " | " + fps + " fps " + ups + " ups");
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
		vacuum.update();
	}

	public void render() {
		// INIT CODE
		bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			Graphics2D g_trail = vacuum_trail.createGraphics();
			g_trail.fillRect(0, 0, WIDTH, HEIGHT);
			g_trail.dispose();
			
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setFont(font);
		// END INIT CODE
		
		//g.setColor(new Color(46, 204, 138));
		//g.fillRect(0, 0, SCL_WIDTH, SCL_HEIGHT);
		//g.setColor(Color.WHITE);
		//g.drawString(("Player - " + player.getPosition().x + ", " + player.getPosition().y), 5, 10);
		//g.drawString(("World - " + world.getPosition().x + ", " + world.getPosition().y), 5, 25);

		renderObstacles(g);
		
		Graphics2D g_trail = vacuum_trail.createGraphics();
		
		g_trail.setColor(new Color(200, 200, 200));
		g_trail.fillOval(vacuum.getPosition().x, vacuum.getPosition().y, vacuum.diameter, vacuum.diameter);
		
		g_trail.dispose();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.drawImage(vacuum_trail, 0, 0, null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		
		g.setColor(Color.black);
		g.fillOval(vacuum.getPosition().x, vacuum.getPosition().y, vacuum.diameter, vacuum.diameter);
		
		// CLOSING CODE
		g.dispose();
		bs.show();
		// END CLOSING CODE
	}
	
	public void renderObstacles(Graphics2D g) {
		g.setColor(Color.gray);
		for (Obstacle obstacle : collision_model.obstacles) {
			if (obstacle.colliders.length > 1) {
				g.setColor(Color.darkGray);
				g.fillRect(obstacle.colliders[0].x, obstacle.colliders[0].y, obstacle.width + Obstacle.LEG_SIZE, obstacle.height + Obstacle.LEG_SIZE);
				g.setColor(Color.gray);
			}
			for (Rectangle rectangle : obstacle.colliders) {
				g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
			}
		}
	}
}
