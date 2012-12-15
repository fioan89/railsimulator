package org.faur.railsim.centralcontroller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.faur.railsim.util.ScheduleUtil;

/**
 * This class deals with delays that were sent from the standard system input.
 * When the command is sent, <code>DelayManager</code> will update the schedule
 * and inform the clients, normally a list of simulators that the schedule was
 * modified.
 * 
 * @author faur
 * @since December 12 2012
 */
public class DelayManager extends Thread {
    private List<Socket> clients;
    private List<String> schedule;
    private List<String> routes;

    private boolean threadAlive;
    private Logger logger;

    /**
     * Constructs the <code>DelayManager</code> with the specified params.
     * 
     * @param clients
     *            a list of clients connected to the master controller. This
     *            list must by synchronized, ex:use {@link java.util.Vector}
     * @param schedule
     *            a list an already built schedule. This list must by
     *            synchronized, ex:use {@link java.util.Vector}
     * @param routes
     *            a list of trains id's their routes and the timings.
     */
    public DelayManager(List<Socket> clients, List<String> schedule,
	    List<String> routes) {
	this.clients = clients;
	this.schedule = schedule;
	this.routes = routes;
	this.setThreadAlive(true);
	logger = Logger.getLogger(DelayManager.class.getName());
    }

    @Override
    public void run() {
	BufferedReader reader = null;
	PrintWriter writer = null;
	reader = new BufferedReader(new InputStreamReader(System.in));
	writer = new PrintWriter(new OutputStreamWriter(System.out));
	String line = null;
	while (threadAlive) {
	    try {
		// read a delay command. It should be in this format:
		// delay <trainId> on <locationId> with <x>
		// or maybe you want to quit. If so, the command should be: quit
		// or exit
		writer.write(">>>");
		writer.flush();
		line = reader.readLine();
		if ("exit".equals(line) || "quit".equals(line)) {
		    setThreadAlive(false);
		} else if (line.startsWith("delay")) {
		    // Send every client a message telling the train with id <x>
		    // is delayed in
		    // location <y> with <z> time unit
		    String[] command = line.split(" ");
		    if (command.length >= 6) {
			StringBuilder builder = new StringBuilder();
			// delay#trainId#location#timeUnit
			builder.append("delay#").append(command[1]).append("#")
				.append(command[3]).append("#")
				.append(command[5]).append("\n");

			// Build the new schedule and send it, but first we need
			// to modify the route.
			// Find the line with ID == command[1]
			int trainIndex = -1;
			for (int i = 0; i <= routes.size() - 3; i += 3) {
			    if (command[1].equals(routes.get(i).trim())) {
				trainIndex = i;
				break;
			    }
			}
			// find the location index
			int locationIndex = -1;
			if (trainIndex != -1) {
			    String[] loc = routes.get(trainIndex + 1)
				    .split(" ");
			    for (int i = 0; i < loc.length; i++) {
				if (command[3].equals(loc[i].trim())) {
				    locationIndex = i;
				    break;
				}
			    }
			}

			// increment that timing with <z> time units.
			if (trainIndex != -1 && locationIndex != -1) {
			    String[] timings = routes.get(trainIndex + 2)
				    .split("\t");
			    timings[locationIndex] = String.valueOf(Integer
				    .parseInt(timings[locationIndex].trim())
				    + Integer.valueOf(command[5].trim()));
			    StringBuilder timing = new StringBuilder();
			    for (int i = 0; i < timings.length; i++) {
				timing.append(timings[i].trim()).append("\t");
			    }
			    routes.set(trainIndex + 2, timing.toString());
			    // Compute schedule
			    schedule.removeAll(schedule);
			    schedule.addAll(ScheduleUtil.buildSchedule(routes));
			    // Send delay signal to clients
			    ScheduleUtil.updateDelayClients(clients,
				    builder.toString());
			    logger.log(Level.INFO, "Message sent:{0}",
				    builder.toString());
			    // Send schedule to everyone
			    ScheduleUtil.updateClients(clients, schedule);
			}
		    }
		}
	    } catch (IOException e) {
		logger.log(Level.SEVERE, "{0}", e);
	    }

	}
    }

    public synchronized boolean isThreadAlive() {
	return threadAlive;
    }

    public synchronized void setThreadAlive(boolean threadAlive) {
	this.threadAlive = threadAlive;
    }

}
