/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.service;

public interface Response{
   public Object createResponse() throws Exception;
}
    
    /*OutputStream os;
    
    Request request;

    String HttpResponse;

    LCPResponse acpr;
    
    String replyString;

    String mapURI = 
	"urn:path:/org/conzilla/builtin/maps/default"; //Eller ngn annan defaultkarta...
    
    public Response(OutputStream os){
	this.os = os;
    }

    public void setRequest(Request request){
	this.request = request;
    }

    public void process(){
	if(request == null){
	    System.out.println("Fel i Response: request �r null!");
	}
	else{
	    if(request.getACPHeader() != null){
		//S�tt till defaultkarta...
		mapURI = request.getACPHeader().getURI();
		System.out.println("mapuri=="+mapURI);
	    } //annars defaultkartan som redan �r satt...
	    
	    if(request.getHttpHeader() != null){
		//S�tt HttpRespons till OK, s� l�nge...
		HttpResponse ="HTTP/1.0 200 OK\r\n";
		System.out.println("S�tter HttpResponse! till: "+HttpResponse);
	    }
	    try{
		MapStoreManager msm = LCPRequestHandler.getMapStoreManager(mapURI);
		if(request.getACPHeader() != null){ 
		    System.out.println("ACPHeader �r inte null...");
		    msm = LCPRequestHandler.getMapStoreManager(request.getACPHeader().getURI());
		    
		    if(request.getACPHeader().getTypeOfRequest().toUpperCase().equals("GETNEIGHBOURHOOD")){
			//getAndSendNeighbourhood(msm);
			acpr = new LCPNeighbourhoodResponse(msm,request);
		    }else if(request.getACPHeader().getTypeOfRequest().toUpperCase().equals("GET")){
			System.out.println("Ska GET: "+mapURI);
			acpr = new LCPMapResponse(msm,request);
		    }
		}else { 
		    //Om ACPHeader==null antas man vilja ha defaultkartan  
		    //acpr = new ACPNeighbourhoodResponse(msm,request,UIT);
		    acpr = new LCPMapResponse(msm,request);//getAndSendMap(msm);
		}
		//ACPResponse = "appelb";
		replyString = acpr.createResponse();
		
	    }catch (Exception e){
		System.out.println("F�r fel i Response.process"+e);
		e.printStackTrace();
		HttpResponse = "HTTP/1.0 404 Not Found";
		return;
	    }
	    
	    //System.out.println("Ska til karta "+mapURI);
	    
	    PrintWriter out = new PrintWriter(os,true);
	    
	    out.println(HttpResponse);
	    //out.println();
	    //System.out.println(HttpResponse);
	    out.println(replyString);
	    //System.out.println(replyString);
	    out.close();
	    
	}
    }
}
*/