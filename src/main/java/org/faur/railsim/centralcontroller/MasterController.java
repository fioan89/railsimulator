package org.faur.railsim.centralcontroller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.faur.railsim.util.ScheduleUtil;

/**
 * Build a train schedule from a list of locations for a certain train id. This
 * schedule is sent to the simulator. If a delay is added to the schedule,
 * <code>MasterController</code> will deal with the delay by changing the table.
 * Of course, every simulator is instructed to do the same set of changes.
 * 
 * @author faur
 * @since December 04 2012
 * @param <T>
 *            a generic parameter, most usually a <code>String</code>.
 */
public class MasterController<T> {
    private List<T> locations;
    private Logger logger;
    private int port;
    private String fileLocation;

    ServerSocket server;
    private List<Socket> clients;
    private boolean isAlive;

    public MasterController() {
	this.setLocations(new ArrayList<T>());
	this.logger = Logger.getLogger(MasterController.class.getName());
	this.setPort(15000);
	clients = new Vector<Socket>();
	setAlive(true);
    }

    /**
     * @return the port
     */
    public int getPort() {
	return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
	this.port = port;
    }

    /**
     * @return the fileLocation
     */
    public String getFileLocation() {
	return fileLocation;
    }

    /**
     * @param fileLocation
     *            the fileLocation to set
     */
    public void setFileLocation(String fileLocation) {
	this.fileLocation = fileLocation;
    }

    /**
     * Constructs command line options.
     * 
     * @return Options expected from command-line of Posix form.
     */
    public Options constructPosixOptions() {
	final Options posixOptions = new Options();
	posixOptions.addOption("port", true,
		"Port binder for client connections.");
	posixOptions.addOption("file", true, "Railway route location.");
	posixOptions.addOption("help", false, "Display help page.");
	return posixOptions;
    }

    /**
     * Apply Apache Commons CLI PosixParser to command-line arguments.
     * 
     * @param commandLineArguments
     *            Command-line arguments to be processed with Posix-style
     *            parser.
     */
    public void usePosixParser(final String[] commandLineArguments) {
	final CommandLineParser cmdLinePosixParser = new PosixParser();
	final Options posixOptions = constructPosixOptions();
	CommandLine commandLine;
	try {
	    commandLine = cmdLinePosixParser.parse(posixOptions,
		    commandLineArguments);
	    HelpFormatter help = new HelpFormatter();
	    if (commandLine.getArgs().length < 1) {
		help.printHelp(MasterController.class.getName(), posixOptions,
			true);
	    }
	    if (commandLine.hasOption("port")) {
		this.setPort(Integer.parseInt(commandLine
			.getOptionValue("port")));
	    }
	    if (commandLine.hasOption("file")) {
		this.setFileLocation(commandLine.getOptionValue("file"));
	    }
	    if (commandLine.hasOption("help")) {
		help.printHelp(MasterController.class.getName(), posixOptions,
			true);
	    }
	} catch (ParseException parseException) {
	    logger.log(Level.SEVERE,
		    "Arguments invalid! Try `mastercontroller --help` for more information");
	}
    }

    /**
     * Loads desired routes from a file, which is in this format:
     * 
     * <pre>
     * T1
     * P1	P2	P3	I1	I3	P6
     * [1,3]	[2,3]	[1,3]	[5,6]   [3,6]	[2,4]
     * </pre>
     * 
     * First line represent's the train id. Second line represent's the fixed
     * route that the train vehicle will visit. And the last one is the minimum
     * and the maximum time, the vehicle will take in order to visit the
     * corresponding location.
     * 
     * @param file a UTF-8 encoded file which contains a list of trains routes.
     * @return a list representing the file lines with one minor change: every
     *         third line will be a fixed time for every location, that fits in
     *         the location's time interval which was read from the file.
     */
    public List<T> loadRouteFromFile(String file) {
	BufferedReader reader = null;
	List<T> lines = new ArrayList<T>();
	try {
	    reader = new BufferedReader(new InputStreamReader(
		    new FileInputStream(file), "UTF-8"));
	    int counter = 0;
	    String s;
	    while ((s = reader.readLine()) != null) {
		counter++;
		// If we found the "time" line, then, parse it and set a
		// random number for each location. This random number is set
		// between the
		// intervals each location has.
		if (counter == 3) {
		    counter = 0;
		    StringTokenizer st = new StringTokenizer(s, " ");
		    StringBuilder sb = new StringBuilder("");
		    while (st.hasMoreTokens()) {
			String ss = st.nextToken();
			int val1 = Integer.parseInt(ss.substring(1,
				ss.indexOf(",")));
			int val2 = Integer.parseInt(ss.substring(
				ss.indexOf(",") + 1, ss.length() - 1));
			sb.append(
				String.valueOf((int) Math.random()
					* (val2 - val1) + val1)).append("\t");
		    }
		    lines.add((T) sb.toString());
		} else {
		    lines.add((T) s);
		}
	    }
	} catch (FileNotFoundException e) {
	    logger.log(Level.SEVERE, "Could not  open file {0}!\n{1}",
		    new String[] { file, e.toString() });
	} catch (NumberFormatException e) {
	    logger.log(Level.SEVERE, "{0}", e.toString());
	} catch (IOException e) {
	    logger.log(Level.SEVERE, "Could not read from file!\n{0}", e.toString());
	} finally {
	    try {
		reader.close();
	    } catch (IOException e) {
		logger.log(Level.SEVERE, "Could not close file {0}!\n{1}",
			new String[] { file, e.toString() });
	    }
	}
	return lines;
    }

    /**
     * Builds a schedule from a list of lines that have this format:
     * 
     * <pre>
     * T1
     * P1	P2	P3	I1	I3	P6
     * 2	3	2	5       3	4
     * T2
     * P1	I2	I3	I1	P5	P6
     * 2	2	1	4       4	3  
     * .		.	.	.	.	.
     * .		.	.	.	.	.
     * .		.	.	.	.	.
     * </pre>
     * 
     * First line represent's the train id. Second line represent's the fixed
     * route that the train vehicle will visit. And the last one is the minimum
     * and the maximum time, the vehicle will take in order to visit the
     * corresponding location.
     * 
     * @param lines
     * @return
     */
    public List<T> createSchedule(List<T> lines) {
	return (List<T>) ScheduleUtil.buildSchedule((List<String>) lines);
    }

    /**
     * @return the isAlive
     */
    public boolean isAlive() {
	return isAlive;
    }

    /**
     * @param isAlive
     *            the isAlive to set
     */
    public void setAlive(boolean isAlive) {
	this.isAlive = isAlive;
    }

    public List<Socket> getClients() {
	return clients;
    }

    public List<T> getLocations() {
	return locations;
    }

    public void setLocations(List<T> locations) {
	this.locations = locations;
    }

    /**
     * Starts Master Controller job. Mainly consisting of actions like sending
     * the scheduling table to the client controller's and assisting them in the
     * action of modifying the railway schedule.
     * 
     * @param schedule
     * @param routes
     */
    public void startServer(List<String> schedule, List<String> routes) {
	DelayManager manager = new DelayManager(clients, schedule, routes);
	manager.start();
	try {
	    server = new ServerSocket(this.port);
	    while (isAlive) {
		clients.add(server.accept());
		logger.log(Level.INFO, "Client connection accepted!");
		// get the last connection added
		new ServantController(clients.get(clients.size() - 1), schedule)
			.start();
		logger.log(Level.INFO, "ServantController started!");
	    }
	} catch (IOException e) {
	    logger.log(Level.SEVERE,
		    "Problem trying to bind on port {0}!\n{1}", new String[] {
			    String.valueOf(port), e.toString() });
	}

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	MasterController<String> mc = new MasterController<String>();
	mc.usePosixParser(args);
	List<String> lines = mc.loadRouteFromFile(mc.getFileLocation());
	List<String> table = mc.createSchedule(lines);
	mc.startServer(table, lines);
    }

}
