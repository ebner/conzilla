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

package se.kth.cid.conzilla.browse;
import se.kth.cid.util.*;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.history.*;
//import se.kth.cid.conzilla.filter.*;
import se.kth.cid.neuron.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a view-command-tool that have to be embedded into a menu.
 *  The reason is that it needs a neuron to act on.
 *  Typically this is done by calling updateState with a mapEvent as input.
 *
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class ViewMapTool extends MapTool
{

  /** Constructs an ViewCommandTool.
   */
  public ViewMapTool(String name, MapController cont)
  {
    super(name,Tool.ACTION, cont);

  }

  /** Updates the aspects in the view menu.
   *  Enables or disenabled the command in the menu accordingly to if
   *  there is content to view.
   */
  protected boolean updateImpl()
  {
      if ( mapEvent.mapObject!=null )
	{
	  Neuron ne=mapEvent.mapObject.getNeuron();
	  if(ne != null)
	    {
	      MetaData.Relation [] rels=ne.getMetaData().get_relation();
	      if (rels!=null)
		for (int i=0;i<rels.length;i++)
		  if (rels[i].kind.string.equals("content"))
		    return true;
	    }
	}
      return false;
  }

  /** This is a view-command that results in a set of content is displayed.
   *  Observe that updateState has to have been succesfully last time called.
   *  Otherwise the surf-action isn't activated and this function isn't called.
   *
   *  @see Controller.selectContent()
   */
  public void activateImpl()
    {
      ContentSelector sel=controller.getContentSelector();
      if ( mapEvent.mapObject!=null )
	{
	    ComponentStore cstore=controller.getConzillaKit().getComponentStore();
	    Neuron ne=mapEvent.mapObject.getNeuron();
	    if (ne!=null)
		{
		    try {
		    RelationSet relset = new RelationSet(URIClassifier.parseURI(ne.getURI()), "content", cstore);
		    sel.selectContentFromSet(relset.getRelations());
		    } catch (Exception e) 
			{
			    Tracer.debug("ViewMapMenuTool: Failed looking up content.");
			}
		}
	}
    }
}
