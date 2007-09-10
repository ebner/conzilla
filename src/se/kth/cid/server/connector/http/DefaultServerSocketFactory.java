/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.connector.http;

import javax.net.ServerSocketFactory;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.IOException;

public class DefaultServerSocketFactory 
    extends ServerSocketFactory{
    
    public ServerSocket createServerSocket(int port) throws IOException{
	return (new ServerSocket(port));
    }
    
    public ServerSocket createServerSocket(int port, int backlog) throws IOException{
	return (new ServerSocket(port, backlog));
    }
    
    public ServerSocket createServerSocket(int port, int backlog, InetAddress address)
	throws IOException{
	return (new ServerSocket(port,backlog,address));
    }
}
