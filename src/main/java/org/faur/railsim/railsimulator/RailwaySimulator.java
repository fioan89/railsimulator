package org.faur.railsim.railsimulator;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Railway Simulator engine. The mechanics for this engine are very simple.
 * First the simulator retrieve the train's schedule from controller and then
 * start's listening for any schedule modifications (there's a possibility that
 * a vehicle got a delay. Therefore the controller needs to update the schedule
 * and resolve conflicts. The simulator needs to be aware of them.) <br>
 * <br>
 * Every time the simulator get's a signal from the synchronization server, it
 * will send a command to the monitor in order to visualize the result.
 * 
 * @author faur
 * @since November 19 2012
 * 
 */
public class RailwaySimulator {
	private String controllerAddress;
	private int controllerPort;
	private String syncServerAddress;
	private int syncServerPort;
	private String monitorAddress;
	private int monitorPort;

	private Logger logger;
	private List<String> schedule;

	/**
	 * Constructs the railway simulator.
	 */
	public RailwaySimulator() {
		this.controllerAddress = "localhost";
		this.controllerPort = 9000;
		this.syncServerAddress = "localhost";
		this.syncServerPort = 10000;
		this.monitorAddress = "localhost";
		this.monitorPort = 5000;
		schedule = new Vector<String>();
		logger = Logger.getLogger(RailwaySimulator.class.getName());

	}

	/**
	 * Gets the controller network address.
	 * 
	 * @return the controllerAddress
	 */
	public String getControllerAddress() {
		return controllerAddress;
	}

	/**
	 * Sets the controller network address.
	 * 
	 * @param controllerAddress
	 *            the controllerAddress to set
	 */
	public void setControllerAddress(String controllerAddress) {
		this.controllerAddress = controllerAddress;
	}

	/**
	 * Gets the controller port.
	 * 
	 * @return the controllerPort
	 */
	public int getControllerPort() {
		return controllerPort;
	}

	/**
	 * Sets the controller port.
	 * 
	 * @param controllerPort
	 *            the controllerPort to set
	 */
	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;
	}

	/**
	 * Gets the synchronization server address.
	 * 
	 * @return the syncServerAddress
	 */
	public String getSyncServerAddress() {
		return syncServerAddress;
	}

	/**
	 * Sets the controller synchronization server adress.
	 * 
	 * @param syncServerAddress
	 *            the syncServerAddress to set
	 */
	public void setSyncServerAddress(String syncServerAddress) {
		this.syncServerAddress = syncServerAddress;
	}

	/**
	 * Gets the synchronization server port.
	 * 
	 * @return the syncServerPort
	 */
	public int getSyncServerPort() {
		return syncServerPort;
	}

	/**
	 * Sets the synchronization server port.
	 * 
	 * @param syncServerPort
	 *            the syncServerPort to set
	 */
	public void setSyncServerPort(int syncServerPort) {
		this.syncServerPort = syncServerPort;
	}

	/**
	 * Gets the monitor server port.
	 * 
	 * @return the syncServerPort
	 */
	public int getMonitorPort() {
		return monitorPort;
	}

	/**
	 * Sets the monitor server port.
	 * 
	 * @param syncServerPort
	 *            the syncServerPort to set
	 */
	public void setMonitorPort(int monitorPort) {
		this.monitorPort = monitorPort;
	}

	/**
	 * Gets the railway monitor server address.
	 * 
	 * @return the syncServerAddress
	 */
	public String getMonitorAddress() {
		return monitorAddress;
	}

	/**
	 * Sets the railway monitor server adress.
	 * 
	 * @param syncServerAddress
	 *            the syncServerAddress to set
	 */
	public void setMonitorAddress(String monitorAddress) {
		this.monitorAddress = monitorAddress;
	}

	/**
	 * Constructs command line options.
	 * 
	 * @return Options expected from command-line of Posix form.
	 */
	public Options constructPosixOptions() {
		final Options posixOptions = new Options();
		posixOptions.addOption("controllerPort", true,
				"Controller's port binder.");
		posixOptions.addOption("controllerAddress", true,
				"Controller's network address.");
		posixOptions.addOption("syncServerPort", true,
				"Synchronization server port binder.");
		posixOptions.addOption("syncServerAddress", true,
				"Synchronization server network address.");
		posixOptions.addOption("monitorPort", true,
				"Railway Monitor port binder.");
		posixOptions.addOption("monitorAddress", true,
				"Railway Monitor network address.");
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
				help.printHelp(RailwaySimulator.class.getName(), posixOptions,
						true);
			}
			if (commandLine.hasOption("controllerPort")) {
				this.setControllerPort(Integer.valueOf(commandLine
						.getOptionValue("controllerPort")));
			}
			if (commandLine.hasOption("controllerAddress")) {
				this.setControllerAddress(commandLine
						.getOptionValue("controllerAddress"));
			}
			if (commandLine.hasOption("synServerPort")) {
				this.setSyncServerPort(Integer.valueOf(commandLine
						.getOptionValue("syncServerPort")));
			}
			if (commandLine.hasOption("syncServerAddress")) {
				this.setSyncServerAddress(commandLine
						.getOptionValue("syncServerAddress"));
			}
			if (commandLine.hasOption("monitorPort")) {
				this.setMonitorPort(Integer.valueOf(commandLine
						.getOptionValue("monitorPort")));
			}
			if (commandLine.hasOption("monitorAddress")) {
				this.setMonitorAddress(commandLine
						.getOptionValue("monitorAddress"));
			}
			if (commandLine.hasOption("help")) {
				help.printHelp(RailwaySimulator.class.getName(), posixOptions,
						true);
			}
		} catch (ParseException parseException) {
			logger.log(Level.SEVERE,
					"Arguments invalid! Try `synchronization --help` for more information");
		}
	}

	/**
	 * Starts railway simulation.
	 */
	public void startSimulationEngine() {
		// Start schedule engine
		StringBuffer buffer = new StringBuffer("no message to monitor");
		ScheduleEngine schEngine = new ScheduleEngine(schedule, buffer,
				controllerAddress, controllerPort);
		Thread tSchEngine = new Thread(schEngine);
		tSchEngine.start();
		// Start simulation engine
		SimulationEngine simEngine = new SimulationEngine(schedule, buffer,
				monitorAddress, monitorPort);
		simEngine.setSyncAddress(syncServerAddress);
		simEngine.setSyncPort(syncServerPort);
		Thread tSimEngine = new Thread(simEngine);
		tSimEngine.start();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RailwaySimulator simulator = new RailwaySimulator();
		simulator.usePosixParser(args);
		simulator.startSimulationEngine();
	}

}
