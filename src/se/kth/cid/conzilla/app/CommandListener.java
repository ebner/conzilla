/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import se.kth.cid.util.Tracer;

/**
 * Listens on a socket for commands such as bringing the window to the foreground or
 * opening a context-map.
 * 
 * @author Hannes Ebner
 */
public class CommandListener {
	
	private ServerSocket socket;

	private File file;

	/**
	 * @param lockFile File to write the port number to. The file should exist.
	 */
	CommandListener(File infoFile) {
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
			Tracer.error(e.getMessage());
		}
	}
	
	private void waitAndServeClient() {
		Socket client;
		try {
			client = socket.accept();
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String command = null;
			while ((command = reader.readLine()) != null) {
				executeCommand(command.toUpperCase());
			}
		} catch (IOException ignored) {}
	}
	
	private void executeCommand(String command) {
		if ("FOREGROUND".equals(command)) {
			ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow().toFront();
		} else {
			System.out.println("Received unknown command: " + command);
		}
	}
	
	/**
	 * This method does not return, ideally started as thread.
	 */
	public void start() {
		try {
			socket = new ServerSocket();
			socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
		} catch (IOException e) {
			Tracer.error(e.getMessage());
			return;
		}
		
		Tracer.debug("Listening on " + socket.getLocalSocketAddress());
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Tracer.debug("Shutting down listener on " + socket.getLocalSocketAddress());
				try {
					if (socket != null) {
						socket.close();
					}
				} catch (IOException ignored) {}
			}
		});
		
		writePortNumberToFile();
		
		// this is not multi-threaded on purpose
		while (true) {
			waitAndServeClient();
		}
	}

}