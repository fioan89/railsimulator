package org.faur.railsim.railsimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read's a time counter from the synchronization server and then send commands
 * to railway monitor. In order to compute the next command, the
 * <code>schedule</code> table is used.
 * 
 * @author faur
 * @since November 26 2012
 */
public class SimulationEngine implements Runnable {
    private String syncAddress;
    private String monitorAddress;
    private int syncPort;
    private int monitorPort;
    private boolean isAlive;

    private List<String> schedule;
    private StringBuffer buffer;
    private Logger logger;
    private Socket serverSync;
    private Socket serverMonitor;

    /**
     * Constructs the schedule engine.
     * 
     * @param schedule
     *            table.
     * @param monitorMsg
     *            a buffer shared by the <code>SimulationEngine</code> and
     *            <code>ScheduleEngine>
     * @param monitorAddress
     *            railway monitor network address.
     * @param monitorPort
     *            railway monitor network binder port;
     */
    SimulationEngine(List<String> schedule, StringBuffer monitorMsg,
	    String monitorAddress, int monitorPort) {
	this.schedule = schedule;
	this.monitorAddress = monitorAddress;
	this.monitorPort = monitorPort;
	this.syncPort = 500;
	this.syncAddress = "localhost";
	this.buffer = monitorMsg;
	this.isAlive = true;

	logger = Logger.getLogger(SimulationEngine.class.getName());
    }

    /**
     * Gets synchronization server port used by this engine.
     * 
     * @return the syncPort
     */
    public int getSyncPort() {
	return syncPort;
    }

    /**
     * Sets synchronization server port used by this engine. <br>
     * Please note that this method should be called before
     * {@link Thread#start()} is called, otherwise port 5000 is used.
     * 
     * @param syncPort
     *            the syncPort to set
     */
    public void setSyncPort(int syncPort) {
	this.syncPort = syncPort;
    }

    /**
     * Gets synchronization server address used by this engine.
     * 
     * @return the syncAddress
     */
    public String getSyncAddress() {
	return syncAddress;
    }

    /**
     * Sets synchronization server address used by this engine. <br>
     * Please note that this method should be called before
     * {@link Thread#start()} is called, otherwise <code>localhost</code> is
     * used.
     * 
     * @param syncAddress
     *            the syncAddress to set
     */
    public void setSyncAddress(String syncAddress) {
	this.syncAddress = syncAddress;
    }

    public void run() {
	// First make sure you have a connection with the railway monitor.
	try {
	    List<String> locations = null;
	    serverMonitor = new Socket(this.monitorAddress, this.monitorPort);
	    OutputStreamWriter monitorWr = new OutputStreamWriter(
		    serverMonitor.getOutputStream());
	    serverSync = new Socket(this.syncAddress, this.syncPort);
	    BufferedReader syncRd = new BufferedReader(new InputStreamReader(
		    serverSync.getInputStream()));
	    // Read sync command and then parse table and send command.
	    String syncCommand = "";
	    while (isAlive) {
		syncCommand = syncRd.readLine();
		int time = Integer.parseInt(syncCommand);
		// First check if buffer has a message to deliver
		if (!"no message to monitor".equals(buffer.toString())) {
		    monitorWr.write(buffer.toString() + "\n");
		    monitorWr.flush();
		    buffer.delete(0, buffer.toString().length());
		    buffer.append("no message to monitor");

		}
		int modulo = schedule.size();
		if (schedule.size() > 0) {
		    if (locations == null) {
			locations = getLocations(schedule.get(0));
		    } else if (time % modulo >= 1) {

			String line = schedule.get(time % modulo);
			StringTokenizer token = new StringTokenizer(line, " ");
			int cc = 0;
			StringBuilder command = new StringBuilder();
			while (token.hasMoreTokens()) {
			    String s = token.nextToken();
			    if (!"#".equals(s)) {
				command.append(locations.get(cc)).append("#")
					.append(s).append("#");
			    }
			    cc++;
			}
			command.append("\n");
			monitorWr.write(command.toString());
			monitorWr.flush();
			logger.log(Level.INFO, "Command sent: {0}", command);
		    }
		} else {
		    logger.log(Level.INFO, "Command not sent because too long!");
		}
	    }
	} catch (UnknownHostException e) {
	    logger.log(Level.SEVERE, "Could not establish connection!\n{0}",
		    e.toString());
	} catch (IOException e) {
	    logger.log(Level.SEVERE, "Could not get IO stream!\n{0}",
		    e.toString());
	}
    }

    /**
     * Builds and returns a list of railway locations from a <code>String</code>
     * line. This line contains location names separated by space.
     * 
     * @param line
     * @return
     */
    private synchronized List<String> getLocations(String line) {
	List<String> locations = null;
	if (line != null) {
	    StringTokenizer tk = new StringTokenizer(line, " ");
	    if (tk.countTokens() > 0) {
		locations = new ArrayList<String>();
		while (tk.hasMoreTokens()) {
		    locations.add(tk.nextToken());
		}
	    }
	}
	return locations;
    }
}
