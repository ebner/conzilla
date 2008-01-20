/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.install;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.Conzilla;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.util.ErrorMessage;

/*
 * Needed resource structure:
 * 
 * |-- components | `-- types | `-- library | `-- generic | |-- bookmarks | |--
 * root | `-- templates `-- install |-- bookmarks | `-- bookmarks |--
 * resolver.xml |--.local | |-- Resources_map | `-- Message |-- root | `-- root
 * `-- templates `-- templates
 * 
 */

public class Installer implements Runnable {
	
	public static final String LOCALDIR = "local";

	public static final String ONTDIR = "ont";

	public static final String CONFIG_FILE = "conzilla.properties";
	
	public static final String LOG_CONFIG_FILE = "log4j.properties";

	static URI configURI;

	URI globalResolver;

	URI localResolver;

	URI rootLibrary;

	String version;

	static File configFilePath;

	public static URI getConfigURI() {
		if (configURI != null) {
			return configURI;
		}

		String userDir = System.getProperty("user.home");
		String conzillaDir;

		if (File.separatorChar == '/') {
			// UNIX etc.
			conzillaDir = ".conzilla2";
		} else {
			// Others
			conzillaDir = "Conzilla2";
		}

		configFilePath = new File(userDir + File.separatorChar + conzillaDir + File.separatorChar + CONFIG_FILE);
		
		//configURI = FileURL.getFileURL(configFilePath.toString());
		configURI = configFilePath.toURI();
		
		return configURI;
	}

	public static File getConzillaDir() {
		getConfigURI();
		return configFilePath.getParentFile();
	}

	boolean stopInstall;

	JTextArea logArea;

	JButton okBut;

	boolean startConzilla = false;

	private Installer(JTextArea logArea, JButton okBut) {
		this.logArea = logArea;
		this.okBut = okBut;
		stopInstall = false;
	}

	public static void installOrExit(String why) {
//		Object[] options = { "Yes, install", "No, exit." };
//		int result = JOptionPane.showOptionDialog(null, why + "\n\n"
//				+ "Conzilla would need to install files in the directory\n" + "\n" + getConzillaDir() + "\n" + "\n"
//				+ "to be able to start. Do you want to do this now?",
//				"Install?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
//				options, // the titles of buttons
//				options[0]); // default button title

//		if (result == JOptionPane.YES_OPTION)
			startInstall();
//		else
//			System.exit(0);
	}

	public static void startInstall() {
		final JDialog dialog = new JDialog((JFrame) null, "Install?", true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		dialog.setSize(new Dimension(500, 300));
		dialog.setLocationRelativeTo(null);

		Container panel = dialog.getContentPane();
		panel.setLayout(new BorderLayout());

		JTextArea logArea = new JTextArea();
		logArea.setEditable(false);

		JScrollPane scroll = new JScrollPane(logArea);
		scroll.setBorder(BorderFactory.createTitledBorder("Install log"));

		panel.add(scroll, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton ok = new JButton("Start!");
		JButton cancel = new JButton("Exit now");

		ok.setEnabled(false);
		buttons.add(ok);
		buttons.add(cancel);

		panel.add(buttons, BorderLayout.SOUTH);

		final Installer install = new Installer(logArea, ok);
		final Thread installThread = new Thread(install, "Install");

		cancel.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent action) {
				dialog.dispose();
			}
		});
		ok.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent action) {
				install.startConzilla = true;
				dialog.dispose();
			}
		});

		dialog.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				installThread.start();
			}
		});

		dialog.setVisible(true);

		install.stopInstall(installThread);

		if (!install.startConzilla) {
			System.exit(0);
		}
	}

	synchronized void stopInstall(Thread installThread) {
		if (installThread.isAlive()) {
			stopInstall = true;
			try {
				wait(7000);
			} catch (InterruptedException e) {
			}
			installThread.interrupt();
		}
	}

	// Methods only used in install thread...

	public void run() {
		try {
			Thread.sleep(500);

			logArea.append("Starting install\n");

			File conzillaDir = getConzillaDir();
			makeDir(conzillaDir);

			makeDir(new File(conzillaDir, LOCALDIR));

			makeDir(new File(conzillaDir, ONTDIR));

			File localDir = new File(conzillaDir, LOCALDIR);
			File ontDir = new File(conzillaDir, ONTDIR);
			File configFile = new File(conzillaDir, CONFIG_FILE);
			File logConfigFile = new File(conzillaDir, LOG_CONFIG_FILE);
			
			installFile(ONTDIR + "/defaultmenu.rdf", new File(ontDir, "defaultmenu.rdf"));
			installFile(ONTDIR + "/defaultstyle.rdf", new File(ontDir, "defaultstyle.rdf"));
			installFile(ONTDIR + "/sessions.rdf", new File(ontDir, "sessions.rdf"));
			installFile(LOCALDIR + "/providedEditableModel.rdf", new File(localDir, "providedEditableModel.rdf"));
			installFile(CONFIG_FILE, configFile);
			installFile(LOG_CONFIG_FILE, logConfigFile);

			logArea.append("Creating default settings...");
			Config config = ConfigurationManager.getConfiguration();
			try {
				//config.load(getInstallResourceURL(CONFIG_FILE));
				config.load(configFile.toURI().toURL());
			} catch (IOException e) {
				logArea.append(e.getMessage());
				return;
			}
			config.setProperty(Settings.CONZILLA_VERSION, Conzilla.CURRENT_VERSION);

			logArea.append("done.\n");
			checkStop();

			logArea.append("Install done!\n");

			done();
		} catch (InterruptedException e) {
		}
	}

	synchronized void checkStop() throws InterruptedException {
		if (stopInstall) {
			notifyAll();
			throw new InterruptedException();
		}
	}

	void makeDir(File dir) throws InterruptedException {
		logArea.append("Creating directory " + dir + "...");
		if (!dir.exists()) {
			if (!dir.mkdir())
				abort("The directory\n\n" + dir.toString() + "\n\ncould not be created!", null);
		}

		if (!dir.isDirectory())
			abort("The directory\n\n" + dir.toString() + "\n\nalready exists but is no directory!", null);

		if (!dir.canWrite())
			abort("The directory\n\n" + dir.toString() + "\n\ncannot be written to!", null);

		logArea.append("done.\n");
		checkStop();
	}
	
	URL getInstallResourceURL (String resource) {
		return getClass().getClassLoader().getResource("install/" + resource);
	}

	void installFile(String srcResource, File destination) throws InterruptedException {
		logArea.append("Installing " + destination + "...");
		OutputStream destFile = null;
		InputStream is = null;
		try {
			destFile = new BufferedOutputStream(new FileOutputStream(destination));
			URL resURL = getInstallResourceURL(srcResource);
			if (resURL == null) {
				abort("Could not find resource\n\n" + srcResource + "\n\nThis is an installer bug.", null);
			}
			is = new BufferedInputStream(resURL.openStream());

			byte[] b = new byte[2048];
			int s;
			while ((s = is.read(b)) != -1) {
				destFile.write(b, 0, s);
			}
		} catch (IOException e) {
			abort("Could not install file\n\n" + destination, e);
		} finally {
			try {
				destFile.close();
				is.close();
			} catch (IOException e) {
			}
		}
		logArea.append("done\n");
		checkStop();
	}

	void abort(String error, Exception e) throws InterruptedException {
		logArea.append("\n" + error + "\n\nInstall aborted.");
		ErrorMessage.showError("Intallation Error", error + "\n\nPlease correct this " + "and re-run Conzilla", e, null);
		throw new InterruptedException();
	}

	void done() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				okBut.setEnabled(true);
			}
		});
	}

}
