/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package se.kth.cid.edutella.extra;

import net.jxta.edutella.consumer.*;
import net.jxta.edutella.util.*;
import net.jxta.edutella.util.datamodel.*;
import net.jxta.edutella.vocabulary.*;
import com.hp.hpl.mesa.rdf.jena.mem.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import net.jxta.edutella.util.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;




public class EdutellaExtra implements Extra
{

    public static final String CONZILLA_NS = "http://www.conzilla.org/edutella/rdf#";
	    // as the providers strip the namespace from the variable URIs currently,
	    // we give the variable a name without namespace as workaround.
	    // The corect code should be:
	    //  ... = CONZILLA_NS + "TITLE_QUERY";
	public static final String CONZILLA_TITLE_QUERY = "#" + "TITLE_QUERY";

    EdutellaConsumer edutellaApp;
    boolean inited = false;
    
    static String QueryFile = "qel3_1.xml";
    
    public boolean initExtra(ConzillaKit kit) 
    {
	return true;
    }

    public String getName() {
	return "Edutella";
    }

    public void refreshExtra() 
    {
    }
    public boolean saveExtra() {return true;}

    public void exitExtra() {}
    
    
    

    public void extendMenu(final ToolsMenu menu, final MapController c)
    {
	if(menu.getName().equals(MenuFactory.TOOLS_MENU))
	{
	    menu.addTool(new Tool("EdutellaExtra", this.getClass().getName()) {
		    public void actionPerformed(ActionEvent e)
		    {
			activate();
		    }}, 1000);
	    menu.addTool(new Tool("EdutellaExtra.exec", this.getClass().getName()) {
		    public void actionPerformed(ActionEvent e)
		    {
			if(inited)
			    {
				Model qmodel = new ModelMem();
				Model rmodel = new ModelMem();
				StringWriter w = new StringWriter();
				EduResultDisplayer disp = null;
				
				try {
				    qmodel.read(new FileReader(QueryFile), "");
				    Resource resVar = findResourceVariable(qmodel);
				    RDFQEL3 qr = new RDFQEL3(qmodel);
				    EduQuery query = queryCompletion(qr.getEduQuery(), resVar);

					if ( query != null ) {
					    disp = new EduResultDisplayer(query, resVar, c);
					    ResultListener listener = disp.createResultListener();
				    	edutellaApp.exececuteQuery(query, listener);
						
					} else {
						ErrorMessage.showError("Edutella error", "couldn't execute query", null, disp);
					}
				} catch (FileNotFoundException ex) {
				    ex.printStackTrace();
				} catch (RDFException ex) {
				    ex.printStackTrace();
				} catch (Exception ex) {
				    ex.printStackTrace();
				}
				JFrame f = new JFrame("Result");
				f.getContentPane().add(new JScrollPane(disp));
				f.setSize(200, 200);
				f.show();
// currently always empty, so it doesn't seem to be necessary				
// 				JFrame f2 = new JFrame("Query");
//				JTextArea a = new JTextArea(w.toString());
//				f2.getContentPane().add(new JScrollPane(a));
//				f2.setSize(200, 200);
//				f2.show();
			    }
		    }}, 1100);
	}
    }
      public void addExtraFeatures(final MapController c, final Object o, 
				 String location, String hint)
    {}

    void activate()
    {
		if(edutellaApp == null)
	    {
			(new Thread() {
				public void run()
				{
			    	try {
						edutellaApp = EdutellaConsumer.getInstance();
						inited = true;
				    } catch(RuntimeException e) {
					e.printStackTrace();
				    }
				}
			}).start();
		}
    }
    
    /**
     * find out which variable to present in displayer later, marked from the
     * query with the property 'http://www.conzilla.org/rdf/edutella#resourceVariable'
     */
    public Resource findResourceVariable(Model m) throws RDFException {
		Property resVar = m.createProperty("http://www.conzilla.org/rdf/edutella#resourceVariable");

		// find query resource
		ResIterator queryRess = m.listSubjectsWithProperty(RDF.type, EDU.QEL3Query);
		Resource queryRes = null;
		if(queryRess.hasNext())
		    queryRes = queryRess.next();
		else
	    {
			Tracer.debug("No Query!");
			return null;
	    }
	
		Resource myVar = null;
	
		NodeIterator resourceVar = m.listObjectsOfProperty(queryRes, resVar);
		if(resourceVar.hasNext())
		    myVar = (Resource) resourceVar.next();
		else
		    {
			Tracer.debug("No resourceVar!");
			return null;
	    }
	    
	    return myVar;
	}	

    EduQuery queryCompletion(EduQuery query, Resource resourceVar) throws RDFException
    {
    	Model m = resourceVar.getModel();

		Resource myQ = m.createResource(CONZILLA_TITLE_QUERY);
		Property title = m.createProperty("http://purl.org/dc/elements/1.1/title");
	
		query.addVariable(myQ, "TITLE_QUERY");
		query.addQueryLiteral(query.createReifiedStatement(m.createStatement(resourceVar, title, myQ)));
		return query;
    }
}


