
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
import java.beans.*;
import se.kth.cid.util.*;
import se.kth.cid.component.rdf.*;
import se.kth.cid.component.local.LocalMetaData;
import se.kth.cid.component.Component;
import org.w3c.rdf.model.*;


public class RDFMetaData extends LocalMetaData implements PropertyChangeListener{
    
    TotalModel totalModel;
    
    public RDFMetaData(TotalModel totalModel, Component comp)
    {
	super(comp);

	this.totalModel = totalModel;
	Tracer.debug("Metadata constructed for "+ getComponent().getURI());
	
	try {
	    RDFMetaDataHandler.load(this, totalModel.getTotalRDFModel(), getComponent().getURI());
	} catch (ModelException me) {}
	
	totalModel.addPropertyChangeListener(this);
    }
    public void detach()
    {
	totalModel.removePropertyChangeListener(this);
    }
    
    public void propertyChange(PropertyChangeEvent e)
    {
	Tracer.debug("Updating metadata for component "+getComponent().getURI());
	try {
	    RDFMetaDataHandler.load(this, totalModel.getTotalRDFModel(), getComponent().getURI());
	} catch (ModelException me) {}
    }
}
