package org.faur.railsim.railmonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main Railway Monitor
 * 
 * @author faur
 * @since November 14 20120
 */
public class RailwayMonitor extends JFrame implements ActionListener {
	ConfigurePanel configurePn;
	RailwayMonitorPanel monitorPn;

	Logger logger;
	GroupLayout layout;
	ConnectionManager con;

	public RailwayMonitor(String name) {
		super(name);

		logger = Logger.getLogger(RailwayMonitor.class.getName());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}

	protected void init() {
		this.setVisible(true);
		this.setSize(800, 600);
		layout = new GroupLayout(this.getContentPane());
		configurePn = new ConfigurePanel(this);
		monitorPn = new RailwayMonitorPanel(null);

		// occupy 70% of the screen width
		int monitorWidth = (int) (8 * this.getWidth()) / 10;
		this.setLayout(layout);
		// Turn on automatically adding gaps between components
		layout.setAutoCreateGaps(true);
		// Turn on automatically creating gaps between components that touch
		// the edge of the container and the container.
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(monitorPn, monitorWidth, monitorWidth,
						Short.MAX_VALUE).addComponent(configurePn));

		layout.setVerticalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup().addComponent(monitorPn))
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(configurePn)));
		this.pack();
	}

	public void actionPerformed(ActionEvent e) {
		JButton bt = (JButton) e.getSource();
		if (bt.equals(configurePn.getBtStart())) {
			String mapPath = "/home/faur/railsimulator/rail1.csv";// configurePn.getMapPath();
			String idPath = "/home/faur/railsimulator/ids.csv";// configurePn.getIdsPath();
			monitorPn.setRailMapPath(mapPath);
			monitorPn.startMonitoring();
			TrainManager tManager = TrainManager.getInstance();
			tManager.loadTrains(idPath);
			con = new ConnectionManager(tManager, monitorPn, configurePn);
			con.start();
		} else if (bt.equals(configurePn.getBtExit())) {
			if (con != null) {
				con.setAlive(false);
			}
			logger.log(Level.WARNING, "Railway Monitor is going to exit!");
			this.dispose();
			System.exit(0);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (UnsupportedLookAndFeelException e) {
			// don't care too much
		} catch (ClassNotFoundException e) {
			// don't care too much
		} catch (InstantiationException e) {
			// don't care too much
		} catch (IllegalAccessException e) {
			// don't care too much
		}

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new RailwayMonitor("Railway Simulator");
			}

		});

	}
}
