package org.faur.railsim.railmonitor;

import java.awt.Graphics;
import java.util.Map;


/**
 * Provides basic functionality for loading and painting the railway.
 * 
 * The format under which the track is stored quite simple: Train station and
 * track switches are named. Track line is marked with a simple "_" or
 * "\" or "/". Example:
 * <br>
 * <pre>A1 _ _ _ _ _ _ _ _ _ _ _ _ _ S1 _ _ _ _ _ _ _ _ _ S3 _ _ _ _ _ _ _ _ B1</pre> 
 * <pre>* * * * * * * * * * * * * * * \ * * * * * * * * * / * * * * * * * * * *</pre>
 * <pre>A2 _ _ _ _ _ _ _ _ _ _ _ _ _ _ S2 _ _ _ _ _ _ _ S4 _ _ _ _ _ _ _ _ _ B2</pre>
 * 
 * Here A1, A2, B1, B2 represents train stations. S1, S2, S3, S4 represents
 * track switches where rail vehicles can switch tracks. "*" is just a mark for unused space.
 * It should be ignored when the actual painting takes place.
 *
 * @author faur
 * @since November 06 2012
 * 
 * @param <T> generic parameter. Usually is a <code>String</code> key.
 * @param <U> generic parameter. Usually is a {@link java.awt.Point}
 */
public interface RailMapLoader<T, U> {
    /**
     * Loads railway from <code>file</code> and return it as a matrix of
     * <code>String</code>. 
     * <br>
     * This file should be encoded with "UTF-8" and
     * should have .csv extension
     * 
     * @param  file where railway map is stored. It is a <code>.csv</code> file encoded int <code>UTF-8</code>.
     * @return a <code>T</code> matrix representing the railway map.
     */
    T[][] loadMapFromFile(String file);
    
    /**
     * Draws the specified matrix on the specified graphic context and build
     * it's location coordinates (Coordination on the screen for train stations and track switches ).
     * <br>
     * In order to retrieve this coordinates call {@link RailMapLoader#getCoordinatesMap()} 
     * 
     * @param panel component context where drawing will take place.
     * @param map   railway map as <code>T</code> matrix.
     */
    void buildMap(Graphics g, T[][] map);
    
    /**
     * Gets the train stations and track switches coordinates. These coordinates
     * are stored in a hash map as {@link java.awt.Point} with the name of the 
     * location as a key. These coordinates represents the <code>x</code> and 
     * <code>y</code> position on the graphic context where railway will be rendered.
     * 
     * @return a map of the train stations and track switches coordinates.
     */
    Map<T, U> getCoordinatesMap();
}
