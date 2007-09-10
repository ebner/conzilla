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


package se.kth.cid.protege;
import se.kth.cid.util.*;
import edu.stanford.smi.protegex.storage.walker.protege.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.model.*;
//import edu.stanford.smi.protegex.storage.rdf.*;


//FIXME: Should be refreshed?? depends on sources is updated or replaced.
/** Functionality for retrieving correct URIs etc.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ProtegeHelper
{
    KnowledgeBase kb;
    Namespaces namespaces;
    String defaultNamespace;
    ProtegeFrames protegeFrames;

    public ProtegeHelper(KnowledgeBase kb)
    {
	this.kb = kb;
	refresh();
    }
    public void refresh()
    {
	PropertyList sources = kb.getProject().getSources();
	defaultNamespace = sources.getString("namespace_name"); //Private static attribute in RDFBackend...
	namespaces = new Namespaces(defaultNamespace, sources);
	protegeFrames = new ProtegeFrames(kb, namespaces, null);
	//	Tracer.debug("The namespaces are: "+map.toString());
    }

    public String getURI(Frame frame)
    {
	if (frame == null)
	    {
		Tracer.debug("getURI in ProtegeHelper:Trying to fetch uri for null");
		return "";
	    }
	ProtegeFrame pf = protegeFrames.getProtegeFrame(frame);
	return pf.getNamespace() + pf.getLocalName();
    }

    public boolean hasSuperCls(Cls cls, Cls parent)
    {
	if (cls == null || parent == null)
	    return false;
	return cls == parent ? true : cls.getSuperclasses().contains(parent);

	/*	while (cls != null && cls instanceof Cls)
	    {
		if (parent == cls)
		    return true;
		cls = cls.getParent();
	    }
	    return false;*/
    }
    public boolean hasInstanceSuperCls(Instance ins, Cls parent)
    {
	return ins == null ? false : hasSuperCls(ins.getDirectType(), parent);
    }
    public boolean isSystemInstance(Instance ins)
    {
	if (ins instanceof Cls)
	    return kb.isClsMetaCls((Cls) ins); //inshasSuperCls((Cls) ins, kb.getDefaultClsMetaCls());
	else
	    return hasInstanceSuperCls(ins, kb.getRootClsMetaCls().getDirectType());
    }
}
