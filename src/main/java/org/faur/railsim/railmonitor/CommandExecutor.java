package org.faur.railsim.railmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gets <code>String</code> commands from the client, usually a rail simulator and executes them.
 * @author faur
 *
 */
public class CommandExecutor extends Thread {
    private TrainManager tManager;
    private RailwayMonitorPanel monitorPanel;
    private Logger logger;

    private Socket listener;
    private boolean isAlive;

    public synchronized void setAlive(boolean isAlive) {
	this.isAlive = isAlive;
    }

    public void setListener(Socket client) {
	this.listener = client;
    }

    public CommandExecutor(TrainManager manager,
	    RailwayMonitorPanel monitorPanel) {
	this.tManager = manager;
	this.monitorPanel = monitorPanel;
	logger = Logger.getLogger(CommandExecutor.class.getName());
	isAlive = true;
    }

    public void run() {
	if (listener != null) {
	    try {
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(listener.getInputStream()));
		while (isAlive) {
		    String line = reader.readLine();
		    logger.log(Level.INFO, "Command received:{0}", line);
		    StringTokenizer token = new StringTokenizer(line, "#");
		    monitorPanel.removeAllTrains();
		    
		    while (token.hasMoreTokens()) {
			String location = token.nextToken();
			int id = Integer.valueOf(token.nextToken());
			ITrain tr = tManager.getTrain(id);
			logger.log(Level.INFO, "Command parsed:Execute " + location + " for train id #" + id);
			monitorPanel.addTrainToPaintList(tr, location);  
		    }
		}
	    } catch (IOException e) {
		logger.log(Level.SEVERE, "Can't get input stream from connection!\n" + e);
	    }
	}

    }

}
