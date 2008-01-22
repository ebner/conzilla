/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.remote;

import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.conzilla.app.ConzillaKit;

/**
 * Listens on a socket for commands such as bringing the window to the foreground or
 * opening a context-map.
 * 
 * @author Hannes Ebner
 */
public class CommandListener {
	
	Log log = LogFactory.getLog(CommandListener.class);
	
	private DatagramSocket socket;

	private File file;

	/**
	 * @param lockFile File to write the port number to. The file should exist.
	 */
	public CommandListener(File infoFile) {
		file = infoFile;
	}

	/**
	 * Writes the port where the socket is listening at into a file.
	 */
	private void writePortNumberToFile() {
		try {
			PrintWriter printer = new PrintWriter(file);
			printer.println(socket.getLocalPort());
			printer.close();
		} catch (FileNotFoundException e) {
			log.error(e);
		}
		file.deleteOnExit();
	}
	
	private void waitAndServeClient() {
		byte[] data = new byte[2048]; 
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
			String command = new String(packet.getData(), 0, packet.getLength());
			executeCommand(command);
		} catch (IOException ignored) {}
	}

	private void executeCommand(String command) {
		if (command.equals(RemoteCommands.FOREGROUND)) {
			Window cWin = ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow();
			if (cWin != null) {
				cWin.toFront();
				cWin.requestFocus();
			}
		} else if (command.startsWith(RemoteCommands.OPEN + " ")) {
			String ccm = command.substring(RemoteCommands.OPEN.length() + 1);
			ConzillaKit.getDefaultKit().getConzillaEnvironment().loadContextMap(ccm, true);
		} else if (command.equals(RemoteCommands.QUIT)) {
			ConzillaKit.getDefaultKit().getConzilla().getViewManager().closeViews();
		} else {
			log.info("Received unknown command: " + command);
		}
	}

	/**
	 * This method does not return, ideally started as thread.
	 */
	public void start() {
		try {
			socket = new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(), 0));
		} catch (IOException e) {
			log.error(e);
			return;
		}
		
		log.info("Listening on " + socket.getLocalSocketAddress());
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Shutting down listener on " + socket.getLocalSocketAddress());
				if (socket != null) {
					socket.close();
				}
			}
		});

		writePortNumberToFile();

		// this is not multi-threaded on purpose
		while (true) {
			waitAndServeClient();
		}
	}

}