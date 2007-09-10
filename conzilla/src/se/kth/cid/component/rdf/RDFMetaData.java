
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


package se.kth.cid.rdf.metadata;
import java.beans.*;
import se.kth.cid.util.*;
import se.kth.cid.rdf.*;
import se.kth.cid.component.local.*;
import se.kth.cid.component.*;
import se.kth.cid.rdf.*;
import se.kth.cid.component.Component;
import org.w3c.rdf.model.*;


public class RDFMetaData extends GenericMetaData //implements PropertyChangeListener
{
    
    TotalModel totalModel;
    FiringComponent component;
    boolean storeUpdate = false;
    
    public RDFMetaData(TotalModel totalModel, FiringComponent component)
    {
	super();
	this.component = component;
	this.totalModel = totalModel;
	Tracer.debug("Metadata constructed for "+ getComponent().getURI());
    }
    public void init()
    {
	try {
	    storeUpdate = true;
	    RDFMetaDataHandler.load(this, totalModel.getTotalRDFModel(), getComponent().getURI());
	    storeUpdate = false;
	} catch (ModelException me) {}
    }
    public void detach()
    {
    }
    
    public void addModel(ConzillaRDFModel m)
    {
	Tracer.debug("Updating metadata for component "+getComponent().getURI());
	init();
    }
    public void removeModel(ConzillaRDFModel m)
    {
	init();
    }

  public Component getComponent()
    {
	return component;
    }

  public void fireEditEvent(EditEvent ee)
    {
	if (storeUpdate)
	    component.fireEditEventNoEdit(ee);
	else
	    component.fireEditEvent(ee);
	/*	if (component instanceof LocalComponent)
	    ((LocalComponent) component).fireEditEvent(ee);
	else if (component instanceof ConzillaRDFModel)
	((ConzillaRDFModel) component).fireEditEvent(ee);*/
    }

  public boolean isEditable()
    {
	//	if (component instanceof LocalComponent)
	    return component.isEditable();
	    /*	else if (component instanceof ConzillaRDFModel)
	    return ((ConzillaRDFModel) component).isEditable();
	else
	return false;*/
    }
	
}
