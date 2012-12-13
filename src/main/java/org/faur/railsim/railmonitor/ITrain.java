package org.faur.railsim.railmonitor;

import java.awt.Graphics;
import java.awt.Point;

/**
 * @author faur
 * @since November 05 2012
 * 
 */
public interface ITrain {

	/**
	 * Sets train identification number.
	 * 
	 * @param id
	 *            identification number.
	 */
	public void setTrainId(int id);

	/**
	 * Gets train identification number.
	 * 
	 * @return <code>int</code> representing the train identification number.
	 */
	public int getTrainId();

	/**
	 * Draw a simple representation of a train on the graphic context at the
	 * specified position.
	 * 
	 * @param g
	 *            graphic context.
	 * @param location
	 *            2 coordinates on the screen where this train will be painted
	 */
	public void draw(Graphics g, Point location);

	/**
	 * Draw a simple representation of a train on the graphic context.
	 * 
	 * @param g
	 *            graphic context.
	 */
	public void draw(Graphics g);

	/**
	 * Sets train location. This is a 2D point and will be mostly used when a
	 * representation of this vehicle will be used to paint it.
	 * 
	 * @param location
	 *            a 2D point on the railway map.
	 */
	public void setTrainPosition(Point location);

	/**
	 * Gets the location where the vehicle will be painted.
	 * 
	 * @return a {@link java.awt.Point} representing the coordinates where the
	 *         vehicle will be painted on the rail map.
	 */
	public Point getTrainPosition();

}
