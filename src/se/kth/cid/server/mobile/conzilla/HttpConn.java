/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.mobile.conzilla;

import java.io.PrintStream;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection; 
import javax.microedition.io.Connector;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import javax.microedition.io.*;
import java.lang.Thread;

/**
 * An example MIDlet to fetch a page using an HttpConnection.
 */
public class HttpConn implements Runnable{

   
    String uri,vers;
    StringBuffer buffer= null;
    
    int connMsg;
    //private String ServerURL = "http://localhost:8082";
    //String ServerURL = "http://id-2713.nada.kth.se:8081/";//Enok
    String ServerURL = "http://knowware.nada.kth.se:8095/mywar/HelloServlet";//Enok
    //String ServerURL = "http://knowware.nada.kth.se:8082";//Enok
    //private String ServerURL = "http://130.237.228.42:8081";//Min
    //private Thread thread;
    private Object threadSync = new Object();
    

    public HttpConn() {
	//display = Display.getDisplay(this);
    }

    public HttpConn(String u){
	if (u == null)
	    return;
	else{
	    uri = u;
	    print("uri= " + uri);
	}
	//thread = new Thread( this );
	//thread.start();
    }


    public void run(){
	//Thread mythread = Thread.currentThread();
        //if (mythread != thread) return;
	//else{
	if(uri == null)
	    return;
	else{
	    try {
	    print("URI i run=" + uri);
	    downloadPage(uri);		
	    //staticPage();		
	    
	    //readContents(HttpConnection.GET);
	    }catch(IOException e) {
		print("Unable to..."+e.getMessage());
		e.printStackTrace();		
	    }
	}	
	//synchronized (threadSync) {
	//  threadSync.notifyAll();
	//}
    }
    
    /**
    public String staticPage(){
	if(uri.equals("http://localhost:8082/"))
	    return protokoll2;
	else
	    return protokoll3;
    }		
    **/
    public synchronized boolean hasBuffer(){
	return (buffer!=null);		    
    }
  
    /**
    private void downloadPage(String u) throws IOException {
	StringBuffer b = new StringBuffer();
        InputStream is = null;
	OutputStream os = null;
	HttpConnection c = null;
	//TextBox t = null;
	try {
	    long len = 0 ;
	    int ch = 0;
	    int rc=0;
            //print("kkkk1");
	    //c = (HttpConnection)Connector.open((u == null)? "http://nada.kth.se" : ServerURL);
	    c = (HttpConnection)Connector.open(ServerURL);
	    //c = (HttpConnection)Connector.open(ServerURL);
	    //c.setRequestMethod(HttpConnection.GET);
	    is = c.openInputStream();
	   	
	    len =c.getLength() ;
	    if ( len != -1) {
		// Read exactly Content-Length bytes
   		for (int i =0 ; i < len ; i++ )
		    if ((ch = is.read()) != -1)
			b.append((char) ch);
	    } else {
                // Read till the connection is closed.
		while ((ch = is.read()) != -1) {
                    len = is.available() ;
		    b.append((char)ch);
		}
	    }
	    buffer = b;
	    // t = new TextBox("hello again....", b.toString(), 2000, 0);
        }  finally {
	    if(is != null)
		is.close();
	    if(os != null)
		os.close();
	    if(c != null)
		c.close();
        }
	

    }  
    **/
    private synchronized void downloadPage(String u) throws IOException{
    //private void downloadPage(String u) throws IOException{
	StringBuffer b = new StringBuffer();
        InputStream is = null;
	OutputStream os = null;
	HttpConnection c = null;
	//TextBox t = null;
	try {
	    print("i DownLoad 1");
	    long len = 0 ;
	    int ch = 0;
	    int rc=0;
	    Thread tt = new Thread();
	 	    
	    int code=0;
	    int max = 4;
	    String ACPReq = null;      
	    if(u!=null&&u.length()>0){
		ACPReq = "GETMAP "+u+" LCP/0.1\r\n";
		//+"se;177,190\r\n"
		//  +"Mindmaps\r\n"
		//  +"Date:03-08-19\r\n\r\n";
		print("kkkk2 + ACPReq= " + ACPReq);
		//Skickar Headern 
	    }
	    //while (max>-1 && code!=200){
	    //while ((is == null || os== null) && max>-1){
	    try{
                print(ServerURL);
		//c = (HttpConnection)Connector.open(ServerURL);
		c = (HttpConnection)Connector.open(ServerURL,Connector.READ_WRITE,true);
		c.setRequestMethod(HttpConnection.GET);
		c.setRequestProperty("ACP","Req");
		//code = c.getResponseCode();
		os = c.openOutputStream();
		if(ACPReq!=null){
		    os.write(ACPReq.getBytes());
		    print("Den var inte null...");
		}
		is = c.openInputStream();
	    }catch (IOException ioe){
		print("Nu vart det fel...");
		print(ioe.getMessage());
		try{ 
		    tt.sleep(0);
		    //System.out.println((new Date()).getTime());
		}catch (java.lang.InterruptedException ie){}
	    }
	    max--;
	    os.close();
	    print("Efter lixom...");
	    
	    // Read till the connection is closed.
	    while ((ch = is.read()) != -1) {//print("jadajada...3");
		//len = is.available() ;
		b.append((char)ch);
	    }
	    buffer = b;
        }  finally {
	    if(is != null)
		is.close();
	    if(os != null)
		os.close();
	    if(c != null){
		c.close();
		print("St?nger c!");
	    }
        }	
    }
    
    public int getConnMsg(){
	return connMsg;
    }
    
    /*
    public StringBuffer getBuffer(){
	return buffer;	
    }
    */
    
    public synchronized StringBuffer getBuffer(){
	synchronized(threadSync){
	    while(!hasBuffer()){
		try{
		    threadSync.wait();
		}catch (java.lang.InterruptedException ie){
		    print("Fel i threadSync");
		}
	    }
	    return buffer;
	}
    }
    
    void print(String s){
	System.out.println("Http:" + s);
    }

/**
     * Read the content of the page. Don't care about the response
     * headers.
     * @param request type of HTTP request (GET or POST)
     */
    private void readContents(String request) throws IOException{
	//StringBuffer b = new StringBuffer();
	buffer = new StringBuffer();
        //++ attempt;
	//        b.append("attempt " + attempt + " content of " 
	//               + request + " " + uri + "\n");
	HttpConnection c = null;
        OutputStream os = null;
        InputStream is = null;
	//TextBox t = null;
	
	try {
	    long len = -1;
	    int ch = 0;
            long count = 0;
            int rc;
	    
            print(request + " Page1: " + uri); 
            //c = (HttpConnection)Connector.open(uri);
            c = (HttpConnection)Connector.open(uri,Connector.READ_WRITE,true);
	    c.setRequestMethod(HttpConnection.GET);
	    c.setRequestProperty("ACP","Req");
	    print("c2= " + c);
	    //HttpConnection.GET
            //c.setRequestMethod(request);
	    //c.setRequestProperty("foldedField", "first line\r\n second line\r\n third line");
	    rc = c.getResponseCode();
	    
	    if (rc != HttpConnection.HTTP_OK) {
                //buffer.append("Response Code: " + c.getResponseCode() + "\n");
                //buffer.append("Response Message: " + c.getResponseMessage() + "\n\n");
		print("HttpOK= False");
	    }
	    is = c.openInputStream();
            print("is = " + is);
            //if (c instanceof HttpConnection) {
	    len = ((HttpConnection)c).getLength();
	    //}
            print("len = " + len);

	    String ACPReq = null;      
	    /**
	    if(!ServerURL.equals(uri)){
		ACPReq = "GET;"+uri+";ACP/0.9;\r\n"
		    +"se;177,190\r\n"
		    +"Mindmaps\r\n"
		    +"Date:03-08-19\r\n\r\n";
		//print("kkkk2 + ACPReq= " + ACPReq);
		//Skickar Headern 
	    }
	    **/
	    ACPReq = "GET;"+uri+";ACP/0.9;\r\n"
		    +"se;177,190\r\n"
		    +"Mindmaps\r\n"
		    +"Date:03-08-19\r\n\r\n";
		
	    //if(ACPReq!=null){
	    os.write(ACPReq.getBytes());
	    print("Skickar Request uri=" + uri);
	    //}
	    while ((ch = is.read()) != -1) {//print("jadajada...3");
		//len = is.available() ;
		buffer.append((char)ch);
	    }
	    
	    /**
	    if (len != -1) {
		// Read exactly Content-Length bytes
		// DEBUG("Content-Length: " + len);

		for (int i = 0; i < len; i++) {
		    if ((ch = is.read()) != -1) {
			if (ch <= ' ') {
                            ch = ' ';
                        }
			buffer.append((char) ch);
                        count ++;
                        if (count > 4000) {
                            break;
                        }
		    }
                }
	    }else{
                byte data[] = new byte[5000];
                int n = is.read(data, 0, data.length);
                for (int i = 0; i < n; i++) {
                    ch = data[i] & 0x000000ff;
		    buffer.append((char)ch);
		}
	    }
	    **/
	    try {
                if (is != null) {
                    is.close();
                }
                if (c != null) {
                    c.close();
                }
	    } catch (Exception ce) {
		print("Error closing connection");
	    }

	    try {
		len = is.available();
		print("Inputstream failed to throw IOException after close");
	    } catch (IOException io) {
                print("expected IOException (available())");
                io.printStackTrace();
		// Test to make sure available() is only valid while
		// the connection is still open.,
	    }

	    //t = new TextBox("Http Test", buffer.toString(), buffer.length(), 0);
            is = null;
            c = null;
	} catch (IOException ex) {
            ex.printStackTrace();
            print(ex.getClass().toString());
            print(ex.toString());
	    print("Exception reading from http");
	    if (c != null) {
		try {
		    String s = null;
                    if (c instanceof HttpConnection) {
                        s = ((HttpConnection)c).getResponseMessage();
                    }
		    print(s);
		    if (s == null)
			s = "No Response message";
		    //t = new TextBox("Http Error", s, s.length(), 0);
		    print(s);
		} catch (IOException e) {
		    e.printStackTrace();
		    String s = e.toString();
		    print(s);
		    if (s == null)
			s = ex.getClass().getName();
		    //t = new TextBox("Http Error", s, s.length(), 0);
		    print(s);
		}

                try {
                    c.close();
                } catch (IOException ioe) {
                    // do not over throw current exception
                }
	    } else {
		//t = new TextBox("Http Error", "Could not open URL", 128, 0);
		print("Http Error: Could not open URL");
	    }
	} catch (IllegalArgumentException ille) {
	    // Check if an invalid proxy web server was detected.
	    //t  = new TextBox("Illegal Argument", ille.getMessage(), 128, 0);
	    print("Illegal Argument" + ille.getMessage());
	}

        if (is != null) {
	    try {
		is.close();
	    } catch (Exception ce) {; }
        }

        if (c != null) {
	    try {
		c.close();
	    } catch (Exception ce) {print("Slut catch"); }
        }

        //setCommands(t, false);
	//display.setCurrent(t);
    }

   
}
