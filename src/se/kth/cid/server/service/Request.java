/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.service;

import se.kth.cid.server.connector.http.HttpHeader;
import se.kth.cid.server.service.lcp.*;

public class Request{
     
    HttpHeader httpheader;
    
    LCPHeader lcpheader;

    Response response = null;

    String language;

    public Request(LCPHeader lcpheader) {
       this.lcpheader = lcpheader;
       language = lcpheader.getLanguage()[0];
    }
    
    public void setResponse(Response r){
	   response = r;
    }

    public void setLanguage(String lang){
	   language=lang;
    }
    
    public String getLanguage(){
	   return language;
    }
    
    private boolean hasHttpHeader(){
	   return lcpheader.hasHttpheader();
    }
    
    public LCPHeader getLCPHeader(){
	   return lcpheader;
    }

    public HttpHeader getHttpHeader(){
	   return lcpheader.getHttpHeader();
    }
    
    public String getURI(){
       return lcpheader.getfirstURI();
    }
}
