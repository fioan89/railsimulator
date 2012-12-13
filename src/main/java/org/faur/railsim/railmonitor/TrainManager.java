package org.faur.railsim.railmonitor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager for the rail vehicles.
 * 
 * @author faur
 * @since November 14 2012
 */
public class TrainManager {
	private static volatile TrainManager trainManager;

	private List<ITrain> trainList;
	private Logger logger;

	private TrainManager() {
		trainList = new ArrayList<ITrain>();
		logger = Logger.getLogger(TrainManager.class.getName());
	}

	/**
	 * Creates and returns an instance for this class.
	 * 
	 * @return {@link TrainManager} instance.
	 */
	public static TrainManager getInstance() {
		if (trainManager == null) {
			synchronized (TrainManager.class) {
				if (trainManager == null) {
					trainManager = new TrainManager();
				}
			}
		}
		return trainManager;
	}

	/**
	 * Adds a new rail vehicle to the train manager.
	 * 
	 * @param id
	 *            vehicle identification number.
	 */
	public synchronized void addTrain(int id) {
		ITrain t = new Train();
		t.setTrainId(id);
		trainList.add(t);
	}

	/**
	 * Searches for the vehicle with the specified identification number and if
	 * it finds it return it's instance. Otherwise just return <code>null</code>
	 * .
	 * 
	 * @param id
	 * @return
	 */
	public synchronized ITrain getTrain(int id) {
		for (ITrain t : trainList) {
			if (id == t.getTrainId()) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Loads train id's from a .csv file which is encoded in UTF-8 and uses
	 * spaces as a separator. With the loaded id's it will instantiate the train
	 * vehicles.
	 * 
	 * @param file
	 *            <code>.csv</code> location.
	 */
	public void loadTrains(String file) {
		if (file != null) {
			BufferedReader reader = null;
			String line = "";
			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
				while ((line = reader.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(line, " ");
					while (st.hasMoreTokens()) {
						int id = Integer.valueOf(st.nextToken(), 10);
						trainList.add(new Train(id));
						logger.log(Level.INFO, "New rail vehicle with id #"
								+ String.valueOf(id));
					}
				}
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
	}
}
