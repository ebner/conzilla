/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.service.lcp;

import java.util.Vector;

import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFContainerManager;
//import se.kth.cid.rdf.RDFModel;
import se.kth.cid.server.service.Request;
//import se.kth.cid.component.ContainerManager;

import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import se.kth.cid.util.Tracer;

public class LCPNeighbourhoodResponse implements LCPResponse{
    
    MapStoreManager msm;

    Request req;

    String[] requestedURIs;

    public LCPNeighbourhoodResponse(Request req){
	   this.req = req;
	   this.requestedURIs = req.getLCPHeader().getURIs();
       //System.out.println("Fixar konstruktorn!");
    }
    
    public String getProtocolVersion(){
       return "0.1";
    }
	public Object createResponse() throws Exception{
		return getHeader()+getReqNeighbourhood(this.requestedURIs);
	}
	
	public String getHeader(){
	   return "LCP/"+getProtocolVersion()+" GETNEIGHBOURHOOD\r\n\r\n" +
	   	"<"+requestedURIs[0]+">\r\n\r\n";
	}
	
    public static Vector
       getNeighbourhood(String[] requestURIs) throws Exception{
    
	   RDFContainerManager rdfmm = 
	     (RDFContainerManager) LCPRequestHandler.getTmpFormatHandler().getContainerManager();
	
	   Vector dl = new Vector();
	   Vector maps = new Vector();
	   Vector maps2 = new Vector();
	
	   //String returnString="";
	   
	   try{
	      Model rr = rdfmm.getTotalRDFModel();//(RDFModel) v.get(i);
	      SelectorImpl ssi = new SelectorImpl(null, CV.displayResource, (RDFNode) null);
	      StmtIterator sts = rr.listStatements(ssi);
	      while(sts.hasNext()){
		     Statement s = sts.nextStatement();
		     for (int reqi=0;reqi<requestURIs.length;reqi++){
		        if(requestURIs[reqi].equals(s.getObject().toString()))
			       dl.add(s.getSubject());
		     }
	      }
	      for (int ii=0;ii<dl.size();ii++){
		     ssi = new SelectorImpl(null, RDF.type , CV.NodeLayout);
		     sts = rr.listStatements(ssi);
		  while(sts.hasNext()){
		     Statement s = sts.nextStatement();
		     StmtIterator stss = s.getSubject().listProperties();
		     while(stss.hasNext()){
			    Statement ss = stss.nextStatement();
			    if(!(maps.contains(ss.getSubject())) && dl.contains(ss.getObject())){
			    //System.out.println(ss.getObject());
			       maps.add(ss.getSubject());
			    }
		     }
		  }
	    }
	    for (int ii=0;ii<maps.size();ii++){
		   ssi = new SelectorImpl(null, RDF.type , CV.ContextMap);
		   sts = rr.listStatements(ssi);
		   while(sts.hasNext()){
			  Statement s = sts.nextStatement();
			  StmtIterator stss = s.getSubject().listProperties();
			  while(stss.hasNext()){
			     Statement ss = stss.nextStatement();
			     if(!(maps2.contains(ss.getSubject())) && maps.contains(ss.getObject())){
				 //System.out.println(ss.getObject());
				    maps2.add(ss.getSubject());
			     }
			  }
		   }
	    }
	   }catch (RDFException rdfe){
		   Tracer.debug("Fick RDFException i ACPNeighbourhoodResponse");
		   rdfe.printStackTrace();
		   throw rdfe;
	   }
	    return maps2;
       }//}
	
	private String getReqNeighbourhood(String[] URIs) throws Exception{
		//System.out.println("K??r private...");
		Vector maps = getNeighbourhood(URIs);
	   String returnString = "";	
	   for (int ii=0;ii<maps.size();ii++){
	   	//System.out.println("L??ngre ??n 0");
	      com.hp.hpl.jena.rdf.model.Resource resource = 
		    (com.hp.hpl.jena.rdf.model.Resource)maps.get(ii);
	        String kartURI = resource.getURI();
	        String kartTitel;
	        if(resource.hasProperty(DC.title))
	          kartTitel = resource.getProperty(DC.title).getString();
	       else
		      kartTitel = kartURI;
	    
	    //if(!kartURI.equals(msm.getConceptMap().getURI()))
		   returnString += "<"+kartURI+";"+kartTitel+">\r\n";
	   }
	
	   return returnString;       
    }
    /*public static String getMapNeighbourhood(String[] URIs) throws Exception{
		Vector maps = getNeighbourhood(URIs);
		String returnString = "<";	
		for (int ii=0;ii<maps.size();ii++){
		   com.hp.hpl.mesa.rdf.jena.model.Resource resource = 
			 (com.hp.hpl.mesa.rdf.jena.model.Resource)maps.get(ii);
			 String kartURI = resource.getURI();
			 String kartTitel;
			 if(resource.hasProperty(DC.title))
			   kartTitel = resource.getProperty(DC.title).getString();
			else
			   kartTitel = kartURI;
	    
     		 //if(!kartURI.equals(msm.getConceptMap().getURI()))
     		 if (maps.size()-1==0)
			   returnString += kartTitel+";"+kartURI;
			else
			   returnString += kartTitel+";"+kartURI+";";
		}
	
		return returnString +">";       
    }*/
}
