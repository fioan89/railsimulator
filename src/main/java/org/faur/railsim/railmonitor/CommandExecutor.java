package org.faur.railsim.railmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gets <code>String</code> commands from the client, usually a rail simulator
 * and executes them.
 * 
 * @author faur
 * 
 */
public class CommandExecutor extends Thread {
    private TrainManager tManager;
    private RailwayMonitorPanel monitorPanel;
    private ConfigurePanel configPanel;
    private Logger logger;

    private Socket listener;
    private boolean threadAlive;

    public synchronized void setAlive(boolean isAlive) {
	this.threadAlive = isAlive;
    }

    public void setListener(Socket client) {
	this.listener = client;
    }

    public CommandExecutor(TrainManager manager,
	    RailwayMonitorPanel monitorPanel) {
	this.tManager = manager;
	this.monitorPanel = monitorPanel;
	this.threadAlive = true;
	logger = Logger.getLogger(CommandExecutor.class.getName());
    }

    public CommandExecutor(TrainManager manager,
	    RailwayMonitorPanel monitorPanel, ConfigurePanel configPanel) {
	this(manager, monitorPanel);
	this.configPanel = configPanel;

    }

    public void run() {
	if (listener != null) {
	    try {
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(listener.getInputStream()));
		while (threadAlive) {
		    String line = reader.readLine();
		    logger.log(Level.INFO, "Command received:{0}", line);
		    if (line.startsWith("delay")) {
			// delay#trainId#location#timeUnits
			String[] command = line.split("#");
			StringBuilder builder = new StringBuilder(
				"Train with Id ");
			builder.append(command[1])
				.append(" was delayed on location ")
				.append(command[2]);
			builder.append(" with ").append(command[3])
				.append(" time units\n");
			configPanel.addMessage(builder.toString());
		    } else {
			StringTokenizer token = new StringTokenizer(line, "#");
			monitorPanel.removeAllTrains();

			while (token.hasMoreTokens()) {
			    String location = token.nextToken();
			    int id = Integer.valueOf(token.nextToken());
			    ITrain tr = tManager.getTrain(id);
			    logger.log(Level.INFO, "Command parsed:Execute "
				    + location + " for train id #" + id);
			    monitorPanel.addTrainToPaintList(tr, location);
			}
		    }
		}
	    } catch (IOException e) {
		logger.log(Level.SEVERE,
			"Can't get input stream from connection!\n" + e);
	    }
	}

    }

}
