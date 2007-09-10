/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.connector.http;

import java.net.ServerSocket;
import java.net.InetAddress;
import javax.net.ServerSocketFactory;

import se.kth.cid.server.connector.Connector;

import java.io.IOException;
import java.net.Socket;
import java.util.Stack;
import java.util.Vector;
import java.util.Date;
import se.kth.cid.util.Tracer;

public class HttpConnector implements Runnable, Connector {

    private ServerSocket socket = null;

    private ServerSocketFactory socketfactory = null;

    private Thread thread;

    int port = 8082;

    InetAddress inetaddress = null;

    int maxQueue = 10;

    private boolean initialized = false;

    private boolean started = false;

    private boolean stopped = false;

    private int curProcessors = 0;

    private int minProcessors = 5;

    private int maxProcessors = 10;

    private String threadName;

    private Stack processors = new Stack();

    private Vector created = new Vector();

    private Object threadSync = new Object();

    public void initialize() {
        if (initialized)
            Tracer.debug("Connector already initialized, skipping...");
        else {
            socket = openServerSocket();
        }

        initialized = true;
        Tracer.debug("initialized finished");
    }

    public ServerSocket openServerSocket() {
        if (socketfactory == null)
            socketfactory = getServerSocketFactory();

        ServerSocket s = null;
        try {
            if (inetaddress == null) { //Takes all possible addresses
                s = socketfactory.createServerSocket(port, maxQueue);
            } else { // A specific address
                s =
                    socketfactory.createServerSocket(
                        port,
                        maxQueue,
                        inetaddress);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return s;
    }

    public void start() {
        if (started)
            Tracer.debug(
                "Connector already started, ignoring this call...");
        else {
            started = true;
            threadName = "Connector [" + port + "]";
            startThisThread();

            while (curProcessors < minProcessors) {
                if ((maxProcessors > 0) && (curProcessors >= maxProcessors))
                    break;
                HttpProcessor processor = newProcessor();
                reuse(processor);
            }

        }
    }

    private HttpProcessor newProcessor() {
        HttpProcessor processor = new HttpProcessor(this, curProcessors++);
        processor.start();
        Tracer.debug(
            "Connetor: Efter start av processor: " + curProcessors);
        created.addElement(processor);
        return processor;
    }

    public void reuse(Object p) {
    	if (p instanceof HttpProcessor) 
           processors.push(p);
    }

    public void startThisThread() {
        thread = new Thread(this, threadName);
        thread.setDaemon(true);
        thread.start();
    }

    public void stopThisThread() {
        started = false;
        stopped = true;
        try {
            threadSync.wait(1000);
        } catch (InterruptedException e) {
            //System.out.println("Nu vart det fel...");
        }
        //System.out.println("Nu vart det inte fel...");
        thread = null;
    }

    public void run() {
        while (!stopped) {
            Socket s = null;
            try {
                s = socket.accept();
                System.out.println(
                    "---------------------------------------------------");
                Tracer.debug(
                    "Recieved a request: "
                        + new Date()
                        + " "
                        + s.getRemoteSocketAddress()
                        + " "
                        + s.getTcpNoDelay());
                s.setSoLinger(true, 5);
                //System.out.println(s.getSoLinger());
            } catch (IOException ioe) {
                Tracer.debug("Something has gone wrong " + ioe.getMessage());
                try {
                    synchronized (threadSync) {
                        if (started && !stopped)
                            Tracer.debug(
                                "Socket error, trying again ");
                        if (!stopped) {
                            socket.close();
                            socket = openServerSocket();
                        }
                    }
                } catch (IOException iooe) {
                    Tracer.debug(
                        "Opening connection failed, closing");
                    break;
                }

            }

            HttpProcessor p = createProcessor();
            if (p == null) {
                try {
                    s.close();
                } catch (IOException ioe) {
                    Tracer.debug("Error in Connector...");
                }
                continue;
            }
            p.assign(s);
        }
        synchronized (threadSync) {
            threadSync.notifyAll();
        }
    }
    private HttpProcessor createProcessor() {
        synchronized (processors) {
            if (processors.size() > 0)
                return (HttpProcessor) processors.pop();
            if (maxProcessors > 0 && curProcessors < maxProcessors)
                return newProcessor();
            else {
                if (maxProcessors < 0)
                    return newProcessor();
                else
                    return null;
            }
        }
    }

    public void stop() {
        for (int i = 0; i < created.size(); i++)
             ((HttpProcessor) created.elementAt(i)).stop();
        synchronized (threadSync) {
            if (socket != null)
               Tracer.debug("socket closing!");
            try {
                socket.close();
            } catch (IOException ioe) {
                ;
            }
            stopThisThread();
        }
        socket = null;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setServerSocketFactory(ServerSocketFactory ssf) {
        this.socketfactory = ssf;
    }

    public ServerSocketFactory getServerSocketFactory() {
        synchronized (this) {
            if (this.socketfactory == null)
                socketfactory = new DefaultServerSocketFactory();
            return this.socketfactory;
        }
    }

    public InetAddress getAddress() {
        return inetaddress;
    }

    public void setAddress(InetAddress e) {
        inetaddress = e;
    }
}
