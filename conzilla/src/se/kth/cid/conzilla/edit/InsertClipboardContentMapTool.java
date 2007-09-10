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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.library.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** 
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class InsertClipboardContentMapTool extends MapTool
{
  public InsertClipboardContentMapTool(String name, MapController cont)
  {
    super(name,Tool.ACTION, cont);
  }
    
  protected boolean updateImpl()
    {
      ClipboardLibrary cl = controller.getConzillaKit().getConzillaEnvironment().getRootLibrary().getClipboardLibrary();
      if (mapEvent.hitType != mapEvent.HIT_NONE && mapObject.getNeuron() != null
	  && cl.getComponent() != null && mapObject.getNeuron().isEditable())
	  return true;
      return false;
    }

  public void activateImpl()
    {
      ClipboardLibrary cl = controller.getConzillaKit().getConzillaEnvironment().getRootLibrary().getClipboardLibrary();
      if (cl.getComponent() == null)
	return;
      
      URI base = URIClassifier.parseValidURI(mapObject.getNeuron().getURI());
      URI absoluteURI=URIClassifier.parseValidURI(cl.getComponent().getURI());
      String relativeURI;
      try {
	  relativeURI=base.makeRelative(absoluteURI, false);
	} catch (MalformedURIException me)
	    {
		relativeURI=absoluteURI.toString();
	    }
      
      //context relation
      if (mapObject.getNeuron().isEditable())
	  {
	      MetaData.Relation relation = new MetaData.Relation(new MetaData.LangString(null, "content"),
								 null,
								 relativeURI);
	      
	      MetaDataUtils.addObject(mapObject.getNeuron().getMetaData(),
				      "relation", relation);
	  }

      // Workaround for save.
      ComponentEdit cEditor = controller.getConzillaKit().getComponentEdit();
      cEditor.editComponent(mapObject.getNeuron(), false);
    }
}

