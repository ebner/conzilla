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

public class ContentMenu extends ToolMenu
{
    //  MapMenuTool info;
    //  MapMenuTool store;
    
  protected MapController controller;
  protected MouseEvent contentEvent;

  public ContentMenu(String name, MapController cont)
  {
    super(name, cont);
    this.controller=controller;

    addTool(new ContentTool("VIEW", Tool.ACTION, cont) {
	protected boolean updateImpl()
	    {return true;}
	public void activateImpl()
	    {
		this.controller.getContentSelector().select(new Integer(contentIndex));
	    }});
    addTool(new ContentTool("INFO", Tool.ACTION, cont) {
	protected boolean updateImpl()
	    { return true;}
	public void activateImpl() {
	    Component comp=this.controller.getContentSelector().getContent(new Integer(contentIndex));
	    this.controller.getConzillaKit().getMetaDataDisplayer().showMetaData(comp);	
	}});
    
    addTool(new ContentTool("COPY", Tool.ACTION, cont) {
	protected boolean updateImpl()
	    { return true;}
	public void activateImpl() {
	    ClipboardLibrary cl=this.controller.getConzillaKit().getConzillaEnvironment().getRootLibrary().getClipboardLibrary();
	    Component comp=this.controller.getContentSelector().getContent(new Integer(contentIndex));
	    cl.setComponent(comp);
	}});

    addTool(new ContentTool("EDIT", Tool.ACTION, cont) {
	protected boolean updateImpl()
	    { 
		Component comp=this.controller.getContentSelector().getContent(new Integer(contentIndex));
		return comp.isEditable();
	    }
	public void activateImpl() {
	    ComponentEdit componentEdit = this.controller.getConzillaKit().getComponentEdit();
	    Component comp=this.controller.getContentSelector().getContent(new Integer(contentIndex));
	    componentEdit.editComponent(comp, ContentDialog.class, true);
	    componentEdit.setVisible(true);
	}}); 
  }

  public void update(MouseEvent e, Object o)
  {
      contentEvent=e;
      if ( o != null && o instanceof Integer)
	  {
	      if (!choice.getPopupMenu().isVisible())
		  choice.update(o);
	  }
  }

  protected void activateImpl()
  {
      choice.getPopupMenu().show((java.awt.Component) contentEvent.getSource(),
				 contentEvent.getX(),
				 contentEvent.getY());      
  }
  
  protected void detachImpl()
  {
    super.detachImpl();
    contentEvent=null;
  }
}
