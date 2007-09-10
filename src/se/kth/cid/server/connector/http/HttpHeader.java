/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.connector.http;

import java.util.Hashtable;
import java.util.StringTokenizer;

public class HttpHeader{
    
    private String header;
    
    private String commandLine;
    
    private String method;
    
    private String URL;
    
    private String protocol;
    
    private Hashtable properties;

    public HttpHeader(String s){
	properties = new Hashtable();
	header = s;
	process(s);
    }
    
    public String getCommandLine(){
	return commandLine;
    }
    
    public String getMethod(){
	return method;
    }

    public String getRequestedURL(){
	return URL;
    }

    public String getProtocol(){
	return protocol;
    }

    public String[] getProperty(String Property){
	return ((String[])properties.get(Property.toLowerCase()));
    }
    
    public void setProperty(String Property, String[] values){
	properties.put(Property.toLowerCase(),values);
    }
    
    private void process(String s){
	//System.out.println(s);
	StringTokenizer st = new StringTokenizer(s,"\n");
	if(st.hasMoreTokens()){
	    commandLine = st.nextToken();
	    sortCommandline(commandLine);
	}
	
	String current;
	while(st.hasMoreTokens()){
	    current=st.nextToken();
	    int jj = current.indexOf(':');
	    if(jj>0){
		String prop = current.substring(0,jj).trim();
		String values=current.substring(jj+1);
		StringTokenizer stt = new StringTokenizer(values,",");
		String[] val = new String[stt.countTokens()];
		int i = 0;
		while(stt.hasMoreTokens()){
		    val[i] = stt.nextToken().trim();
		    i++;
		}
		setProperty(prop,val);
	    }
	}
	/*Enumeration e = properties.keys();
	  while(e.hasMoreElements()){
	  String sbd = (String) e.nextElement();
	  System.out.println(sbd);
	  String[] ss = (String[])properties.get(sbd);
	  for (int i=0;i<ss.length;i++)
	  System.out.println(";;"+ss[i]);
	  }*/
    }
    
    private void sortCommandline(String s){
	
	StringTokenizer st = new StringTokenizer(s);
	
	if(st.hasMoreTokens())
	    method = st.nextToken().trim();
	
	if(st.hasMoreTokens())
	    URL = st.nextToken().trim();
	
	if(st.hasMoreTokens())
	    protocol = st.nextToken().trim();	
    }
}
