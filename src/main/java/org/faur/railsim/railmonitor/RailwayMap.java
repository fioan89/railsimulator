package org.faur.railsim.railmonitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the mechanism that loads a railway map from a map and draw this
 * representation on the graphic context. </br> For a more detailed explanation
 * please examine {@link RailMapLoader}
 * 
 * @author faur
 * @see RailMapLoader
 * @since November 06 2012
 */
public class RailwayMap implements RailMapLoader<String, Point> {
    private Logger logger;
    private Map<String, Point> coordinatesMap;

    public RailwayMap() {
	logger = Logger.getLogger(RailwayMap.class.getName());
    }

    public String[][] loadMapFromFile(String file) {
	// First read line by line from file and store it. Also we need
	// to see the maximum number of columns, therefore we need to process it
	// before storing.
	if (file != null) {
	    BufferedReader reader = null;
	    String line = "";
	    List<String> lines = new ArrayList<String>();
	    int max = Integer.MIN_VALUE;

	    try {
		reader = new BufferedReader(new InputStreamReader(
			new FileInputStream(file), "UTF-8"));
		while ((line = reader.readLine()) != null) {
		    lines.add(line);
		    StringTokenizer st = new StringTokenizer(line, " ");
		    if (max < st.countTokens()) {
			max = st.countTokens();
		    }
		}
		// we have the number of rows and the number of columns, lets
		// build the string matrix
		String[][] matrix = new String[lines.size()][max];
		for (int i = 0; i < lines.size(); i++) {
		    StringTokenizer st = new StringTokenizer(lines.get(i), " ");
		    for (int j = 0; j < max; j++) {
			String token = " ";
			if (st.hasMoreTokens()) {
			    token = st.nextToken();
			}
			matrix[i][j] = token;
		    }
		}
		return matrix;

	    } catch (FileNotFoundException e) {
		logger.log(Level.SEVERE, "File loading exception: {0}", e);
	    } catch (IOException e) {
		logger.log(Level.WARNING, "File reading exception: {0}", e);
	    } finally {
		try {
		    reader.close();
		} catch (IOException e) {
		    logger.log(Level.WARNING, "File closing exception: {0}", e);
		}
	    }
	}
	return null;
    }

    public void buildMap(Graphics g, final String[][] map) {
	// We have a matrix of characters (railway map) that needs to be painted
	// on
	// the graphic context. In order to do that we need to have a measure
	// unit which
	// is relative to the drawing area size.
	if (map != null) {
	    Graphics2D gContext = (Graphics2D) g;
	    // gContext.setColor(Color.BLACK);
	    gContext.setFont(new Font("sansserif", Font.BOLD, 10));
	    Rectangle dimension = g.getClip().getBounds();
	    // Measure unit for X and Y axis
	    int stepX = (int) (dimension.getWidth() / map[0].length);
	    int stepY = (int) (dimension.getHeight() / map.length);
	    coordinatesMap = new HashMap<String, Point>();
	    // Draw map on the graphic context, but ignore "*". Also build the
	    // the coordinates map for train stations and track switches.
	    if (map != null) {
		int fontHeight = gContext.getFontMetrics().getHeight();
		// Draw railway
		for (int i = 0; i < map.length; i++) {
		    for (int j = 0; j < map[0].length; j++) {
			int pozX = j * stepX;
			int pozY = i * stepY;
			// Check if a portion of the line
			if (("\\".equals(map[i][j])) || ("/".equals(map[i][j]))
				|| ("_".equals(map[i][j]))) {
			    // gContext.drawChars(map[i][j].toCharArray(), 0,
			    // map[i][j].length(), pozX, pozY);
			    if ("/".equals(map[i][j])) {
				int lastX = 0;
				if ("_".equals(map[i - 1][j - 1])) {
				    lastX = (j + 1) * stepX;
				} else {
				    lastX = (j - 1) * stepX - stepX / 2;
				}
				gContext.drawLine(lastX, pozY - stepY, pozX
					- stepX, pozY + stepY);
			    } else if ("\\".equals(map[i][j])) {
				int lastX = 0;
				int lastY = 0;
				if ("_".equals(map[i - 1][j - 1])) {
				    lastX = (j - 1) * stepX;
				    lastY = (i) * stepY;
				} else {
				    lastX = (j - 1) * stepX - stepX / 2;
				    lastY = (i) * stepY;
				}
				gContext.drawLine(lastX + stepX, lastY - stepY,
					pozX + stepX, pozY + stepY);
			    } else {
				gContext.drawLine(pozX - stepX, pozY, pozX
					+ stepX, pozY);
			    }
			}
		    }
		}

		// Draw locations
		for (int i = 0; i < map.length; i++) {
		    for (int j = 0; j < map[0].length; j++) {
			int pozX = j * stepX;
			int pozY = i * stepY;

			if (!"*".equals(map[i][j]) && !" ".equals(map[i][j])
				&& !"\\".equals(map[i][j])
				&& !"/".equals(map[i][j])
				&& !"_".equals(map[i][j])) {
			    // Draw and store it's coordinates
			    gContext.setColor(Color.blue);
			    gContext.fillOval(pozX - stepX / 2, pozY - stepY
				    / 2 - fontHeight / 2, stepX, stepY
				    + fontHeight / 2);
			    gContext.setColor(Color.yellow);
			    gContext.drawChars(map[i][j].toCharArray(), 0,
				    map[i][j].length(), pozX, pozY);
			    coordinatesMap
				    .put(map[i][j], new Point(pozX, pozY));
			}
		    }
		}
	    }
	}
    }

    public Map<String, Point> getCoordinatesMap() {
	return coordinatesMap;
    }

}