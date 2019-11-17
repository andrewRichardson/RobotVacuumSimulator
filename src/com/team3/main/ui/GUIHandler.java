package com.team3.main.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.team3.main.util.InputHandler;

public class GUIHandler {
	
	private HashMap<String, Button> button_list;
	private Color button_color = Color.GRAY, font_color = Color.WHITE, outline_color = Color.DARK_GRAY, pressed_color = Color.LIGHT_GRAY;
	private final int stroke = 1;
	private boolean last_click_status = false;
	private final float alpha;
	private BasicStroke stroke_style = new BasicStroke(stroke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	
	public GUIHandler() {
		button_list = new HashMap<String, Button>();
		alpha = 0.5f;
	}
	
	public GUIHandler(Color button_color, Color pressed_color, Color outline_color, Color font_color, float alpha) {
		button_list = new HashMap<String, Button>();
		
		this.button_color = button_color;
		this.font_color = font_color;
		this.outline_color = outline_color;
		this.pressed_color = pressed_color;
		
		this.alpha = alpha;
	}
	
	public void addButton(Button button, String name) {
		button_list.put(name, button);
	} // Add new button to array
	
	public void update(InputHandler input, int mouse_x, int mouse_y, int frame_time) {
		for (Button button : button_list.values()) { // For each button, press if the mouse clicked within its bounds
			if (input.mouseClicked != last_click_status && button.getBounds().contains(mouse_x, mouse_y) && !button.pressed) {
				button.pressed = true;
				button.press(frame_time);
			}
			
			if (button.pressed) { // If the button is pressed, trigger its timer call until done
				button.pressedTimer();
			}
		}

		last_click_status = input.mouseClicked;
	}
	
	public HashMap<String, Button> getButtons() {
		return button_list;
	}
	
	public void changeButtonText(String name, String text) { // Since button names are immutable, replace the button with a new button with an updated name
		Button new_button = button_list.get(name);
		new_button.text = text;
		
		button_list.replace(name, new_button);
	}
	
	public void render(Graphics2D g, boolean run_simulation, boolean draw_mode, boolean tool_mode, boolean data_mode, boolean has_started) {
		g.setStroke(stroke_style);

		if(run_simulation) { // If running in simulation mode

			// Draw Run Button
			drawButton(g, "run", 0.5f);

			// Draw Stop Button
			drawButton(g, "stop", 0.5f);
		} else { // If not running in simulation mode
			if (draw_mode) { // If draw mode
				if (tool_mode) {
					// Draw Draw Mode Button
					drawButton(g, "simulation", 1.0f);

					// Draw Brush Button
					drawButton(g, "brush", 1.0f);
				} else {
					// Draw Tool Mode Button
					drawButton(g, "tools", 0.5f);
				}
			} else if (data_mode) { // If data mode
				// Draw Simulation Mode Button
				drawButton(g, "simulation", 1.0f);


			} else if (has_started) { // If not running in simulation mode and not started yet
				// Draw Run Button
				drawButton(g, "run", 1.0f);

				// Draw Speed Button
				drawButton(g, "speed", 1.0f);

			} else { // Only display the default buttons
				// Remove blacklisted buttons that should only display in other modes -----------------

				// Add the buttons to a temporary array list
				List<Button> button_temp_list = new ArrayList<Button>();
				button_temp_list.add(button_list.get("brush"));
				button_temp_list.add(button_list.get("simulation"));
				button_temp_list.add(button_list.get("tools"));
				button_temp_list.add(button_list.get("stop"));

				// Remove them
				button_list.remove("brush");
				button_list.remove("simulation");
				button_list.remove("tools");
				button_list.remove("stop");

				// Display all other buttons
				for (Button button : button_list.values()) {
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

					g.setColor(button.pressed ? pressed_color : button_color);
					g.fill(button.getBounds());

					g.setColor(outline_color);
					g.draw(button.getBounds());

					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
					g.setColor(font_color);
					drawCenteredString(g, button.text, button.getBounds().getBounds(), new Font("Helvetica", Font.BOLD, 16));
				}

				// Re-add the blacklisted buttons from the temporary array
				button_list.put("brush", button_temp_list.get(0));
				button_list.put("simulation", button_temp_list.get(1));
				button_list.put("tools", button_temp_list.get(2));
				button_list.put("stop", button_temp_list.get(3));

				// --------------------------------------------------------------------------------
			}
		}
	}

	private void drawButton(Graphics2D g, String name, float alpha_mul) {
		// Get the button instance and draw with alpha transparency
		Button button = button_list.get(name);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * alpha_mul));

		// Set color according to button state
		g.setColor(button.pressed ? pressed_color : button_color);
		g.fill(button.getBounds()); // Fill background

		g.setColor(outline_color);
		g.draw(button.getBounds()); // Fill outline

		// Draw text with no transparency
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.setColor(font_color);
		drawCenteredString(g, button.text, button.getBounds().getBounds(), new Font("Helvetica", Font.BOLD, 16));
	}
	
	private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
}
