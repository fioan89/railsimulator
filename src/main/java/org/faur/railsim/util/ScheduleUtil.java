package org.faur.railsim.util;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;

/**
 * Util class for common operations like sending, reading the schedule table.
 * 
 * @author faur
 * @since December 05 2012
 */
public class ScheduleUtil {
    private static Logger logger = Logger.getLogger(ScheduleUtil.class
	    .getName());

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
    public static List<String> buildSchedule(List<String> lines) {
	DefaultTableModel tbModel = new DefaultTableModel();
	if (lines != null) {
	    // Add column names (location names)
	    for (int i = 1; i < lines.size(); i = i + 3) {
		StringTokenizer st = new StringTokenizer((String) lines.get(i),
			" ");
		while (st.hasMoreTokens()) {
		    String column = st.nextToken().trim();
		    if (tbModel.findColumn(column) == -1) {
			tbModel.addColumn(column);
		    }
		}
	    }
	    // build schedule
	    for (int index = 0; index < lines.size(); index = index + 3) {
		String traindId = (String) lines.get(index).trim();
		String[] locations = ((String) lines.get(index + 1)).split(" ");
		String tmpLine = (String) lines.get(index + 2);
		String[] time = tmpLine.split("\t");

		List<String[]> data = new ArrayList<String[]>(3);
		data.add(new String[] { traindId });
		data.add(locations);
		data.add(time);
		if (locations.length == time.length) {
		    int counter = 0;
		    boolean ok = false;
		    while (!ok) {
			ok = checkRow(tbModel, data, counter);
			if (ok) {
			    writeRow(tbModel, data, counter);
			}
			counter++;
		    }
		}
	    }
	    List<String> convertTableToList = (List<String>) convertTableToList(tbModel);
	    return convertTableToList;
	} else {
	    logger.log(Level.WARNING, "Table is null");
	    return null;
	}
    }

    /**
     * Checks if data could be written on the table starting from
     * <code>row</code>.
     * 
     * @param table
     * @param data
     *            a list of data containing the train id, train locations and
     *            their timing.
     * @param row
     *            starting row.
     * @return <code>true</code> if we can schedule the train from the starting
     *         row, <code>false</code> otherwise.
     */
    private static boolean checkRow(DefaultTableModel table,
	    List<String[]> data, int row) {
	String[] locations = data.get(1);
	String[] time = data.get(2);

	int rows = table.getRowCount();
	int cols = table.getColumnCount();
	int counter = 0;
	for (int i = 0; i < locations.length; i++) {
	    if (i - 1 >= 0) {
		counter += Integer.parseInt(time[i - 1].trim());
	    }
	    for (int j = 0; j < Integer.valueOf(time[i].trim()); j++) {
		int column = table.findColumn(locations[i].trim());
		String val = null;
		if ((counter + row + j < rows) && (column < cols)) {
		    val = (String) table.getValueAt(counter + row + j, column);
		}
		// Check for interlocking
		if ((j == (Integer.valueOf(time[i].trim()) - 1))
			&& (counter + row + j + 1 < rows)) {
		    // check if at this time the next location where I will be
		    // it's ocuppied by someone else and at the next unit time
		    // that
		    // resource will ocupy this position
		    if (i + 1 < locations.length) {
			int column1 = table.findColumn(locations[i + 1].trim());
			String val1 = (String) table.getValueAt(counter + row
				+ j, column1);
			// if occupied by val1
			if (val1 != null) {
			    // check next row at this column if is occupied by
			    // val1
			    String val2 = (String) table.getValueAt(counter
				    + row + j + 1, column);
			    if (val1.equals(val2)) {
				// interlocking
				val = new String("interlocking");
			    }
			}
		    }
		}

		if (val != null) {
		    // we found a position occupied so it's no good
		    return false;
		}
	    }
	}
	return true;
    }

    /**
     * Writes data to the table starting from <code>row</code>.
     * 
     * @param table
     * @param data
     *            a list of data containing the train id, train locations and
     *            their timing.
     * @param row
     *            starting row.
     * @return <code>true</code> if we can schedule the train from the starting
     *         row, <code>false</code> otherwise.
     */
    private static boolean writeRow(DefaultTableModel table,
	    List<String[]> data, int row) {
	String trainId = data.get(0)[0].trim();
	String[] locations = data.get(1);
	String[] time = data.get(2);

	int cols = table.getColumnCount();
	int counter = 0;
	for (int i = 0; i < locations.length; i++) {
	    if (i - 1 >= 0) {
		counter += Integer.parseInt(time[i - 1].trim());
	    }
	    for (int j = 0; j < Integer.valueOf(time[i].trim()); j++) {
		int rows = table.getRowCount();
		int column = table.findColumn(locations[i].trim());
		if (counter + row + j >= rows) {
		    String[] tmpData = new String[cols];
		    table.insertRow(rows, tmpData);
		}
		table.setValueAt(new String(trainId), counter + row + j, column);
	    }
	}
	return true;
    }

    /**
     * Returns a list as a table with the first line representing the name of
     * the columns. The other rows represent table data.
     * 
     * @param table
     * @return a synchronized list with table data.
     */
    private static List<String> convertTableToList(DefaultTableModel table) {
	List<String> schedule = new Vector<String>();
	StringBuilder builder = new StringBuilder();

	int numCol = table.getColumnCount();
	int numRow = table.getRowCount();
	// Add column names
	for (int i = 0; i < numCol; i++) {
	    builder.append(table.getColumnName(i)).append(" ");
	}
	schedule.add(builder.toString());
	// build data
	for (int i = 0; i < numRow; i++) {
	    builder = new StringBuilder();
	    for (int j = 0; j < numCol; j++) {
		String s = (String) table.getValueAt(i, j);
		if (s == null) {
		    s = "#";
		}
		s = s.trim();
		builder.append(s).append(" ");
	    }
	    schedule.add(builder.toString());
	}
	return schedule;
    }

    /**
     * Sends schedule to client.
     * 
     * @param writer
     *            client writer stream.
     * @param schedule
     *            railway scheduling table.
     */
    public static void sendSchedule(PrintWriter writer, List<String> schedule) {
	// Tell the client we are sending X number's of lines from the tabel
	StringBuilder b = new StringBuilder();
	b.append("schedule#").append(String.valueOf(schedule.size()))
		.append("\n");
	writer.write(b.toString());
	writer.flush();
	for (int i = 0; i < schedule.size(); i++) {
	    writer.write(schedule.get(i) + "\n");
	    writer.flush();
	}
    }

    /**
     * Reads schedule from server.
     * 
     * @param reader
     * @return a list of <code>String</code> containing the schedule.
     */
    public static List<String> readSchedule(BufferedReader reader) {
	List<String> schedule = new Vector<String>();
	try {
	    String line = reader.readLine();
	    if (line.startsWith("schedule")) {
		int nrOfLines = Integer.parseInt(line.split("#")[1]);
		for (int i = 0; i < nrOfLines; i++) {
		    schedule.add(reader.readLine());
		}
	    }
	} catch (IOException e) {
	    logger.log(Level.SEVERE, "{0}", e.toString());
	}
	return schedule;
    }

    /**
     * Finds the line where <code>trainId</code> first appear on column
     * <code>location</code>.
     * 
     * @param schedule
     * @param location
     * @param trainId
     * @return
     */
    public static int findLineToClone(List<String> schedule, String location,
	    String trainId) {
	// First find the column
	String[] locations = schedule.get(0).split(" ");
	int position = -1;
	for (int i = 0; i < location.length(); i++) {
	    if (location.equals(locations[i])) {
		position = i;
		break;
	    }
	}
	// Now find the first line where train id appears
	if (position != -1) {
	    for (int i = 1; i < schedule.size(); i++) {
		String[] cols = schedule.get(i).split(" ");
		if (trainId.equals(cols[position])) {
		    return i;
		}
	    }
	}
	return -1;
    }

    /**
     * Sends the schedule to a list of clients.
     * 
     * @param clients
     * @param schedule
     */
    public static void updateClients(List<Socket> clients, List<String> schedule) {
	Iterator<Socket> iterator = clients.iterator();
	while (iterator.hasNext()) {
	    Socket client = iterator.next();
	    try {
		synchronized (client) {
		    PrintWriter writer = new PrintWriter(
			    new OutputStreamWriter(client.getOutputStream()));
		    ScheduleUtil.sendSchedule(writer, schedule);
		}
	    } catch (IOException e) {
		logger.log(Level.SEVERE, "{0}", e.toString());
	    }
	}
    }

    /**
     * Sends a message to a list of clients.
     * 
     * @param clients
     * @param message
     */
    public static void updateDelayClients(List<Socket> clients, String message) {
	Iterator<Socket> iterator = clients.iterator();
	while (iterator.hasNext()) {
	    Socket client = iterator.next();
	    try {
		synchronized (client) {
		    PrintWriter writer = new PrintWriter(
			    new OutputStreamWriter(client.getOutputStream()));
		    writer.write(message);
		    writer.flush();
		}
	    } catch (IOException e) {
		logger.log(Level.SEVERE, "{0}", e.toString());
	    }
	}
    }

    /**
     * Prints the message list to the console. Only for debugging purpose.
     * 
     * @param table
     */
    public static void printMessageList(List<String> msgList) {
	Iterator<String> iterator = msgList.iterator();
	logger.log(
		Level.INFO,
		"######################### Printing message list #########################################");
	while (iterator.hasNext()) {
	    logger.log(Level.INFO, "{0}", iterator.next());
	}
	logger.log(
		Level.INFO,
		"##########################################################################################");
    }

    /**
     * Prints the message list to the console. Only for debugging purpose.
     * 
     * @param table
     */
    public static void printMessageList(String[] msgList) {
	logger.log(
		Level.INFO,
		"######################### Printing message list #########################################");
	for (String str : msgList) {
	    logger.log(Level.INFO, "{0}", str);
	}
	logger.log(
		Level.INFO,
		"##########################################################################################");
    }

    /**
     * Prints the message list to the console. Only for debugging purpose.
     * 
     * @param table
     * @param separator
     *            code between messages
     */
    public static void printMessageList(String[] msgList, String separator) {
	logger.log(
		Level.INFO,
		"######################### Printing message list #########################################");
	for (String str : msgList) {
	    logger.log(Level.INFO, "{0}{1}{2}", new String[] { separator, str,
		    separator });
	}
	logger.log(
		Level.INFO,
		"##########################################################################################");
    }

    /**
     * Prints the schedule table to the console. Only for debugging purpose.
     * 
     * @param table
     */
    public static void printTable(List<String> table) {
	int nrRows = table.size();
	logger.log(
		Level.INFO,
		"######################### Printing Table #################################################");
	for (int row = 0; row < nrRows; row++) {
	    String[] s = table.get(row).split(" ");
	    for (int col = 0; col < s.length; col++) {
		logger.log(Level.INFO, "{0}", s[col] + "\t");
	    }
	    logger.log(Level.INFO, "\n");
	}
	logger.log(
		Level.INFO,
		"##########################################################################################");
    }
}
