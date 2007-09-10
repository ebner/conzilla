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
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import java.util.*;


public class EditConceptMapTool extends AbstractTool implements EditListener
{
  protected MapManager manager;
  protected MapController controller;
  protected LayerComponent layer;
  
  public EditConceptMapTool(String name, MapController controller,
			    MapManager manager, LayerComponent layer)
  {
    super(name,Tool.EXCLUSIVE);
    this.layer=layer;
    this.manager=manager;
    this.controller=controller;
    manager.getDisplayer().getMap().addEditListener(this);
    check();
  }
  
  protected void detachImpl()
  {
    ConceptMap conceptmap=manager.getDisplayer().getMap();
    conceptmap.removeEditListener(this);
  }

  protected void activateImpl()
  {
    manager.getDisplayer().getMap().setEditable(true);
    if (layer!=null)
      {
	manager.addOverlayLayer(layer);
	layer.activate();
      }
  }
  
  protected void deactivateImpl()
  {
    manager.getDisplayer().getMap().setEditable(false);
    if (layer!=null)
      {      
	manager.removeOverlayLayer(layer);
	layer.deActivate();
      }
  }
  

  public void componentEdited(EditEvent e)
  {
    if (e.getEditType()==Component.EDITABLE_CHANGED)
      {
	check();
      }
  }
  
  private void check()
  {
    ConceptMap conceptmap=manager.getDisplayer().getMap();
    ComponentSaver csaver=controller.getComponentSaver();
    if (conceptmap.isEditingPossible() && csaver!=null && csaver.isComponentSavable(conceptmap))
      enable();
    else
      disable();
  }
}
