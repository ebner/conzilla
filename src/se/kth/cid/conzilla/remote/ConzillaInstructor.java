/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.remote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import se.kth.cid.util.Tracer;

/**
 * Checks for a lock file, opens it, and reads the port number in it.
 * It then sends commands to the given port number, remote controlling Conzilla
 * using the small built-in server. 
 * 
 * @author Hannes Ebner
 */
public class ConzillaInstructor {
	
	InetSocketAddress inetSocketAddr;

	/**
	 * @param lockFile
	 *            File to write the port number from. The file should exist.
	 * @throws IllegalArgumentException
	 *             If the port number cannot be read from the file (because file
	 *             does not exist etc) this Exception is thrown.
	 */
	public ConzillaInstructor(File infoFile) throws IllegalArgumentException {
		int port = readPortNumberFromFile(infoFile);
		if (port == -1) {
			throw new IllegalArgumentException("Unable to determine port number.");
		}
		try {
			inetSocketAddr = new InetSocketAddress(InetAddress.getLocalHost(), port);
		} catch (UnknownHostException ignored) {}
	}

	/**
	 * Writes the port where the socket is listening at into a file.
	 */
	private int readPortNumberFromFile(File file) {
		BufferedReader reader = null;
		String portStr = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			portStr = reader.readLine();
		} catch (IOException e) {
			Tracer.error(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored) {}
			}
		}

		int port = -1;

		if (portStr != null) {
			try {
				port = Integer.parseInt(portStr);
			} catch (NumberFormatException nfe) {
				Tracer.error(nfe.getMessage());
			}
		}
		
		return port;
	}
	
	private void sendCommandToConzilla(String command) {
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket(null);
			packet = new DatagramPacket(command.getBytes(), command.getBytes().length);
			socket.connect(inetSocketAddr);
			socket.send(packet);
			Tracer.debug("Sent \"" + command + "\" to " + inetSocketAddr);
	    } catch (IOException e) {
			Tracer.error(e.getMessage());
	    } finally {
	    	if (socket != null) {
	    		socket.close();
	    	}
	    }
	}
	
	public void toForeground() {
		sendCommandToConzilla(RemoteCommands.FOREGROUND);
	}
	
	public void terminate() {
		sendCommandToConzilla(RemoteCommands.QUIT);
	}
	
	public void openContextMap(String uri) {
		sendCommandToConzilla(RemoteCommands.OPEN + " " + uri);
	}

}