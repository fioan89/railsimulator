package org.faur.railsim.railsimulator;

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
     * @param id identification number.
     */
    public void setTrainId(int id);
    /**
     * Gets train identification number.
     * 
     * @return <code>int</code> representing the train identification number.
     */
    public int getTrainId();
 
    /**
     * Draw a simple representation of a train on the graphic context at the specified position.
     * 
     * @param g graphic context.
     * @param location 2 coordinates on the screen where this train will be painted
     */
    public void draw(Graphics g, Point location);

}
