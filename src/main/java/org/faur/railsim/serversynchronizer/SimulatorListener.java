package org.faur.railsim.serversynchronizer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for client connections, in this case railway simulators and insert
 * them in the list. To be noted that List instance should be
 * <code>synchronized</code>. Please see {@link java.util.Vector}
 * 
 * @author faur
 * @since November 18 2012
 * 
 */
public class SimulatorListener implements Runnable {
	private Logger logger;
	private int port;
	private boolean isAlive;
	private List<Socket> connections;
	private ServerSocket server;

	/**
	 * Constructs a client listener with <code>connections</code> param being
	 * the synchronized list where the clients will be inserted.
	 * 
	 * @param connections
	 *            a synchronized list where clients will be inserted.
	 * @param port
	 *            where server will bound.
	 */
	public SimulatorListener(List<Socket> connections, int port) {
		this.connections = connections;
		this.port = port;
		isAlive = true;
		logger = Logger.getLogger(SimulatorListener.class.getName());
	}

	public synchronized boolean isAlive() {
		return isAlive;
	}

	public synchronized void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public void run() {
		try {
			logger.log(Level.INFO, "Waiting for connections on port {0}",
					String.valueOf(port));
			server = new ServerSocket(port);
			while (isAlive) {
				Socket client = server.accept();
				connections.add(client);
				logger.log(
						Level.INFO,
						"Connection accepted  on port {0} for client with address: {1}",
						new String[] { String.valueOf(port),
								client.getInetAddress().toString() });
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not bound server to port:{0}\n{1}",
					new String[] { String.valueOf(port), e.toString() });
		}

	}

}
