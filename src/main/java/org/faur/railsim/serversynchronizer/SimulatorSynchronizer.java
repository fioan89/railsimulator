package org.faur.railsim.serversynchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Send a counter to every client in the connections list.
 * In order to provide speed improvements output streams are
 * cached.
 *  
 * @author faur
 * @since November 15 2012
 *
 */
public class SimulatorSynchronizer implements Runnable {
    private Logger logger;
    private boolean isAlive;
    private List<Socket> connections;
    private long thresholding;
    private long signaltime;
    private Map<Socket, PrintWriter> streams;
    
    /**
     * Constructs a synchronization server with the specified threshold and signaltime
     * 
     * @param connections a synchronized list of clients.
     * @param thresholding a value after which the server counter is reset.
     * @param signaltime time between two synchronization messages.
     */
    public SimulatorSynchronizer(List<Socket> connections, long threshold, long signaltime) {
	this.connections = connections;
	this.thresholding = threshold;
	this.signaltime = signaltime;
	streams = new HashMap<Socket, PrintWriter>();
    }

    /**
     * @return the isAlive
     */
    public boolean isAlive() {
	return isAlive;
    }

    /**
     * @param isAlive the isAlive to set
     */
    public void setAlive(boolean isAlive) {
	this.isAlive = isAlive;
    }

    public void run() {
	int counter = 0;
	while (isAlive) {
	    counter++;
	    if (counter > thresholding) {
		counter = 0;
		logger.log(Level.INFO, "Reseting counter");
	    }
	    logger.log(Level.INFO, "Sending synchronization message number {0}", String.valueOf(counter));
	    for (Socket client : connections) {
		// for speed reasons keep the out stream in a hashmap
		if (!streams.containsKey(client)) {
		    try {
			streams.put(client, new PrintWriter(client.getOutputStream()));
		    } catch (IOException e) {
			logger.log(Level.SEVERE, "Could not get output stream!\n {0}", e.toString());
		    }
		}
		PrintWriter writer = streams.get(client);
		if (writer != null) {
		    writer.print(String.valueOf(counter) + "\n");
		    writer.flush();
		}
		
	    }
	    try {
		Thread.sleep(signaltime);
	    } catch (InterruptedException e) {
		logger.log(Level.WARNING, "Thread could not sleep!\n {0}", e.toString());
	    }
	}
	
    }

}
