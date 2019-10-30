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
	}
	
	public void update(InputHandler input, int mouse_x, int mouse_y, int frame_time) {
		for (Button button : button_list.values()) {
			if (input.mouseClicked != last_click_status && button.getBounds().contains(mouse_x, mouse_y) && !button.pressed) {
				button.pressed = true;
				button.press(frame_time);
			}
			
			if (button.pressed) {
				button.pressedTimer();
			}
		}

		last_click_status = input.mouseClicked;
	}
	
	public HashMap<String, Button> getButtons() {
		return button_list;
	}
	
	public void changeButtonText(String name, String text) {
		Button new_button = button_list.get(name);
		new_button.text = text;
		
		button_list.replace(name, new_button);
	}
	
	public void render(Graphics2D g, boolean run_simulation, boolean draw_mode, boolean tool_mode) {
		g.setStroke(stroke_style);

		if(run_simulation) {

			// Draw Run Button
			Button button = button_list.get("run");
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.5f));

			g.setColor(button.pressed ? pressed_color : button_color);
			g.fill(button.getBounds());

			g.setColor(outline_color);
			g.draw(button.getBounds());

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g.setColor(font_color);
			drawCenteredString(g, button.text, button.getBounds().getBounds(), new Font("Helvetica", Font.BOLD, 16));
		} else {
			if (draw_mode) {
				if (tool_mode) {
					// Draw Draw Mode Button
					Button button = button_list.get("simulation");
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

					g.setColor(button.pressed ? pressed_color : button_color);
					g.fill(button.getBounds());

					g.setColor(outline_color);
					g.draw(button.getBounds());

					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
					g.setColor(font_color);
					drawCenteredString(g, button.text, button.getBounds().getBounds(), new Font("Helvetica", Font.BOLD, 16));

					// Draw Brush Button
					button = button_list.get("brush");
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

					g.setColor(button.pressed ? pressed_color : button_color);
					g.fill(button.getBounds());

					g.setColor(outline_color);
					g.draw(button.getBounds());

					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
					g.setColor(font_color);
					drawCenteredString(g, button.text, button.getBounds().getBounds(), new Font("Helvetica", Font.BOLD, 16));
				} else {
					// Draw Tool Mode Button
					Button button = button_list.get("tools");
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.5f));

					g.setColor(button.pressed ? pressed_color : button_color);
					g.fill(button.getBounds());

					g.setColor(outline_color);
					g.draw(button.getBounds());

					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
					g.setColor(font_color);
					drawCenteredString(g, button.text, button.getBounds().getBounds(), new Font("Helvetica", Font.BOLD, 16));
				}
			} else {
				List<Button> button_temp_list = new ArrayList<Button>();
				button_temp_list.add(button_list.get("brush"));
				button_temp_list.add(button_list.get("simulation"));
				button_temp_list.add(button_list.get("tools"));

				button_list.remove("brush");
				button_list.remove("simulation");
				button_list.remove("tools");

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
				button_list.put("brush", button_temp_list.get(0));
				button_list.put("simulation", button_temp_list.get(1));
				button_list.put("tools", button_temp_list.get(2));
			}
		}
	}
	
	private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
}
