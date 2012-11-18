package org.faur.railsim.serversynchronizer;

import java.io.PrintWriter;
import java.net.Socket;
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
 * Synchronization server used by rail simulator to synchronize the simulation.
 * It's working pretty simple: one thread listen for new client connections
 * which are stored in a shared vector. Another thread parses the above client
 * list and send a message to the client's. This message is sent every
 * <code>numberOfMilisBeforeNextMsg</code> milliseconds. By default, the time
 * before next message is one second, but user can specify it's own time in
 * milliseconds. 
 * <br><br>
 * The message contains a <code>String</code>. It's format is very simple: just
 * a number representing a counter. This counter starts with <code>1</code> and
 * is reset after a certain <code>threshold</code> is reached. This
 * <code>threshold</code> is <code>100</code>, but user is able to specify this
 * value at runtime.
 * 
 * @author faur
 * @since November 18 2012
 */
public class SynchronizationServer {
    private long threshold;
    private long timeBeforeNextMsg;
    private int listeningPort;

    private List<Socket> connections;
    private Logger logger;

    public SynchronizationServer() {
	this.setThreshold(100);
	this.setTimeBeforeNextMsg(1000);
	this.setListeningPort(10000);
	this.connections = new Vector<Socket>();
	logger = Logger.getLogger(SynchronizationServer.class.getName());
    }

    /**
     * Gets the <code>threshold</code> after server counter is reset.
     * 
     * @return the threshold
     */
    public long getThreshold() {
	return threshold;
    }

    /**
     * Sets the <code>threshold</code> after server counter is reset.
     * 
     * @param threshold
     *            the threshold to set
     */
    public void setThreshold(long threshold) {
	this.threshold = threshold;
    }

    /**
     * Gets the time before the next message is sent. This time corresponds to
     * the time the server counter is increased.
     * 
     * @return the timeBeforeNextMsg
     */
    public long getTimeBeforeNextMsg() {
	return timeBeforeNextMsg;
    }

    /**
     * Sets the time before the next message is sent. This time corresponds to
     * the time the server counter is increased.
     * 
     * @param timeBeforeNextMsg
     *            the timeBeforeNextMsg to set
     */
    public void setTimeBeforeNextMsg(long timeBeforeNextMsg) {
	this.timeBeforeNextMsg = timeBeforeNextMsg;
    }

    /**
     * @return the listeningPort
     */
    public int getListeningPort() {
	return listeningPort;
    }

    /**
     * @param listeningPort
     *            the listeningPort to set
     */
    public void setListeningPort(int listeningPort) {
	this.listeningPort = listeningPort;
    }

    /**
     * Constructs command line options.
     * 
     * @return Options expected from command-line of Posix form.
     */
    public Options constructPosixOptions() {
	final Options posixOptions = new Options();
	posixOptions.addOption("port", true, "Set listening port for client connections.");
	posixOptions.addOption("threshold", true, "Set the threshold after which the server counter is reset.");
	posixOptions.addOption("signaltime", true, "Set the time in milliseconds after which the server counter is increased and the message is sent.");
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
	    commandLine = cmdLinePosixParser.parse(posixOptions, commandLineArguments);
	    HelpFormatter help = new HelpFormatter();
	    if (commandLine.getArgs().length < 1) {
		help.printHelp(SynchronizationServer.class.getName(), posixOptions, true);		
	    }
	    if (commandLine.hasOption("port")) {
		this.setListeningPort(Integer.parseInt(commandLine.getOptionValue("port")));
	    }
	    if (commandLine.hasOption("threshold")) {
		this.setThreshold(Integer.parseInt(commandLine.getOptionValue("threshold")));
	    }
	    if (commandLine.hasOption("signaltime")) {
		this.setTimeBeforeNextMsg(Integer.parseInt(commandLine.getOptionValue("signal-time")));
	    }
	    if (commandLine.hasOption("help")) {
		help.printHelp(SynchronizationServer.class.getName(), posixOptions, true);
	    }
	} catch (ParseException parseException) {
	    logger.log(Level.SEVERE, "Arguments invalid! Try `synchronization --help` for more information");
	}
    }
    
    public void startSynchronization() {
	SimulatorListener simulatorListener = new SimulatorListener(connections, getListeningPort());
	Thread tListener = new Thread(simulatorListener);
	tListener.start();
	
	SimulatorSynchronizer simulatorSynchronizer = new SimulatorSynchronizer(connections, threshold, timeBeforeNextMsg );
	Thread tSynchronizer = new Thread(simulatorSynchronizer);
	tSynchronizer.start();	
    }

    /**
     * Main method.
     * @param args command line arguments
     */
    public static void main(String[] args) {
	SynchronizationServer ss = new SynchronizationServer();
	ss.usePosixParser(args);
	ss.startSynchronization();
    }

}
