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
import se.kth.cid.neuron.DataValue;
import org.w3c.rdf.model.*;
import org.w3c.rdf.util.RDFUtil;

public class RDFDataValue implements DataValue 
{
    Statement statement;

    RDFDataValue(Statement statement)
    {
	this.statement = statement;
    }
	
    public String predicate()
    {
	try {
	    return statement.predicate().getURI();
	} catch (ModelException me) {}
	    return ConzillaRDFModel.S_RDF+"predicate";
    }

    //This function is already defined in LocalComponent but without throwing an ModelException...
    //It seems like the compiler manages though.
    /*    public java.lang.String getURI()
	throws ModelException
    {
	return statement.getURI();
	}*/

    public String objectValue()
    {
	try {
	    return statement.object().getLabel();
	} catch (ModelException me) {}
	    return "";
    }
}
