package org.faur.railsim.railsimulator;

import java.io.File;

/**
 * This class provides a standardized way to  
 * load and draw the rail track. The mechanism of 
 * how this implemented is up to the child class. 
 *  
 * @author faur
 * @since November 05 2012
 *
 */
public abstract class RailTrack {
    
    
    /**
     * Loads track configuration from a file, interpret 
     * the file structure and paint the rail road.
     * <br>
     * The structure implemented in the <code>track</code> file
     * is up to the user specifications.
     * 
     * @param track provides the configuration file for the rail track.
     */
    public abstract void drawTrack(File track);
    
    

}