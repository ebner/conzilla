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

import se.kth.cid.identity.*;
import org.w3c.rdf.util.*;
import org.w3c.rdf.model.*;

/** Overrides the default model to be a conzilla model.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ConzillaRDFFactoryImpl extends RDFFactoryImpl 
{
    TotalModel totalModel;
    public ConzillaRDFFactoryImpl()
    {
	totalModel = new TotalModel(getNodeFactory());
    }
    public TotalModel getTotalModel()
    {
	return totalModel;
    }
    public ConzillaRDFModel createModel(URI uri, URI loaduri) 
    {
	ConzillaRDFModel m = new ConzillaRDFModel(totalModel, uri, loaduri, MIMEType.RDF);
	return m;
    }
}
