/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.service.lcp;

import java.util.Hashtable;
import java.util.StringTokenizer;

import se.kth.cid.server.connector.http.HttpHeader;
import se.kth.cid.util.Tracer;
import se.kth.cid.conzilla.properties.GlobalConfig;

public class LCPHeader{

    //InstanceVariables
    
    private HttpHeader httpheader;

    private String commandLine;

    private String typeOfRequest="GETMAP"; //The default request;

    private String protocol = "LCP/0.1";

    private String URI;// = "urn:path:/org/conzilla/builtin/maps/default";

    private String[] URIs;
    
    private Hashtable properties;
    
    public LCPHeader(String head){
       try{properties = new Hashtable();
 	   StringTokenizer st;
 	   StringTokenizer stt;
 	   String tmp;
 	   if(head != null){
 	      st= new StringTokenizer(head,"\n");
 	      //F?rsta raden i headern
 	      if(st.hasMoreTokens()){
 	      	commandLine = st.nextToken();
 	      }
 	      Tracer.debug("The LCP-request is: "+commandLine);
 	      //Alla Properties som finns i LCP-delen
 	      while(st.hasMoreTokens()){
 	      	setProperty(st.nextToken());
 	      }
 	      stt = new StringTokenizer(commandLine);
 	      if(stt.hasMoreTokens())
 	      	typeOfRequest = stt.nextToken();
 	      int j = stt.countTokens();     
          URIs = new String[j-1];
 	      for(int i=0;i<j-1;i++)
 	         URIs[i] = stt.nextToken();
 	      protocol = stt.nextToken();
 	      URI = URIs[0];
 	   } else{
		URI =GlobalConfig.getGlobalConfig().getProperty("conzilla.startmap");
		if (URI == null || URI.length()<1)
		   URI = "urn:path:/org/conzilla/builtin/maps/default";
 	   }
    }catch(Exception e){
    e.printStackTrace();
    }
    }
    
    
    public void setHttpHeader(HttpHeader httpheader){
    	this.httpheader=httpheader;
    }

    public HttpHeader getHttpHeader(){
		return httpheader;
	}
    
    public boolean hasHttpheader(){
    	return httpheader != null;
    }
    /*
     * Funktionen returnerar den f?rsta URI:n som skickats med
     */
    public String getfirstURI(){
	return URI;
    }
    
    /*
     * Funktionen returnerar samtliga URI:er
     */
    public String[] getURIs(){
	return URIs;
    }

    public String getTypeOfRequest(){
	return typeOfRequest;
    }
    
    public String getProtcol(){
	return protocol;
    }
    
    public String[] getMaxSize(){ //Of the "file" to be transferred
	   return getProperty("maxSize");
    }
    
    public String[] getLanguage(){
	   return getProperty("language");
    }
    
    public String[] getProperty(String Property){
    	String[] returnString =
    		((String[])properties.get(Property.toLowerCase()));
    	if(returnString == null)
    		returnString = httpheader.getProperty(Property.toLowerCase());
    	if (returnString == null){
    		returnString = new String[1];
    		returnString[0]="en";
    	}
    	return returnString;
    }
    
    private void setProperty(String Property, String[] values){
	   if(Property != null && values != null)
          properties.put(Property.toLowerCase(),values);
    }
    
    private void setProperty(String s){
       if(s!=null){
          int kolon = s.indexOf(":");
          int length = s.length();
          String s1 = null;
          String s2;
          String[] ss = null;
          if(kolon>0){
             s1 = s.substring(0,kolon-1).trim();
             s2 = s.substring(kolon,length-1).trim();
             StringTokenizer st = new StringTokenizer(s2,";");
             int antal = st.countTokens();
             ss = new String[antal];
             int i=0;
             while(st.hasMoreTokens()){
                ss[i] = st.nextToken().trim();
                i++;
             }
          }
          setProperty(s1,ss);
       }
    }
    
    public String[] getScreenSize(){ 
    	//in pixels
    	return getProperty("Client-Screen-Size");
    }
    
    /* F??ljande "kod tillh??r den gamla versionen av protokollet
     * 
     * private String maxSize; //Of the "file" to be transferred

    private int[] clientScreenSize; //in pixels

    private String language;
    
    
    public LCPHeader(String head){
	   properties = new Hashtable();
	   StringTokenizer st;
	   StringTokenizer stt;
	   String tmp;
	   if(head != null){
	      st = new StringTokenizer(head.trim(),"\n");
	      if(st.hasMoreTokens()){
	         commandLine = st.nextToken();
	         stt=new StringTokenizer(commandLine,";");
	         if(stt.hasMoreTokens()){
		        typeOfRequest=stt.nextToken();
	         }
	         if(stt.hasMoreTokens()){
		        URI=stt.nextToken();
		        if(URI.length()<2 || URI.toLowerCase().equals("null"))
		           URI = "urn:path:/org/conzilla/builtin/maps/default";
	         }
	         if(stt.hasMoreTokens()){
		        protocol=stt.nextToken();
	         }
	         int i=0;
	         URIs = new String[stt.countTokens()];
	         while (stt.hasMoreTokens()){
		        URIs[i]=stt.nextToken();
		        i++;
	         }
	      }
	      if(st.hasMoreTokens()){
	         tmp = st.nextToken();
	         int pos = tmp.indexOf(";");
	         if(pos>0){
		        language = tmp.substring(0,pos);
		        setScreenSize(tmp.substring(pos+1));
	        }
	      }
	      if (st.hasMoreTokens()){
	         tmp = st.nextToken();
	         int pos = tmp.indexOf(";");
	         if(pos>0)
		        maxSize=tmp.substring(0,pos);
	      }  
	
	      if(st.hasMoreTokens())
	         st.nextToken(); //Datum av ngt slag, bra f???r vad?
	
	      if(st.hasMoreTokens()){
	         stt = new StringTokenizer(st.nextToken(),";");
	         String tmp2;
	         while(stt.hasMoreTokens()){
		        tmp2 = stt.nextToken();
		        int b = tmp2.indexOf(":");
		        int i=0;
		       StringTokenizer sttt = new StringTokenizer(tmp2.substring(b+1),",");
		       String[] sdd = new String[sttt.countTokens()];
		       while(sttt.hasMoreTokens()){
		          sdd[i] = sttt.nextToken();
		          i++;
		       }
		       setProperty(tmp2.substring(0,b),sdd);
	        }	
	     }   
      }
       System.out.println("Lyckades initialisera LCP-head!");
       System.out.println("URI=="+URI);
    }
    
    public void setScreenSize(String s){
	int jj=s.indexOf(",");
	clientScreenSize = new int[2];
	if(jj>0){
	    clientScreenSize[0]=Integer.parseInt(s.substring(0,jj).trim());
	    clientScreenSize[1]=Integer.parseInt(s.substring(jj+1).trim());
	}
    }
    
    private void setScreenSize(int x, int y){
	int[] xy={x,y};
	clientScreenSize = xy;
    }
    
    private void setScreenSize(int[] xy){
	clientScreenSize=xy;
    }

    }*/  
}           
