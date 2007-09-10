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


package se.kth.cid.conzilla.content;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.library.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.component.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

public class ContentMenu extends ToolsMenu
{
    public static final String CONTENT_MENU = "CONTENT_MENU";
    //  MapMenuTool info;
    //  MapMenuTool store;
    
    MapController controller;
    
  public ContentMenu(MapController cont)
  {
    super(CONTENT_MENU, ContentMenu.class.getName());

    controller = cont;
    addTool(new ContentTool("VIEW", ContentMenu.class.getName()) {
	    public void actionPerformed(ActionEvent e)
	    {
		controller.getContentSelector().select(contentIndex);
	    }}, 100);
    //FIXME to extra!
    /*    addTool(new ContentTool("INFO", ContentMenu.class.getName()) {
	    public void actionPerformed(ActionEvent e) {
		Component comp = controller.getContentSelector().getContent(contentIndex);
		controller.getConzillaKit().getMetaDataDisplayer().showMetaData(comp);	
	    }}, 200);
    */
    this.controller.getConzillaKit().extendMenu(this, controller);
  }
    
  public MapController getController()
  {
    return controller;
  }
    
  public void showPopup(MouseEvent ev, int index)
  {
      if (!getPopupMenu().isVisible())
	  {
	      Enumeration e = getTools();
	      while(e.hasMoreElements())
		  {
		      Tool t = (Tool) e.nextElement();
		      if(t instanceof ContentTool)
			  ((ContentTool) t).update(index);
		  }
	  }
      getPopupMenu().show((java.awt.Component) ev.getSource(),
			  ev.getX(),
			  ev.getY());   
  }
}
