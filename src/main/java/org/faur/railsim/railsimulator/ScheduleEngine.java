package org.faur.railsim.railsimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.faur.railsim.util.ScheduleUtil;

/**
 * Get train schedule from controller and watch if any modification are needed.
 * 
 * @author faur
 * @since November 21 2012
 */
public class ScheduleEngine implements Runnable {
    private String controllerAddress;
    private int controllerPort;
    private List<String> schedule;
    private StringBuffer buffer;
    private boolean isAlive;
    private Logger logger;
    private Socket controller;

    public ScheduleEngine(List<String> schedule, StringBuffer monitorMsg,
	    String cntAddress, int cntPort) {
	this.schedule = schedule;
	this.controllerAddress = cntAddress;
	this.controllerPort = cntPort;
	this.buffer = monitorMsg;
	this.isAlive = true;

	logger = Logger.getLogger(ScheduleEngine.class.getName());
    }

    /**
     * Checks if this thread is alive.
     * 
     * @return the isAlive
     */
    public synchronized boolean isAlive() {
	return isAlive;
    }

    /**
     * Sets this thread alive or dead.
     * 
     * @param isAlive
     *            the isAlive to set
     */
    public synchronized void setAlive(boolean isAlive) {
	this.isAlive = isAlive;
    }

    public void run() {
	try {
	    controller = new Socket(InetAddress.getByName(controllerAddress),
		    controllerPort);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
		    controller.getInputStream()));

	    schedule.removeAll(schedule);
	    schedule.addAll(ScheduleUtil.readSchedule(reader));
	    ScheduleUtil.printTable(schedule);
	    // Now we got the schedule so we need to start retrieving any
	    // modifications the controller will push.
	    while (isAlive) {
		// Now the schedule can only be updated if a delay has been
		// identified.
		// But before the new schedule is sent a message signaling the
		// delay is sent.
		String line = reader.readLine();
		if (line.startsWith("delay")) {
		    // Send the message to the monitor
		    buffer.delete(0, buffer.toString().length());
		    buffer.append(line);
		}
		// Get the schedule
		List<String> schd = ScheduleUtil.readSchedule(reader);
		schedule.removeAll(schedule);
		schedule.addAll(schd);
		ScheduleUtil.printTable(schedule);

	    }
	} catch (UnknownHostException e) {
	    logger.log(
		    Level.SEVERE,
		    "Could not connect to {0}:{1}\n {2}",
		    new String[] { controllerAddress,
			    String.valueOf(controllerPort), e.toString() });
	} catch (IOException e) {
	    logger.log(Level.SEVERE, "Error while trying to read buffer!\n{0}",
		    e.toString());
	}
    }
}
