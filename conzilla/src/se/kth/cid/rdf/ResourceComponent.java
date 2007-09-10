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


package se.kth.cid.rdf;


import org.w3c.rdf.model.*;
import org.w3c.rdf.implementation.model.*;
import org.w3c.rdf.util.RDFUtil;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.component.rdf.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import java.util.*;
import org.w3c.rdf.vocabulary.rdf_syntax_19990222.RDF;
import org.w3c.rdf.vocabulary.rdf_schema_200001.RDFS;

/** This class wraps RDF-resources into neurons.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ResourceComponent extends LocalComponent implements Neuron
{  
  static final String VIEW="http://www.conzilla.org/2001/09/16/view#style";
  static Resource STYLE = new ResourceImpl(VIEW);
    
  /** A reference to a total model, one containing the union of all models.
   */ 
  protected TotalModel totalModel;

  protected Resource resource;
  ConzillaRDFModel model;
  DataValue [] dataValues;

  public boolean isEditable()
    {
	return model.isEditable();
    }
    
  public ResourceComponent(TotalModel totalModel, ConzillaRDFModel model, Resource resource, URI uri) 
    {
      super(uri, uri, MIMEType.RDF);
      this.totalModel = totalModel;
      this.model    = model;
      this.resource = resource;

      metaData = new RDFMetaData(totalModel, this);
    }  
  	    
  public String getType()
    {
	return getType(model);
    } 

  public String getType(Model m)
    {
	//	return "urn:path:/org/conzilla/builtin/types/UML/ClassD/concept";
	//	    RDFUtil.printStatements(model, System.out);
	String type = getType(m, resource);
	if (type != null)
	    return type;
	Resource r = qetClassOf(m, resource);
	while (r!=null)
	    {
		type = getType(m , r);
		if (type != null)
		    return type;
		r = qetSuperClassOf(m, resource);
	    }
	Tracer.debug("No type found, choosing resource as default");
	return "urn:path:/org/conzilla/builtin/types/rdf/rdfs/Resource";
    }

    private Resource qetClassOf(Model m, Resource r)
    {
	try {
	    Model mod1 = m.find(r, RDF.type, null);	    
	    return (Resource) RDFUtil.get1(mod1).object();
 	} catch (ModelException me) {}
	return null;
    }

    private Resource qetSuperClassOf(Model m, Resource r)
    {
	try {
	    Model mod = m.find(r, RDFS.subClassOf, null);	    
	    if (mod.isEmpty())
		return null;
	    return (Resource) RDFUtil.get1(mod).object();
 	} catch (ModelException me) {}
	return null;
    }
    private String getType(Model m, Resource r)
    {
	try {
	    Tracer.debug("Investigating resource: "+r.getURI());		
	} catch (Exception e) {}
	if (r.equals(RDFS.Resource))
	    {
		Tracer.debug("The resource is the Resource resource in RDFS.");		
		return "urn:path:/org/conzilla/builtin/types/rdf/rdfs/Resource";
	    }
	if (r.equals(RDFS.Class))
	    {
		Tracer.debug("The resource is the Class resource in RDFS.");		
		return "urn:path:/org/conzilla/builtin/types/rdf/rdfs/Class";
	    }
	try {
	    //	    Tracer.debug("Found type "+res1.getURI());
	    Model mod = m.find(r, STYLE, null);
	    if (mod.isEmpty())
		return null;
	    return ((Resource) RDFUtil.get1(mod).object()).getURI();
	    //	    Tracer.debug("Found type description "+res1.getURI());
 	} catch (ModelException me) {
	} catch (NullPointerException ne) {}
	return null;
    }	
  public DataValue[] getDataValues()
    {
	return getDataValues(model);
    }
    
  public DataValue[] getDataValues(Model m)
    {
	try {
	    Vector data = new Vector();
	    Statement st;
	    Enumeration en=m.find(resource, null, null).elements();
	    while (en.hasMoreElements())
		{
		    st = (Statement) en.nextElement();
		    if (! (st.object() instanceof Resource))
			data.addElement(new RDFDataValue(st));
		}
	    RDFDataValue [] dataValues = new RDFDataValue[data.size()];
	    data.copyInto(dataValues);
	    return dataValues;
	} catch (ModelException me)
	    {}
	return new DataValue[0];
    }

  public void setDataValues(DataValue[] values)
    throws ReadOnlyException
    {    
      if(!isEditable())
	throw new ReadOnlyException("This Neuron is Read-Only!");

      //Code for adding properties with Literals as objects goes here. 

      fireEditEvent(new EditEvent(this, DATAVALUES_EDITED, dataValues));
    }
  
  public Axon[] getAxons()
    {
	return totalModel.getReifiedResourceComponents(resource);
    }

  public Axon[] getAxons(ConzillaRDFModel m)
    {
	return m.getReifiedResourceComponents(resource);
    }
    
  public Axon getAxon(String id)
    {
	return totalModel.getReifiedResourceComponent(id);
    }

  public Axon getAxon(ConzillaRDFModel m, String id) 
    {
	return m.getReifiedResourceComponent(id);
    }

  public void removeAxon(String id) throws ReadOnlyException
    {
	removeAxon(model, id);
    }
  
  public void removeAxon(Model m, String id) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      
      //Code for removing axons should probably be inside the model.
      
      fireEditEvent(new EditEvent(this, AXON_REMOVED, id));
    }
  

  /** Used to add an Axon with a known ID. Typically used only
   *  when loading a saved Neuron.
   *
   *  @param id the ID of the Axon.
   *  @param type the type of the Axon.
   *  @param neuronURI the URI of the pointed-to Neuron.
   *
   *  @return the created Axon.
   *  @exception ReadOnlyException if the Neuron was not editable.
   *  @exception NeuronException if the Axon could not be created.
   *  @exception InvalidURIException if the Neuron URI was not valid.
   */
  public Axon addAxon(String id, String type, String neuronURI)
    throws NeuronException, ReadOnlyException, InvalidURIException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      
      //Code for adding axons should probably be inside the model.

      fireEditEvent(new EditEvent(this, AXON_ADDED, id));
      return  null;//axon;
    }
  
  public Axon addAxon(String type, String neuronURI)
    throws NeuronException, ReadOnlyException, InvalidURIException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      //Code for adding axons should probably be inside the model.      
      
      //      fireEditEvent(new EditEvent(this, AXON_ADDED, id));
      return null; //axon;
    }    
}





    /*  public Statement[] getStatements()
    {
	return getStatements(model);
    }

  public Statement[] getStatements(Model m)
    {
	try {
	    return RDFUtil.getStatementArray(m.find(this, null, null));
	} catch (ModelException me)
	    {}
	return null;
    }
    */
