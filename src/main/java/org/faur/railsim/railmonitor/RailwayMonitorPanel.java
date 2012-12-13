package org.faur.railsim.railmonitor;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Monitor panel used to visualize rail road and train vehicles state. The state
 * of the train vehicles is provided by the rail simulator.
 * 
 * @author faur
 * @since November 07 2012
 */
public class RailwayMonitorPanel extends JPanel {
	private String[][] map;
	private Map<String, Point> coordinates;
	private RailwayMap mapLoader;
	private Vector<ITrain> currentTrains;

	private String railMapPath;
	private Logger logger;

	public RailwayMonitorPanel(String railMapPath) {
		super();
		logger = Logger.getLogger(RailwayMonitorPanel.class.getName());
		this.railMapPath = railMapPath;
		currentTrains = new Vector<ITrain>();
		setVisible(true);
	}

	public String getRailMapPath() {
		return railMapPath;
	}

	public void setRailMapPath(String railMapPath) {
		this.railMapPath = railMapPath;
	}

	/**
	 * Loads map and train coordinates, draws them on the graphic context and
	 * enables the auto refreshing of the panel.
	 */
	public void startMonitoring() {
		// load map and train coordinates
		mapLoader = new RailwayMap();
		if (mapLoader != null) {
			// paintComponent will autoredraw the map while this is not null.
			map = mapLoader.loadMapFromFile(railMapPath);
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						repaint();
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							logger.log(Level.SEVERE,
									"Painting thread interrupted: {0}", e);
						}
					}
				}
			}).start();
		} else {
			JOptionPane.showMessageDialog(this, "Warning!",
					"Could not load file " + railMapPath
							+ " .\nPlease specify a new file path!",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Removes all the trains from that needs to be painted on the map.
	 */
	public void removeAllTrains() {
		currentTrains.clear();
	}

	/**
	 * Adds a new train that should be painted on the railway map and the
	 * location where it will be painted.
	 * 
	 * @param train
	 *            an instance of the rail vehicle.
	 * @param location
	 *            place on the rail map where the vehicle will be painted.
	 */
	public void addTrainToPaintList(ITrain train, String location) {
		train.setTrainPosition(coordinates.get(location));
		currentTrains.add(train);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.map != null) {
			mapLoader.buildMap(g, map);
			coordinates = mapLoader.getCoordinatesMap();
			for (ITrain train : currentTrains) {
				train.draw(g);
			}
		}
	}
}
