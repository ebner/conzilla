/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import se.kth.cid.util.Tracer;

import se.kth.cid.server.connector.http.HttpConnector;

public class DefaultServer implements Server{
    
    // boolean starting = false;
    
    //  boolean stopping = false;
    
    private int port = 8006;

    private String shutdown = "shutdown";

    private HttpConnector connector = new HttpConnector();

    public void initialize(){
	   connector.initialize();
    }
    
    public void start(){
	   connector.start();
	   Tracer.debug("Server.start finished!");
    }
    
    public void stop(){
	connector.stop();
    }
    
    public int getPort(){
	return port;
    }

    public String getShutdown(){
	return shutdown;
    }
    
    public void await(){
	Tracer.debug("Awaiting connections");
        // Set up a server socket to wait on
        ServerSocket serverSocket = null;
        try {
            serverSocket =
                new ServerSocket(port, 1,
                                 InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            Tracer.debug("StandardServer.await: create[" + port
                               + "]: " + e);
            e.printStackTrace();
            System.exit(1);
        }
	
        // Loop waiting for a connection and a valid command
        while (true) {
	    
            // Wait for the next connection
            Socket socket = null;
            InputStream stream = null;
            try {
                socket = serverSocket.accept();
                socket.setSoTimeout(10 * 1000);  // Ten seconds
                stream = socket.getInputStream();
            } catch (AccessControlException ace) {
                System.err.println("StandardServer.accept security exception: "
                                   + ace.getMessage());
                continue;
            } catch (IOException e) {
                System.err.println("StandardServer.await: accept: " + e);
                e.printStackTrace();
                System.exit(1);
            }
	    
            // Read a set of characters from the socket
            StringBuffer command = new StringBuffer();
            int expected = 1024; // Cut off to avoid DoS attack
            /*while (expected < shutdown.length()) {
                if (random == null)
                    random = new Random(System.currentTimeMillis());
                expected += (random.nextInt() % 1024);
            }*/
            while (expected > 0) {
                int ch = -1;
                try {
                    ch = stream.read();
                } catch (IOException e) {
                    System.err.println("StandardServer.await: read: " + e);
                    e.printStackTrace();
                    ch = -1;
                }
                if (ch < 32)  // Control character or EOF terminates loop
                    break;
                command.append((char) ch);
                expected--;
            }
	    
            // Close the socket now that we are done with it
            try {
                socket.close();
            } catch (IOException e) {
                ;
            }
	    
            // Match against our command string
            boolean match = command.toString().equals(shutdown);
            if (match) {
                break;
            } else
                System.err.println("StandardServer.await: Invalid command '" +
                                   command.toString() + "' received");
	    
        }
	
        // Close the server socket and return
        try {
            serverSocket.close();
        } catch (IOException e) {
            ;
        }
	
    }
    
}
    
