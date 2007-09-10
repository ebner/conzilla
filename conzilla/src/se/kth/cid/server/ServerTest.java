/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server;

import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.io.OutputStream;
import java.io.InputStream;
/**
 * @author enok
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerTest{
	
	public ServerTest() {}
	
	public static void usage(){
		System.out.println("Fel användning av programmet");
	}
	public static void main(String argv[]){
		Socket socket;
		
		String karta;
		
		if (argv.length<=0)
			karta = "urn:path:/org/conzilla/builtin/maps/default";
			//karta = "urn:path:/org/conzilla/local/providedEditableModel.rdf#12884e0f400176089";
		else {
			karta = argv[0];
	       ServerTest st = new ServerTest();
	       try{
			   socket = new Socket(InetAddress.getLocalHost(),8082);
			   OutputStream os = socket.getOutputStream();
			   //String LCP = "GETNEIGHBOURHOOD "+karta+
				//	  " LCP/0.1\r\n\r\n"; 
			   String LCP = "GETMAP "+karta+" LCP/0.1\r\n\r\n";
			   String http = "POST / HTTP/1.1\r\n" +
		   		"Content-Length: "+LCP.length()+"\r\n\r\n";
			   byte b[] = (http+LCP).getBytes();
			   os.write(b);
			   os.flush(); 
			   InputStream ins = socket.getInputStream();
			   int r = ins.read();
			   int counting=0;
			   int maxInputSize=10000;
			   byte bbb[] = new byte[maxInputSize];
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
						   counting++;;
						   if(r==10){
						      String s = (new String(bbb,0,counting,"UTF-8")).trim();
							  bbb = new byte[maxInputSize/2];
							  counting = 0;
							  //System.out.println("s == "+s);
						   }
						}
					 }
				     r=ins.read();
				  }
				  socket.close();
	       }catch (IOException ioe){
				System.out.println("Fick IOException i konstruktor");
			
			}catch (SecurityException se){
			   System.out.println("Fick IOException i konstruktor");
			}
		   
		}
	}

}
