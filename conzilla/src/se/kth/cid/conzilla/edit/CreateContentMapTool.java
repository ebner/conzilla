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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** 
 *  @author Matthias Palm�r
 *  @version $Revision$
 */
public class CreateContentMapTool extends ActionMapMenuTool
{

  public CreateContentMapTool(MapController cont)
  {
    super("CREATE_CONTENT", EditMapManagerFactory.class.getName(), cont);
  }
    
  protected boolean updateEnabled()
    {
      if (mapEvent.hitType!=mapEvent.HIT_NONE && mapObject.getNeuron()!=null)
	  return true;
      return false;
    }

  public void actionPerformed(ActionEvent e)
    {
      Neuron neuron = mapObject.getNeuron();

      ComponentDraft componentDraft = new ContentDraft(controller.getConzillaKit(), controller.getMapScrollPane());
      componentDraft.hintsFromComponent(neuron);
      componentDraft.hintBaseURI(controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI(), true);
      MetaData md = neuron.getMetaData();
      componentDraft.show();


      Component comp = componentDraft.getComponent();
      if(comp == null)
	return;

      URI base = URIClassifier.parseValidURI(neuron.getURI());
      URI absoluteURI=URIClassifier.parseValidURI(comp.getURI());
      String relativeURI;
      try {
	relativeURI = base.makeRelative(absoluteURI, false);
      } catch (MalformedURIException me)
	{
	  relativeURI = comp.getURI();
	}
      
      MetaData.Relation relation=new MetaData.Relation(new MetaData.LangString(null, "content"),
						       null,
						       relativeURI);
      
      MetaDataUtils.addObject(neuron.getMetaData(), "relation", relation);

    }
}
