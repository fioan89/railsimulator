package org.faur.railsim.railmonitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server that listens for simulator clients. If a connection is found, the
 * manager will start a command executor associated with the client.
 * 
 * @author faur
 * @since November 15 2012
 */
public class ConnectionManager extends Thread {
    private TrainManager tManager;
    private RailwayMonitorPanel monitorPanel;
    private ConfigurePanel configPanel;
    private Logger logger;

    private int port;
    private Socket listener;
    private boolean isAlive;

    private Vector<CommandExecutor> c;
    private ServerSocket server;

    public ConnectionManager(TrainManager manager,
	    RailwayMonitorPanel monitorPanel, int port) {
	this.tManager = manager;
	this.monitorPanel = monitorPanel;
	this.port = port;
	c = new Vector<CommandExecutor>();
	logger = Logger.getLogger(CommandExecutor.class.getName());
	isAlive = true;
    }

    public ConnectionManager(TrainManager manager,
	    RailwayMonitorPanel monitorPanel, ConfigurePanel configPanel) {
	this(manager, monitorPanel, 0);
	this.port = configPanel.getPort();
	this.configPanel = configPanel;
    }

    /**
     * Sets this thread alive or dead.
     * 
     * @param isAlive
     */
    public synchronized void setAlive(boolean isAlive) {
	this.isAlive = isAlive;
	for (CommandExecutor ce : c) {
	    ce.setAlive(isAlive);
	}
    }

    public void run() {
	try {
	    logger.log(Level.SEVERE, "Listening for connection on port "
		    + String.valueOf(port));
	    server = new ServerSocket(port);
	    while (isAlive) {
		Socket client = server.accept();
		logger.log(Level.INFO, "A new connection accepted!");
		CommandExecutor cc = new CommandExecutor(tManager,
			monitorPanel, configPanel);
		cc.setListener(client);
		cc.start();
		c.add(cc);
	    }
	} catch (IOException e) {
	    logger.log(Level.SEVERE,
		    "Can't listen on port " + String.valueOf(port) + "\n" + e);
	}

    }

    /**
     * @return the listener
     */
    public Socket getListener() {
	return listener;
    }

    /**
     * @param listener
     *            the listener to set
     */
    public void setListener(Socket listener) {
	this.listener = listener;
    }

}
