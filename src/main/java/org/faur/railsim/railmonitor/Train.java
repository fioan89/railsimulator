package org.faur.railsim.railmonitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Vehicle used by the rail simulator.
 * 
 * @author faur
 * @since November 05 2012
 * 
 */
public class Train implements ITrain {
	private int id;
	private Point location;
	private int width;

	public Train() {
		new Point();
		width = 30;
	}

	public Train(int id) {
		this();
		this.id = id;
	}

	public void setTrainId(int id) {
		this.id = id;

	}

	public int getTrainId() {
		return this.id;
	}

	public void draw(Graphics g, Point location) {
		g.setColor(Color.RED);
		g.setFont(new Font("Arial", Font.BOLD, 9));
		g.drawRect(location.x - 15, location.y - 15, width, width);
		char[] data = String.valueOf(id).toCharArray();
		g.drawChars(data, 0, data.length, location.x + 5, location.y + 5);
	}

	public void draw(Graphics g) {
		if (location != null) {
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.BOLD, 9));
			// g.drawRect(location.x - 15, location.y - 15, width , width);
			g.fillOval(location.x - 15, location.y - 15, width, width);
			g.setColor(Color.blue);
			char[] data = String.valueOf(id).toCharArray();
			g.drawChars(data, 0, data.length, location.x + 5, location.y + 5);
		}
	}

	public void setTrainPosition(Point location) {
		this.location = location;

	}

	public Point getTrainPosition() {
		return this.location;
	}

}
