package org.faur.railsim.railmonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

/**
 * Basic UI resource configuration like port where rail monitor should listen for connection,
 * railway map configuration file on the local disk and train id's configuration file.
 * 
 * 
 * @author faur
 * @since November 06 2012
 *
 */
public class ConfigurePanel extends JPanel implements ActionListener {
    private GroupLayout layout;

    private JButton btBrowseMap;
    private JButton btBrowseId;
    private JButton btStart;
    
    private JButton btExit;

    private JTextField tfMapPath;
    private JTextField tfIdPath;
    private JTextField tfPort;

    private JLabel mapLabel;
    private JLabel idLabel;
    private JLabel portLabel;    

    private Logger logger;

    private String mapPath;
    private String idsPath;
    private int port;

    private ActionListener btStartListener;

    public ConfigurePanel(ActionListener listener) {
	super();
	logger = Logger.getLogger(ConfigurePanel.class.getName());
	btStartListener = listener;
	btBrowseMap = new JButton("Browse");
	btBrowseMap.addActionListener(this);
	btBrowseId = new JButton("Browse");
	btBrowseId.addActionListener(this);
	btStart = new JButton("Start Monitoring");
	btStart.setEnabled(false);
	btStart.addActionListener(btStartListener);
	btExit = new JButton("Exit");
	btExit.addActionListener(btStartListener);

	tfMapPath = new JTextField(System.getProperty("user.home"));
	tfIdPath = new JTextField(System.getProperty("user.home"));
	tfPort = new JTextField("5000");
	
	mapLabel = new JLabel("Railway Map path:");
	idLabel = new JLabel("Train Id's path:");
	portLabel = new JLabel("Listening port:");
	init();
    }

    protected void init() {
	layout = new GroupLayout(this);
	this.setLayout(layout);

	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);

	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup()
			.addComponent(mapLabel)
			.addComponent(idLabel)
			.addGroup(layout.createSequentialGroup()
				.addComponent(portLabel)
				.addComponent(tfPort)))
				.addGroup(layout.createParallelGroup()
					.addComponent(tfMapPath)
					.addComponent(tfIdPath)
					.addGroup(layout.createSequentialGroup()
						.addComponent(btStart)
						.addComponent(btExit)))
						.addGroup(layout.createParallelGroup()
							.addComponent(btBrowseMap)
							.addComponent(btBrowseId)));

	layout.linkSize(SwingConstants.HORIZONTAL, btBrowseMap, btBrowseId);
	layout.linkSize(SwingConstants.VERTICAL,tfMapPath,tfIdPath, tfPort, btBrowseMap, btBrowseId, btStart, btExit);

	layout.setVerticalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup()
			.addComponent(mapLabel)
			.addComponent(tfMapPath)
			.addComponent(btBrowseMap))
			.addGroup(layout.createParallelGroup()
				.addComponent(idLabel)
				.addComponent(tfIdPath)
				.addComponent(btBrowseId))
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(portLabel)
						.addComponent(tfPort))
						.addGroup(layout.createParallelGroup()
							.addComponent(btStart)
							.addComponent(btExit))));    
    }

    public void actionPerformed(ActionEvent e) {
	JButton bt = (JButton) e.getSource();
	if (bt.equals(btBrowseMap)) {
	    JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
	    fc.setFileFilter(new FileFilter() {
		public boolean accept(File f) {
		    return f.getName().toLowerCase().endsWith(".csv");
		}

		public String getDescription() {
		    return "CSV files";
		}
	    });
	    
	    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    int returnVal = fc.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		this.setMapPath(file.getAbsolutePath());
		logger.log(Level.INFO, "Map path selected to " + file.getAbsolutePath());
	    } else {
		logger.log(Level.INFO, "Open command cancelled by user.\n");
	    }
	} else if (bt.equals(btBrowseId)) {
	    JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
	    fc.setFileFilter(new FileFilter() {
		public boolean accept(File f) {
		    return f.getName().toLowerCase().endsWith(".csv");
		}

		public String getDescription() {
		    return "CSV files";
		}
	    });
	    
	    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    int returnVal = fc.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		this.setIdsPath(file.getAbsolutePath());
		logger.log(Level.INFO, "Ids path selected to " + file.getAbsolutePath());
	    } else {
		logger.log(Level.INFO, "Open command cancelled by user.\n");
	    }
	}
		
    }

    public String getIdsPath() {
	return idsPath;
    }

    public void setIdsPath(String idsPath) {
	this.idsPath = idsPath;
	this.tfIdPath.setText(idsPath);
	if ((tfIdPath.getText().endsWith(".csv")) && (tfMapPath.getText().endsWith(".csv"))) {
	    btStart.setEnabled(true);
	}
    }

    public String getMapPath() {
	return mapPath;
    }
    
    public void setMapPath(String mapPath) {
	this.mapPath = mapPath;
	this.tfMapPath.setText(mapPath);
	if ((tfIdPath.getText().endsWith(".csv")) && (tfMapPath.getText().endsWith(".csv"))) {
	    btStart.setEnabled(true);
	}
    }
    
    public JButton getBtStart() {
        return btStart;
    }
    public JButton getBtExit() {
        return btExit;
    }
    
    public int getPort() {
        return Integer.valueOf(tfPort.getText());
    }


}
