/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.connector.http;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.kth.cid.server.connector.http.HttpHeader;
import se.kth.cid.server.service.lcp.LCPRequestHandler;
import se.kth.cid.server.service.lcp.LCPHeader;
import se.kth.cid.server.service.Request;
import se.kth.cid.util.Tracer;
/**
 * @author su99-fen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HttpProcessor implements Runnable{
	
	private LCPHeader lcpheader;
	
	private HttpConnector httpConnector;
	
	private LCPRequestHandler lcpreqhandler;
	
	private int processNr;
	
	private boolean stopped = false;
	
	private boolean started = false;
	
	private boolean socketAvailable = false;
	
	private Socket socket;
	
	private int SO_TIMEOUT = 6000;

	private HttpHeader httpheader;
	
	private int maxInputSize = 2048;
	
	private Thread thread = null;
	
	private Object threadSync =new Object();
	
	public HttpProcessor(HttpConnector c, int processNr){
	   this.httpConnector = c;	
	   this.processNr = processNr;
	}
	
	private synchronized Socket awaitSocket(){
		while(!socketAvailable){
			try{
			wait();
			}catch (InterruptedException ie){
			;
			}
		}
		socketAvailable=false;
		Socket s = this.socket;
		notifyAll();
       
		return (s);
		}	
		
	public synchronized void assign(Socket socket){
	   while(socketAvailable){
		   try{
		   wait();
		   }catch (InterruptedException ie){
		   ;
		   }	    
	   }
	   socketAvailable = true;
	   this.socket=socket;
	   notifyAll();
	}
	   
	public void processSocket(Socket s){
		try{
			//System.out.println("InputShutdown?");
			s.setSoTimeout(SO_TIMEOUT);
			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			
			//TODO: Innan behandlingen av LCP-protokollet, s??? ska http-delen avhandlas.
			//System.out.println(s.isInputShutdown());
			//System.out.println("SO_TIMEOUT == "+s.getSoTimeout());
			try{
			   Tracer.debug("Start processing");
			   processHttp(is);
			   Tracer.debug("Finished with HTTP");
			   processLcp(is);
			   Tracer.debug("Done with LCP");
			   lcpreqhandler = new LCPRequestHandler(this.lcpheader);
			   Request request = new Request(lcpheader);
			   Object o = lcpreqhandler.process(request);
			   os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
			   os.write(((String) o).getBytes());
			} catch (Exception e){
				os.write("HTTP/1.1 500 FAILURE\r\n\r\n".getBytes());
				Tracer.debug("Exception caught..."+e.getMessage());
			}
			Tracer.debug("Closing socket...");
			s.close();
			Tracer.debug("Socket closed");
		}catch (java.io.IOException ioe){
		   Tracer.debug("Processor: IO-error" + ioe.getMessage());
			ioe.printStackTrace();
		}
		}
	public void start(){
	   started = true;
	   startThisThread();
	}
	public void stop(){
	   stopped = true;
	   stopThisThread();
	}
    
	private void stopThisThread(){
		try{
			socket.close();
			}catch (IOException ioe) {
				Tracer.debug("Unable to close socket, exiting anyway");
			}
	   assign(null);
	   thread = null;
	}
    
	private void startThisThread(){
	   thread = new Thread(this);
	   thread.setDaemon(true);
	   thread.start();
	}
	
	public void run(){
		while(!stopped){
			Socket s = awaitSocket();
			if (s == null)
			continue;
			try{
			Tracer.debug("Start processing socket nr "+processNr);
			processSocket(socket);
			}catch(Exception e){
			Tracer.debug("Error in Processor.run()"+e.getMessage());
			e.printStackTrace();
			}
			httpConnector.reuse(this);
		}
	
		synchronized (threadSync) {
				threadSync.notifyAll();
		}
	}
	
	private boolean isHttpHeader(String prot){
	boolean isHttp = false;
	int httpIndex = prot.toUpperCase().indexOf("HTTP/");
	int newlineIndex = prot.indexOf("\n");
	if((httpIndex>-1) && (httpIndex<newlineIndex)){
		isHttp = true;
		return true;
	}
	else{
		isHttp = false;
		return false;
	}
	}
		
   private void processHttp(InputStream ins) throws IOException{
      byte[] bbb = new byte[maxInputSize/2];
      int r = ins.read();
	  //String byteStringen = new String(bbb,0,r,"UTF-8");
	  
      int counting = 0;
	  while(r>-1 && counting<maxInputSize){
	     bbb[counting]=(byte)r;
		 counting++;
		 if(r==10){
		    r=ins.read();
			bbb[counting]=(byte)r;
			counting++;  
			if(r==13){
			   r=ins.read();
		       bbb[counting]=(byte)r;
			   counting++;
			   if(r==10){
			      String s = (new String(bbb,0,counting,"UTF-8")).trim();
				  bbb = new byte[maxInputSize/2];
				  counting = 0;
				  //Remove // below for printing the instream
				  System.out.println("s == "+s);
				  
				  if(isHttpHeader(s)){
				     httpheader = new HttpHeader(s);
				  /*os.write("HTTP/1.1 202 Accepted\n".getBytes());
				  	 try{
					    int ContentLength = Integer.parseInt(hh.getProperty("Content-Length")[0]);
					    if(ContentLength>0)
						   continue;
					    else
						   break;
					 } catch (NumberFormatException e) {
					    break;
					} catch (NullPointerException ne) {
					   System.out.println("NullPointer i Request!");
					   break;
				     }*/
					 break;}  
				  //Detta ???r till f???r om enbart LCP k???rs direkt.
				//else if(lcpreqhandler.isLCPHeader(s)){
				//	break;
				//}
				else{
					throw new IOException("Fel p??? inskickat protokoll!");
				}
				}
			}
			}
			r=ins.read();
		}
   }
   public void processLcp(InputStream is){
      byte[] bbb; 
	  int ContentLength = 0;
	  int r=0;
	  String byteStringen = null; 
   	  
   	  Tracer.debug("Starting to process LCP!");
   	   
      if (httpheader.getProperty("Content-Length")!=null){
      	Tracer.debug("Request with Content-Length");
   	     ContentLength=Integer.parseInt(httpheader.getProperty("Content-Length")[0]);
         Tracer.debug("Content-Length: "+ContentLength);
      }
      if(ContentLength > 0){
	     bbb = new byte[ContentLength]; 
         try{
	        r = is.read(bbb);
	        byteStringen = new String(bbb);
	     }catch (Exception e){
	        Tracer.debug("Read not possible on InputStream, No LCP included");
	     }     
      }else{
         Tracer.debug( "Only HTTP-header exists, according to Content-Length");       
      }
	  lcpheader = new LCPHeader(byteStringen);
	  lcpheader.setHttpHeader(httpheader);
    }
   }