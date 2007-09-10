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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.filter.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.ConceptMap;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

/** This is a view-command-tool that have to be embedded into a menu.
 *  The reason is that it needs a neuron to act on.
 *  Typically this is done by calling updateState with a mapEvent as input.
 *
 *  @author Matthias Palmèr
 *  @version $Revision$
 */

public class ViewAlterationTool extends MapMenuTool implements ActionListener
{
    JMenu choice;
    //  Filter filter;
    
    JMenuItem simpleItem;
    Component[] components;
    

  /** Constructs an ViewAlterationTool.
   */
  public ViewAlterationTool(String name, String resbundle, MapController cont)
  {
    super(name, resbundle, cont);

    choice = new JMenu();  
    simpleItem = new JMenuItem();
    setJMenuItem(simpleItem);
    simpleItem.addActionListener(this);
  }

    public Neuron getCurrentNeuron()
    {
	if (mapEvent!=null && mapEvent.mapObject != null)
	    return mapEvent.mapObject.getNeuron();
	else return null;
    }
	  
	
    public void update(MapEvent e)
    {
	super.update(e);

	Neuron neuron = getCurrentNeuron();
	if (neuron == null)
	    {
		Tracer.debug("Neuron was null");		
		choice.removeAll();
		simpleItem.setEnabled(false);
		setJMenuItem(simpleItem);
	    }
	else 
	    {
		ConceptMap cMap = controller.getMapScrollPane().getDisplayer().
		    getStoreManager().getConceptMap();			
		setJMenuItem(getMenuItemForNeuron(mapEvent.mapObject.getNeuron(), cMap));
	    }
    }

    public JMenuItem getMenuItemForNeuron(Neuron neuron, ConceptMap cMap)
    {
	choice.removeAll();
	
	if (neuron != null)
	    {
		Vector menus = new Vector();
		Enumeration extras = controller.getConzillaKit().getExtras();
		while(extras.hasMoreElements())
		    {
			JMenu menu = new JMenu();
			Object [] arr = {this, menu, neuron, cMap};
			((Extra) extras.nextElement()).addExtraFeatures(controller, arr, "viewalterationtool", null);
			if (menu.getItemCount()>0)
			    menus.add(menu);
		    }
		if (menus.size() == 1)
		    {
			choice = (JMenu) menus.firstElement();
			return choice;		    
		    }
		//FIXME: fill in >1 alternative....
		
		ComponentStore cstore = controller.getConzillaKit().getComponentStore();
		RelationSet relset = new RelationSet(neuron, "content", cstore);
		components = relset.getRelations();
		simpleItem.setEnabled(components.length != 0);	  
	    }
	else
	    simpleItem.setEnabled(false);
    
	return simpleItem;
    }
    
    public void actionPerformed(ActionEvent ae)
    {
	show(components, "all", null);
    }

  /** This command results in a set of content is displayed.
   *  Observe that update has to have been succesfull last time called.
   *  Otherwise the view-action isn't activated and this function isn't called.
   *
   *  @see Controller.selectContent()
   */
    public void show(Object comps, String leaf, Object path)
    {
      ContentSelector sel=controller.getContentSelector();

      Component [] vcomps=null;
      if (comps instanceof Collection)
	  {
	      vcomps = new Component[((Collection) comps).size()];
	      vcomps = (Component []) ((Collection) comps).toArray(vcomps);
	  }
      else if (comps instanceof Component[])
	vcomps = (Component []) comps;

      String [] strpath=null;
      if (path != null && path instanceof FilterNode)
	  {
	      Vector vpath=new Vector();
	      if (leaf != null)
		  vpath.addElement(leaf);
	      FilterNode node=(FilterNode) path;
	      do{
		  vpath.addElement(node.getFilterTag());
		  node=node.getTop();
	      } while (node!=null && node.getTop()!=null); //The top node isn't neccessary to show....
	      
	      strpath=new String[vpath.size()];
	      strpath=(String []) vpath.toArray(strpath);
	  }
      else if (leaf!=null)
	  {
	      strpath=new String[1];
	      strpath[0]=leaf;
	  }
      
      sel.selectContentFromSet(vcomps);
      sel.setContentPath(strpath);
      highlightNeuronMapObject();
    }


  /** This function highlight the current mapevent......
   */   
  private void highlightNeuronMapObject()
  {  
      if (mapEvent == null || mapEvent.mapObject ==null)
	  return;
	    
   final NeuronMapObject nmo=mapEvent.mapObject.getNeuronMapObject();
   final ContentSelector sel=controller.getContentSelector();

    nmo.pushMark(new Mark(ContentSelector.COLOR_CONTENT_FROM_BOX, null, null), this);
    sel.addSelectionListener(ContentSelector.SELECTOR, new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent e)
	    {
		sel.removeSelectionListener(ContentSelector.SELECTOR, this);
		nmo.popMark(ViewAlterationTool.this);
	    }
	});
  }
}
