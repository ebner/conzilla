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
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import java.util.*;


public class EditMenu2 extends ToolMenu
{   
  public EditMenu2(String name, MapController controller, AxonEdit axonEdit)
  {
    super(name,controller);
    
    addTool(new NeuronDisplayerMapTool("EDIT_NEURON", controller));
    addTool(new RemoveNeuronMapTool("REMOVE_NEURON", controller));
    
    choice.addSeparator();

    addTool(new AxonShowMapTool("SHOW_AXON", controller, axonEdit));
    addTool(new AxonHideMapTool("HIDE_AXON", controller));
    addTool( new AxonCreateMapTool("CREATE_AXON", controller, axonEdit));
    addTool(new AxonRemoveMapTool("REMOVE_AXON", controller));

    choice.addSeparator();
    
    ToolMenu contentMenu=new ToolMenu("CONTENT", controller);
    contentMenu.addTool(new CreateContentMapTool("CREATE", controller));
    contentMenu.addTool(new InsertClipboardContentMapTool("CONTENT_FROM_CLIPBOARD", controller));

    addTool(contentMenu);
    addTool(new NeuronBoxMapTool("HIDE_BOX", "SHOW_BOX", controller));
    addTool(new BoxLineMapTool("HIDE_NEURONLINE", "SHOW_NEURONLINE", controller));
    addTool(new EditDetailedMapMapTool("EDIT_DETAILEDMAP", controller));

    choice.addSeparator();
    
    addTool(new ViewMapTool("VIEW",controller));
    addTool(new InfoMapTool("INFO",controller));
    addTool(new StoreMapTool("COPY",controller));
  }    
}
