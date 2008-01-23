package se.kth.cid.conzilla.browse;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogViewer extends JFrame {
	
	Log log = LogFactory.getLog(LogViewer.class);
	
	File logFile = new File("conzilla.log");
	
	JTextArea text;
	
	public LogViewer() {
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int)(screenSize.width/2),(int)(screenSize.height/2));
		int x = (int)(frameSize.width/2);
		int y = (int)(frameSize.height/2);
		setBounds(x, y, frameSize.width, frameSize.height);
		setTitle("Conzilla log file");
		
		text = new JTextArea();
		text.setEditable(false);
		text.setFont(new Font("Courier", Font.PLAIN, 12));
		
		JPanel statusPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Conzilla log file location: " + logFile.getAbsolutePath());
		label.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
		statusPanel.add(new JLabel(" "), BorderLayout.WEST);
		statusPanel.add(label, BorderLayout.CENTER);
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(statusPanel.getPreferredSize().width, statusPanel.getPreferredSize().height + 6));
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(new JScrollPane(text), BorderLayout.CENTER);
		mainPanel.add(statusPanel, BorderLayout.SOUTH);
		
		setContentPane(mainPanel);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadLogFile(logFile);
			}
		});
	}
	
	private void loadLogFile(File logFile) {
		BufferedReader reader = null;
		try {
			log.debug("Opening log file from " + logFile.getAbsolutePath());
			reader = new BufferedReader(new FileReader(logFile));
		} catch (FileNotFoundException e) {
			text.append(e.getMessage());
			log.error(e);
		}
		
		if (reader == null) {
			return;
		}
		
		StringBuffer strBuf = new StringBuffer();
		try {
			while (reader.ready()) {
				strBuf.append(reader.readLine());
				strBuf.append("\n");
			}
		} catch (IOException e) {
			text.append(e.getMessage());
			log.error(e);
		}
		
		text.setText(strBuf.toString());
		text.setCaretPosition(0);
		
		try {
			reader.close();
		} catch (IOException ignored) {}
	}

}