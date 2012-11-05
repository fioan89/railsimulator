package org.faur.railsim.railsimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Vehicle used by the rail simulator.
 * 
 * @author faur
 * @since November 05 2012
 *
 */
public class Train implements ITrain {
    private int id;
    private Point lastPosition;
    private int width;
    
    public Train() {
	lastPosition = new Point();
	width = 30;
    }

    public void setTrainId(int id) {
	this.id = id;

    }

    public int getTrainId() {
	return id;
    }

    public void draw(Graphics g, Point location) {
	// remove last position from the railway
	g.clearRect(lastPosition.x, lastPosition.y, width, width);
	// draw the new position and store the new coordinates
	g.setColor(Color.green);
	g.drawRect(location.x, location.y, width, width);
	char[] data = String.valueOf(id).toCharArray();
	g.drawChars(data, 0, data.length, location.x + 5, location.y + 5);
	lastPosition = location;
    }
    

}
