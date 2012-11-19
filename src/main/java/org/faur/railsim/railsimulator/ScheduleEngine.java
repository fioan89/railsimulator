package org.faur.railsim.railsimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Get train schedule from controller and watch if any modification
 * are needed.
 * 
 * @author faur
 * @since November 21 2012
 */
public class ScheduleEngine implements Runnable {
    private String controllerAddress;
    private int controllerPort;
    private List<String> schedule;

    private boolean isAlive;
    private Logger logger;

    public ScheduleEngine(List<String> schedule, String cntAddress, int cntPort) {
	this.schedule = schedule;
	this.controllerAddress = cntAddress;
	this.controllerPort = cntPort;
	this.isAlive = true;

	logger = Logger.getLogger(ScheduleEngine.class.getName());
    }

    /**
     * Checks if this thread is alive.
     * @return the isAlive
     */
    public synchronized boolean isAlive() {
	return isAlive;
    }

    /**
     * Sets this thread alive or dead.
     * @param isAlive the isAlive to set
     */
    public synchronized void setAlive(boolean isAlive) {
	this.isAlive = isAlive;
    }

    public void run() {
	try {
	    Socket controller = new Socket(InetAddress.getByName(controllerAddress), controllerPort);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(controller.getInputStream()));
	    // First read the number of lines the train schedule has.
	    int tableSize = Integer.parseInt(reader.readLine());
	    String line = "";
	    // Now read the schedule
	    while (tableSize > 0) {
		line = reader.readLine();
		if ((line != null) && (!" ".equals(line))) {
		    schedule.add(line);
		}
		tableSize--;
	    }
	    // Now we got the schedule so we need to start retrieving any
	    // modifications the controller will push.
	    // There are 3 commands:
	    // remove line X
	    // insert line on position X
	    // append line to schedule
	    while (isAlive) {
		line = reader.readLine();
		if (line.startsWith("rm")) {
		    StringTokenizer tk = new StringTokenizer(line, " ");
		    // Trash rm command
		    tk.nextToken();
		    // Get the line number to remove
		    int lineToRm = Integer.parseInt(tk.nextToken());
		    schedule.remove(lineToRm);
		} else if (line.startsWith("insert")) {
		    StringTokenizer tk = new StringTokenizer(line, " ");
		    // Trash insert command
		    tk.nextToken();
		    // Get the line index and the schedule data
		    int lineIndex = Integer.parseInt(tk.nextToken());
		    String data = tk.nextToken();
		    schedule.add(lineIndex, data);
		} else if (line.startsWith("append")) {
		    StringTokenizer tk = new StringTokenizer(line, " ");
		    // Trash append command
		    tk.nextToken();
		    // Get data
		    String data = tk.nextToken();
		    schedule.add(data);
		} else {
		    
		    logger.log(Level.WARNING, "Unknown command:{0}", line );
		}
	    }
	} catch (UnknownHostException e) {
	    logger.log(Level.SEVERE, "Could not connect to {0}:{1}\n {2}", new String[] {controllerAddress, 
		    String.valueOf(controllerPort), e.toString()});
	} catch (IOException e) {
	    logger.log(Level.SEVERE, "Error while trying to read buffer!\n{0}", e.toString());
	}
    }
}

