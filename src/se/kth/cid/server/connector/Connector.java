/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.connector;

import java.net.InetAddress;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

/**
 * Automatically generated, write something here.
 * 
 * @author Fredrik
 *
 */
public interface Connector {
    public abstract void initialize();
    public abstract ServerSocket openServerSocket();
    public abstract void start();
    public abstract void reuse(Object p);
    public abstract void startThisThread();
    public abstract void stopThisThread();
    public abstract void run();
    public abstract void stop();
    public abstract void setPort(int port);
    public abstract int getPort();
    public abstract void setServerSocketFactory(ServerSocketFactory ssf);
    public abstract ServerSocketFactory getServerSocketFactory();
    public abstract InetAddress getAddress();
    public abstract void setAddress(InetAddress e);
}