package org.faur.railsim.centralcontroller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.faur.railsim.util.ScheduleUtil;

/**
 * A servant started by the Master Controller and his main function is to manage
 * client request's.
 * 
 * @author faur
 * @since December 05 2012
 * 
 */
public class ServantController extends Thread {
    private Logger logger;
    private Socket client;
    private List<String> schedule;

    private PrintWriter writer;

    /**
     * Constructs the servant.
     * 
     * @param client
     * @param schedule
     * @param mc
     *            Master Controller reference.
     */
    public ServantController(Socket client, List<String> schedule) {
	this.client = client;
	this.schedule = schedule;
	this.logger = Logger.getLogger(ServantController.class.getName());
    }

    public void run() {
	try {
	    writer = new PrintWriter(new OutputStreamWriter(
		    client.getOutputStream()));
	    // send schedule.
	    ScheduleUtil.sendSchedule(writer, schedule);

	} catch (IOException e) {
	    logger.log(Level.SEVERE, "{0}", e.toString());
	}
    }
}
