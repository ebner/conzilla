/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server;

import java.net.*;
import java.io.*;

import se.kth.cid.server.Server;
import se.kth.cid.server.service.lcp.LCPRequestHandler;

import se.kth.cid.util.Tracer;

/**
 * This is a server leveraging ContextMaps over a thin protocol.
 * Various transfermethods are possible, currently only http is supported.
 * The current protocol is designed to be useful for thin clients.
 * 
 * @author Fredrik
 */
public class ConzillaServer{
    
    //-----Instance Variables

    protected Server server = null;

    protected boolean starting = false;

    protected boolean stopping = false;
    
    
    //-----Main method

    public static void main(String argv[]){
	(new ConzillaServer()).startup(argv);
    }
    
    
    //-----Public Methods

    public void setServer(Server server){
	this.server = server;
    }
    
    public void startup(String argv[]){
	if(validArguments(argv)){
	    try{
		if(server == null)
		    server = new DefaultServer();
		if(starting){
		    server.initialize();
		    LCPRequestHandler.initConzilla();
		    server.start();
		    server.await();
		    server.stop();
		}else if(stopping){
		    stop();
		}
	    }catch (Exception e){
		e.printStackTrace();
	    }
	}else
	    usage();
    }
    
    //-----Private Methods
    
    private boolean validArguments(String argv[]){
	
	for (int i = 0; i < argv.length; i++) {
	    if (argv[i].equals("-help")) {
                usage();
                return (false);
            } else if (argv[i].equals("start")) {
                starting = true;
            } else if (argv[i].equals("stop")) {
                stopping = true;
            } else {
                usage();
                return (false);
            }
        }
	
        return (true);
	
    }
    
    private void usage(){
	System.out.println("Usage: ServerConzilla [ start | stop ]");
    }
    
    private void stop(){	
	try {
	    
	    Socket socket = new Socket("127.0.0.1", server.getPort());
	    OutputStream stream = socket.getOutputStream();
	    String shutdown = server.getShutdown();
	    for (int i = 0; i < shutdown.length(); i++)
		stream.write(shutdown.charAt(i));
	    stream.flush();
	    stream.close();
	    socket.close();
	} catch (IOException e) {
	   Tracer.debug("Conzilla-server.stop: " + e);
	    e.printStackTrace(System.out);
	    System.exit(1);
	}
    }
}
